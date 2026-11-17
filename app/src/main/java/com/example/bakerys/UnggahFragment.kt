package com.example.bakerys

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class UnggahFragment : Fragment() {

    private var imageUri: Uri? = null
    private var draftIdEdit: Int? = null
    private var draftImageRes: Int? = null

    private lateinit var ivPreviewFoto: ImageView
    private lateinit var btnUploadFoto: MaterialCardView
    private lateinit var etCaption: EditText
    private lateinit var btnPublikasikan: Button
    private lateinit var btnSimpanDraft: Button

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            draftImageRes = null
            ivPreviewFoto.setImageURI(uri)
            ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER_CROP
            ivPreviewFoto.imageTintList = null
            Toast.makeText(requireContext(), "Foto berhasil dipilih!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_unggah, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnUploadFoto = view.findViewById(R.id.btnUploadFoto)
        ivPreviewFoto = view.findViewById(R.id.ivPreviewFoto)
        etCaption = view.findViewById(R.id.etCaption)
        btnPublikasikan = view.findViewById(R.id.btnPublikasikan)
        btnSimpanDraft = view.findViewById(R.id.btnSimpanDraft)

        btnUploadFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // --- BACA DATA DRAFT YANG DIKIRIM DARI DRAFTFRAGMENT ---
        arguments?.let { bundle ->
            if (bundle.containsKey("DRAFT_ID") || bundle.containsKey("DRAFT_TITLE")) {
                if (bundle.containsKey("DRAFT_ID")) {
                    draftIdEdit = bundle.getInt("DRAFT_ID")
                }
                val draftTitle = bundle.getString("DRAFT_TITLE", "")
                val draftUriString = bundle.getString("DRAFT_IMAGE_URI")
                val imgResBundle = bundle.getInt("DRAFT_IMAGE_RES", 0)
                if (imgResBundle != 0) {
                    draftImageRes = imgResBundle
                }

                // Set Caption
                if (!draftTitle.isNullOrEmpty()) {
                    etCaption.setText(draftTitle)
                }

                // Set Foto dari Draft
                if (!draftUriString.isNullOrEmpty()) {
                    imageUri = Uri.parse(draftUriString)
                    ivPreviewFoto.setImageURI(imageUri)
                    ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivPreviewFoto.imageTintList = null
                } else if (imgResBundle != 0) {
                    ivPreviewFoto.setImageResource(imgResBundle)
                    ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivPreviewFoto.imageTintList = null
                }
            }
        }

        // 1. Publikasikan ke Jelajah
        btnPublikasikan.setOnClickListener {
            // Validasi Foto: Tidak Boleh Kosong (Wajib Diisi)
            val punyaFoto = (imageUri != null) || (draftImageRes != null && draftImageRes != 0)
            if (!punyaFoto) {
                Toast.makeText(requireContext(), "⚠️ Foto postingan tidak boleh kosong! Wajib pilih foto terlebih dahulu.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val caption = etCaption.text.toString().trim()
            if (caption.isEmpty()) {
                etCaption.error = "Caption postingan tidak boleh kosong"
                return@setOnClickListener
            }

            val newId = draftIdEdit ?: (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

            val postinganBaru = Recipe(
                id = newId,
                title = caption,
                description = "",
                imageUri = imageUri,
                imageResId = if (imageUri == null) (draftImageRes ?: R.drawable.dubai) else null
            )

            // Tambahkan ke feed Jelajah
            RecipeRepository.tambahPostingan(postinganBaru)

            // Simpan postingan resep ke Supabase Database
            BakeryStorageManager.simpanResepKeSupabase(caption, "Resep kreasi baker terbit di Jelajah")

            // Hapus dari draft jika postingan ini berasal dari draft
            draftIdEdit?.let { id ->
                RecipeRepository.draftList.removeAll { it.id == id }
                BakeryStorageManager.simpanDaftarDraft(requireContext(), RecipeRepository.draftList)
                draftIdEdit = null
            }

            NotifikasiBottomSheet.tambahNotif("Postingan kamu berhasil dipublikasikan!")
            Toast.makeText(requireContext(), "Postingan terbit di Jelajah!", Toast.LENGTH_SHORT).show()
            resetForm()

            // PINDAH KE FRAGMENT BERANDA (JELAJAH)
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, BerandaFragment())
                ?.commit()
        }

        // 2. Simpan Sebagai Draft (Tetap di halaman Unggah)
        btnSimpanDraft.setOnClickListener {
            // Validasi Foto: Tidak Boleh Kosong untuk Draft juga
            val punyaFoto = (imageUri != null) || (draftImageRes != null && draftImageRes != 0)
            if (!punyaFoto) {
                Toast.makeText(requireContext(), "⚠️ Foto draft tidak boleh kosong! Wajib pilih foto terlebih dahulu.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val caption = etCaption.text.toString().trim()
            if (caption.isEmpty()) {
                etCaption.error = "Caption postingan tidak boleh kosong"
                return@setOnClickListener
            }

            val newId = draftIdEdit ?: (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

            val draftBaru = Recipe(
                id = newId,
                title = caption,
                description = "",
                imageUri = imageUri,
                imageResId = if (imageUri == null) (draftImageRes ?: R.drawable.dubai) else null
            )

            // Update jika draft lama, atau tambah jika baru
            draftIdEdit?.let { id ->
                RecipeRepository.draftList.removeAll { it.id == id }
            }
            RecipeRepository.tambahDraft(draftBaru)

            // Simpan draft secara persisten ke database
            BakeryStorageManager.simpanDaftarDraft(requireContext(), RecipeRepository.draftList)

            Toast.makeText(requireContext(), "Draft berhasil disimpan!", Toast.LENGTH_SHORT).show()
            resetForm()
        }
    }

    private fun resetForm() {
        etCaption.setText("")
        imageUri = null
        draftIdEdit = null
        draftImageRes = null
        arguments?.clear()
        ivPreviewFoto.setImageResource(android.R.drawable.ic_menu_camera)
        ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER
    }
}