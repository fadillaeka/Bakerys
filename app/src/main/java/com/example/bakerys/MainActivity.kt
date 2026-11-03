package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@kotlinx.serialization.InternalSerializationApi

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav_main)

        if (savedInstanceState == null) {
            loadFragment(BerandaFragment())
        }

        handleIntentNavigation(intent)
        cekKoneksiDatabase()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    loadFragment(BerandaFragment())
                    true
                }
                R.id.nav_unggah -> {
                    loadFragment(UnggahFragment())
                    true
                }
                R.id.nav_jelajah -> {
                    loadFragment(JelajahFragment())
                    true
                }
                R.id.nav_profil -> {
                    loadFragment(ProfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntentNavigation(intent)
    }

    private fun handleIntentNavigation(intent: Intent?) {
        val target = intent?.getStringExtra("EXTRA_NAV_TARGET") ?: return
        when (target) {
            "TARGET_BERANDA" -> bottomNav.selectedItemId = R.id.nav_beranda
            "TARGET_UNGGAH" -> bottomNav.selectedItemId = R.id.nav_unggah
            "TARGET_JELAJAH" -> bottomNav.selectedItemId = R.id.nav_jelajah
            "TARGET_PROFIL" -> bottomNav.selectedItemId = R.id.nav_profil
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun cekKoneksiDatabase() {
        lifecycleScope.launch {
            try {
                val listKategori = SupabaseClient.instance.from("kategori")
                    .select()
                    .decodeList<Kategori>()

                Log.d("SUPABASE_TEST", "Koneksi Berhasil! Jumlah Kategori: ${listKategori.size}")
            } catch (e: Exception) {
                Log.e("SUPABASE_ERROR", "Gagal konek ke database: ${e.message}")
            }
        }
    }

    // FUNGSI INSERT DATA LANGSUNG KE SUPABASE (Tanpa Butuh Plugin Serialization)
    fun tambahDataDatabase(namaResep: String, deskripsiResep: String) {
        lifecycleScope.launch {
            try {
                val dataJson = buildJsonObject {
                    put("nama_resep", namaResep)
                    put("deskripsi", deskripsiResep)
                }

                SupabaseClient.instance.from("resep").insert(dataJson)

                Log.d("SUPABASE_INSERT", "Data berhasil ditambahkan!")
                Toast.makeText(this@MainActivity, "Data Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("SUPABASE_INSERT_ERROR", "Gagal simpan data: ${e.message}")
                Toast.makeText(this@MainActivity, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun logoutUser() {
        Toast.makeText(this, "Berhasil Log Out", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    // Tambahkan di dalam MainActivity
    fun bukaUnggahDenganData(recipe: Recipe) {
        val fragment = UnggahFragment().apply {
            arguments = Bundle().apply {
                putInt("DRAFT_ID", recipe.id)
                putString("DRAFT_TITLE", recipe.title)
                putString("DRAFT_DESC", recipe.description)
                recipe.imageUri?.let { putString("DRAFT_IMAGE_URI", it.toString()) }
                recipe.imageResId?.let { putInt("DRAFT_IMAGE_RES", it) }
            }
        }

        // Ganti fragment aktif ke UnggahFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Sesuaikan ID container fragment di activity_main.xml kamu
            .commit()

        // Sesuaikan BottomNavigationView agar tab Unggah terpilih (jika ada)
        // binding.bottomNavigation.selectedItemId = R.id.nav_unggah
    }
}
