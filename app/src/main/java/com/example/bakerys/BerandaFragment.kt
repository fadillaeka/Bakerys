package com.example.bakerys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BerandaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        val rvTren = view.findViewById<RecyclerView>(R.id.rvKueTren)
        if (rvTren != null) {
            rvTren.layoutManager = GridLayoutManager(requireContext(), 2)
            // Data tiruan untuk kue yang sedang tren
            val dataTren = listOf(
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

            rvTren.adapter = ResepAdapter(dataTren)
        }

        return view
    }
}