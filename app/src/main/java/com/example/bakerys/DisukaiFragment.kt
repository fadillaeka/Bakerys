package com.example.bakerys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


@kotlinx.serialization.InternalSerializationApi
class DisukaiFragment : Fragment() {

    private lateinit var adapterDisukai: DisukaiAdapter
    private lateinit var rvDisukai: RecyclerView

    companion object {
        val daftarResepDisukai = mutableListOf<ResepModel>()

        fun tambahKeDisukai(resep: ResepModel) {
            if (!daftarResepDisukai.any { it.judul == resep.judul }) {
                daftarResepDisukai.add(resep)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_disukai, container, false)

        rvDisukai = view.findViewById(R.id.rvDisukai)
        rvDisukai.layoutManager = LinearLayoutManager(requireContext())

        adapterDisukai = DisukaiAdapter(
            listDisukai = daftarResepDisukai,
            onItemClick = {}
        )
        rvDisukai.adapter = adapterDisukai

        return view
    }

    // Agar setiap kali halaman Disukai dibuka kembali, datanya otomatis ter-update
    override fun onResume() {
        super.onResume()
        if (::adapterDisukai.isInitialized) {
            adapterDisukai.notifyDataSetChanged()
        }
    }
}