package com.example.bakerys

import android.app.Activity
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
    private val onItemClick: ((Recipe) -> Unit)? = null
) : RecyclerView.Adapter<JelajahAdapter.JelajahViewHolder>() {

    inner class JelajahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumb: ImageView = itemView.findViewById(R.id.ivJelajahThumb)
        val tvTitle: TextView = itemView.findViewById(R.id.tvJelajahTitle)
        val tvDesc: TextView = itemView.findViewById(R.id.tvJelajahDesc)
        val btnLike: ImageView = itemView.findViewById(R.id.btnLike)
        val tvJumlahLike: TextView = itemView.findViewById(R.id.tvJumlahLike)
        val btnComment: ImageView = itemView.findViewById(R.id.btnComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JelajahViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jelajah_card, parent, false)
        return JelajahViewHolder(view)
    }

    override fun onBindViewHolder(holder: JelajahViewHolder, position: Int) {
        val recipe = recipeList[position]

        holder.tvTitle.text = recipe.title
        holder.tvDesc.text = recipe.description
        holder.tvJumlahLike.text = "${recipe.likeCount}"

        try {
            if (recipe.imageUri != null) {
                holder.ivThumb.setImageURI(recipe.imageUri)
            } else if (recipe.imageResId != null) {
                holder.ivThumb.setImageResource(recipe.imageResId)
            } else {
                holder.ivThumb.setImageResource(R.drawable.dubai)
            }
            if (holder.ivThumb.drawable == null) {
                holder.ivThumb.setImageResource(recipe.imageResId ?: R.drawable.dubai)
            }
        } catch (e: Exception) {
            holder.ivThumb.setImageResource(recipe.imageResId ?: R.drawable.dubai)
        }

        updateLikeUI(holder.btnLike, recipe.isLiked)

        // Hanya bisa melihat postingan, memberi like, dan berkomentar (tidak bisa membuka detail resep)
        holder.itemView.setOnClickListener(null)
        holder.itemView.isClickable = false

        holder.btnLike.setOnClickListener {
            val animPulse = android.view.animation.AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_pulse)
            holder.btnLike.startAnimation(animPulse)

            recipe.isLiked = !recipe.isLiked
            if (recipe.isLiked) {
                recipe.likeCount++
            } else if (recipe.likeCount > 0) {
                recipe.likeCount--
            }

            updateLikeUI(holder.btnLike, recipe.isLiked)
            holder.tvJumlahLike.text = "${recipe.likeCount}"

            val targetId = if (recipe.id > 0) recipe.id.toLong() else 11L
            BakeryStorageManager.simpanSukaKeSupabase(
                holder.itemView.context,
                targetId,
                recipe.isLiked
            ) { serverCount ->
                recipe.likeCount = serverCount
                (holder.itemView.context as? Activity)?.runOnUiThread {
                    holder.tvJumlahLike.text = "$serverCount"
                }
            }

            if (recipe.isLiked) {
                NotifikasiBottomSheet.tambahNotif("Seseorang menyukai postingan Anda.")
            }
        }

        holder.btnComment.setOnClickListener {
            NotifikasiBottomSheet.tambahNotif("Seseorang mengomentari postingan Anda.")
            val activity = holder.itemView.context as? FragmentActivity
            activity?.let {
                val targetResepId = if (recipe.id > 0) recipe.id.toLong() else 11L
                val commentSheet = KomentarBottomSheet(mutableListOf(), targetResepId)
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
}
