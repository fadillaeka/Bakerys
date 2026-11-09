package com.example.bakerys

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

@kotlinx.serialization.InternalSerializationApi
class TersimpanAdapter(
    private val bookmarkedList: MutableList<Recipe>,
    private val onItemClick: (Recipe) -> Unit,
    private val onRemoveBookmark: (Recipe, Int) -> Unit
) : RecyclerView.Adapter<TersimpanAdapter.TersimpanViewHolder>() {

    inner class TersimpanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumb: ImageView = itemView.findViewById(R.id.ivTersimpanThumb)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTersimpanTitle)
        val btnRemove: ImageView = itemView.findViewById(R.id.btnRemoveBookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TersimpanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tersimpan_card, parent, false)
        return TersimpanViewHolder(view)
    }

    override fun onBindViewHolder(holder: TersimpanViewHolder, position: Int) {
        val recipe = bookmarkedList[position]

        holder.tvTitle.text = recipe.title

        if (recipe.imageUri != null) {
            holder.ivThumb.setImageURI(recipe.imageUri)
        } else if (recipe.imageResId != null) {
            holder.ivThumb.setImageResource(recipe.imageResId)
        } else {
            holder.ivThumb.setImageResource(R.drawable.dubai)
        }

        // Hapus dari bookmark
        holder.btnRemove.setOnClickListener {
            onRemoveBookmark(recipe, holder.adapterPosition)
        }

        // Klik item resep -> Buka DetailResepActivity
        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int = bookmarkedList.size
}