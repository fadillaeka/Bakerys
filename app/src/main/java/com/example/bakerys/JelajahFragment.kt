package com.example.bakerys

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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

class JelajahFragment : Fragment() {

    private lateinit var rvJelajah: RecyclerView
    private lateinit var jelajahAdapter: JelajahAdapter

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

        muatResepDariDatabase()
    }

    override fun onResume() {
        super.onResume()
        if (::jelajahAdapter.isInitialized) {
            jelajahAdapter.notifyDataSetChanged()
        }
        muatResepDariDatabase()
    }

    private fun muatResepDariDatabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val listData = SupabaseClient.instance.from("resep")
                    .select()
                    .decodeList<JsonObject>()

                var adaDataBaru = false
                for (item in listData) {
                    val nama = item["judul"]?.jsonPrimitive?.content
                        ?: item["nama_resep"]?.jsonPrimitive?.content
                        ?: continue
                    val deskripsi = item["deskripsi"]?.jsonPrimitive?.content ?: "Resep olahan lezat"
                    val id = item["id"]?.jsonPrimitive?.intOrNull ?: (nama.hashCode().let { if (it < 0) -it else it })

                    // Tambahkan ke feed Jelajah jika belum ada
                    if (RecipeRepository.recipeList.none { it.title.equals(nama, ignoreCase = true) }) {
                        val newRecipe = Recipe(
                            id = id,
                            title = nama,
                            description = deskripsi,
                            imageResId = R.drawable.cookies,
                            isLiked = false,
                            isBookmarked = false
                        )
                        RecipeRepository.tambahPostingan(newRecipe)
                        adaDataBaru = true
                    }
                }

                if (adaDataBaru && ::jelajahAdapter.isInitialized) {
                    jelajahAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("SUPABASE_JELAJAH", "Gagal memuat resep dari Supabase: ${e.message}")
            }
        }
    }
}
