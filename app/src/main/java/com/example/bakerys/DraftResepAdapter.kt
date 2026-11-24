package com.example.bakerys

import android.app.AlertDialog
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class DraftResepAdapter(
    private val draftList: MutableList<DraftResep>,
    private val onItemClick: (DraftResep) -> Unit,
    private val onItemDeleted: ((DraftResep) -> Unit)? = null
) : RecyclerView.Adapter<DraftResepAdapter.DraftResepViewHolder>() {

    inner class DraftResepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgDraft: ImageView = itemView.findViewById(R.id.imgDraft)
        val tvJudulDraft: TextView = itemView.findViewById(R.id.tvJudulDraft)
        val btnHapusDraft: MaterialButton? = itemView.findViewById(R.id.btnHapusDraft)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftResepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_draft_card, parent, false)
        return DraftResepViewHolder(view)
    }

    override fun onBindViewHolder(holder: DraftResepViewHolder, position: Int) {
        val draft = draftList[position]

        holder.tvJudulDraft.text = draft.judul ?: "Tanpa Judul"

        val gambarStr = draft.gambar
        var imgLoaded = false
        if (!gambarStr.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(gambarStr)
                holder.imgDraft.setImageURI(uri)
                if (holder.imgDraft.drawable != null) {
                    imgLoaded = true
                }
            } catch (e: Exception) {
                imgLoaded = false
            }
        }

        if (!imgLoaded) {
            holder.imgDraft.setImageResource(R.drawable.bakery_mark)
        }

        holder.btnHapusDraft?.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                val context = holder.itemView.context
                AlertDialog.Builder(context)
                    .setTitle("Hapus Draft Resep")
                    .setMessage("Apakah Anda yakin ingin menghapus draft resep \"${draft.judul}\"?")
                    .setPositiveButton("Hapus") { _, _ ->
                        val itemToRemove = draftList[currentPos]
                        draftList.removeAt(currentPos)
                        notifyItemRemoved(currentPos)
                        notifyItemRangeChanged(currentPos, draftList.size - currentPos)
                        onItemDeleted?.invoke(itemToRemove)
                        Toast.makeText(context, "Draft resep berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(draft)
        }
    }

    override fun getItemCount(): Int = draftList.size
}
