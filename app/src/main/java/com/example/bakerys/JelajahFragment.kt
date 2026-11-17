package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        rvJelajah = view.findViewById(R.id.rvJelajah)
        rvJelajah.layoutManager = LinearLayoutManager(requireContext())

        jelajahAdapter = JelajahAdapter(
            recipeList = RecipeRepository.recipeList
        )

        rvJelajah.adapter = jelajahAdapter
        rvJelajah.scheduleLayoutAnimation()
    }

    override fun onResume() {
        super.onResume()
        if (::jelajahAdapter.isInitialized) {
            jelajahAdapter.notifyDataSetChanged()
        }
    }
}