package com.example.bakerys

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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

        holder.ivThumb.setImageResource(recipe.imageResId)
        holder.tvTitle.text = recipe.title

        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }

        holder.btnRemove.setOnClickListener {
            onRemoveBookmark(recipe, holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int = bookmarkedList.size
}