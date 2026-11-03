package com.example.bakerys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DraftFragment : Fragment() {

    private lateinit var rvDraft: RecyclerView
    private lateinit var draftAdapter: JelajahAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_draft, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvDraft = view.findViewById(R.id.rvDraft)
        rvDraft.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter dengan aksi klik item untuk pindah ke UnggahFragment
        draftAdapter = JelajahAdapter(
            recipeList = RecipeRepository.draftList,
            onItemClick = { recipe ->
                // Panggil fungsi di MainActivity untuk pindah ke UnggahFragment membawa data draft
                (activity as? MainActivity)?.bukaUnggahDenganData(recipe)
            },
            onBookmarkClick = { }
        )
        rvDraft.adapter = draftAdapter
    }

    override fun onResume() {
        super.onResume()
        // Refresh otomatis saat tab Draft dibuka
        if (::draftAdapter.isInitialized) {
            draftAdapter.notifyDataSetChanged()
        }
    }
}