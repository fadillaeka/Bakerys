package com.example.bakerys

import android.content.Intent
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
            onItemClick = { resep ->
                val intent = Intent(requireContext(), DetailResepActivity::class.java).apply {
                    putExtra("EXTRA_JUDUL", resep.judul)
                    putExtra("EXTRA_GAMBAR", resep.gambar ?: R.drawable.dubai)

                    val durasiFormat = if (resep.deskripsi.contains("•")) {
                        "⏱️ " + resep.deskripsi.split("•").last().trim()
                    } else {
                        "⏱️ " + resep.deskripsi
                    }
                    putExtra("EXTRA_WAKTU", durasiFormat)
                    putExtra("EXTRA_TENTANG", "Resep olahan lezat ${resep.judul} yang mudah dan praktis dibuat di rumah.")
                }
                startActivity(intent)
            }
        )
        rvDisukai.adapter = adapterDisukai
        rvDisukai.scheduleLayoutAnimation()

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