package com.example.bakerys

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UnggahFragment : Fragment() {

    // --- State Switcher ---
    private var isTabResep = true

    // --- State Unggah Postingan (Sudah Ada) ---
    private var imageUri: Uri? = null
    private var draftIdEdit: Int? = null
    private var draftImageRes: Int? = null

    private lateinit var ivPreviewFoto: ImageView
    private lateinit var btnUploadFoto: MaterialCardView
    private lateinit var etCaption: EditText
    private lateinit var btnPublikasikan: Button
    private lateinit var btnSimpanDraft: Button

    // --- State Unggah Resep (Baru) ---
    private var resepImageUri: Uri? = null
    private var listKategori = listOf<Kategori>()
    private var draftResepIdEdit: String? = null
    private var draftResepJudulAsal: String? = null
    private var pendingDraftKategoriId: Long? = null
    private var postCompressJob: Job? = null
    private var resepCompressJob: Job? = null

    private lateinit var ivPreviewFotoResep: ImageView
    private lateinit var btnUploadFotoResep: MaterialCardView
    private lateinit var etJudulResep: EditText
    private lateinit var spinnerKategoriResep: Spinner
    private lateinit var etWaktuPanggang: EditText
    private lateinit var etPorsiResep: EditText
    private lateinit var etDeskripsiResep: EditText
    private lateinit var etBahanUtama: EditText
    private lateinit var etTakaranGram: EditText
    private lateinit var etTakaranSendok: EditText
    private lateinit var etBahanPengganti: EditText
    private lateinit var etLangkahMemasak: EditText
    private lateinit var btnSimpanDraftResep: Button
    private lateinit var btnPublikasikanResep: Button

    // --- Kontainer & Header ---
    private lateinit var layoutUnggahResep: LinearLayout
    private lateinit var layoutUnggahPostingan: LinearLayout
    private lateinit var btnTabUnggahResep: MaterialButton
    private lateinit var btnTabUnggahPostingan: MaterialButton
    private lateinit var tvJudulHalamanUnggah: TextView

    // Launcher Foto Postingan (kompresi di Dispatchers.IO agar UI tidak macet)
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val ctx = context ?: return@registerForActivityResult
            imageUri = uri
            draftImageRes = null
            ivPreviewFoto.setImageURI(uri)
            ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER_CROP
            ivPreviewFoto.imageTintList = null
            postCompressJob?.cancel()
            postCompressJob = viewLifecycleOwner.lifecycleScope.launch {
                val compressed = BakeryStorageManager.kompresDanSimpanGambar(
                    ctx, uri, "post_${System.currentTimeMillis()}.jpg"
                )
                if (compressed != null) {
                    imageUri = compressed
                    if (isAdded) ivPreviewFoto.setImageURI(compressed)
                }
                if (isAdded) Toast.makeText(ctx, "Foto berhasil dipilih!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher Foto Resep (kompresi di Dispatchers.IO agar UI tidak macet)
    private val pickResepImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val ctx = context ?: return@registerForActivityResult
            resepImageUri = uri
            ivPreviewFotoResep.setImageURI(uri)
            ivPreviewFotoResep.scaleType = ImageView.ScaleType.CENTER_CROP
            ivPreviewFotoResep.imageTintList = null
            resepCompressJob?.cancel()
            resepCompressJob = viewLifecycleOwner.lifecycleScope.launch {
                val compressed = BakeryStorageManager.kompresDanSimpanGambar(
                    ctx, uri, "resep_${System.currentTimeMillis()}.jpg"
                )
                if (compressed != null) {
                    resepImageUri = compressed
                    if (isAdded) ivPreviewFotoResep.setImageURI(compressed)
                }
                if (isAdded) Toast.makeText(ctx, "Foto resep berhasil dipilih!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri, prefix: String): Uri? {
        return try {
            val context = context ?: return null
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "${prefix}_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(context.filesDir, fileName)
            val outputStream = java.io.FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            null
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

        initViews(view)
        setupSwitcherListeners()
        setupPostinganListeners()
        setupResepListeners()

        bacaDraftArgument()
        muatKategoriResep()
    }

    private fun initViews(view: View) {
        // Switcher & Kontainer
        layoutUnggahResep = view.findViewById(R.id.layoutUnggahResep)
        layoutUnggahPostingan = view.findViewById(R.id.layoutUnggahPostingan)
        btnTabUnggahResep = view.findViewById(R.id.btnTabUnggahResep)
        btnTabUnggahPostingan = view.findViewById(R.id.btnTabUnggahPostingan)
        tvJudulHalamanUnggah = view.findViewById(R.id.tvJudulHalamanUnggah)

        // Komponen Form Postingan
        btnUploadFoto = view.findViewById(R.id.btnUploadFoto)
        ivPreviewFoto = view.findViewById(R.id.ivPreviewFoto)
        etCaption = view.findViewById(R.id.etCaption)
        btnPublikasikan = view.findViewById(R.id.btnPublikasikan)
        btnSimpanDraft = view.findViewById(R.id.btnSimpanDraft)

        // Komponen Form Resep
        btnUploadFotoResep = view.findViewById(R.id.btnUploadFotoResep)
        ivPreviewFotoResep = view.findViewById(R.id.ivPreviewFotoResep)
        etJudulResep = view.findViewById(R.id.etJudulResep)
        spinnerKategoriResep = view.findViewById(R.id.spinnerKategoriResep)
        etWaktuPanggang = view.findViewById(R.id.etWaktuPanggang)
        etPorsiResep = view.findViewById(R.id.etPorsiResep)
        etDeskripsiResep = view.findViewById(R.id.etDeskripsiResep)
        etBahanUtama = view.findViewById(R.id.etBahanUtama)
        etTakaranGram = view.findViewById(R.id.etTakaranGram)
        etTakaranSendok = view.findViewById(R.id.etTakaranSendok)
        etBahanPengganti = view.findViewById(R.id.etBahanPengganti)
        etLangkahMemasak = view.findViewById(R.id.etLangkahMemasak)
        btnSimpanDraftResep = view.findViewById(R.id.btnSimpanDraftResep)
        btnPublikasikanResep = view.findViewById(R.id.btnPublikasikanResep)
    }

    private fun setupSwitcherListeners() {
        btnTabUnggahResep.setOnClickListener {
            if (!isTabResep) {
                isTabResep = true
                updateSwitcherView()
            }
        }

        btnTabUnggahPostingan.setOnClickListener {
            if (isTabResep) {
                isTabResep = false
                updateSwitcherView()
            }
        }
    }

    private fun updateSwitcherView() {
        val maroonColor = Color.parseColor("#802C3E")
        val creamColor = Color.parseColor("#FCEAE6")

        if (isTabResep) {
            // Tab Unggah Resep Aktif (Merah Marun + Teks Putih Tebal)
            btnTabUnggahResep.backgroundTintList = ColorStateList.valueOf(maroonColor)
            btnTabUnggahResep.setTextColor(Color.WHITE)
            btnTabUnggahResep.setTypeface(null, android.graphics.Typeface.BOLD)

            // Tab Unggah Postingan Tidak Aktif (Krem Muda + Teks Merah Marun)
            btnTabUnggahPostingan.backgroundTintList = ColorStateList.valueOf(creamColor)
            btnTabUnggahPostingan.setTextColor(maroonColor)
            btnTabUnggahPostingan.setTypeface(null, android.graphics.Typeface.NORMAL)

            layoutUnggahResep.visibility = View.VISIBLE
            layoutUnggahPostingan.visibility = View.GONE
            tvJudulHalamanUnggah.text = "Unggah Resep"
        } else {
            // Tab Unggah Postingan Aktif (Merah Marun + Teks Putih Tebal)
            btnTabUnggahPostingan.backgroundTintList = ColorStateList.valueOf(maroonColor)
            btnTabUnggahPostingan.setTextColor(Color.WHITE)
            btnTabUnggahPostingan.setTypeface(null, android.graphics.Typeface.BOLD)

            // Tab Unggah Resep Tidak Aktif (Krem Muda + Teks Merah Marun)
            btnTabUnggahResep.backgroundTintList = ColorStateList.valueOf(creamColor)
            btnTabUnggahResep.setTextColor(maroonColor)
            btnTabUnggahResep.setTypeface(null, android.graphics.Typeface.NORMAL)

            layoutUnggahPostingan.visibility = View.VISIBLE
            layoutUnggahResep.visibility = View.GONE
            tvJudulHalamanUnggah.text = "Unggah Postingan"
        }
    }

    // =========================================================================
    // ALUR FORM RESEP BARU
    // =========================================================================
    private fun setupResepListeners() {
        btnUploadFotoResep.setOnClickListener {
            pickResepImageLauncher.launch("image/*")
        }

        btnPublikasikanResep.setOnClickListener {
            prosesPublikasiResep()
        }

        btnSimpanDraftResep.setOnClickListener {
            prosesSimpanDraftResep()
        }
    }

    private fun muatKategoriResep() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                listKategori = BakeryStorageManager.ambilDaftarKategori()
                val namaList = mutableListOf("Pilih Kategori (Default)")
                namaList.addAll(listKategori.map { it.nama_kategori.trim() })

                val ctx = context ?: return@launch
                val adapter = ArrayAdapter(
                    ctx,
                    android.R.layout.simple_spinner_dropdown_item,
                    namaList
                )
                spinnerKategoriResep.adapter = adapter

                // Sinkronkan pilihan kategori jika dibuka dari Draft Resep
                pendingDraftKategoriId?.let { targetId ->
                    val idx = listKategori.indexOfFirst { it.id == targetId }
                    if (idx >= 0) {
                        spinnerKategoriResep.setSelection(idx + 1)
                    }
                    pendingDraftKategoriId = null
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("UNGGAH_KATEGORI", "Gagal memuat kategori: ${e.message}")
            }
        }
    }

    private fun getSelectedKategoriId(): Long? {
        val selectedPos = spinnerKategoriResep.selectedItemPosition
        return if (selectedPos > 0 && selectedPos - 1 < listKategori.size) {
            listKategori[selectedPos - 1].id
        } else {
            null
        }
    }

    private fun prosesPublikasiResep() {
        if (!btnPublikasikanResep.isEnabled) return
        val judul = etJudulResep.text.toString().trim()
        if (judul.isEmpty()) {
            etJudulResep.error = "Judul resep wajib diisi!"
            etJudulResep.requestFocus()
            return
        }

        val bahanUtama = etBahanUtama.text.toString().trim()
        if (bahanUtama.isEmpty()) {
            etBahanUtama.error = "Bahan utama resep wajib diisi!"
            etBahanUtama.requestFocus()
            return
        }

        val langkahMemasak = etLangkahMemasak.text.toString().trim()
        if (langkahMemasak.isEmpty()) {
            etLangkahMemasak.error = "Langkah memasak wajib diisi!"
            etLangkahMemasak.requestFocus()
            return
        }

        val deskripsi = etDeskripsiResep.text.toString().trim()
        val waktuPanggang = etWaktuPanggang.text.toString().trim().toIntOrNull()
        val porsi = etPorsiResep.text.toString().trim()
        val takaranSendok = etTakaranSendok.text.toString().trim()
        val takaranGram = etTakaranGram.text.toString().trim()
        val bahanPengganti = etBahanPengganti.text.toString().trim()

        val currentUserId = BakeryStorageManager.dapatkanUserId(requireContext())

        val resepBaru = ResepSupabase(
            user_id = currentUserId,
            kategori_id = getSelectedKategoriId(),
            judul = judul,
            deskripsi = deskripsi.ifEmpty { "Resep olahan lezat kreasi baker." },
            gambar = resepImageUri?.toString(),
            waktu_panggang = waktuPanggang,
            porsi = porsi.ifEmpty { null },
            status = "published",
            bahan_utama = bahanUtama.ifEmpty { null },
            takaran_gram = takaranGram.ifEmpty { null },
            takaran_sendok = takaranSendok.ifEmpty { null },
            bahan_pengganti = bahanPengganti.ifEmpty { null },
            langkah_memasak = langkahMemasak.ifEmpty { null }
        )

        // Parse list bahan_resep
        val listBahan = mutableListOf<BahanResep>()
        val barisBahan = bahanUtama.lines().filter { it.isNotBlank() }
        val barisSendok = takaranSendok.lines().filter { it.isNotBlank() }
        val barisGram = takaranGram.lines().filter { it.isNotBlank() }

        for (i in barisBahan.indices) {
            val nama = barisBahan[i].trim()
            val sdm = barisSendok.getOrNull(i)?.trim()
            val gr = barisGram.getOrNull(i)?.trim()
            listBahan.add(
                BahanResep(
                    resep_id = 0L,
                    nama_bahan = nama,
                    takaran_sendok = sdm,
                    takaran_gram = gr,
                    tipe_bahan = "utama"
                )
            )
        }

        if (bahanPengganti.isNotBlank()) {
            for (line in bahanPengganti.lines().filter { it.isNotBlank() }) {
                listBahan.add(
                    BahanResep(
                        resep_id = 0L,
                        nama_bahan = line.trim(),
                        tipe_bahan = "pengganti"
                    )
                )
            }
        }

        // Parse list langkah_resep
        val listLangkah = mutableListOf<LangkahResep>()
        val barisLangkah = langkahMemasak.lines().filter { it.isNotBlank() }
        for ((idx, line) in barisLangkah.withIndex()) {
            listLangkah.add(
                LangkahResep(
                    resep_id = 0L,
                    nomor_langkah = idx + 1,
                    instruksi = line.trim()
                )
            )
        }

        btnPublikasikanResep.isEnabled = false
        btnSimpanDraftResep.isEnabled = false
        btnPublikasikanResep.text = "Mempublikasikan..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Pastikan kompresi gambar selesai sebelum publikasi
                resepCompressJob?.join()
                val finalResep = resepBaru.copy(gambar = resepImageUri?.toString())
                val newId = BakeryStorageManager.publikasikanResep(finalResep, listBahan, listLangkah)
                if (newId != null && newId > 0) {
                    // Optimistic UI: langsung tambahkan ke cache Beranda di urutan paling atas
                    val deskripsiCard = if (waktuPanggang != null && waktuPanggang > 0) {
                        val porsiStr = if (porsi.isNotEmpty()) " • $porsi" else ""
                        "⏱️ ${waktuPanggang}m$porsiStr • ${finalResep.deskripsi ?: ""}"
                    } else {
                        finalResep.deskripsi ?: "Resep olahan lezat kreasi baker."
                    }
                    RecipeRepository.tambahResepBerandaKeCache(
                        ResepModel(
                            id = newId,
                            judul = judul,
                            deskripsi = deskripsiCard,
                            gambar = if (resepImageUri == null) dapatkanGambarResepSesuai(judul) else null,
                            imageUri = resepImageUri,
                            fotoUrl = resepImageUri?.toString()
                        )
                    )

                    // Hapus draft resep dari database Supabase jika berasal dari draft atau ada draft yang sama
                    try {
                        BakeryStorageManager.hapusDraftResep(draftResepIdEdit, draftResepJudulAsal ?: judul)
                        if (draftResepJudulAsal != null && !draftResepJudulAsal.equals(judul, ignoreCase = true)) {
                            BakeryStorageManager.hapusDraftResep(null, judul)
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e("UNGGAH_RESEP", "Gagal hapus draft resep setelah publikasi: ${e.message}")
                    }

                    NotifikasiBottomSheet.tambahNotif("Resep '${judul}' berhasil dipublikasikan ke Beranda!")
                    Toast.makeText(requireContext(), "Resep berhasil dipublikasikan!", Toast.LENGTH_SHORT).show()
                    resetFormResep()

                    // Navigasi ke Beranda
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, BerandaFragment())
                        ?.commit()
                } else {
                    Toast.makeText(requireContext(), "Gagal mempublikasikan resep ke database.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal mempublikasikan resep: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                if (isAdded) {
                    btnPublikasikanResep.isEnabled = true
                    btnSimpanDraftResep.isEnabled = true
                    btnPublikasikanResep.text = "Publikasikan Resep"
                }
            }
        }
    }

    private fun prosesSimpanDraftResep() {
        if (!btnSimpanDraftResep.isEnabled) return
        val judul = etJudulResep.text.toString().trim()
        if (judul.isEmpty()) {
            etJudulResep.error = "Judul resep wajib diisi untuk menyimpan draf!"
            etJudulResep.requestFocus()
            return
        }

        val deskripsi = etDeskripsiResep.text.toString().trim()
        val waktuPanggang = etWaktuPanggang.text.toString().trim().toIntOrNull()
        val porsi = etPorsiResep.text.toString().trim()
        val bahanUtama = etBahanUtama.text.toString().trim()
        val takaranSendok = etTakaranSendok.text.toString().trim()
        val takaranGram = etTakaranGram.text.toString().trim()
        val bahanPengganti = etBahanPengganti.text.toString().trim()
        val langkahMemasak = etLangkahMemasak.text.toString().trim()
        val currentUserId = BakeryStorageManager.dapatkanUserId(requireContext())

        val draft = DraftResep(
            user_id = currentUserId,
            kategori_id = getSelectedKategoriId(),
            judul = judul,
            deskripsi = deskripsi.ifEmpty { "Draft resep olahan kreasi baker." },
            gambar = resepImageUri?.toString(),
            waktu_panggang = waktuPanggang,
            porsi = porsi.ifEmpty { null },
            status = "draft",
            bahan_utama = bahanUtama.ifEmpty { null },
            takaran_gram = takaranGram.ifEmpty { null },
            takaran_sendok = takaranSendok.ifEmpty { null },
            bahan_pengganti = bahanPengganti.ifEmpty { null },
            langkah_memasak = langkahMemasak.ifEmpty { null }
        )

        btnSimpanDraftResep.isEnabled = false
        btnPublikasikanResep.isEnabled = false
        btnSimpanDraftResep.text = "Menyimpan..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                resepCompressJob?.join()
                val finalDraft = draft.copy(gambar = resepImageUri?.toString())
                val berhasil = BakeryStorageManager.simpanDraftResep(finalDraft)
                if (berhasil) {
                    Toast.makeText(requireContext(), "Draft resep berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    resetFormResep()
                } else {
                    Toast.makeText(requireContext(), "Gagal menyimpan draft resep.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal menyimpan draft: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                if (isAdded) {
                    btnSimpanDraftResep.isEnabled = true
                    btnPublikasikanResep.isEnabled = true
                    btnSimpanDraftResep.text = "Simpan Draft"
                }
            }
        }
    }

    private fun resetFormResep() {
        draftResepIdEdit = null
        draftResepJudulAsal = null
        pendingDraftKategoriId = null
        etJudulResep.setText("")
        etWaktuPanggang.setText("")
        etPorsiResep.setText("")
        etDeskripsiResep.setText("")
        etBahanUtama.setText("")
        etTakaranGram.setText("")
        etTakaranSendok.setText("")
        etBahanPengganti.setText("")
        etLangkahMemasak.setText("")
        resepImageUri = null
        ivPreviewFotoResep.setImageResource(android.R.drawable.ic_menu_camera)
        ivPreviewFotoResep.scaleType = ImageView.ScaleType.CENTER
        spinnerKategoriResep.setSelection(0)
    }

    // =========================================================================
    // ALUR FORM POSTINGAN ASLI (YANG SUDAH ADA DI PROYEK)
    // =========================================================================
    private fun setupPostinganListeners() {
        btnUploadFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 1. Publikasikan ke Jelajah
        btnPublikasikan.setOnClickListener {
            if (!btnPublikasikan.isEnabled) return@setOnClickListener

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

            val currentCtx = requireContext()
            btnPublikasikan.isEnabled = false
            btnSimpanDraft.isEnabled = false
            btnPublikasikan.text = "Mempublikasikan..."

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // Tunggu kompresi background jika masih berjalan
                    postCompressJob?.join()

                    // Pastikan URI gambar persisten & terkompresi di Dispatchers.IO
                    var gambarFinal = imageUri?.toString() ?: ""
                    val currentUri = imageUri
                    if (currentUri != null && gambarFinal.startsWith("content://")) {
                        val compressed = BakeryStorageManager.kompresDanSimpanGambar(
                            currentCtx, currentUri, "post_${System.currentTimeMillis()}.jpg"
                        )
                        if (compressed != null) {
                            imageUri = compressed
                            gambarFinal = compressed.toString()
                        }
                    }

                    BakeryStorageManager.simpanPostinganKeSupabase(
                        caption = caption,
                        gambarUrl = gambarFinal,
                        context = currentCtx
                    ) { berhasil, errorMsg ->
                        activity?.runOnUiThread {
                            if (isAdded) {
                                btnPublikasikan.isEnabled = true
                                btnSimpanDraft.isEnabled = true
                                btnPublikasikan.text = "Publikasikan"
                            }
                            if (berhasil) {
                                // Jika postingan berasal dari draft, hapus draft dari lokal & Supabase
                                draftIdEdit?.let { id ->
                                    RecipeRepository.draftList.removeAll { it.id == id }
                                    BakeryStorageManager.simpanDaftarDraft(currentCtx, RecipeRepository.draftList)
                                    BakeryStorageManager.hapusDraftDariSupabase(caption, currentCtx)
                                    draftIdEdit = null
                                }

                                // Optimistic UI: langsung tambahkan postingan baru ke urutan paling atas Jelajah
                                val postBaru = Recipe(
                                    id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                                    title = caption,
                                    description = "",
                                    imageUri = imageUri,
                                    imageResId = if (imageUri == null) (draftImageRes ?: R.drawable.cookies) else null
                                )
                                RecipeRepository.recipeList.add(0, postBaru)

                                NotifikasiBottomSheet.tambahNotif("Postingan kamu berhasil dipublikasikan!")
                                Toast.makeText(currentCtx, "Postingan berhasil terbit di Jelajah!", Toast.LENGTH_SHORT).show()
                                resetFormPostingan()

                                // Arahkan ke tab Jelajah agar pengguna langsung melihat postingannya
                                val bottomNav = activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_nav_main)
                                if (bottomNav != null) {
                                    bottomNav.selectedItemId = R.id.nav_jelajah
                                } else {
                                    activity?.supportFragmentManager?.beginTransaction()
                                        ?.replace(R.id.fragment_container, JelajahFragment())
                                        ?.commit()
                                }
                            } else {
                                val pesan = errorMsg ?: "Kesalahan koneksi database Supabase"
                                Toast.makeText(currentCtx, "Gagal mengunggah postingan: $pesan", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    if (isAdded) {
                        btnPublikasikan.isEnabled = true
                        btnSimpanDraft.isEnabled = true
                        btnPublikasikan.text = "Publikasikan"
                        Toast.makeText(currentCtx, "Gagal mengunggah postingan: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // 2. Simpan Sebagai Draft Postingan
        btnSimpanDraft.setOnClickListener {
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

            draftIdEdit?.let { id ->
                RecipeRepository.draftList.removeAll { it.id == id }
            }
            RecipeRepository.tambahDraft(draftBaru)

            BakeryStorageManager.simpanDraftKeSupabase(caption, imageUri?.toString() ?: "", requireContext())
            BakeryStorageManager.simpanDaftarDraft(requireContext(), RecipeRepository.draftList)

            Toast.makeText(requireContext(), "Draft berhasil disimpan!", Toast.LENGTH_SHORT).show()
            resetFormPostingan()
        }
    }

    private fun bacaDraftArgument() {
        val bundle = arguments ?: return

        when {
            // =====================================================================
            // DRAFT RESEP — dari DraftResepActivity via Intent extras
            // =====================================================================
            bundle.containsKey("EXTRA_DRAFT_RESEP_JUDUL") || bundle.containsKey("EXTRA_DRAFT_RESEP_ID") -> {
                isTabResep = true
                updateSwitcherView()

                draftResepIdEdit = bundle.getString("EXTRA_DRAFT_RESEP_ID")
                draftResepJudulAsal = bundle.getString("EXTRA_DRAFT_RESEP_JUDUL")

                etJudulResep.setText(bundle.getString("EXTRA_DRAFT_RESEP_JUDUL", ""))
                etDeskripsiResep.setText(bundle.getString("EXTRA_DRAFT_RESEP_DESKRIPSI", ""))
                etWaktuPanggang.setText(
                    bundle.getInt("EXTRA_DRAFT_RESEP_WAKTU", 0).let { if (it > 0) it.toString() else "" }
                )
                etPorsiResep.setText(bundle.getString("EXTRA_DRAFT_RESEP_PORSI", ""))
                etBahanUtama.setText(bundle.getString("EXTRA_DRAFT_RESEP_BAHAN_UTAMA", ""))
                etTakaranGram.setText(bundle.getString("EXTRA_DRAFT_RESEP_TAKARAN_GRAM", ""))
                etTakaranSendok.setText(bundle.getString("EXTRA_DRAFT_RESEP_TAKARAN_SENDOK", ""))
                etBahanPengganti.setText(bundle.getString("EXTRA_DRAFT_RESEP_BAHAN_PENGGANTI", ""))
                etLangkahMemasak.setText(bundle.getString("EXTRA_DRAFT_RESEP_LANGKAH", ""))

                val gambarStr = bundle.getString("EXTRA_DRAFT_RESEP_GAMBAR")
                if (!gambarStr.isNullOrEmpty()) {
                    try {
                        resepImageUri = Uri.parse(gambarStr)
                        ivPreviewFotoResep.setImageURI(resepImageUri)
                        ivPreviewFotoResep.scaleType = ImageView.ScaleType.CENTER_CROP
                        ivPreviewFotoResep.imageTintList = null
                    } catch (e: Exception) {
                        resepImageUri = null
                    }
                }

                // Set spinner kategori setelah daftar kategori dimuat (atau langsung jika sudah dimuat)
                val kategoriId = bundle.getLong("EXTRA_DRAFT_RESEP_KATEGORI_ID", -1L)
                if (kategoriId > 0) {
                    pendingDraftKategoriId = kategoriId
                    if (listKategori.isNotEmpty()) {
                        val idx = listKategori.indexOfFirst { it.id == kategoriId }
                        if (idx >= 0) {
                            spinnerKategoriResep.setSelection(idx + 1)
                            pendingDraftKategoriId = null
                        }
                    }
                }

                // Bersihkan argumen agar tidak terpanggil ulang secara tidak sengaja
                arguments?.remove("EXTRA_DRAFT_RESEP_ID")
                arguments?.remove("EXTRA_DRAFT_RESEP_JUDUL")
                arguments?.remove("EXTRA_DRAFT_RESEP_KATEGORI_ID")
            }

            // =====================================================================
            // DRAFT POSTINGAN — dari DraftFragment via bukaUnggahDenganData()
            // Dikenali via marker DRAFT_IS_POSTINGAN atau kunci DRAFT_ID / DRAFT_TITLE
            // =====================================================================
            bundle.getBoolean("DRAFT_IS_POSTINGAN", false)
                    || bundle.containsKey("DRAFT_ID")
                    || bundle.containsKey("DRAFT_TITLE") -> {

                isTabResep = false
                updateSwitcherView()

                draftIdEdit = if (bundle.containsKey("DRAFT_ID")) bundle.getInt("DRAFT_ID") else null

                val draftTitle = bundle.getString("DRAFT_TITLE", "")
                val draftUriString = bundle.getString("DRAFT_IMAGE_URI")
                val imgResBundle = bundle.getInt("DRAFT_IMAGE_RES", 0)
                if (imgResBundle != 0) draftImageRes = imgResBundle

                if (!draftTitle.isNullOrEmpty()) etCaption.setText(draftTitle)

                if (!draftUriString.isNullOrEmpty()) {
                    try {
                        imageUri = Uri.parse(draftUriString)
                        ivPreviewFoto.setImageURI(imageUri)
                        ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER_CROP
                        ivPreviewFoto.imageTintList = null
                    } catch (e: Exception) {
                        imageUri = null
                    }
                } else if (imgResBundle != 0) {
                    ivPreviewFoto.setImageResource(imgResBundle)
                    ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER_CROP
                    ivPreviewFoto.imageTintList = null
                }
            }
        }
    }

    private fun resetFormPostingan() {
        etCaption.setText("")
        imageUri = null
        draftIdEdit = null
        draftImageRes = null
        arguments?.clear()
        ivPreviewFoto.setImageResource(android.R.drawable.ic_menu_camera)
        ivPreviewFoto.scaleType = ImageView.ScaleType.CENTER
    }
}
