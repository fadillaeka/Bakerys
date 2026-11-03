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

    // Daftar lengkap semua resep
    private val semuaDataResep = listOf(
        ResepModel(
            judul = "San Francisco Sourdough",
            deskripsi = "Easy • 45m",
            gambar = R.drawable.sourdough1
        ),
        ResepModel(
            judul = "country loaf sourdough",
            deskripsi = "Medium • 1h 10m",
            gambar = R.drawable.sourdough2
        ),
        ResepModel(
            judul = "Borodinsky Bread",
            deskripsi = "Medium • 3h 30m",
            gambar = R.drawable.borondisky
        ),
        ResepModel(
            judul = "Baguette Sourdough",
            deskripsi = "Medium • 2h 00m",
            gambar = R.drawable.baguete
        ),
        ResepModel(
            judul = "Plain Sourdough",
            deskripsi = "Easy • 55m",
            gambar = R.drawable.plainsourdough
        ),
        ResepModel(
            judul = "Walnut & Raisin Bread",
            deskripsi = "Easy • 1h 20m",
            gambar = R.drawable.walnut
        ),
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
            judul = "Dubai Chewy Cookies",
            deskripsi = "Intermediate • 1h 15m",
            gambar = R.drawable.cookies
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

        // Inisialisasi Tombol Kategori
        val btnSemua = view.findViewById<MaterialButton>(R.id.btnKategoriSemua)
        val btnSourdough = view.findViewById<MaterialButton>(R.id.btnKategoriSourdough)
        val btnKue = view.findViewById<MaterialButton>(R.id.btnKategoriKue)
        val btnKueKering = view.findViewById<MaterialButton>(R.id.btnKategoriKueKering)

        // Inisialisasi Card Bake of the Day
        val cardBakeOfTheDay = view.findViewById<View>(R.id.cardBakeOfTheDay)

        val daftarTombol = listOfNotNull(btnSemua, btnSourdough, btnKue, btnKueKering)

        btnSemua?.setOnClickListener {
            aturWarnaTombolAktif(btnSemua, daftarTombol)
            updateDataResep(semuaDataResep)
            cardBakeOfTheDay?.visibility = View.VISIBLE
        }

        btnSourdough?.setOnClickListener {
            aturWarnaTombolAktif(btnSourdough, daftarTombol)
            val dataSourdough = semuaDataResep.filter {
                it.judul.contains("Sourdough", ignoreCase = true) ||
                        it.judul.contains("Bread", ignoreCase = true) ||
                        it.judul.contains("loaf", ignoreCase = true)
            }
            updateDataResep(dataSourdough)
            cardBakeOfTheDay?.visibility = View.GONE
        }

        btnKue?.setOnClickListener {
            aturWarnaTombolAktif(btnKue, daftarTombol)
            val dataKue = semuaDataResep.filter {
                it.judul.contains("Cake", ignoreCase = true) ||
                        it.judul.contains("Kue", ignoreCase = true) ||
                        it.judul.contains("Bars", ignoreCase = true)
            }
            updateDataResep(dataKue)
            cardBakeOfTheDay?.visibility = View.GONE
        }

        btnKueKering?.setOnClickListener {
            aturWarnaTombolAktif(btnKueKering, daftarTombol)
            val dataKueKering = semuaDataResep.filter {
                it.judul.contains("Cookies", ignoreCase = true) ||
                        it.judul.contains("Kering", ignoreCase = true)
            }
            updateDataResep(dataKueKering)
            cardBakeOfTheDay?.visibility = View.GONE
        }

        return view
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