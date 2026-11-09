package com.example.bakerys

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DraftAdapter(
    private val draftList: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<DraftAdapter.DraftViewHolder>() {

    inner class DraftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgDraft: ImageView = itemView.findViewById(R.id.imgDraft)
        val tvJudulDraft: TextView = itemView.findViewById(R.id.tvJudulDraft)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_draft_card, parent, false)
        return DraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        val recipe = draftList[position]

        holder.tvJudulDraft.text = recipe.title

        if (recipe.imageUri != null) {
            holder.imgDraft.setImageURI(recipe.imageUri)
        } else if (recipe.imageResId != null) {
            holder.imgDraft.setImageResource(recipe.imageResId)
        } else {
            holder.imgDraft.setImageResource(R.drawable.bakery_mark)
        }

        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int = draftList.size
}