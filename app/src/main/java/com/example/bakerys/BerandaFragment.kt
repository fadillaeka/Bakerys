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
                ResepModel("Classic Choco Brownies", "Cokelat lumer dengan tekstur lembut di dalam."),
                ResepModel("Creamy Cheese Tart", "Tart keju gurih dengan crust renyah."),
                ResepModel("Matcha Roll Cake", "Kue gulung lembut aroma jepang.")
            )

            rvTren.adapter = ResepAdapter(dataTren)
        }

        return view
    }
}