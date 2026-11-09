package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@kotlinx.serialization.InternalSerializationApi
class TersimpanFragment : Fragment() {

    private var _binding: View? = null
    private lateinit var rvTersimpan: RecyclerView
    private lateinit var adapter: TersimpanAdapter

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

        adapter = TersimpanAdapter(
            bookmarkedList = BookmarkManager.bookmarkedList,
            onItemClick = { resep ->
                val intent = Intent(requireContext(), DetailResepActivity::class.java).apply {
                    putExtra("EXTRA_JUDUL", resep.title)
                    if (resep.imageResId != null) {
                        putExtra("EXTRA_GAMBAR", resep.imageResId)
                    }
                }
                startActivity(intent)
            },
            onRemoveBookmark = { resep, position ->
                if (position != RecyclerView.NO_POSITION && position < BookmarkManager.bookmarkedList.size) {
                    BookmarkManager.removeBookmark(resep)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, BookmarkManager.bookmarkedList.size - position)
                }
            }
        )

        rvTersimpan.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}