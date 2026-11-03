package com.example.bakerys

import android.graphics.Color
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class BerandaFragment : Fragment() {

    private lateinit var rvTren: RecyclerView
    private lateinit var adapterResep: ResepAdapter

    // Daftar lengkap semua resep (kamu bisa tambahkan properti kategori di ResepModel nantinya)
    private val semuaDataResep = listOf(
        ResepModel(
            judul = "Chocolate Lava Cakes",
            deskripsi = "Easy • 45m",
            gambar = R.drawable.lava_cakes
        ),
        ResepModel(
            judul = "Caramel Apple Magic Fridge Cake",
            deskripsi = "Medium • 1h 10m",
            gambar = R.drawable.caramel
        ),
        ResepModel(
            judul = "Rainbow Cake",
            deskripsi = "Easy • 55m",
            gambar = R.drawable.rainbow
        ),
        ResepModel(
            judul = "Cheseecake Bars",
            deskripsi = "Medium • 3h 30m",
            gambar = R.drawable.no_bake_pumpkin_cheesecake_bars
        ),
        ResepModel(
            judul = "Cookies",
            deskripsi = "Easy • 35m",
            gambar = R.drawable.cookies
        ),
        ResepModel(
            judul = "Fudgy Brownies",
            deskripsi = "Easy • 1h 20m",
            gambar = R.drawable.classic_choco_brownies
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        rvTren = view.findViewById(R.id.rvKueTren)
        rvTren.layoutManager = GridLayoutManager(requireContext(), 2)

        // Tampilkan semua data secara default
        adapterResep = ResepAdapter(semuaDataResep)
        rvTren.adapter = adapterResep

        // Inisialisasi Tombol Kategori (Pastikan ID di fragment_beranda.xml sudah sesuai)
        val btnSemua = view.findViewById<MaterialButton>(R.id.btnKategoriSemua)
        val btnSourdough = view.findViewById<MaterialButton>(R.id.btnKategoriSourdough)
        val btnKue = view.findViewById<MaterialButton>(R.id.btnKategoriKue)
        val btnKueKering = view.findViewById<MaterialButton>(R.id.btnKategoriKueKering)

        val daftarTombol = listOf(btnSemua, btnSourdough, btnKue, btnKueKering)

        // Aksi klik tombol Kategori
        btnSemua?.setOnClickListener {
            aturWarnaTombolAktif(btnSemua, daftarTombol)
            updateDataResep(semuaDataResep)
        }

        btnSourdough?.setOnClickListener {
            aturWarnaTombolAktif(btnSourdough, daftarTombol)
            // Contoh filter untuk Sourdough (bisa disesuaikan dengan kriteria datamu)
            val dataSourdough = semuaDataResep.filter { it.judul.contains("Sourdough", ignoreCase = true) }
            updateDataResep(dataSourdough)
        }

        btnKue?.setOnClickListener {
            aturWarnaTombolAktif(btnKue, daftarTombol)
            // Contoh filter untuk Kue (seperti Cake/Lava Cake/Rainbow Cake)
            val dataKue = semuaDataResep.filter { it.judul.contains("Cake", ignoreCase = true) || it.judul.contains("Brownies", ignoreCase = true) }
            updateDataResep(dataKue)
        }

        btnKueKering?.setOnClickListener {
            aturWarnaTombolAktif(btnKueKering, daftarTombol)
            // Contoh filter untuk Kue Kering / Cookies
            val dataKueKering = semuaDataResep.filter { it.judul.contains("Cookies", ignoreCase = true) }
            updateDataResep(dataKueKering)
        }

        return view
    }

    // Fungsi untuk mengubah warna chip (aktif jadi maroon, lainnya peach)
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

    // Fungsi untuk memperbarui data resep di RecyclerView secara dinamis
    private fun updateDataResep(newData: List<ResepModel>) {
        adapterResep = ResepAdapter(newData)
        rvTren.adapter = adapterResep
    }
}