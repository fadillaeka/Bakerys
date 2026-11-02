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
                    judul = "Classic Choco Brownies",
                    deskripsi = "Cokelat lumer dengan tekstur...",
                    gambar = R.drawable.classic_choco_brownies
                ),
                ResepModel(
                    judul = "Creamy Cheese Tart",
                    deskripsi = "Tart keju gurih...",
                    gambar = R.drawable.chese_tart
                ),
                ResepModel(
                    judul = "Matcha Roll Cake",
                    deskripsi = "Kue gulung lembut...",
                    gambar = R.drawable.matcha_roll
                )
            )

            rvTren.adapter = ResepAdapter(dataTren)
        }

        return view
    }
}