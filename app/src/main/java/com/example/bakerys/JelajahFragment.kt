package com.example.bakerys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        // Refresh tampilan agar status ikon heart & bookmark selalu update jika kembali ke tab Jelajah
        if (::jelajahAdapter.isInitialized) {
            jelajahAdapter.notifyDataSetChanged()
        }
    }

    private fun setupRecyclerView() {
        jelajahAdapter = JelajahAdapter(
            recipeList = RecipeRepository.recipeList,
            onItemClick = { recipe ->
                Toast.makeText(requireContext(), "Membuka: ${recipe.title}", Toast.LENGTH_SHORT).show()
            },
            onBookmarkClick = { recipe ->
                // Update status bookmark di RecipeRepository utama
                val repoRecipe = RecipeRepository.recipeList.find { it.id == recipe.id }
                repoRecipe?.isBookmarked = recipe.isBookmarked

                val statusText = if (recipe.isBookmarked) "Tersimpan" else "Dihapus dari simpanan"
                Toast.makeText(requireContext(), "${recipe.title} $statusText", Toast.LENGTH_SHORT).show()
            }
        )

        rvJelajah.layoutManager = LinearLayoutManager(requireContext())
        rvJelajah.adapter = jelajahAdapter
    }
}