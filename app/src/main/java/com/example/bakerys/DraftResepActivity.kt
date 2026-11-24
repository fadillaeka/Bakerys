package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class DraftResepActivity : AppCompatActivity() {

    private lateinit var rvDraftResep: RecyclerView
    private lateinit var adapter: DraftResepAdapter
    private val draftList = mutableListOf<DraftResep>()
    private var pbLoading: ProgressBar? = null
    private var tvEmpty: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft_resep)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack?.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val btnRefresh = findViewById<ImageView>(R.id.btnRefreshDraft)
        btnRefresh?.setOnClickListener {
            muatDraft()
        }

        rvDraftResep = findViewById(R.id.rvDraftResep)
        rvDraftResep.layoutManager = LinearLayoutManager(this)
        pbLoading = findViewById(R.id.pbLoadingDraft)
        tvEmpty = findViewById(R.id.tvEmptyDraft)

        adapter = DraftResepAdapter(
            draftList = draftList,
            onItemClick = { draft ->
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("EXTRA_NAV_TARGET", "TARGET_UNGGAH")
                    putExtra("EXTRA_DRAFT_RESEP_ID", draft.id)
                    putExtra("EXTRA_DRAFT_RESEP_JUDUL", draft.judul ?: "")
                    putExtra("EXTRA_DRAFT_RESEP_DESKRIPSI", draft.deskripsi ?: "")
                    putExtra("EXTRA_DRAFT_RESEP_GAMBAR", draft.gambar ?: "")
                    putExtra("EXTRA_DRAFT_RESEP_WAKTU", draft.waktu_panggang ?: 0)
                    putExtra("EXTRA_DRAFT_RESEP_PORSI", draft.porsi ?: "")
                    putExtra("EXTRA_DRAFT_RESEP_BAHAN_UTAMA", draft.bahan_utama ?: "")
                    putExtra("EXTRA_DRAFT_RESEP_TAKARAN_GRAM", draft.takaran_gram ?: "")
                    putExtra("EXTRA_DRAFT_RESEP_TAKARAN_SENDOK", draft.takaran_sendok ?: "")
                    putExtra("EXTRA_DRAFT_RESEP_BAHAN_PENGGANTI", draft.bahan_pengganti ?: "")
                    putExtra("EXTRA_DRAFT_RESEP_LANGKAH", draft.langkah_memasak ?: "")
                    draft.kategori_id?.let { putExtra("EXTRA_DRAFT_RESEP_KATEGORI_ID", it) }
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                startActivity(intent)
                finish()
            },
            onItemDeleted = { draft ->
                lifecycleScope.launch {
                    try {
                        BakeryStorageManager.hapusDraftResep(draft.id, draft.judul)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                updateEmptyState()
            }
        )

        rvDraftResep.adapter = adapter
        muatDraft()
    }

    private fun muatDraft() {
        pbLoading?.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val list = BakeryStorageManager.muatDraftResepDariSupabase(this@DraftResepActivity)
                draftList.clear()
                draftList.addAll(list)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@DraftResepActivity, "Gagal memuat draft resep", Toast.LENGTH_SHORT).show()
            } finally {
                pbLoading?.visibility = View.GONE
                updateEmptyState()
            }
        }
    }

    private fun updateEmptyState() {
        if (draftList.isEmpty()) {
            tvEmpty?.visibility = View.VISIBLE
            rvDraftResep.visibility = View.GONE
        } else {
            tvEmpty?.visibility = View.GONE
            rvDraftResep.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
