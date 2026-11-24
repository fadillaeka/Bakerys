package com.example.bakerys

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

class JelajahFragment : Fragment() {

    private lateinit var rvJelajah: RecyclerView
    private lateinit var jelajahAdapter: JelajahAdapter
    private var loadJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_jelajah, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvJelajah = view.findViewById(R.id.rvJelajah)
        rvJelajah.layoutManager = LinearLayoutManager(requireContext())

        jelajahAdapter = JelajahAdapter(
            recipeList = RecipeRepository.recipeList
        )

        rvJelajah.adapter = jelajahAdapter
        rvJelajah.scheduleLayoutAnimation()
    }

    override fun onResume() {
        super.onResume()
        if (::jelajahAdapter.isInitialized) {
            jelajahAdapter.notifyDataSetChanged()
        }
        muatPostinganDariDatabase()
    }

    private fun muatPostinganDariDatabase() {
        loadJob?.cancel()
        loadJob = viewLifecycleOwner.lifecycleScope.launch {
            try {
                val listData = SupabaseClient.instance.from("postingan_users")
                    .select(Columns.list("id", "caption", "foto_url")) {
                        order("id", Order.DESCENDING)
                    }
                    .decodeList<JsonObject>()

                val ctx = context ?: return@launch

                val parsedPosts = mutableListOf<Triple<Int, String, Uri?>>()
                val seenIds = mutableSetOf<Int>()

                for (item in listData) {
                    val id = item["id"]?.jsonPrimitive?.contentOrNull?.toIntOrNull()
                        ?: item["id"]?.jsonPrimitive?.intOrNull
                        ?: continue

                    if (!seenIds.add(id)) continue

                    val caption = item["caption"]?.jsonPrimitive?.contentOrNull ?: continue
                    val fotoUrl = item["foto_url"]?.jsonPrimitive?.contentOrNull
                    val uri = if (!fotoUrl.isNullOrEmpty()) {
                        try { Uri.parse(fotoUrl) } catch (_: Exception) { null }
                    } else null

                    parsedPosts.add(Triple(id, caption, uri))
                }

                // Ambil ringkasan suka untuk semua postingan dalam 1 query (bukan 2N query berurutan)
                val sukaMap = BakeryStorageManager.ambilRingkasanSukaPostingan(
                    ctx,
                    parsedPosts.map { it.first.toLong() }
                )

                val listUserPosts = parsedPosts.map { (id, caption, uri) ->
                    val (likeCount, isLiked) = sukaMap[id.toLong()] ?: Pair(0, false)
                    Recipe(
                        id = id,
                        title = caption,
                        description = "",
                        imageUri = uri,
                        imageResId = if (uri == null) R.drawable.cookies else null,
                        isLiked = isLiked,
                        isBookmarked = false,
                        likeCount = likeCount
                    )
                }

                // Bersihkan postingan user lama dari memory list, hanya pertahankan 5 resep bawaan (ID 1..5)
                RecipeRepository.recipeList.removeAll { it.id !in 1..5 }

                // Masukkan postingan terbaru dari database Supabase di paling atas
                RecipeRepository.recipeList.addAll(0, listUserPosts)

                if (::jelajahAdapter.isInitialized) {
                    jelajahAdapter.notifyDataSetChanged()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("SUPABASE_JELAJAH", "Gagal memuat postingan dari Supabase: ${e.message}")
            }
        }
    }
}
