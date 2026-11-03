package com.example.bakerys

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

data class CommentItem(
    val username: String,
    val text: String,
    val avatarColor: Int = Color.parseColor("#B8383B")
)

class KomentarAdapter(
    private val commentList: MutableList<CommentItem>
) : RecyclerView.Adapter<KomentarAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUser: TextView = itemView.findViewById(R.id.tvCommentUser)
        val tvText: TextView = itemView.findViewById(R.id.tvCommentText)
        val cvAvatar: MaterialCardView = itemView.findViewById(R.id.cvAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_komentar, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.tvUser.text = comment.username
        holder.tvText.text = comment.text
        holder.cvAvatar.setCardBackgroundColor(comment.avatarColor)
    }

    override fun getItemCount(): Int = commentList.size

    fun addComment(comment: CommentItem) {
        commentList.add(comment)
        notifyItemInserted(commentList.size - 1)
    }
}