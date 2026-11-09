package com.example.bakerys

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class DisukaiAdapter(
    private val listDisukai: MutableList<ResepModel>,
    private val onItemClick: (ResepModel) -> Unit
) : RecyclerView.Adapter<DisukaiAdapter.DisukaiViewHolder>() {

    class DisukaiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivGambar: ImageView = view.findViewById(R.id.ivDisukaiGambar)
        val tvJudul: TextView = view.findViewById(R.id.tvDisukaiJudul)
        val tvDeskripsi: TextView = view.findViewById(R.id.tvDisukaiDeskripsi)
        val ivHeart: ImageView = view.findViewById(R.id.ivDisukaiHeart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisukaiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_disukai_card, parent, false)
        return DisukaiViewHolder(view)
    }

    override fun onBindViewHolder(holder: DisukaiViewHolder, position: Int) {
        val item = listDisukai[position]
        holder.tvJudul.text = item.judul
        holder.tvDeskripsi.text = item.deskripsi
        holder.ivGambar.setImageResource(item.gambar ?: R.drawable.dubai)

        // 1. Klik Kartu Resep -> Pindah ke DetailResepActivity
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        // 2. Aksi ketika ikon hati diklik (Unlike / Hapus dari daftar disukai)
        holder.ivHeart.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val removedItem = listDisukai[currentPosition]

                // Hapus dari list
                listDisukai.removeAt(currentPosition)
                notifyItemRemoved(currentPosition)
                notifyItemRangeChanged(currentPosition, listDisukai.size - currentPosition)

                // Tampilkan pesan
                Toast.makeText(holder.itemView.context, "${removedItem.judul} dihapus dari Disukai", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = listDisukai.size
}