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
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
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
                    .select {
                        order("id", Order.DESCENDING)
                    }
                    .decodeList<JsonObject>()

                val ctx = context ?: return@launch

                val listUserPosts = mutableListOf<Recipe>()
                val seenIds = mutableSetOf<Int>()

                for (item in listData) {
                    val id = item["id"]?.jsonPrimitive?.content?.toIntOrNull()
                        ?: item["id"]?.jsonPrimitive?.intOrNull
                        ?: continue

                    if (seenIds.contains(id)) continue
                    seenIds.add(id)

                    val caption = item["caption"]?.jsonPrimitive?.content ?: continue
                    val fotoUrl = item["foto_url"]?.jsonPrimitive?.content
                        ?: item["gambar_url"]?.jsonPrimitive?.content
                    val uri = if (!fotoUrl.isNullOrEmpty()) {
                        try { Uri.parse(fotoUrl) } catch (e: Exception) { null }
                    } else null

                    val likeCount = BakeryStorageManager.ambilJumlahSuka(id.toLong())
                    val isLiked = BakeryStorageManager.cekUserMenyukaiPostingan(ctx, id.toLong())

                    val postRecipe = Recipe(
                        id = id,
                        title = caption,
                        description = "",
                        imageUri = uri,
                        imageResId = if (uri == null) R.drawable.cookies else null,
                        isLiked = isLiked,
                        isBookmarked = false,
                        likeCount = likeCount
                    )
                    listUserPosts.add(postRecipe)
                }

                // Bersihkan postingan user lama dari memory list, hanya pertahankan 5 resep bawaan (ID 1..5)
                RecipeRepository.recipeList.removeAll { it.id !in 1..5 }

                // Masukkan postingan terbaru dari database Supabase di paling atas
                RecipeRepository.recipeList.addAll(0, listUserPosts)

                if (::jelajahAdapter.isInitialized) {
                    jelajahAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_JELAJAH", "Gagal memuat postingan dari Supabase: ${e.message}")
            }
        }
    }
}
