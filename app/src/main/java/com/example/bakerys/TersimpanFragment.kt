package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
class TersimpanFragment : Fragment() {

    private lateinit var rvTersimpan: RecyclerView
    private lateinit var tersimpanAdapter: TersimpanAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tersimpan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTersimpan = view.findViewById(R.id.rvTersimpan)
        rvTersimpan.layoutManager = LinearLayoutManager(requireContext())

        tersimpanAdapter = TersimpanAdapter(
            bookmarkedList = RecipeRepository.resepTersimpanList,
            onItemClick = { recipe ->
                val intent = Intent(requireContext(), DetailResepActivity::class.java).apply {
                    putExtra("EXTRA_RECIPE_ID", recipe.id)
                }
                startActivity(intent)
            },
            onRemoveBookmark = { recipe, position ->
                recipe.isBookmarked = false
                RecipeRepository.resepTersimpanList.removeAll { it.id == recipe.id }
                BookmarkManager.removeBookmark(recipe)
                tersimpanAdapter.removeItemAt(position)
                Toast.makeText(requireContext(), "Resep dihapus dari simpanan!", Toast.LENGTH_SHORT).show()
            }
        )
        rvTersimpan.adapter = tersimpanAdapter
    }

    override fun onResume() {
        super.onResume()
        if (::tersimpanAdapter.isInitialized) {
            tersimpanAdapter.notifyDataSetChanged()
        }
    }
}