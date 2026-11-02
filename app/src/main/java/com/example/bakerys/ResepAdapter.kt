package com.example.bakerys

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 1. Data Model
data class ResepModel(
    val judul: String,
    val deskripsi: String,
    val gambar: Int,
    var isFavorite: Boolean = false
)

// 2. Adapter
class ResepAdapter(private val listResep: List<ResepModel>) :
    RecyclerView.Adapter<ResepAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgKue: ImageView? = itemView.findViewById(R.id.imgKue)
        val btnFavorite: ImageView? = itemView.findViewById(R.id.btnFavorite)
        val tvJudulKue: TextView? = itemView.findViewById(R.id.tvJudulKue)
        val tvWaktuLevel: TextView? = itemView.findViewById(R.id.tvWaktuLevel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resipe_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listResep[position]

        holder.tvJudulKue?.text = item.judul
        holder.tvWaktuLevel?.text = item.deskripsi

        // Pasang gambar kue secara dinamik
        holder.imgKue?.setImageResource(item.gambar)

        // Logik butang favorit
        if (item.isFavorite) {
            holder.btnFavorite?.setColorFilter(Color.parseColor("#D32F2F"))
        } else {
            holder.btnFavorite?.setColorFilter(Color.parseColor("#8C7A6B"))
        }

        holder.btnFavorite?.setOnClickListener {
            item.isFavorite = !item.isFavorite
            if (item.isFavorite) {
                holder.btnFavorite?.setColorFilter(Color.parseColor("#D32F2F"))
            } else {
                holder.btnFavorite?.setColorFilter(Color.parseColor("#8C7A6B"))
            }
        }
    }

    override fun getItemCount(): Int = listResep.size
}