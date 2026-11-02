package com.example.bakerys

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JelajahAdapter(
    private val recipeList: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<JelajahAdapter.JelajahViewHolder>() {

    inner class JelajahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumb: ImageView = itemView.findViewById(R.id.ivJelajahThumb)
        val tvTitle: TextView = itemView.findViewById(R.id.tvJelajahTitle)
        val tvDesc: TextView = itemView.findViewById(R.id.tvJelajahDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JelajahViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jelajah_card, parent, false)
        return JelajahViewHolder(view)
    }

    override fun onBindViewHolder(holder: JelajahViewHolder, position: Int) {
        val recipe = recipeList[position]

        holder.ivThumb.setImageResource(recipe.imageResId)
        holder.tvTitle.text = recipe.title
        holder.tvDesc.text = recipe.description

        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int = recipeList.size
}