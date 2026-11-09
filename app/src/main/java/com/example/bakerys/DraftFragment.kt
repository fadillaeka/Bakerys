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
    private lateinit var draftAdapter: DraftAdapter

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

        // Mengirimkan RecipeRepository.draftList ke DraftAdapter
        draftAdapter = DraftAdapter(
            draftList = RecipeRepository.draftList,
            onItemClick = { recipe: Recipe ->
                val fragmentUnggah = UnggahFragment().apply {
                    arguments = Bundle().apply {
                        putInt("DRAFT_ID", recipe.id)
                        putString("DRAFT_TITLE", recipe.title)
                        putString("DRAFT_IMAGE_URI", recipe.imageUri?.toString())
                        putInt("DRAFT_IMAGE_RES", recipe.imageResId ?: 0)
                    }
                }

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragmentUnggah)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        )

        rvDraft.adapter = draftAdapter
    }

    override fun onResume() {
        super.onResume()
        if (::draftAdapter.isInitialized) {
            draftAdapter.notifyDataSetChanged()
        }
    }
}