package com.example.bakerys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakerys.databinding.FragmentDraftBinding

class DraftFragment : Fragment() {

    private var _binding: FragmentDraftBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDraftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Data dummy khusus Draft sesuai gambar referensimu
        val listDraft = listOf(
            DraftModel("No-Bake Pumpkin\nCheesecake Bars", R.drawable.no_bake_pumpkin_cheesecake_bars),
            DraftModel("Rainbow Cake", R.drawable.rainbow),
            DraftModel("Caramel Apple Magic\nFridge Cake", R.drawable.caramel),
            DraftModel("Chocolate Lava Cake", R.drawable.classic_choco_brownies)
        )

        // Menggunakan DraftAdapter khusus agar kartu item_draft_card terbaca
        binding.rvDraft.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDraft.adapter = DraftAdapter(listDraft) { item ->
            Toast.makeText(requireContext(), "Membuka: ${item.judul}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}