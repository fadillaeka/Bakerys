package com.example.bakerys

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ResepAdapter(private val listResep: List<ResepModel>) :
    RecyclerView.Adapter<ResepAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardContainer: MaterialCardView = itemView.findViewById(R.id.cardContainer)
        val imgKue: ImageView = itemView.findViewById(R.id.imgKue)
        val btnFavorite: ImageView = itemView.findViewById(R.id.btnFavorite)
        val tvJudulKue: TextView = itemView.findViewById(R.id.tvJudulKue)
        val tvWaktuLevel: TextView = itemView.findViewById(R.id.tvWaktuLevel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resipe_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listResep[position]

        // 1. Tampilkan teks
        holder.tvJudulKue.text = item.judul
        holder.tvWaktuLevel.text = item.deskripsi

        // 2. Logika warna awal tombol favorit
        if (item.isFavorite) {
            holder.btnFavorite.setColorFilter(Color.parseColor("#D32F2F")) // Merah
        } else {
            holder.btnFavorite.setColorFilter(Color.parseColor("#8C7A6B")) // Cokelat/Abu
        }

        // 3. Logika klik ikon favorit (Merah <-> Cokelat)
        holder.btnFavorite.setOnClickListener {
            item.isFavorite = !item.isFavorite
            if (item.isFavorite) {
                holder.btnFavorite.setColorFilter(Color.parseColor("#D32F2F"))
            } else {
                holder.btnFavorite.setColorFilter(Color.parseColor("#8C7A6B"))
            }
        }
    }

    override fun getItemCount(): Int = listResep.size
}