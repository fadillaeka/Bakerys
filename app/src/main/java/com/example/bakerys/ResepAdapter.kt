package com.example.bakerys

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Data Model untuk Resep
data class ResepModel(
    val judul: String,
    val deskripsi: String
    var isFavorite: Boolean = false
)

class ResepAdapter(private val listResep: List<ResepModel>) : RecyclerView.Adapter<ResepAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Disesuaikan dengan ID di item_resipe_card.xml
        val tvJudul: TextView = view.findViewById(R.id.tvJudulKue)
        val tvDeskripsi: TextView = view.findViewById(R.id.tvWaktuLevel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resipe_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listResep[position]
        holder.tvJudul.text = item.judul
        holder.tvDeskripsi.text = item.deskripsi
    }

    override fun getItemCount(): Int = listResep.size
}