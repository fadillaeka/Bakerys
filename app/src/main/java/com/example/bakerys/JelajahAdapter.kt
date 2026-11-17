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
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<JelajahAdapter.JelajahViewHolder>() {

    inner class JelajahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumb: ImageView = itemView.findViewById(R.id.ivJelajahThumb)
        val tvTitle: TextView = itemView.findViewById(R.id.tvJelajahTitle)
        val tvDesc: TextView = itemView.findViewById(R.id.tvJelajahDesc)

        val btnLike: ImageView = itemView.findViewById(R.id.btnLike)
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

        if (recipe.imageUri != null) {
            holder.ivThumb.setImageURI(recipe.imageUri)
        } else if (recipe.imageResId != null) {
            holder.ivThumb.setImageResource(recipe.imageResId)
        } else {
            holder.ivThumb.setImageResource(R.drawable.dubai)
        }

        // Animasi kartu muncul halus saat di-scroll
        holder.itemView.alpha = 0f
        holder.itemView.scaleX = 0.94f
        holder.itemView.scaleY = 0.94f
        holder.itemView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .start()

        updateLikeUI(holder.btnLike, recipe.isLiked)

        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }

        holder.btnLike.setOnClickListener {
            animasiMembal(holder.btnLike) // Efek hati membal saat diklik
            recipe.isLiked = !recipe.isLiked
            updateLikeUI(holder.btnLike, recipe.isLiked)
            if (recipe.isLiked) {
                NotifikasiBottomSheet.tambahNotif("Seseorang menyukai postingan Anda.")
            }
        }

        holder.btnComment.setOnClickListener {
            animasiMembal(holder.btnComment) // Efek ikon komentar membal saat diklik
            NotifikasiBottomSheet.tambahNotif("Seseorang mengomentari postingan Anda.")
            val activity = holder.itemView.context as? FragmentActivity
            activity?.let {
                val commentSheet = KomentarBottomSheet(mutableListOf())
                commentSheet.show(it.supportFragmentManager, "KomentarBottomSheet")
            }
        }
    }

    override fun getItemCount(): Int = recipeList.size

    private fun animasiMembal(view: View) {
        view.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(120)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }
            .start()
    }

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