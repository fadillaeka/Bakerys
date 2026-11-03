package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav_main)

        // Set halaman pertama kali aplikasi dibuka (Beranda)
        if (savedInstanceState == null) {
            loadFragment(BerandaFragment())
        }

        // Cek jika ada perintah pindah fragment dari DetailResepActivity
        handleIntentNavigation(intent)

        // Jalankan tes koneksi ke Supabase Database
        cekKoneksiDatabase()

        // Aksi ketika menu di Bottom Navigation diklik
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

    // Fungsi untuk memindahkan fragment & memilih ikon bottom nav secara otomatis
    private fun handleIntentNavigation(intent: Intent?) {
        val target = intent?.getStringExtra("EXTRA_NAV_TARGET") ?: return
        when (target) {
            "TARGET_BERANDA" -> {
                bottomNav.selectedItemId = R.id.nav_beranda
            }
            "TARGET_UNGGAH" -> {
                bottomNav.selectedItemId = R.id.nav_unggah
            }
            "TARGET_JELAJAH" -> {
                bottomNav.selectedItemId = R.id.nav_jelajah
            }
            "TARGET_PROFIL" -> {
                bottomNav.selectedItemId = R.id.nav_profil
            }
        }
    }

    // Fungsi bantuan untuk mengganti Fragment di dalam FrameLayout
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Fungsi tes koneksi ke database Supabase
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
}