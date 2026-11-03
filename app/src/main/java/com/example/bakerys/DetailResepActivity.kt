package com.example.bakerys

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bakerys.databinding.ActivityDetailResepBinding
import com.google.android.material.button.MaterialButton

class DetailResepActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailResepBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailResepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val judul = intent.getStringExtra("EXTRA_JUDUL") ?: "Dubai Chewy Cookies"
        val gambar = intent.getIntExtra("EXTRA_GAMBAR", R.drawable.dubai)
        val waktu = intent.getStringExtra("EXTRA_WAKTU") ?: "⏱️ 1h 15m"
        val tentang = intent.getStringExtra("EXTRA_TENTANG") ?: "Resep olahan lezat yang mudah dibuat di rumah."

        binding.tvDetailTitle.text = judul
        binding.ivDetailThumb.setImageResource(gambar)
        binding.tvWaktuMemasak.text = waktu
        binding.tvTentangResep.text = tentang

        // Percabangan Memuat Resep Dinamis Sesuai Judul
        when {
            judul.contains("Dubai", ignoreCase = true) -> muatResepDubaiCookies()
            judul.contains("Caramel", ignoreCase = true) || judul.contains("Apple", ignoreCase = true) -> muatResepCaramelApple()
            judul.contains("Sourdough", ignoreCase = true) -> muatResepSourdough()
            judul.contains("Lava", ignoreCase = true) -> muatResepLavaCake()
            judul.contains("Rainbow", ignoreCase = true) -> muatResepRainbowCake()
            judul.contains("Fudgy", ignoreCase = true) || judul.contains("Brownies", ignoreCase = true) -> muatResepFudgyBrownies()
            judul.contains("Cookies", ignoreCase = true) -> muatResepCookies()
            judul.contains("Cheesecake", ignoreCase = true) -> muatResepCheesecake()
            else -> muatResepCheesecake()
        }

        setupBottomNavigation()

        binding.btnTimer.setOnClickListener {
            TimerDialog().show(supportFragmentManager, "TimerDialog")
        }
    }

    // =========================================================================
    // 1. DUBAI CHEWY COOKIES (VERSI KUNAFA RUMAHAN)
    // =========================================================================
    private fun muatResepDubaiCookies() {
        binding.tvLangkah1.text = "Lelehkan cokelat putih couverture dengan metode tim. Campurkan dengan kunafa panggang, pasta pistachio, dan pistachio cincang hingga merata."
        binding.tvLangkah2.text = "Bagi adonan isi menjadi 8 bagian sama besar, padatkan dan bentuk bulat, lalu simpan di kulkas selama 30 menit agar set."
        binding.tvLangkah3.text = "Lelehkan mentega dan marshmallow di teflon api kecil hingga halus. Masukkan susu bubuk skim dan bubuk kakao, aduk cepat sampai elastis."
        binding.tvLangkah4.text = "Ratakan adonan marshmallow di atas loyang beralas kertas roti. Ambil adonan isi dari kulkas, lalu bungkus rapat dengan lapisan chewy cokelat."
        binding.tvLangkah5.text = "Gulingkan di atas bubuk kakao hingga tertutup rata, dinginkan di kulkas 20–30 menit, dan sajikan pada suhu ruang agar tekstur kenyalnya maksimal."

        binding.tvBahanPengganti.text = "• Kunafa panggang dapat diganti dengan pastry Kataifi sangrai atau mi kunafa halus.\n• Pasta pistachio dapat diganti dengan selai kacang atau pasta hazelnut.\n• Susu bubuk skim dapat diganti dengan susu bubuk full cream."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Kunafa panggang\n• Pasta pistachio\n• Cokelat putih couverture\n• Pistachio cincang"
            binding.tvBahanKanan.text = "• Marshmallow\n• Mentega tawar\n• Susu bubuk skim\n• Bubuk kakao (isi & topping)"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 1/2 cangkir kunafa\n• 1/2 cangkir pasta pistachio\n• 2 sdm cokelat putih leleh\n• 2 sdm pistachio cincang"
            binding.tvBahanKanan.text = "• 2 cangkir marshmallow\n• 2 sdm mentega tawar\n• 1 sdm susu bubuk skim\n• 2 sdm bubuk kakao"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 160 gr kunafa panggang\n• 135 gr pasta pistachio\n• 32 gr cokelat putih couverture\n• 20 gr pistachio cincang"
            binding.tvBahanKanan.text = "• 130 gr marshmallow\n• 30 gr mentega tawar\n• 12 gr susu bubuk skim\n• 18 gr bubuk kakao + topping secukupnya"
        }

        binding.btnTabBahanUtama.performClick()
    }

    // =========================================================================
    // 2. CARAMEL APPLE MAGIC FRIDGE CAKE (ISABELLE ESTER)
    // =========================================================================
    private fun muatResepCaramelApple() {
        binding.tvLangkah1.text = "Kocok krim kental, bumbu pai apel, dan 3 sdm saus karamel dengan mikser kecepatan sedang hingga membentuk puncak kaku (sekitar 2 menit)."
        binding.tvLangkah2.text = "Hancurkan 6 biskuit graham dalam mangkuk untuk taburan sekeliling. Oleskan 1/3 cangkir krim kocok di dasar piring panjang."
        binding.tvLangkah3.text = "Olesi setiap biskuit dengan 1 sdm krim kocok dan 1 sdt selai apel, lalu susun tegak secara vertikal di piring hingga membentuk gulungan kue."
        binding.tvLangkah4.text = "Tutup rapat seluruh permukaan atas dan samping gulungan dengan sisa krim kocok, lalu simpan di kulkas selama 6–8 jam."
        binding.tvLangkah5.text = "Siram kue dingin dengan sisa 1 sdm saus karamel, baluri sekeliling kue dengan remah biskuit graham, lalu iris secara diagonal."

        binding.tvBahanPengganti.text = "• Biskuit graham rasa kayu manis dapat diganti biskuit gandum atau biskuit renyah rasa vanila."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Krim kental\n• Bumbu pai apel\n• Saus karamel (Smucker's®)"
            binding.tvBahanKanan.text = "• Biskuit graham kayu manis\n• Selai apel"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 1/2 cangkir krim kental\n• 1/2 sdt bumbu pai apel\n• 1/4 cangkir saus karamel"
            binding.tvBahanKanan.text = "• 28 biskuit graham kayu manis\n• 1/2 cangkir selai apel"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 360 ml krim kental\n• 2 gr bumbu pai apel\n• 60 ml saus karamel"
            binding.tvBahanKanan.text = "• 280 gr biskuit graham kayu manis\n• 150 gr selai apel"
        }

        binding.btnTabBahanUtama.performClick()
    }

    // =========================================================================
    // 3. FUDGY BROWNIES
    // =========================================================================
    private fun muatResepFudgyBrownies() {
        binding.tvLangkah1.text = "Tim dark cooking chocolate potongan, margarin BlueBand, dan minyak goreng di atas panci air mendidih hingga meleleh lezat."
        binding.tvLangkah2.text = "Kocok telur dan gula pasir hingga merata, lalu diamkan adonan selama 10 menit agar gula benar-benar larut sempurna."
        binding.tvLangkah3.text = "Tuangkan campuran cokelat dan margarin leleh ke dalam adonan telur, lalu aduk hingga tercampur rata."
        binding.tvLangkah4.text = "Ayak tepung terigu dan cokelat bubuk agar tidak menggumpal, campurkan ke dalam adonan lalu aduk hingga merata."
        binding.tvLangkah5.text = "Tuang ke loyang beralas kertas roti, hentakkan beberapa kali, taburi chocochips, lalu panggang oven suhu 160°C (api atas bawah) selama 40 menit."

        binding.tvBahanPengganti.text = "• Margarin BlueBand dapat diganti dengan mentega tawar untuk aroma gurih yang lebih tajam.\n• Minyak goreng dapat diganti dengan minyak canola atau minyak kelapa murni."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Dark Cooking Chocolate\n• Gula Pasir\n• Margarin BlueBand\n• Minyak Goreng"
            binding.tvBahanKanan.text = "• Tepung Terigu\n• Cokelat Bubuk\n• Telur Ayam\n• Chocochips (Topping)"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 1/2 cangkir DCC potongan\n• 16 sdm Gula Pasir\n• 6 sdm Margarin BlueBand\n• 3 sdm Minyak Goreng"
            binding.tvBahanKanan.text = "• 12 sdm Tepung Terigu\n• 6 sdm Cokelat Bubuk\n• 3 butir Telur Ayam\n• Chocochips secukupnya"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 225 gr Dark Cooking Chocolate\n• 240 gr Gula Pasir\n• 90 gr Margarin BlueBand\n• 45 ml Minyak Goreng"
            binding.tvBahanKanan.text = "• 150 gr Tepung Terigu\n• 60 gr Cokelat Bubuk\n• 3 butir Telur Ayam\n• Chocochips secukupnya"
        }

        binding.btnTabBahanUtama.performClick()
    }

    // =========================================================================
    // 4. SOURDOUGH
    // =========================================================================
    private fun muatResepSourdough() {
        binding.tvLangkah1.text = "Campurkan tepung terigu protein tinggi, air matang, dan starter sourdough aktif. Istirahatkan adonan (autolyse) selama 45 menit."
        binding.tvLangkah2.text = "Tambahkan garam halus, lalu lakukan teknik stretch and fold sebanyak 4 kali dengan jeda 30 menit."
        binding.tvLangkah3.text = "Bentuk adonan (shaping) menjadi bulat padat, masukkan ke wadah banneton yang diayaki tepung beras."
        binding.tvLangkah4.text = "Simpan adonan dalam kulkas (cold fermentation) selama 8–12 jam untuk memunculkan cita rasa gurih asam alami."
        binding.tvLangkah5.text = "Kerat permukaan adonan dengan silet dapur, lalu panggang dalam Dutch Oven tertutup suhu 230°C selama 45 menit hingga garing renyah."

        binding.tvBahanPengganti.text = "• Starter sourdough buatan sendiri dapat diganti dengan ragi instan + sedikit yogurt tanpa rasa."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Tepung Protein Tinggi\n• Starter Sourdough Aktif\n• Air Matang"
            binding.tvBahanKanan.text = "• Garam Laut (Sea Salt)\n• Tepung Beras (dusting)"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 4 cangkir Tepung Terigu\n• 1/2 cangkir Starter Sourdough\n• 1 1/2 cangkir Air Matang"
            binding.tvBahanKanan.text = "• 2 sdt Garam Laut\n• Tepung Beras Secukupnya"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 500 gr Tepung Protein Tinggi\n• 100 gr Starter Sourdough\n• 350 ml Air Matang"
            binding.tvBahanKanan.text = "• 10 gr Garam Laut\n• Tepung Beras Secukupnya"
        }

        binding.btnTabBahanUtama.performClick()
    }

    // =========================================================================
    // 5. CHOCOLATE LAVA CAKES
    // =========================================================================
    private fun muatResepLavaCake() {
        binding.tvLangkah1.text = "Lelehkan cokelat hitam dan mentega dalam wadah tahan panas di atas air mendidih."
        binding.tvLangkah2.text = "Kocok telur dan gula pasir menggunakan whisk hingga mengembang dan berwarna pucat."
        binding.tvLangkah3.text = "Campurkan adonan cokelat leleh dan tepung terigu ke kocokan telur secara perlahan."
        binding.tvLangkah4.text = "Tuangkan adonan ke dalam cetakan ramekin yang sudah diolesi mentega dan bubuk cokelat."
        binding.tvLangkah5.text = "Panggang selama 10-12 menit hingga pinggirannya matang namun bagian tengah tetap leleh."

        binding.tvBahanPengganti.text = "• Mentega dapat diganti dengan margarin kualitas tinggi atau minyak kelapa."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Cokelat Hitam (DCC)\n• Mentega\n• Telur Ayam\n• Gula Pasir\n• Tepung Terigu"
            binding.tvBahanKanan.text = "• Ekstrak Vanila\n• Garam Halus\n• Gula Halus Tabur\n• Es Krim Vanila"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 cangkir Cokelat Hitam\n• 1/2 cangkir Mentega\n• 2 butir Telur\n• 4 sdm Gula Pasir"
            binding.tvBahanKanan.text = "• 3 sdm Tepung Terigu\n• 1 sdt Ekstrak Vanila\n• 1/4 sdt Garam"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 170 gr Cokelat Hitam\n• 115 gr Mentega\n• 2 Butir Telur\n• 50 gr Gula Pasir"
            binding.tvBahanKanan.text = "• 30 gr Tepung Terigu\n• 5 ml Ekstrak Vanila\n• 2 gr Garam"
        }

        binding.btnTabBahanUtama.performClick()
    }

    // =========================================================================
    // 6. RAINBOW CAKE
    // =========================================================================
    private fun muatResepRainbowCake() {
        binding.tvLangkah1.text = "Kocok telur, gula pasir, dan SP menggunakan mikser dengan kecepatan tinggi hingga mengembang kental dan berjejak."
        binding.tvLangkah2.text = "Masukkan tepung terigu dan susu bubuk yang sudah diayak, aduk balik dengan spatula. Tuang margarin cair dan aduk rata."
        binding.tvLangkah3.text = "Bagi adonan menjadi 6 bagian sama rata, lalu beri masing-masing pewarna makanan (merah, jingga, kuning, hijau, biru, ungu)."
        binding.tvLangkah4.text = "Kukus atau panggang setiap lapisan loyang secara terpisah selama 10–12 menit hingga matang. Biarkan dingin sepenuhnya."
        binding.tvLangkah5.text = "Susun lapisan kue berurutan, olesi buttercream di antara setiap lapisan, lalu lapisi seluruh permukaan kue hingga rapi."

        binding.tvBahanPengganti.text = "• Buttercream dapat diganti dengan cream cheese frosting untuk cita rasa gurih legit."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Telur Ayam\n• Gula Pasir\n• SP / Emulsifier\n• Tepung Terigu Protein Sedang"
            binding.tvBahanKanan.text = "• Susu Bubuk\n• Margarin Leleh\n• Pewarna Makanan Pelangi\n• Buttercream"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 4 butir Telur\n• 12 sdm Gula Pasir\n• 1 sdt SP\n• 15 sdm Tepung Terigu"
            binding.tvBahanKanan.text = "• 3 sdm Susu Bubuk\n• 10 sdm Margarin Leleh\n• Pewarna Secukupnya\n• Buttercream"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 4 butir Telur\n• 150 gr Gula Pasir\n• 5 gr SP\n• 150 gr Tepung Terigu"
            binding.tvBahanKanan.text = "• 20 gr Susu Bubuk\n• 100 gr Margarin Leleh\n• Pewarna Pelangi\n• 200 gr Buttercream"
        }

        binding.btnTabBahanUtama.performClick()
    }

    // =========================================================================
    // 7. CLASSIC COOKIES
    // =========================================================================
    private fun muatResepCookies() {
        binding.tvLangkah1.text = "Kocok mentega tawar, gula palem, dan gula pasir hingga lembut dan menyatu mengembang."
        binding.tvLangkah2.text = "Tambahkan telur dan ekstrak vanila, kocok sebentar hingga adonan tercampur rata."
        binding.tvLangkah3.text = "Ayak tepung terigu, baking soda, dan garam. Masukkan ke adonan basah lalu aduk lipat menggunakan spatula."
        binding.tvLangkah4.text = "Campurkan choco chips ke dalam adonan. Sendokkan adonan bulat-bulat di atas loyang beralas kertas roti."
        binding.tvLangkah5.text = "Panggang dalam oven suhu 170°C selama 10–12 menit hingga bagian tepinya agak keemasan namun tengahnya tetap lembut."

        binding.tvBahanPengganti.text = "• Gula palem bisa diganti penuh dengan gula pasir, namun aroma karamel harumnya akan berkurang."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Mentega Tawar\n• Gula Palem (Brown Sugar)\n• Gula Pasir\n• Telur Ayam"
            binding.tvBahanKanan.text = "• Tepung Terigu\n• Baking Soda\n• Choco Chips\n• Ekstrak Vanila & Garam"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 8 sdm Mentega Tawar\n• 6 sdm Gula Palem\n• 4 sdm Gula Pasir\n• 1 butir Telur"
            binding.tvBahanKanan.text = "• 14 sdm Tepung Terigu\n• 1/2 sdt Baking Soda\n• 1 cangkir Choco Chips"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 115 gr Mentega Tawar\n• 100 gr Gula Palem\n• 50 gr Gula Pasir\n• 1 butir Telur"
            binding.tvBahanKanan.text = "• 180 gr Tepung Terigu\n• 3 gr Baking Soda\n• 150 gr Choco Chips"
        }

        binding.btnTabBahanUtama.performClick()
    }

    // =========================================================================
    // 8. NO-BAKE PUMPKIN CHEESECAKE BARS
    // =========================================================================
    private fun muatResepCheesecake() {
        binding.tvLangkah1.text = "Campur remahan biskuit graham, mentega cair, dan gula pasir hingga merata."
        binding.tvLangkah2.text = "Tekan adonan biskuit ke dasar loyang secara merata lalu bekukan selama 10 menit."
        binding.tvLangkah3.text = "Kocok keju krim, labu, gula merah, dan bumbu pai labu sampai halus."
        binding.tvLangkah4.text = "Tuangkan adonan labu di atas lapisan biskuit dan ratakan permukaannya."
        binding.tvLangkah5.text = "Simpan dalam kulkas minimal 4 jam sebelum dipotong dan disajikan."

        binding.tvBahanPengganti.text = "• Mentega cair dapat diganti dengan minyak kelapa cair."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Remahan biskuit graham\n• Mentega cair\n• Gula pasir\n• Puree labu kuning\n• Keju krim\n• Gula merah"
            binding.tvBahanKanan.text = "• Bumbu pai labu\n• Cokelat putih\n• Krim kental\n• Gula halus\n• Ekstrak vanila"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 1/4 cangkir remahan biskuit graham\n• 1/3 cangkir mentega cair\n• 2 sendok makan gula pasir\n• 1 kaleng (15 ons) labu\n• 1 kemasan (8 ons) keju krim"
            binding.tvBahanKanan.text = "• 2 sendok teh bumbu pai labu\n• 8 ons cokelat putih\n• 3/4 cangkir krim kental\n• 1/4 cangkir gula halus"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 150 gr biskuit graham\n• 75 gr mentega cair\n• 30 gr gula pasir\n• 425 gr puree labu\n• 225 gr keju krim"
            binding.tvBahanKanan.text = "• 10 gr bumbu pai labu\n• 225 gr cokelat putih\n• 180 ml krim kental\n• 30 gr gula halus"
        }

        binding.btnTabBahanUtama.performClick()
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