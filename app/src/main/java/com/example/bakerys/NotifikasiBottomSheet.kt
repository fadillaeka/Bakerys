package com.example.bakerys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotifikasiBottomSheet : BottomSheetDialogFragment() {

    companion object {
        // Biarkan KOSONG saat awal aplikasi berjalan
        val listNotif = mutableListOf<String>()

        // Fungsi penambah notifikasi
        fun tambahNotif(pesan: String) {
            listNotif.add(0, pesan)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_notifikasi, container, false)

        val btnClose = view.findViewById<ImageView>(R.id.btnCloseNotif)
        btnClose?.setOnClickListener { dismiss() }

        val rvNotif = view.findViewById<RecyclerView>(R.id.rvNotifDialog)
        rvNotif?.layoutManager = LinearLayoutManager(requireContext())
        rvNotif?.adapter = SimpleNotifAdapter(listNotif)

        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    class SimpleNotifAdapter(private val items: List<String>) :
        RecyclerView.Adapter<SimpleNotifAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvNotifText: TextView? = view.findViewById(R.id.tvNotifText)
            val ivNotifIcon: ImageView? = view.findViewById(R.id.ivNotifIcon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notifikasi, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val pesan = items[position]
            holder.tvNotifText?.text = pesan

            if (pesan.contains("mengomentari", ignoreCase = true)) {
                holder.ivNotifIcon?.setImageResource(android.R.drawable.stat_notify_chat)
            } else if (pesan.contains("favorit", ignoreCase = true) || pesan.contains("menyimpan", ignoreCase = true)) {
                holder.ivNotifIcon?.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                holder.ivNotifIcon?.setImageResource(R.drawable.ic_heart_filled)
            }
        }

        override fun getItemCount(): Int = items.size
    }
}