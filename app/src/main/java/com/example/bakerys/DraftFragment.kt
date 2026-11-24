package com.example.bakerys

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class DraftFragment : Fragment() {

    private lateinit var rvDraft: RecyclerView
    private lateinit var draftAdapter: DraftAdapter
    private var pbLoading: ProgressBar? = null
    private var tvEmptyDraft: TextView? = null
    private var btnRefreshDraft: ImageView? = null

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
        pbLoading = view.findViewById(R.id.pbLoadingDraftPostingan)
        tvEmptyDraft = view.findViewById(R.id.tvEmptyDraftPostingan)
        btnRefreshDraft = view.findViewById(R.id.btnRefreshDraftPostingan)

        // Bersihkan list lama sebelum diisi ulang dari Supabase
        RecipeRepository.draftList.clear()

        draftAdapter = DraftAdapter(
            draftList = RecipeRepository.draftList,
            onItemClick = { recipe: Recipe ->
                bukaEditorDraftPostingan(recipe)
            }
        )

        rvDraft.adapter = draftAdapter

        btnRefreshDraft?.setOnClickListener {
            muatDraftPostinganDariSupabase()
        }

        muatDraftPostinganDariSupabase()
    }

    // onResume TIDAK memanggil muat ulang agar tidak double-fetch saat kembali dari editor
    override fun onResume() {
        super.onResume()
        if (::draftAdapter.isInitialized) {
            draftAdapter.notifyDataSetChanged()
        }
    }

    private fun bukaEditorDraftPostingan(recipe: Recipe) {
        val mainActivity = activity as? MainActivity
        if (mainActivity != null) {
            mainActivity.bukaUnggahDenganData(recipe)
        } else {
            val fragmentUnggah = UnggahFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("DRAFT_IS_POSTINGAN", true)
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

    private fun muatDraftPostinganDariSupabase() {
        pbLoading?.visibility = View.VISIBLE
        tvEmptyDraft?.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val ctx = context ?: return@launch
                val currentUserId = BakeryStorageManager.dapatkanUserId(ctx)

                // Jika sesi tidak ada, tampilkan empty state tanpa redirect
                if (currentUserId.isNullOrEmpty()) {
                    android.util.Log.w("DRAFT_POSTINGAN", "Sesi login tidak ditemukan, tampilkan empty state")
                    tvEmptyDraft?.visibility = View.VISIBLE
                    pbLoading?.visibility = View.GONE
                    return@launch
                }

                val listData = SupabaseClient.instance.from("draft_postingan_user")
                    .select {
                        filter { eq("user_id", currentUserId) }
                    }
                    .decodeList<JsonObject>()

                // Reset dan isi ulang draftList dari Supabase
                RecipeRepository.draftList.clear()

                for (item in listData) {
                    val rawId = item["id"]?.jsonPrimitive?.contentOrNull ?: continue
                    val id = rawId.hashCode().let { if (it < 0) -it else it }

                    val caption = item["caption"]?.jsonPrimitive?.contentOrNull ?: ""
                    val fotoUrl = item["gambar_url"]?.jsonPrimitive?.contentOrNull
                        ?: item["foto_url"]?.jsonPrimitive?.contentOrNull
                    val uri = if (!fotoUrl.isNullOrEmpty()) {
                        try { Uri.parse(fotoUrl) } catch (e: Exception) { null }
                    } else null

                    RecipeRepository.draftList.add(
                        Recipe(
                            id = id,
                            title = caption,
                            description = "",
                            imageUri = uri,
                            imageResId = if (uri == null) R.drawable.bakery_mark else null
                        )
                    )
                }

                if (::draftAdapter.isInitialized) {
                    draftAdapter.notifyDataSetChanged()
                }

                tvEmptyDraft?.visibility = if (RecipeRepository.draftList.isEmpty()) View.VISIBLE else View.GONE

            } catch (e: Exception) {
                android.util.Log.e("DRAFT_POSTINGAN", "Gagal muat draft postingan: ${e.message}", e)
                val ctx = context
                if (ctx != null && isAdded) {
                    Toast.makeText(ctx, "Gagal memuat draft postingan. Coba lagi.", Toast.LENGTH_SHORT).show()
                }
                tvEmptyDraft?.visibility = View.VISIBLE
            } finally {
                pbLoading?.visibility = View.GONE
            }
        }
    }
}
