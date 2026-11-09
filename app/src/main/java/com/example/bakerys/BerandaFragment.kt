@file:OptIn(
    io.github.jan.supabase.annotations.SupabaseExperimental::class,
    kotlinx.serialization.InternalSerializationApi::class
)
package com.example.bakerys

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class BerandaFragment : Fragment() {

    private lateinit var rvTren: RecyclerView
    private lateinit var adapterResep: ResepAdapter
    private lateinit var etSearch: EditText
    private lateinit var cardBakeOfTheDay: MaterialCardView

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

        // --- SENARAI KUIH KERING LENGKAP ---
        ResepModel(judul = "Classic Cookies", deskripsi = "Easy • 35m", gambar = R.drawable.cookies),
        ResepModel(judul = "Nastar Premium", deskripsi = "Medium • 1h", gambar = R.drawable.nastar),
        ResepModel(judul = "Kastangel Keju", deskripsi = "Easy • 45m", gambar = R.drawable.kastangel),
        ResepModel(judul = "Lidah Kucing", deskripsi = "Easy • 35m", gambar = R.drawable.lidah_kucing),
        ResepModel(judul = "Kuping Gajah", deskripsi = "Easy • 30m", gambar = R.drawable.kuping_gajah),
        ResepModel(judul = "Kue Kering Nastar Klasik", deskripsi = "Easy • 50m", gambar = R.drawable.nastar),
        ResepModel(judul = "Butter Cookies", deskripsi = "Easy • 60m", gambar = R.drawable.butter),
        ResepModel(judul = "Kastengel Keju Spesial", deskripsi = "Easy • 45m", gambar = R.drawable.kastangel_keju),
        ResepModel(judul = "Classic Lidah Kucing", deskripsi = "Easy • 40m", gambar = R.drawable.kucing),
        ResepModel(judul = "Kue Kuping Gajah Renyah", deskripsi = "Easy • 45m", gambar = R.drawable.gajah)
    )

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
        adapterResep = ResepAdapter(semuaDataResep)
        rvTren.adapter = adapterResep

        // 4. Set Up Kolom Pencarian & Keyboard
        etSearch = view.findViewById(R.id.etSearch)
        etSearch.setOnClickListener {
            etSearch.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterResep(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 5. Popup Menu Pengurutan Resep (Ikon Filter)
        val btnFilterIcon = view.findViewById<ImageView>(R.id.btnFilterIcon)
        btnFilterIcon?.setOnClickListener { viewAnchor ->
            val popup = PopupMenu(requireContext(), viewAnchor)
            popup.menu.add(0, 1, 0, "Urutkan: Terbaru ke Terlama")
            popup.menu.add(0, 2, 1, "Urutkan: Terlama ke Terbaru")
            popup.menu.add(0, 3, 2, "Reset ke Default")

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        updateDataResep(semuaDataResep.reversed())
                        true
                    }
                    2, 3 -> {
                        updateDataResep(semuaDataResep)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        // 6. Inisialisasi & Listener Tombol Filter Kategori
        val btnSemua = view.findViewById<MaterialButton>(R.id.btnKategoriSemua)
        val btnSourdough = view.findViewById<MaterialButton>(R.id.btnKategoriSourdough)
        val btnKue = view.findViewById<MaterialButton>(R.id.btnKategoriKue)
        val btnKueKering = view.findViewById<MaterialButton>(R.id.btnKategoriKueKering)

        val daftarTombol = listOfNotNull(btnSemua, btnSourdough, btnKue, btnKueKering)

        // Tombol SEMUA
        btnSemua?.setOnClickListener {
            aturWarnaTombolAktif(btnSemua, daftarTombol)
            etSearch.setText("")
            cardBakeOfTheDay.visibility = View.VISIBLE
            updateDataResep(semuaDataResep)
        }

        // Tombol SOURDOUGH
        btnSourdough?.setOnClickListener {
            aturWarnaTombolAktif(btnSourdough, daftarTombol)
            etSearch.setText("")
            cardBakeOfTheDay.visibility = View.GONE
            val dataSourdough = semuaDataResep.filter { resep: ResepModel ->
                resep.judul.contains("Sourdough", ignoreCase = true) ||
                        resep.judul.contains("Bread", ignoreCase = true) ||
                        resep.judul.contains("loaf", ignoreCase = true)
            }
            updateDataResep(dataSourdough)
        }

        // Tombol KUE
        btnKue?.setOnClickListener {
            aturWarnaTombolAktif(btnKue, daftarTombol)
            etSearch.setText("")
            cardBakeOfTheDay.visibility = View.GONE
            val dataKue = semuaDataResep.filter { resep: ResepModel ->
                resep.judul.contains("Cake", ignoreCase = true) ||
                        resep.judul.contains("Brownies", ignoreCase = true) ||
                        resep.judul.contains("Cheesecake", ignoreCase = true)
            }
            updateDataResep(dataKue)
        }

        // Tombol KUE KERING
        btnKueKering?.setOnClickListener {
            aturWarnaTombolAktif(btnKueKering, daftarTombol)
            etSearch.setText("")
            cardBakeOfTheDay.visibility = View.GONE
            val dataKueKering = semuaDataResep.filter { resep: ResepModel ->
                resep.judul.contains("Cookie", ignoreCase = true) ||
                        resep.judul.contains("Cookies", ignoreCase = true) ||
                        resep.judul.contains("Nastar", ignoreCase = true) ||
                        resep.judul.contains("Kastangel", ignoreCase = true) ||
                        resep.judul.contains("Kastengel", ignoreCase = true) ||
                        resep.judul.contains("Lidah", ignoreCase = true) ||
                        resep.judul.contains("Kuping", ignoreCase = true) ||
                        resep.judul.contains("Kering", ignoreCase = true) ||
                        resep.judul.contains("Butter", ignoreCase = true)
            }
            updateDataResep(dataKueKering)
        }

        return view
    }

    private fun filterResep(query: String) {
        if (query.isNotEmpty()) {
            cardBakeOfTheDay.visibility = View.GONE
        } else {
            cardBakeOfTheDay.visibility = View.VISIBLE
        }

        val hasil = if (query.isEmpty()) {
            semuaDataResep
        } else {
            semuaDataResep.filter { resep: ResepModel ->
                resep.judul.contains(query, ignoreCase = true) || resep.deskripsi.contains(query, ignoreCase = true)
            }
        }
        updateDataResep(hasil)
    }

    private fun aturWarnaTombolAktif(btnAktif: MaterialButton, semuaTombol: List<MaterialButton>) {
        for (btn in semuaTombol) {
            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FCEBE5"))
            btn.setTextColor(Color.parseColor("#802C3E"))
            btn.strokeColor = ColorStateList.valueOf(Color.parseColor("#D7B5AE"))
            btn.strokeWidth = 1
        }

        btnAktif.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#802C3E"))
        btnAktif.setTextColor(Color.parseColor("#FFF9F8"))
        btnAktif.strokeWidth = 0
    }

    private fun updateDataResep(newData: List<ResepModel>) {
        adapterResep = ResepAdapter(newData)
        rvTren.adapter = adapterResep
    }
}