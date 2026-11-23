package com.example.bakerys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

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
                // Gunakan MainActivity.bukaUnggahDenganData agar tab Unggah aktif dengan benar
                val mainActivity = activity as? MainActivity
                if (mainActivity != null) {
                    mainActivity.bukaUnggahDenganData(recipe)
                } else {
                    // Fallback: navigasi manual
                    val fragmentUnggah = UnggahFragment().apply {
                        arguments = Bundle().apply {
                            putInt("DRAFT_ID", recipe.id)
                            putString("DRAFT_TITLE", recipe.title)
                            putString("DRAFT_IMAGE_URI", recipe.imageUri?.toString())
                            putInt("DRAFT_IMAGE_RES", recipe.imageResId ?: 0)
                        }
                    }
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        ?.replace(R.id.fragment_container, fragmentUnggah)
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }
        )

        rvDraft.adapter = draftAdapter
        rvDraft.scheduleLayoutAnimation()
    }

    override fun onResume() {
        super.onResume()
        if (::draftAdapter.isInitialized) {
            draftAdapter.notifyDataSetChanged()
        }

        // Sinkronisasi data draft dari tabel draft_resep_admin Supabase
        // Gunakan try-catch agar jika gagal, tidak crash ke halaman lain
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val ctx = context ?: return@launch
                val draftsSupabase = BakeryStorageManager.muatDraftAdminDariSupabase(ctx)
                if (draftsSupabase.isNotEmpty()) {
                    var adaBaru = false
                    for (d in draftsSupabase) {
                        val title = d.judul ?: continue
                        if (RecipeRepository.draftList.none { it.title.equals(title, ignoreCase = true) }) {
                            val uri = if (!d.gambar.isNullOrEmpty()) android.net.Uri.parse(d.gambar) else null
                            val newDraft = Recipe(
                                id = (title.hashCode().let { if (it < 0) -it else it }),
                                title = title,
                                description = d.deskripsi ?: "",
                                imageUri = uri,
                                imageResId = if (uri == null) R.drawable.bakery_mark else null
                            )
                            RecipeRepository.draftList.add(0, newDraft)
                            adaBaru = true
                        }
                    }
                    if (adaBaru && ::draftAdapter.isInitialized) {
                        draftAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                // Jangan crash, cukup log error
                android.util.Log.e("DRAFT_FRAGMENT", "Gagal muat draft dari Supabase: ${e.message}")
            }
        }
    }
}
