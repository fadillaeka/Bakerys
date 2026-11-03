package com.example.bakerys

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KomentarBottomSheet(
    private val commentsList: MutableList<CommentItem>
) : BottomSheetDialogFragment() {

    private lateinit var adapter: KomentarAdapter

    // Palet variasi warna avatar
    private val avatarColors = listOf(
        Color.parseColor("#B8383B"),
        Color.parseColor("#D87070"),
        Color.parseColor("#E57373"),
        Color.parseColor("#C62828"),
        Color.parseColor("#6D1B1B"),
        Color.parseColor("#E53935")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_komentar, container, false)

        val btnClose = view.findViewById<ImageView>(R.id.btnCloseComment)
        val etComment = view.findViewById<EditText>(R.id.etAddComment)
        val btnSend = view.findViewById<ImageView>(R.id.btnSendComment)
        val rvComments = view.findViewById<RecyclerView>(R.id.rvComments)

        adapter = KomentarAdapter(commentsList)
        rvComments?.layoutManager = LinearLayoutManager(context)
        rvComments?.adapter = adapter

        btnClose?.setOnClickListener {
            dismiss()
        }

        fun sendComment() {
            val text = etComment?.text?.toString()?.trim().orEmpty()
            if (text.isNotEmpty()) {
                val nextProfileNumber = adapter.itemCount + 1
                val username = "Profile $nextProfileNumber"
                val avatarColor = avatarColors[adapter.itemCount % avatarColors.size]

                val newComment = CommentItem(username, text, avatarColor)
                adapter.addComment(newComment)

                etComment?.text?.clear()
                rvComments?.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }

        btnSend?.setOnClickListener {
            sendComment()
        }

        etComment?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendComment()
                true
            } else {
                false
            }
        }

        return view
    }
}