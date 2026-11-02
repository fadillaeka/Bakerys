package com.example.bakerys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class JelajahFragment : Fragment() {

    private lateinit var rvJelajah: RecyclerView
    private lateinit var jelajahAdapter: JelajahAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_jelajah, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ID disesuaikan dengan android:id="@+id/rvJelajah"
        rvJelajah = view.findViewById(R.id.rvJelajah)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val sampleRecipes = listOf(
            Recipe(
                id = 1,
                title = "No-Bake Pumpkin Cheesecake Bars",
                description = "Resep No-Bake Pumpkin Cheesecake Bars ini terdiri dari lapisan dasar biskuit graham dengan isian cheesecake labu yang lembut dan krim kocok manis.",
                imageResId = R.drawable.no_bake_pumpkin_cheesecake_bars
            ),
            Recipe(
                id = 2,
                title = "Caramel Apple Magic Fridge Cake",
                description = "Dibuat dengan menyusun biskuit atau biskuit graham secara berlapis dengan adonan krim, lalu didinginkan di kulkas hingga tekstur biskuitnya melunak dan menyatu seperti kue spons.",
                imageResId = R.drawable.caramel
            )
        )

        jelajahAdapter = JelajahAdapter(sampleRecipes) { recipe ->
            Toast.makeText(requireContext(), "Membuka: ${recipe.title}", Toast.LENGTH_SHORT).show()
        }

        rvJelajah.layoutManager = LinearLayoutManager(requireContext())
        rvJelajah.adapter = jelajahAdapter
    }
}