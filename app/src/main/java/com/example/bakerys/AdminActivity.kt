package com.example.bakerys

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private lateinit var rvAdmin: RecyclerView
    private var pbLoadingAdmin: ProgressBar? = null
    private var tvEmptyAdmin: TextView? = null
    private lateinit var btnTabKonten: MaterialButton
    private lateinit var btnTabUser: MaterialButton

    private val kontenList = mutableListOf<KontenModerasi>()
    private val userList = mutableListOf<UserModerasi>()
    private var currentTab = 0 // 0: Konten, 1: User

    private lateinit var kontenAdapter: KontenModerasiAdapter
    private lateinit var userAdapter: UserModerasiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. CEK HAK AKSES ADMIN (Hanya akun belanjasapi & wapa)
        val sharedPref = getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE)
        val savedName = sharedPref.getString("profile_name", "") ?: ""
        val savedRole = sharedPref.getString("profile_role", "") ?: ""
        val isAdmin = sharedPref.getBoolean("is_admin", false) ||
                      savedRole.equals("admin", ignoreCase = true) ||
                      savedName.equals("belanjasapi", ignoreCase = true) ||
                      savedName.equals("wapa", ignoreCase = true)

        if (!isAdmin) {
            Toast.makeText(this, "⚠️ Akses ditolak: Hanya admin (belanjasapi & wapa) yang dapat mengakses Panel Moderator!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContentView(R.layout.activity_admin)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack?.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val btnRefresh = findViewById<ImageView>(R.id.btnRefreshAdmin)
        btnRefresh?.setOnClickListener {
            loadData()
        }

        btnTabKonten = findViewById(R.id.btnTabKonten)
        btnTabUser = findViewById(R.id.btnTabUser)
        rvAdmin = findViewById(R.id.rvAdmin)
        pbLoadingAdmin = findViewById(R.id.pbLoadingAdmin)
        tvEmptyAdmin = findViewById(R.id.tvEmptyAdmin)

        rvAdmin.layoutManager = LinearLayoutManager(this)

        kontenAdapter = KontenModerasiAdapter(
            kontenList = kontenList,
            onHideClick = { konten ->
                if (konten.tipe == "RESEP" && konten.rawId != null) {
                    val isHidden = konten.status == "hidden"
                    val newHiddenState = !isHidden
                    lifecycleScope.launch {
                        val success = BakeryStorageManager.sembunyikanResepModerasi(konten.rawId, newHiddenState)
                        if (success) {
                            Toast.makeText(this@AdminActivity, if (newHiddenState) "Resep disembunyikan" else "Resep ditampilkan", Toast.LENGTH_SHORT).show()
                            loadData()
                        } else {
                            Toast.makeText(this@AdminActivity, "Gagal mengubah status resep", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            onDeleteClick = { konten ->
                AlertDialog.Builder(this)
                    .setTitle("Hapus Konten")
                    .setMessage("Yakin ingin menghapus ${konten.tipe}: \"${konten.judul}\"?")
                    .setPositiveButton("Hapus") { _, _ ->
                        lifecycleScope.launch {
                            val success = BakeryStorageManager.hapusKontenModerasi(konten.tipe, konten.rawId)
                            if (success) {
                                // Segera sinkronkan penghapusan ke RecipeRepository agar konten langsung hilang dari Jelajah
                                RecipeRepository.hapusPostingan(konten.rawId?.toInt() ?: 0, konten.judul)
                                Toast.makeText(this@AdminActivity, "Konten berhasil dihapus", Toast.LENGTH_SHORT).show()
                                loadData()
                            } else {
                                Toast.makeText(this@AdminActivity, "Gagal menghapus konten", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )

        userAdapter = UserModerasiAdapter(
            userList = userList,
            onBlockClick = { user ->
                val isBlocked = user.role == "blocked"
                val newBlockState = !isBlocked
                val actionName = if (newBlockState) "memblokir" else "membuka blokir"
                AlertDialog.Builder(this)
                    .setTitle("Manajemen User")
                    .setMessage("Yakin ingin $actionName user ${user.username}?")
                    .setPositiveButton("Ya") { _, _ ->
                        lifecycleScope.launch {
                            val success = BakeryStorageManager.ubahStatusBlokirUser(user.id, newBlockState)
                            if (success) {
                                Toast.makeText(this@AdminActivity, "Status user berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                loadData()
                            } else {
                                Toast.makeText(this@AdminActivity, "Gagal memperbarui status user", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )

        btnTabKonten.setOnClickListener {
            currentTab = 0
            updateTabUI()
            rvAdmin.adapter = kontenAdapter
            updateEmptyState()
        }

        btnTabUser.setOnClickListener {
            currentTab = 1
            updateTabUI()
            rvAdmin.adapter = userAdapter
            updateEmptyState()
        }

        rvAdmin.adapter = kontenAdapter
        loadData()
    }

    private fun updateTabUI() {
        if (currentTab == 0) {
            btnTabKonten.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#802C3E"))
            btnTabKonten.setTextColor(Color.WHITE)
            btnTabUser.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EED5CE"))
            btnTabUser.setTextColor(Color.parseColor("#802C3E"))
        } else {
            btnTabUser.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#802C3E"))
            btnTabUser.setTextColor(Color.WHITE)
            btnTabKonten.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EED5CE"))
            btnTabKonten.setTextColor(Color.parseColor("#802C3E"))
        }
    }

    private fun loadData() {
        pbLoadingAdmin?.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                if (currentTab == 0) {
                    val list = BakeryStorageManager.ambilSemuaKontenModerasi()
                    kontenList.clear()
                    kontenList.addAll(list)
                    kontenAdapter.notifyDataSetChanged()
                } else {
                    val list = BakeryStorageManager.ambilSemuaUserModerasi()
                    userList.clear()
                    userList.addAll(list)
                    userAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AdminActivity, "Gagal memuat data moderator", Toast.LENGTH_SHORT).show()
            } finally {
                pbLoadingAdmin?.visibility = View.GONE
                updateEmptyState()
            }
        }
    }

    private fun updateEmptyState() {
        val isEmpty = if (currentTab == 0) kontenList.isEmpty() else userList.isEmpty()
        if (isEmpty) {
            tvEmptyAdmin?.visibility = View.VISIBLE
            rvAdmin?.visibility = View.GONE
        } else {
            tvEmptyAdmin?.visibility = View.GONE
            rvAdmin?.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    class KontenModerasiAdapter(
        private val kontenList: List<KontenModerasi>,
        private val onHideClick: (KontenModerasi) -> Unit,
        private val onDeleteClick: (KontenModerasi) -> Unit
    ) : RecyclerView.Adapter<KontenModerasiAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTipeKonten: TextView = view.findViewById(R.id.tvTipeKonten)
            val tvStatusKonten: TextView = view.findViewById(R.id.tvStatusKonten)
            val tvJudulKonten: TextView = view.findViewById(R.id.tvJudulKonten)
            val tvDeskripsiKonten: TextView = view.findViewById(R.id.tvDeskripsiKonten)
            val btnSembunyikanKonten: MaterialButton = view.findViewById(R.id.btnSembunyikanKonten)
            val btnHapusKonten: MaterialButton = view.findViewById(R.id.btnHapusKonten)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val konten = kontenList[position]
            holder.tvTipeKonten.text = konten.tipe
            holder.tvJudulKonten.text = konten.judul
            holder.tvDeskripsiKonten.text = konten.deskripsi
            holder.tvStatusKonten.text = konten.status

            if (konten.tipe == "RESEP") {
                holder.btnSembunyikanKonten.visibility = View.VISIBLE
                if (konten.status == "hidden") {
                    holder.btnSembunyikanKonten.text = "Tampilkan"
                    holder.tvStatusKonten.setTextColor(Color.parseColor("#FF9800"))
                } else {
                    holder.btnSembunyikanKonten.text = "Sembunyikan"
                    holder.tvStatusKonten.setTextColor(Color.parseColor("#4CAF50"))
                }
            } else {
                holder.btnSembunyikanKonten.visibility = View.GONE
                holder.tvStatusKonten.setTextColor(Color.parseColor("#4CAF50"))
            }

            holder.btnSembunyikanKonten.setOnClickListener { onHideClick(konten) }
            holder.btnHapusKonten.setOnClickListener { onDeleteClick(konten) }
        }

        override fun getItemCount(): Int = kontenList.size
    }

    class UserModerasiAdapter(
        private val userList: List<UserModerasi>,
        private val onBlockClick: (UserModerasi) -> Unit
    ) : RecyclerView.Adapter<UserModerasiAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivFotoUser: ImageView = view.findViewById(R.id.ivFotoUser)
            val tvUsername: TextView = view.findViewById(R.id.tvUsername)
            val tvEmailUser: TextView = view.findViewById(R.id.tvEmailUser)
            val tvRoleUser: TextView = view.findViewById(R.id.tvRoleUser)
            val btnBlokirUser: MaterialButton = view.findViewById(R.id.btnBlokirUser)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_user, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val user = userList[position]
            holder.tvUsername.text = user.username.ifEmpty { user.namaLengkap ?: "Tanpa Nama" }
            holder.tvEmailUser.text = user.email
            holder.tvRoleUser.text = "Role: ${user.role}"

            if (user.role == "blocked") {
                holder.btnBlokirUser.text = "Buka Blokir"
                holder.btnBlokirUser.setTextColor(Color.parseColor("#4CAF50"))
                holder.tvRoleUser.setTextColor(Color.parseColor("#E53935"))
            } else {
                holder.btnBlokirUser.text = "Blokir"
                holder.btnBlokirUser.setTextColor(Color.parseColor("#E53935"))
                holder.tvRoleUser.setTextColor(Color.parseColor("#3E2723"))
            }

            val foto = user.fotoProfil
            if (!foto.isNullOrEmpty()) {
                try {
                    holder.ivFotoUser.setImageURI(Uri.parse(foto))
                } catch (e: Exception) {
                    holder.ivFotoUser.setImageResource(R.drawable.bakery_mark)
                }
            } else {
                holder.ivFotoUser.setImageResource(R.drawable.bakery_mark)
            }

            holder.btnBlokirUser.setOnClickListener { onBlockClick(user) }
        }

        override fun getItemCount(): Int = userList.size
    }
}
