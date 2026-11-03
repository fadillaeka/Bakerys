package com.example.bakerys

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class JelajahAdapter(
    private val recipeList: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit,
    private val onBookmarkClick: (Recipe) -> Unit
) : RecyclerView.Adapter<JelajahAdapter.JelajahViewHolder>() {

    inner class JelajahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumb: ImageView = itemView.findViewById(R.id.ivJelajahThumb)
        val tvTitle: TextView = itemView.findViewById(R.id.tvJelajahTitle)
        val tvDesc: TextView = itemView.findViewById(R.id.tvJelajahDesc)

        val btnLike: ImageView = itemView.findViewById(R.id.btnLike)
        val btnComment: ImageView = itemView.findViewById(R.id.btnComment)
        val btnBookmark: ImageView = itemView.findViewById(R.id.btnBookmark)
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

        updateLikeUI(holder.btnLike, recipe.isLiked)
        updateBookmarkUI(holder.btnBookmark, recipe.isBookmarked)

        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }

        holder.btnLike.setOnClickListener {
            NotifikasiBottomSheet.tambahNotif("Seseorang menyukai resep Anda.")
            recipe.isLiked = !recipe.isLiked
            updateLikeUI(holder.btnLike, recipe.isLiked)
        }

        holder.btnBookmark.setOnClickListener {
            NotifikasiBottomSheet.tambahNotif("Seseorang menambahkan resep Anda ke favorit.")
            recipe.isBookmarked = !recipe.isBookmarked
            updateBookmarkUI(holder.btnBookmark, recipe.isBookmarked)
            onBookmarkClick(recipe)
        }

        holder.btnComment.setOnClickListener {
            NotifikasiBottomSheet.tambahNotif("Seseorang mengomentari resep Anda.")
            val activity = holder.itemView.context as? FragmentActivity
            activity?.let {
                val commentSheet = KomentarBottomSheet(recipe.comments)
                commentSheet.show(it.supportFragmentManager, "KomentarBottomSheet")
            }
        }
    }

    override fun getItemCount(): Int = recipeList.size

    private fun updateLikeUI(imageView: ImageView, isLiked: Boolean) {
        if (isLiked) {
            imageView.setImageResource(R.drawable.ic_heart_filled)
            imageView.imageTintList = null
        } else {
            imageView.setImageResource(R.drawable.ic_heart_outline)
            imageView.imageTintList = ColorStateList.valueOf(Color.parseColor("#3E2723"))
        }
    }

    private fun updateBookmarkUI(imageView: ImageView, isBookmarked: Boolean) {
        if (isBookmarked) {
            imageView.setImageResource(R.drawable.ic_bookmark_card)
            imageView.imageTintList = ColorStateList.valueOf(Color.parseColor("#791E2A"))
        } else {
            imageView.setImageResource(R.drawable.ic_bookmark_card)
            imageView.imageTintList = ColorStateList.valueOf(Color.parseColor("#3E2723"))
        }
    }
}