@file:OptIn(
    io.github.jan.supabase.annotations.SupabaseExperimental::class,
    kotlinx.serialization.InternalSerializationApi::class
)
package com.example.bakerys

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import android.util.Log

class BerandaFragment : Fragment() {

    private lateinit var rvTren: RecyclerView
    private lateinit var adapterResep: ResepAdapter
    private lateinit var etSearch: EditText
    private lateinit var cardBakeOfTheDay: MaterialCardView
    private var floatingAnimator: ObjectAnimator? = null
    private var shimmerAnimator: ObjectAnimator? = null
    private var bellAnimator: ObjectAnimator? = null

    // Daftar lengkap semua resep makanan & kue
    private val semuaDataResep = listOf(
        ResepModel(judul = "San Francisco Sourdough", deskripsi = "Hard • 12h", gambar = R.drawable.sourdough1),
        ResepModel(judul = "Country Loaf Sourdough", deskripsi = "Medium • 8h", gambar = R.drawable.sourdough2),
        ResepModel(judul = "Borodinsky Bread", deskripsi = "Medium • 10h", gambar = R.drawable.borondisky),
        ResepModel(judul = "Baguette Sourdough", deskripsi = "Medium • 6h", gambar = R.drawable.baguete),
        ResepModel(judul = "Plain Sourdough", deskripsi = "Easy • 5h", gambar = R.drawable.plainsourdough),
        ResepModel(judul = "Walnut & Raisin Bread", deskripsi = "Easy • 1h 20m", gambar = R.drawable.walnut),

        ResepModel(judul = "Chocolate Lava Cakes", deskripsi = "Easy • 45m", gambar = R.drawable.lava_cakes),
        ResepModel(judul = "Caramel Apple Magic Fridge Cake", deskripsi = "Medium • 1h 10m", gambar = R.drawable.caramel),
        ResepModel(judul = "Rainbow Cake", deskripsi = "Easy • 55m", gambar = R.drawable.rainbow),
        ResepModel(judul = "Fudgy Brownies", deskripsi = "Easy • 1h 20m", gambar = R.drawable.classic_choco_brownies),
        ResepModel(judul = "Cheesecake Bars", deskripsi = "Medium • 3h 30m", gambar = R.drawable.no_bake_pumpkin_cheesecake_bars),

        ResepModel(judul = "Classic Cookies", deskripsi = "Easy • 35m", gambar = R.drawable.cookies),
        ResepModel(judul = "Kue Kering Nastar Klasik", deskripsi = "Easy • 50m", gambar = R.drawable.nastar),
        ResepModel(judul = "Butter Cookies", deskripsi = "Easy • 60m", gambar = R.drawable.butter),
        ResepModel(judul = "Kastengel Keju Spesial", deskripsi = "Easy • 45m", gambar = R.drawable.kastangel_keju),
        ResepModel(judul = "Classic Lidah Kucing", deskripsi = "Easy • 40m", gambar = R.drawable.kucing),
        ResepModel(judul = "Kue Kuping Gajah Renyah", deskripsi = "Easy • 45m", gambar = R.drawable.gajah)
    )

    // Daftar dinamis yang menggabungkan resep Supabase dan lokal
    private val daftarSemuaResep = mutableListOf<ResepModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        // 1. Tombol Notifikasi
        val btnNotifikasi = view.findViewById<ImageView>(R.id.btnNotifikasi)
        btnNotifikasi?.setOnClickListener {
            val bottomSheet = NotifikasiBottomSheet()
            bottomSheet.show(parentFragmentManager, "NotifikasiBottomSheet")
        }

        // 2. Inisialisasi & Listener Kartu Utama Bake of the Day (Dubai Chewy Cookies)
        cardBakeOfTheDay = view.findViewById(R.id.cardBakeOfTheDay)
        cardBakeOfTheDay.setOnClickListener {
            val intent = Intent(requireContext(), DetailResepActivity::class.java).apply {
                putExtra("EXTRA_JUDUL", "Dubai Chewy Cookies")
                putExtra("EXTRA_GAMBAR", R.drawable.dubai)
                putExtra("EXTRA_WAKTU", "⏱️ 1h 15m active")
                putExtra("EXTRA_TENTANG", "Terbuat dari marshmallow yang dilelehkan dan dicampur dengan bubuk kakao, menghasilkan tekstur yang kenyal dan elastis.")
            }
            startActivity(intent)
        }

        // 3. Set Up RecyclerView Resep
        rvTren = view.findViewById(R.id.rvKueTren)
        rvTren.layoutManager = GridLayoutManager(requireContext(), 2)
        if (daftarSemuaResep.isEmpty()) {
            daftarSemuaResep.addAll(semuaDataResep)
        }
        adapterResep = ResepAdapter(daftarSemuaResep)
        rvTren.adapter = adapterResep

        // Muat resep dinamis yang dipublikasikan dari Supabase
        muatResepDariSupabase()

        // 4. Jalankan Animasi Halaman Beranda
        jalankanAnimasiBeranda(view)

        // 5. Set Up Kolom Pencarian & Keyboard Otomatis Muncul Saat Diklik
        etSearch = view.findViewById(R.id.etSearch)
        val layoutSearchBar = view.findViewById<View>(R.id.layoutSearchBar)
        val ivSearchIcon = view.findViewById<View>(R.id.ivSearchIcon)

        fun tampilkanKeyboard() {
            etSearch.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

            // Paksa keyboard muncul meskipun emulator mendeteksi keyboard laptop/PC (SHOW_FORCED)
            imm?.showSoftInput(etSearch, InputMethodManager.SHOW_FORCED)
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

            activity?.window?.let { window ->
                WindowCompat.getInsetsController(window, etSearch).show(WindowInsetsCompat.Type.ime())
            }

            etSearch.postDelayed({
                if (isAdded && context != null) {
                    val immDelayed = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    immDelayed?.showSoftInput(etSearch, InputMethodManager.SHOW_FORCED)
                    activity?.window?.let { window ->
                        WindowCompat.getInsetsController(window, etSearch).show(WindowInsetsCompat.Type.ime())
                    }
                }
            }, 120)
        }

        // Deteksi sentuhan langsung pada kolom input
        etSearch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                tampilkanKeyboard()
                v.performClick()
            }
            false
        }

        // Buka keyboard saat kolom teks diklik
        etSearch.setOnClickListener {
            tampilkanKeyboard()
        }

        // Buka keyboard saat kolom teks mendapatkan fokus
        etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                tampilkanKeyboard()
            }
        }

        // Buka keyboard saat kotak pencarian atau ikon loop diklik
        layoutSearchBar?.setOnClickListener {
            tampilkanKeyboard()
        }
        ivSearchIcon?.setOnClickListener {
            tampilkanKeyboard()
        }

        // Sembunyikan keyboard saat menekan tombol Search / Enter pada keyboard
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(etSearch.windowToken, 0)
                etSearch.clearFocus()
                true
            } else {
                false
            }
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterResep(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 6. Popup Menu Pengurutan Resep (Ikon Filter)
        val btnFilterIcon = view.findViewById<ImageView>(R.id.btnFilterIcon)
        btnFilterIcon?.setOnClickListener { viewAnchor ->
            val popup = PopupMenu(requireContext(), viewAnchor)
            popup.menu.add(0, 1, 0, "Urutkan: Terbaru ke Terlama")
            popup.menu.add(0, 2, 1, "Urutkan: Terlama ke Terbaru")
            popup.menu.add(0, 3, 2, "Reset ke Default")

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        updateDataResep(daftarSemuaResep.reversed())
                        true
                    }
                    2, 3 -> {
                        updateDataResep(daftarSemuaResep)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        // 7. Inisialisasi & Listener Tombol Filter Kategori
        val btnSemua = view.findViewById<MaterialButton>(R.id.btnKategoriSemua)
        val btnSourdough = view.findViewById<MaterialButton>(R.id.btnKategoriSourdough)
        val btnKue = view.findViewById<MaterialButton>(R.id.btnKategoriKue)
        val btnKueKering = view.findViewById<MaterialButton>(R.id.btnKategoriKueKering)

        val daftarTombol = listOfNotNull(btnSemua, btnSourdough, btnKue, btnKueKering)

        // Set tombol 'Semua' aktif secara awal dengan animasi naik
        btnSemua?.let { aturWarnaTombolAktif(it, daftarTombol) }

        btnSemua?.setOnClickListener {
            aturWarnaTombolAktif(btnSemua, daftarTombol)
            etSearch.setText("")
            cardBakeOfTheDay.visibility = View.VISIBLE
            updateDataResep(daftarSemuaResep)
        }

        btnSourdough?.setOnClickListener {
            aturWarnaTombolAktif(btnSourdough, daftarTombol)
            etSearch.setText("")
            cardBakeOfTheDay.visibility = View.GONE
            val dataSourdough = daftarSemuaResep.filter {
                it.judul.contains("Sourdough", ignoreCase = true) ||
                        it.judul.contains("Bread", ignoreCase = true) ||
                        it.judul.contains("loaf", ignoreCase = true)
            }
            updateDataResep(dataSourdough)
        }

        btnKue?.setOnClickListener {
            aturWarnaTombolAktif(btnKue, daftarTombol)
            etSearch.setText("")
            cardBakeOfTheDay.visibility = View.GONE
            val dataKue = daftarSemuaResep.filter {
                it.judul.contains("Cake", ignoreCase = true) ||
                        it.judul.contains("Brownies", ignoreCase = true) ||
                        it.judul.contains("Cheesecake", ignoreCase = true) ||
                        it.judul.contains("Ciskek", ignoreCase = true) ||
                        it.judul.contains("Kue", ignoreCase = true)
            }
            updateDataResep(dataKue)
        }

        btnKueKering?.setOnClickListener {
            aturWarnaTombolAktif(btnKueKering, daftarTombol)
            etSearch.setText("")
            cardBakeOfTheDay.visibility = View.GONE
            val dataKueKering = daftarSemuaResep.filter {
                it.judul.contains("Cookie", ignoreCase = true) ||
                        it.judul.contains("Cookies", ignoreCase = true) ||
                        it.judul.contains("Nastar", ignoreCase = true) ||
                        it.judul.contains("Kering", ignoreCase = true) ||
                        it.judul.contains("Butter", ignoreCase = true) ||
                        it.judul.contains("Kastengel", ignoreCase = true) ||
                        it.judul.contains("Lidah", ignoreCase = true) ||
                        it.judul.contains("Kuping", ignoreCase = true)
            }
            updateDataResep(dataKueKering)
        }

        // Sinkronisasi kategori dari Supabase
        viewLifecycleOwner.lifecycleScope.launch {
            val listKategori = BakeryStorageManager.ambilDaftarKategori()
            if (listKategori.isNotEmpty()) {
                Log.d("SUPABASE_KATEGORI", "Berhasil sinkronisasi ${listKategori.size} kategori dari Supabase: ${listKategori.map { it.nama_kategori.trim() }}")
            }
        }

        return view
    }

    private fun jalankanAnimasiBeranda(rootView: View) {
        // 1. Kartu Bake of the Day mengambang naik-turun terus-menerus
        floatingAnimator = ObjectAnimator.ofFloat(cardBakeOfTheDay, "translationY", -8f, 8f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        // 2. Kilau cahaya menyapu kartu Bake of the Day terus-menerus
        val viewShimmer = rootView.findViewById<View>(R.id.viewShimmerCard)
        viewShimmer?.let { shimmer ->
            shimmerAnimator = ObjectAnimator.ofFloat(shimmer, "translationX", -220f, 1100f).apply {
                duration = 1700
                startDelay = 800
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }

        // 3. Ikon Lonceng Notifikasi berayun kiri-kanan terus-menerus
        val btnNotifikasi = rootView.findViewById<ImageView>(R.id.btnNotifikasi)
        btnNotifikasi?.let { bell ->
            bellAnimator = ObjectAnimator.ofFloat(bell, "rotation", -12f, 12f).apply {
                duration = 900
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }

        // 4. Daftar resep meluncur naik dari bawah saat halaman dibuka
        rvTren.alpha = 0f
        rvTren.translationY = 60f
        rvTren.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(150)
            .start()
    }

    private fun filterResep(query: String) {
        if (query.isNotEmpty()) {
            cardBakeOfTheDay.visibility = View.GONE
        } else {
            cardBakeOfTheDay.visibility = View.VISIBLE
        }

        val hasil = if (query.isEmpty()) {
            daftarSemuaResep
        } else {
            daftarSemuaResep.filter {
                it.judul.contains(query, ignoreCase = true) || it.deskripsi.contains(query, ignoreCase = true)
            }
        }
        updateDataResep(hasil)
    }

    private fun aturWarnaTombolAktif(btnAktif: MaterialButton, semuaTombol: List<MaterialButton>) {
        val density = resources.displayMetrics.density
        val bounceElevation = 6f * density
        val bounceTranslationY = -8f * density

        for (btn in semuaTombol) {
            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FCEBE5"))
            btn.setTextColor(Color.parseColor("#802C3E"))
            btn.strokeColor = ColorStateList.valueOf(Color.parseColor("#D7B5AE"))
            btn.strokeWidth = 1
            btn.elevation = 0f

            // Tombol tidak terpilih kembali normal ke posisi semula
            btn.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .translationY(0f)
                .setDuration(200)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }

        btnAktif.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#802C3E"))
        btnAktif.setTextColor(Color.parseColor("#FFF9F8"))
        btnAktif.strokeWidth = 0
        btnAktif.elevation = bounceElevation

        // Animasi terangkat naik dan membal seperti navbar melengkung
        btnAktif.animate()
            .scaleX(1.08f)
            .scaleY(1.08f)
            .translationY(bounceTranslationY)
            .setDuration(260)
            .setInterpolator(android.view.animation.OvershootInterpolator(2.5f))
            .start()
    }

    private fun updateDataResep(newData: List<ResepModel>) {
        adapterResep = ResepAdapter(newData)
        rvTren.adapter = adapterResep

        // Animasi transisi halus setiap kali daftar resep diperbarui (klik kategori/filter)
        rvTren.alpha = 0f
        rvTren.translationY = 25f
        rvTren.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(350)
            .start()
    }

    private fun muatResepDariSupabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resepDb = BakeryStorageManager.ambilResepBerandaDariSupabase()
                if (resepDb.isNotEmpty()) {
                    val hasilGabung = mutableListOf<ResepModel>()

                    // 1. Resep buatan pengguna yang baru dipublikasikan: tampil paling atas di bawah Bake of the Day
                    val resepUserPublikasi = resepDb.filter { dbItem ->
                        semuaDataResep.none { it.judul.equals(dbItem.judul, ignoreCase = true) }
                    }
                    for (dbItem in resepUserPublikasi) {
                        val itemFinal = if (dbItem.gambar == null || dbItem.gambar == R.drawable.dubai) {
                            dbItem.copy(gambar = dapatkanGambarResepSesuai(dbItem.judul, dbItem.gambar))
                        } else {
                            dbItem
                        }
                        hasilGabung.add(itemFinal)
                    }

                    // 2. Resep katalog bawaan (dimulai dari San Francisco Sourdough)
                    // Jika ada data di Supabase untuk resep katalog, perbarui detailnya
                    for (item in semuaDataResep) {
                        val dbMatch = resepDb.find { it.judul.equals(item.judul, ignoreCase = true) }
                        val itemFinal = if (dbMatch != null) {
                            if (dbMatch.fotoUrl.isNullOrEmpty() && dbMatch.imageUri == null) {
                                dbMatch.copy(gambar = item.gambar)
                            } else {
                                dbMatch
                            }
                        } else {
                            item
                        }
                        hasilGabung.add(itemFinal)
                    }

                    daftarSemuaResep.clear()
                    daftarSemuaResep.addAll(hasilGabung)
                    if (::adapterResep.isInitialized) {
                        updateDataResep(daftarSemuaResep)
                    }
                }
            } catch (e: Exception) {
                Log.e("BERANDA_SUPABASE", "Gagal sinkronisasi resep Supabase: ${e.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        muatResepDariSupabase()
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val listDb = BakeryStorageManager.muatResepDisukaiDariSupabase(requireContext())
                if (listDb.isNotEmpty()) {
                    DisukaiFragment.daftarResepDisukai.clear()
                    DisukaiFragment.daftarResepDisukai.addAll(listDb)
                }
                if (::adapterResep.isInitialized) {
                    adapterResep.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        floatingAnimator?.cancel()
        shimmerAnimator?.cancel()
        bellAnimator?.cancel()
    }
}