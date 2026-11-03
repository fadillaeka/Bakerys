package com.example.bakerys

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class TersimpanModel(
    val judul: String,
    val gambar: Int,
    var isBookmarked: Boolean = true
)

class TersimpanAdapter(
    private val listTersimpan: List<TersimpanModel>,
    private val onItemClick: (TersimpanModel) -> Unit
) : RecyclerView.Adapter<TersimpanAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTersimpanThumb: ImageView = itemView.findViewById(R.id.ivTersimpanThumb)
        val tvTersimpanTitle: TextView = itemView.findViewById(R.id.tvTersimpanTitle)
        val ivBookmark: ImageView = itemView.findViewById(R.id.ivBookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tersimpan_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listTersimpan[position]

        holder.tvTersimpanTitle.text = item.judul
        holder.ivTersimpanThumb.setImageResource(item.gambar)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = listTersimpan.size
}