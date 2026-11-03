package com.example.bakerys

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

// Data Model khusus untuk item Draft
data class DraftModel(
    val judul: String,
    val gambar: Int
)

class DraftAdapter(
    private val listDraft: List<DraftModel>,
    private val onItemClick: (DraftModel) -> Unit
) : RecyclerView.Adapter<DraftAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgDraft: ImageView = itemView.findViewById(R.id.imgDraft)
        val tvJudulDraft: TextView = itemView.findViewById(R.id.tvJudulDraft)
        val btnLihatResep: MaterialButton = itemView.findViewById(R.id.btnLihatResep)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // DI SINI KUNCI PERUBAHANNYA: Meng-inflate item_draft_card.xml
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_draft_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listDraft[position]

        holder.tvJudulDraft.text = item.judul
        holder.imgDraft.setImageResource(item.gambar)

        holder.btnLihatResep.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = listDraft.size
}