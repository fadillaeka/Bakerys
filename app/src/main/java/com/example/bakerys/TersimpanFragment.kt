package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakerys.databinding.FragmentTersimpanBinding

class TersimpanFragment : Fragment() {

    private var _binding: FragmentTersimpanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTersimpanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listTersimpan = listOf(
            TersimpanModel("Cookies", R.drawable.cookies),
            TersimpanModel("Lava Chocolate Cake", R.drawable.lava_cakes),
            TersimpanModel("Rainbow", R.drawable.rainbow)
        )

        binding.rvTersimpan.layoutManager = LinearLayoutManager(requireContext())

        // Membuka DetailResepActivity saat kartu diklik
        binding.rvTersimpan.adapter = TersimpanAdapter(listTersimpan) { item ->
            val intent = Intent(requireContext(), DetailResepActivity::class.java).apply {
                putExtra("EXTRA_JUDUL", item.judul)
                putExtra("EXTRA_GAMBAR", item.gambar)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}