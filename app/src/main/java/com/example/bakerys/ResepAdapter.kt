package com.example.bakerys

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

// 1. Data Model
data class ResepModel(
    val judul: String,
    val deskripsi: String,
    val gambar: Int,
    var isFavorite: Boolean = false,
    var isDisukai: Boolean = false
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

        // Logika tombol favorit (warna ikon)
        if (item.isFavorite) {
            holder.btnFavorite?.setColorFilter(Color.parseColor("#802C3E"))
        } else {
            holder.btnFavorite?.setColorFilter(Color.parseColor("#8C7A6B"))
        }

        // --- AKSI KLIK TOMBOL FAVORIT / LIKE ---
        holder.btnFavorite?.setOnClickListener {
            item.isFavorite = !item.isFavorite

            if (item.isFavorite) {
                holder.btnFavorite?.setColorFilter(Color.parseColor("#802C3E"))

                // TAMBAHKAN INI: Kirim data resep ke DisukaiFragment
                DisukaiFragment.tambahKeDisukai(item)
                Toast.makeText(holder.itemView.context, "${item.judul} ditambahkan ke Disukai", Toast.LENGTH_SHORT).show()
            } else {
                holder.btnFavorite?.setColorFilter(Color.parseColor("#8C7A6B"))

                // TAMBAHKAN INI: Hapus data resep dari DisukaiFragment jika di-unlike
                DisukaiFragment.daftarResepDisukai.removeAll { it.judul == item.judul }
                Toast.makeText(holder.itemView.context, "${item.judul} dihapus dari Disukai", Toast.LENGTH_SHORT).show()
            }
        }

        // --- AKSI KLIK KARTU UNTUK MEMBUKA DETAIL RESEP ---
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailResepActivity::class.java).apply {
                putExtra("EXTRA_JUDUL", item.judul)
                putExtra("EXTRA_GAMBAR", item.gambar)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listResep.size
}