package com.example.bakerys

import android.net.Uri
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

        try {
            var imageLoaded = false
            val uriStr = item.fotoUrl ?: item.imageUri?.toString()
            if (!uriStr.isNullOrEmpty()) {
                holder.ivGambar.setImageURI(Uri.parse(uriStr))
                if (holder.ivGambar.drawable != null) {
                    imageLoaded = true
                }
            }
            if (!imageLoaded) {
                val resId = if (item.gambar != null && item.gambar != 0 && item.gambar != R.drawable.dubai) {
                    item.gambar
                } else {
                    dapatkanGambarResepSesuai(item.judul, item.gambar)
                }
                holder.ivGambar.setImageResource(resId)
            }
        } catch (e: Exception) {
            val resId = dapatkanGambarResepSesuai(item.judul, item.gambar)
            holder.ivGambar.setImageResource(resId)
        }

        // 1. Klik Kartu Resep -> Pindah ke DetailResepActivity
        val bukaDetail = View.OnClickListener {
            onItemClick(item)
            val context = holder.itemView.context
            if (context is android.app.Activity) {
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        holder.itemView.setOnClickListener(bukaDetail)
        holder.ivGambar.setOnClickListener(bukaDetail)
        holder.tvJudul.setOnClickListener(bukaDetail)
        holder.tvDeskripsi.setOnClickListener(bukaDetail)

        // 2. Aksi ketika ikon hati diklik (Unlike / Hapus dari daftar disukai)
        holder.ivHeart.setOnClickListener {
            val animPulse = android.view.animation.AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_pulse)
            holder.ivHeart.startAnimation(animPulse)

            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < listDisukai.size) {
                val removedItem = listDisukai[currentPosition]

                // Hapus dari list
                listDisukai.removeAt(currentPosition)
                BakeryStorageManager.simpanDaftarDisukai(holder.itemView.context, listDisukai)
                val targetId = removedItem.id ?: 11L
                BakeryStorageManager.simpanResepDisukaiKeSupabase(holder.itemView.context, targetId, false)
                notifyItemRemoved(currentPosition)
                notifyItemRangeChanged(currentPosition, listDisukai.size - currentPosition)

                // Tampilkan pesan
                Toast.makeText(holder.itemView.context, "${removedItem.judul} dihapus dari Disukai", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = listDisukai.size
}
