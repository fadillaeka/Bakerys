package com.example.bakerys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TersimpanFragment : Fragment() {

    private lateinit var rvTersimpan: RecyclerView
    private lateinit var adapter: TersimpanAdapter
    private var bookmarkedList: MutableList<Recipe> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_tersimpan, container, false)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        btnBack?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        rvTersimpan = view.findViewById(R.id.rvTersimpan)
        rvTersimpan.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshSavedData()
    }

    override fun onResume() {
        super.onResume()
        // Panggil refresh data setiap kali layar TersimpanFragment aktif/tampil kembali
        refreshSavedData()
    }

    private fun refreshSavedData() {
        // Ambil resep terbaru langsung dari RecipeRepository yang status isBookmarked = true
        bookmarkedList = RecipeRepository.recipeList.filter { it.isBookmarked }.toMutableList()

        adapter = TersimpanAdapter(
            bookmarkedList = bookmarkedList,
            onItemClick = { recipe ->
                // Aksi saat item resep diklik
            },
            onRemoveBookmark = { recipe, position ->
                if (position in 0 until bookmarkedList.size) {
                    // Update status di object asli repository
                    val repoRecipe = RecipeRepository.recipeList.find { it.id == recipe.id }
                    repoRecipe?.isBookmarked = false

                    bookmarkedList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, bookmarkedList.size)
                }
            }
        )

        rvTersimpan.adapter = adapter
    }
}