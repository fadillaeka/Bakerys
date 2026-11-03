package com.example.bakerys

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class BerandaFragment : Fragment() {

    private lateinit var rvTren: RecyclerView
    private lateinit var adapterResep: ResepAdapter
    private lateinit var etSearch: EditText

    // Daftar resep campuran
    private val semuaDataResep = listOf(
        ResepModel(
            judul = "San Francisco Sourdough",
            deskripsi = "Hard • 12h",
            gambar = R.drawable.caramel
        ),
        ResepModel(
            judul = "Chocolate Lava Cakes",
            deskripsi = "Easy • 45m",
            gambar = R.drawable.lava_cakes
        ),
        ResepModel(
            judul = "Classic Choco Cookies",
            deskripsi = "Easy • 35m",
            gambar = R.drawable.cookies
        ),
        ResepModel(
            judul = "Fudgy Brownies",
            deskripsi = "Easy • 1h 20m",
            gambar = R.drawable.classic_choco_brownies
        ),
        ResepModel(
            judul = "Country Loaf Sourdough",
            deskripsi = "Medium • 8h",
            gambar = R.drawable.caramel
        ),
        ResepModel(
            judul = "Rainbow Cake",
            deskripsi = "Easy • 55m",
            gambar = R.drawable.rainbow
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        // 0. Event Klik Ikon Notifikasi (Lonceng Header)
        val btnNotifikasi = view.findViewById<ImageView>(R.id.btnNotifikasi)
        btnNotifikasi?.setOnClickListener {
            val bottomSheet = NotifikasiBottomSheet()
            bottomSheet.show(parentFragmentManager, "NotifikasiBottomSheet")
        }

        // 1. Event Klik Card Utama Dubai Chewy Cookies (Bake of the Day)
        val cardBakeOfDay = view.findViewById<View>(R.id.cardBakeOfDay)
        cardBakeOfDay?.setOnClickListener {
            val intent = Intent(requireContext(), DetailResepActivity::class.java).apply {
                putExtra("EXTRA_JUDUL", "Dubai Chewy Cookies")
                putExtra("EXTRA_GAMBAR", R.drawable.dubai)
                putExtra("EXTRA_WAKTU", "⏱️ 1h 15m")
                putExtra("EXTRA_TENTANG", "Terbuat dari marshmallow yang dilelehkan dan dicampur dengan bubuk kakao, menghasilkan tekstur yang kenyal dan elastis.")
            }
            startActivity(intent)
        }

        // 2. Setup RecyclerView
        rvTren = view.findViewById(R.id.rvKueTren)
        rvTren.layoutManager = GridLayoutManager(requireContext(), 2)

        adapterResep = ResepAdapter(semuaDataResep)
        rvTren.adapter = adapterResep

        // 3. Logika Pencarian Real-Time (Search Bar)
        etSearch = view.findViewById(R.id.etSearch)
        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterResep(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 4. Setup Tombol Filter Kategori
        val btnSemua = view.findViewById<MaterialButton>(R.id.btnKategoriSemua)
        val btnSourdough = view.findViewById<MaterialButton>(R.id.btnKategoriSourdough)
        val btnKue = view.findViewById<MaterialButton>(R.id.btnKategoriKue)
        val btnKueKering = view.findViewById<MaterialButton>(R.id.btnKategoriKueKering)

        val daftarTombol = listOfNotNull(btnSemua, btnSourdough, btnKue, btnKueKering)

        btnSemua?.setOnClickListener {
            aturWarnaTombolAktif(btnSemua, daftarTombol)
            etSearch?.setText("")
            updateDataResep(semuaDataResep)
        }

        btnSourdough?.setOnClickListener {
            aturWarnaTombolAktif(btnSourdough, daftarTombol)
            etSearch?.setText("")
            val dataSourdough = semuaDataResep.filter {
                it.judul.contains("Sourdough", ignoreCase = true) || it.judul.contains("Bread", ignoreCase = true)
            }
            updateDataResep(dataSourdough)
        }

        btnKue?.setOnClickListener {
            aturWarnaTombolAktif(btnKue, daftarTombol)
            etSearch?.setText("")
            val dataKue = semuaDataResep.filter {
                it.judul.contains("Cake", ignoreCase = true) || it.judul.contains("Brownies", ignoreCase = true) || it.judul.contains("Cheesecake", ignoreCase = true)
            }
            updateDataResep(dataKue)
        }

        btnKueKering?.setOnClickListener {
            aturWarnaTombolAktif(btnKueKering, daftarTombol)
            etSearch?.setText("")
            val dataKueKering = semuaDataResep.filter {
                it.judul.contains("Cookie", ignoreCase = true) || it.judul.contains("Cookies", ignoreCase = true)
            }
            updateDataResep(dataKueKering)
        }

        return view
    }

    private fun filterResep(query: String) {
        val hasilFilter = if (query.isEmpty()) {
            semuaDataResep
        } else {
            semuaDataResep.filter {
                it.judul.contains(query, ignoreCase = true) || it.deskripsi.contains(query, ignoreCase = true)
            }
        }
        updateDataResep(hasilFilter)
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