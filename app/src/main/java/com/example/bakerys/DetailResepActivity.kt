package com.example.bakerys

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bakerys.databinding.ActivityDetailResepBinding
import com.google.android.material.button.MaterialButton

@kotlinx.serialization.InternalSerializationApi
class DetailResepActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailResepBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailResepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val judul = intent.getStringExtra("EXTRA_JUDUL") ?: "No-Bake Pumpkin Cheesecake Bars"
        val gambar = intent.getIntExtra("EXTRA_GAMBAR", R.drawable.cookies)

        binding.tvDetailTitle.text = judul
        binding.ivDetailThumb.setImageResource(gambar)

        isiLangkahMemasak()
        setupTabBahanListeners()
        setupBottomNavigation()

        binding.btnTimer.setOnClickListener {
            TimerDialog().show(supportFragmentManager, "TimerDialog")        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = binding.bottomNavMain
        bottomNav.selectedItemId = R.id.nav_beranda

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    bukaMainActivityDenganTarget("TARGET_BERANDA")
                    true
                }
                R.id.nav_unggah -> {
                    bukaMainActivityDenganTarget("TARGET_UNGGAH")
                    true
                }
                R.id.nav_jelajah -> {
                    bukaMainActivityDenganTarget("TARGET_JELAJAH")
                    true
                }
                R.id.nav_profil -> {
                    bukaMainActivityDenganTarget("TARGET_PROFIL")
                    true
                }
                else -> false
            }
        }
    }

    private fun bukaMainActivityDenganTarget(target: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("EXTRA_NAV_TARGET", target)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    private fun isiLangkahMemasak() {
        binding.tvLangkah1.text = "Campur remahan biskuit graham, mentega cair, dan gula pasir hingga merata."
        binding.tvLangkah2.text = "Tekan adonan biskuit ke dasar loyang secara merata lalu bekukan 10 menit."
        binding.tvLangkah3.text = "Kocok keju krim, labu, gula merah, dan bumbu pai labu sampai halus."
        binding.tvLangkah4.text = "Tuangkan adonan labu di atas lapisan biskuit dan ratakan permukaannya."
        binding.tvLangkah5.text = "Simpan dalam kulkas minimal 4 jam sebelum dipotong dan disajikan."
    }

    private fun setupTabBahanListeners() {
        // Tampilkan bahan utama secara default
        tampilkanBahanUtama()

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            tampilkanBahanUtama()
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 1/4 cangkir biskuit graham\n• 1/3 cangkir mentega cair\n• 2 sdm gula pasir\n• 1 kaleng (15 ons) labu\n• 1 kemasan (8 ons) keju krim\n• 1 sdm gula merah"
            binding.tvBahanKanan.text = "• 2 sdt bumbu pai labu\n• 8 ons cokelat putih\n• 3/4 cangkir krim kental\n• 1/4 cangkir gula halus\n• 1/2 sdt ekstrak vanila"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 150 gr biskuit graham\n• 75 gr mentega cair\n• 30 gr gula pasir\n• 425 gr puree labu\n• 225 gr keju krim\n• 15 gr gula merah"
            binding.tvBahanKanan.text = "• 10 gr bumbu pai labu\n• 225 gr cokelat putih\n• 180 ml krim kental\n• 30 gr gula halus\n• 5 ml ekstrak vanila"
        }
    }

    private fun tampilkanBahanUtama() {
        binding.tvBahanKiri.text = "• Remahan biskuit graham\n• Mentega cair\n• Gula pasir\n• Labu kuning\n• Keju krim\n• Gula merah"
        binding.tvBahanKanan.text = "• Bumbu pai labu\n• Cokelat putih\n• Krim kental\n• Gula halus\n• Ekstrak vanila"
    }

    private fun setActiveTab(selected: MaterialButton, unselected1: MaterialButton, unselected2: MaterialButton) {
        selected.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#802C3E"))
        selected.setTextColor(Color.parseColor("#FFF9F8"))
        selected.strokeWidth = 0

        val unselectedBg = ColorStateList.valueOf(Color.parseColor("#FCEBE5"))
        val unselectedTextColor = Color.parseColor("#802C3E")

        unselected1.backgroundTintList = unselectedBg
        unselected1.setTextColor(unselectedTextColor)
        unselected1.strokeColor = ColorStateList.valueOf(Color.parseColor("#D7B5AE"))
        unselected1.strokeWidth = 1

        unselected2.backgroundTintList = unselectedBg
        unselected2.setTextColor(unselectedTextColor)
        unselected2.strokeColor = ColorStateList.valueOf(Color.parseColor("#D7B5AE"))
        unselected2.strokeWidth = 1
    }
}