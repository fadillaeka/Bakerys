package com.example.bakerys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set halaman pertama kali aplikasi dibuka (Beranda)
        if (savedInstanceState == null) {
            loadFragment(BerandaFragment())
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_main)

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

    // Fungsi bantuan untuk mengganti Fragment di dalam FrameLayout
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}