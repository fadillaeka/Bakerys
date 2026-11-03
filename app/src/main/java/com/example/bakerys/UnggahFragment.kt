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

        // --- BACA DATA DRAFT YANG DIKIRIM ---
        arguments?.let { bundle ->
            if (bundle.containsKey("DRAFT_ID")) {
                draftIdEdit = bundle.getInt("DRAFT_ID")
                val draftTitle = bundle.getString("DRAFT_TITLE", "")
                val draftUriString = bundle.getString("DRAFT_IMAGE_URI")
                val draftImageRes = bundle.getInt("DRAFT_IMAGE_RES", 0)

                // Set Caption
                etCaption.setText(draftTitle)

                // Set Foto dari Draft
                if (!draftUriString.isNullOrEmpty()) {
                    imageUri = Uri.parse(draftUriString)
                    ivPreviewFoto.setImageURI(imageUri)
                    ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivPreviewFoto.imageTintList = null
                } else if (draftImageRes != 0) {
                    ivPreviewFoto.setImageResource(draftImageRes)
                    ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivPreviewFoto.imageTintList = null
                }
            }
        }

        // 1. Publikasikan ke Jelajah
        btnPublikasikan.setOnClickListener {
            val caption = etCaption.text.toString().trim()

            if (caption.isEmpty()) {
                etCaption.error = "Caption postingan tidak boleh kosong"
                return@setOnClickListener
            }

            val newId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

            val postinganBaru = Recipe(
                id = newId,
                title = caption,
                description = "",
                imageUri = imageUri,
                imageResId = if (imageUri == null) R.drawable.dubai else null
            )

            // Tambahkan ke feed Jelajah
            RecipeRepository.tambahPostingan(postinganBaru)

            // Hapus dari draft jika postingan ini berasal dari draft
            draftIdEdit?.let { id ->
                RecipeRepository.draftList.removeAll { it.id == id }
                draftIdEdit = null
            }

            NotifikasiBottomSheet.tambahNotif("Postingan kamu berhasil dipublikasikan!")
            Toast.makeText(requireContext(), "Postingan terbit di Jelajah!", Toast.LENGTH_SHORT).show()
            resetForm()
        }

        // 2. Simpan Sebagai Draft
        btnSimpanDraft.setOnClickListener {
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
                imageResId = if (imageUri == null) R.drawable.dubai else null
            )

            // Update jika draft lama, atau tambah jika baru
            draftIdEdit?.let { id ->
                RecipeRepository.draftList.removeAll { it.id == id }
            }
            RecipeRepository.tambahDraft(draftBaru)

            Toast.makeText(requireContext(), "Draft berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            resetForm()
        }
    }

    private fun resetForm() {
        etCaption.setText("")
        imageUri = null
        draftIdEdit = null
        arguments?.clear()
        ivPreviewFoto.setImageResource(android.R.drawable.ic_menu_camera)
        ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER
    }
}