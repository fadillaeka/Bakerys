@file:OptIn(
    io.github.jan.supabase.annotations.SupabaseExperimental::class,
    kotlinx.serialization.InternalSerializationApi::class
)
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

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav_main)

        if (savedInstanceState == null) {
            loadFragment(BerandaFragment())
        }

        // Sibak tirai kue membuka halaman Beranda jika baru selesai Login
        val pastryTransition = findViewById<PastryTransitionView>(R.id.pastryTransitionView)
        if (intent.getBooleanExtra("FROM_LOGIN", false)) {
            pastryTransition?.animateReveal()
        }

        handleIntentNavigation(intent)
        inisialisasiDataDatabase()
        cekKoneksiDatabase()

        var currentTabIndex = 0

        fun bukaTab(targetIndex: Int, fragment: Fragment) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

            val isDiChildProfil = currentFragment is DraftFragment ||
                                  currentFragment is DisukaiFragment ||
                                  currentFragment is EditProfileFragment

            if (targetIndex != currentTabIndex || (targetIndex == 3 && isDiChildProfil)) {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }

                val animEnter = if (targetIndex >= currentTabIndex) R.anim.slide_in_right else R.anim.slide_in_left
                val animExit = if (targetIndex >= currentTabIndex) R.anim.slide_out_left else R.anim.slide_out_right

                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(animEnter, animExit)
                    .replace(R.id.fragment_container, fragment)
                    .commit()

                currentTabIndex = targetIndex
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            val targetIndex = when (item.itemId) {
                R.id.nav_beranda -> 0
                R.id.nav_unggah -> 1
                R.id.nav_jelajah -> 2
                R.id.nav_profil -> 3
                else -> 0
            }

            val fragment = when (item.itemId) {
                R.id.nav_beranda -> BerandaFragment()
                R.id.nav_unggah -> UnggahFragment()
                R.id.nav_jelajah -> JelajahFragment()
                R.id.nav_profil -> ProfilFragment()
                else -> BerandaFragment()
            }

            bukaTab(targetIndex, fragment)
            true
        }

        // KETIKA TAB PROFIL SUDAH AKTIF LALU DIKLIK KEMBALI DI NAVBAR (Misal saat buka Draft, Disukai, atau Edit Profile)
        bottomNav.setOnItemReselectedListener { item ->
            if (item.itemId == R.id.nav_profil) {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment !is ProfilFragment) {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, ProfilFragment())
                        .commit()
                }
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
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun inisialisasiDataDatabase() {
        val disukaiTersimpan = BakeryStorageManager.muatDaftarDisukai(this)
        if (disukaiTersimpan.isNotEmpty()) {
            DisukaiFragment.daftarResepDisukai.clear()
            DisukaiFragment.daftarResepDisukai.addAll(disukaiTersimpan)
        }

        val draftTersimpan = BakeryStorageManager.muatDaftarDraft(this)
        if (draftTersimpan.isNotEmpty()) {
            RecipeRepository.draftList.clear()
            RecipeRepository.draftList.addAll(draftTersimpan)
        }

        val bookmarkTersimpan = BakeryStorageManager.muatDaftarTersimpan(this)
        if (bookmarkTersimpan.isNotEmpty()) {
            RecipeRepository.resepTersimpanList.clear()
            RecipeRepository.resepTersimpanList.addAll(bookmarkTersimpan)
            for (bm in bookmarkTersimpan) {
                RecipeRepository.recipeList.find { it.id == bm.id || it.title == bm.title }?.isBookmarked = true
            }
        }
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

    // FUNGSI INSERT DATA LANGSUNG KE SUPABASE
    fun tambahDataDatabase(namaResep: String, deskripsiResep: String) {
        lifecycleScope.launch {
            try {
                val dataJson = buildJsonObject {
                    put("judul", namaResep)
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    // MEMBUKA UNGGAH FRAGMENT DENGAN DATA DRAFT (Mendukung segala tipe objek Recipe/ResepModel)
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

        // Pindah ke Fragment Unggah dan aktifkan tab Bottom Navigation
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        bottomNav.selectedItemId = R.id.nav_unggah
    }
}