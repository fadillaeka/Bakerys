package com.example.bakerys

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class DraftAdapter(
    private val draftList: MutableList<Recipe>,
    private val onItemClick: (Recipe) -> Unit,
    private val onItemDeleted: ((Recipe) -> Unit)? = null
) : RecyclerView.Adapter<DraftAdapter.DraftViewHolder>() {

    inner class DraftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgDraft: ImageView = itemView.findViewById(R.id.imgDraft)
        val tvJudulDraft: TextView = itemView.findViewById(R.id.tvJudulDraft)
        val btnHapusDraft: MaterialButton? = itemView.findViewById(R.id.btnHapusDraft)
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

        // Aksi Tombol Hapus Draft dengan Konfirmasi
        holder.btnHapusDraft?.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                val context = holder.itemView.context
                AlertDialog.Builder(context)
                    .setTitle("Hapus Draft")
                    .setMessage("Apakah Anda yakin ingin menghapus draft \"${recipe.title}\"?")
                    .setPositiveButton("Hapus") { _, _ ->
                        val itemToRemove = draftList[currentPos]
                        draftList.removeAt(currentPos)
                        RecipeRepository.draftList.removeAll { it.id == itemToRemove.id }
                        notifyItemRemoved(currentPos)
                        notifyItemRangeChanged(currentPos, draftList.size - currentPos)
                        onItemDeleted?.invoke(itemToRemove)
                        Toast.makeText(context, "Draft berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int = draftList.size
}