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
            judul.contains("Nastar", ignoreCase = true) -> muatResepNastar()
            judul.contains("Cookies", ignoreCase = true) -> muatResepCookies()
            judul.contains("Cheesecake", ignoreCase = true) -> muatResepCheesecake()
            judul.contains("Kastengel", ignoreCase = true) -> muatResepKastengel()
            judul.contains("Lidah", ignoreCase = true) || judul.contains("Kucing", ignoreCase = true) -> muatResepLidahKucing()
            judul.contains("Kuping", ignoreCase = true) || judul.contains("Gajah", ignoreCase = true) -> muatResepKupingGajah()
            else -> muatResepCheesecake()
        }

        setupBottomNavigation()

        binding.btnSetAlarm.setOnClickListener {
            TimerDialog().show(supportFragmentManager, "TimerDialog")
        }
    }

    private fun muatResepDubaiCookies() {
        binding.tvLangkah1.text = "Lelehkan cokelat putih couverture dengan metode tim. Campurkan dengan kunafa panggang, pasta pistachio, dan pistachio cincang hingga merata."
        binding.tvLangkah2.text = "Bagi adonan isi menjadi 8 bagian sama besar, padatkan dan bentuk bulat, lalu simpan di kulkas selama 30 menit agar set."
        binding.tvLangkah3.text = "Lelehkan mentega dan marshmallow di teflon api kecil hingga halus. Masukkan susu bubuk skim dan bubuk kakao, aduk cepat sampai elastis."
        binding.tvLangkah4.text = "Ratakan adonan marshmallow di atas loyang beralas kertas roti. Ambil adonan isi dari kulkas, lalu bungkus rapat dengan lapisan chewy cokelat."
        binding.tvLangkah5.text = "Gulingkan di atas bubuk kakao hingga tertutup rata, dinginkan di kulkas 20–30 menit, dan sajikan pada suhu ruang agar tekstur kenyalnya maksimal."

        binding.tvBahanPengganti.text = "• Kunafa panggang dapat diganti dengan pastry Kataifi sangrai atau mi kunafa halus.\nPasta pistachio dapat diganti dengan selai kacang atau pasta hazelnut.\nSusu bubuk skim dapat diganti dengan susu bubuk full cream."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Kunafa panggang\nPasta pistachio\nCokelat putih couverture\nPistachio cincang"
            binding.tvBahanKanan.text = "• Marshmallow\nMentega tawar\nSusu bubuk skim\nBubuk kakao (isi & topping)"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 1/2 cangkir kunafa\n1/2 cangkir pasta pistachio\n2 sdm cokelat putih leleh\n2 sdm pistachio cincang"
            binding.tvBahanKanan.text = "• 2 cangkir marshmallow\n2 sdm mentega tawar\n1 sdm susu bubuk skim\n2 sdm bubuk kakao"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 160 gr kunafa panggang\n135 gr pasta pistachio\n32 gr cokelat putih couverture\n20 gr pistachio cincang"
            binding.tvBahanKanan.text = "• 130 gr marshmallow\n30 gr mentega tawar\n12 gr susu bubuk skim\n18 gr bubuk kakao + topping secukupnya"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepCaramelApple() {
        binding.tvLangkah1.text = "Kocok krim kental, bumbu pai apel, dan 3 sdm saus karamel dengan mikser kecepatan sedang hingga membentuk puncak kaku (sekitar 2 menit)."
        binding.tvLangkah2.text = "Hancurkan 6 biskuit graham dalam mangkuk untuk taburan sekeliling. Oleskan 1/3 cangkir krim kocok di dasar piring panjang."
        binding.tvLangkah3.text = "Olesi setiap biskuit dengan 1 sdm krim kocok dan 1 sdt selai apel, lalu susun tegak secara vertikal di piring hingga membentuk gulungan kue."
        binding.tvLangkah4.text = "Tutup rapat seluruh permukaan atas dan samping gulungan dengan sisa krim kocok, lalu simpan di kulkas selama 6–8 jam."
        binding.tvLangkah5.text = "Siram kue dingin dengan sisa 1 sdm saus karamel, baluri sekeliling kue dengan remah biskuit graham, lalu iris secara diagonal."

        binding.tvBahanPengganti.text = "• Biskuit graham rasa kayu manis dapat diganti biskuit gandum atau biskuit renyah rasa vanila."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Krim kental\nBumbu pai apel\nSaus karamel (Smucker's®)"
            binding.tvBahanKanan.text = "• Biskuit graham kayu manis\nSelai apel"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 1/2 cangkir krim kental\n1/2 sdt bumbu pai apel\n1/4 cangkir saus karamel"
            binding.tvBahanKanan.text = "• 28 biskuit graham kayu manis\n1/2 cangkir selai apel"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 360 ml krim kental\n2 gr bumbu pai apel\n60 ml saus karamel"
            binding.tvBahanKanan.text = "• 280 gr biskuit graham kayu manis\n150 gr selai apel"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepFudgyBrownies() {
        binding.tvLangkah1.text = "Tim dark cooking chocolate potongan, margarin BlueBand, dan minyak goreng di atas panci air mendidih hingga meleleh lezat."
        binding.tvLangkah2.text = "Kocok telur dan gula pasir hingga merata, lalu diamkan adonan selama 10 menit agar gula benar-benar larut sempurna."
        binding.tvLangkah3.text = "Tuangkan campuran cokelat dan margarin leleh ke dalam adonan telur, lalu aduk hingga tercampur rata."
        binding.tvLangkah4.text = "Ayak tepung terigu dan cokelat bubuk agar tidak menggumpal, campurkan ke dalam adonan lalu aduk hingga merata."
        binding.tvLangkah5.text = "Tuang ke loyang beralas kertas roti, hentakkan beberapa kali, taburi chocochips, lalu panggang oven suhu 160°C (api atas bawah) selama 40 menit."

        binding.tvBahanPengganti.text = "• Margarin BlueBand dapat diganti dengan mentega tawar untuk aroma gurih yang lebih tajam.\nMinyak goreng dapat diganti dengan minyak canola atau minyak kelapa murni."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Dark Cooking Chocolate\nGula Pasir\nMargarin BlueBand\nMinyak Goreng"
            binding.tvBahanKanan.text = "• Tepung Terigu\nCokelat Bubuk\nTelur Ayam\nChocochips (Topping)"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 1/2 cangkir DCC potongan\n16 sdm Gula Pasir\n6 sdm Margarin BlueBand\n3 sdm Minyak Goreng"
            binding.tvBahanKanan.text = "• 12 sdm Tepung Terigu\n6 sdm Cokelat Bubuk\n3 butir Telur Ayam\nChocochips secukupnya"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 225 gr Dark Cooking Chocolate\n240 gr Gula Pasir\n90 gr Margarin BlueBand\n45 ml Minyak Goreng"
            binding.tvBahanKanan.text = "• 150 gr Tepung Terigu\n60 gr Cokelat Bubuk\n3 butir Telur Ayam\nChocochips secukupnya"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepSourdough() {
        binding.tvLangkah1.text = "Campurkan tepung terigu protein tinggi, air matang, dan starter sourdough aktif. Istirahatkan adonan (autolyse) selama 45 menit."
        binding.tvLangkah2.text = "Tambahkan garam halus, lalu lakukan teknik stretch and fold sebanyak 4 kali dengan jeda 30 menit."
        binding.tvLangkah3.text = "Bentuk adonan (shaping) menjadi bulat padat, masukkan ke wadah banneton yang diayaki tepung beras."
        binding.tvLangkah4.text = "Simpan adonan dalam kulkas (cold fermentation) selama 8–12 jam untuk memunculkan cita rasa gurih asam alami."
        binding.tvLangkah5.text = "Kerat permukaan adonan dengan silet dapur, lalu panggang dalam Dutch Oven tertutup suhu 230°C selama 45 menit hingga garing renyah."

        binding.tvBahanPengganti.text = "• Starter sourdough buatan sendiri dapat diganti dengan ragi instan + sedikit yogurt tanpa rasa."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Tepung Protein Tinggi\nStarter Sourdough Aktif\nAir Matang"
            binding.tvBahanKanan.text = "• Garam Laut (Sea Salt)\nTepung Beras (dusting)"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 4 cangkir Tepung Terigu\n1/2 cangkir Starter Sourdough\n1 1/2 cangkir Air Matang"
            binding.tvBahanKanan.text = "• 2 sdt Garam Laut\nTepung Beras Secukupnya"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 500 gr Tepung Protein Tinggi\n100 gr Starter Sourdough\n350 ml Air Matang"
            binding.tvBahanKanan.text = "• 10 gr Garam Laut\nTepung Beras Secukupnya"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepLavaCake() {
        binding.tvLangkah1.text = "Lelehkan cokelat hitam dan mentega dalam wadah tahan panas di atas air mendidih."
        binding.tvLangkah2.text = "Kocok telur dan gula pasir menggunakan whisk hingga mengembang dan berwarna pucat."
        binding.tvLangkah3.text = "Campurkan adonan cokelat leleh dan tepung terigu ke kocokan telur secara perlahan."
        binding.tvLangkah4.text = "Tuangkan adonan ke dalam cetakan ramekin yang sudah diolesi mentega dan bubuk cokelat."
        binding.tvLangkah5.text = "Panggang selama 10-12 menit hingga pinggirannya matang namun bagian tengah tetap leleh."

        binding.tvBahanPengganti.text = "• Mentega dapat diganti dengan margarin kualitas tinggi atau minyak kelapa."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Cokelat Hitam (DCC)\nMentega\nTelur Ayam\nGula Pasir\nTepung Terigu"
            binding.tvBahanKanan.text = "• Ekstrak Vanila\nGaram Halus\nGula Halus Tabur\nEs Krim Vanila"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 cangkir Cokelat Hitam\n1/2 cangkir Mentega\n2 butir Telur\n4 sdm Gula Pasir"
            binding.tvBahanKanan.text = "• 3 sdm Tepung Terigu\n1 sdt Ekstrak Vanila\n1/4 sdt Garam"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 170 gr Cokelat Hitam\n115 gr Mentega\n2 Butir Telur\n50 gr Gula Pasir"
            binding.tvBahanKanan.text = "• 30 gr Tepung Terigu\n5 ml Ekstrak Vanila\n2 gr Garam"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepRainbowCake() {
        binding.tvLangkah1.text = "Kocok telur, gula pasir, dan SP menggunakan mikser dengan kecepatan tinggi hingga mengembang kental dan berjejak."
        binding.tvLangkah2.text = "Masukkan tepung terigu dan susu bubuk yang sudah diayak, aduk balik dengan spatula. Tuang margarin cair dan aduk rata."
        binding.tvLangkah3.text = "Bagi adonan menjadi 6 bagian sama rata, lalu beri masing-masing pewarna makanan (merah, jingga, kuning, hijau, biru, ungu)."
        binding.tvLangkah4.text = "Kukus atau panggang setiap lapisan loyang secara terpisah selama 10–12 menit hingga matang. Biarkan dingin sepenuhnya."
        binding.tvLangkah5.text = "Susun lapisan kue berurutan, olesi buttercream di antara setiap lapisan, lalu lapisi seluruh permukaan kue hingga rapi."

        binding.tvBahanPengganti.text = "• Buttercream dapat diganti dengan cream cheese frosting untuk cita rasa gurih legit."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Telur Ayam\nGula Pasir\nSP / Emulsifier\nTepung Terigu Protein Sedang"
            binding.tvBahanKanan.text = "• Susu Bubuk\nMargarin Leleh\nPewarna Makanan Pelangi\nButtercream"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 4 butir Telur\n12 sdm Gula Pasir\n1 sdt SP\n15 sdm Tepung Terigu"
            binding.tvBahanKanan.text = "• 3 sdm Susu Bubuk\n10 sdm Margarin Leleh\nPewarna Secukupnya\nButtercream"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 4 butir Telur\n150 gr Gula Pasir\n5 gr SP\n150 gr Tepung Terigu"
            binding.tvBahanKanan.text = "• 20 gr Susu Bubuk\n100 gr Margarin Leleh\nPewarna Pelangi\n200 gr Buttercream"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepCookies() {
        binding.tvLangkah1.text = "Kocok mentega tawar, gula palem, dan gula pasir hingga lembut dan menyatu mengembang."
        binding.tvLangkah2.text = "Tambahkan telur dan ekstrak vanila, kocok sebentar hingga adonan tercampur rata."
        binding.tvLangkah3.text = "Ayak tepung terigu, baking soda, dan garam. Masukkan ke adonan basah lalu aduk lipat menggunakan spatula."
        binding.tvLangkah4.text = "Campurkan choco chips ke dalam adonan. Sendokkan adonan bulat-bulat di atas loyang beralas kertas roti."
        binding.tvLangkah5.text = "Panggang dalam oven suhu 170°C selama 10–12 menit hingga bagian tepinya agak keemasan namun tengahnya tetap lembut."

        binding.tvBahanPengganti.text = "• Gula palem bisa diganti penuh dengan gula pasir, namun aroma karamel harumnya akan berkurang."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Mentega Tawar\nGula Palem (Brown Sugar)\nGula Pasir\nTelur Ayam"
            binding.tvBahanKanan.text = "• Tepung Terigu\nBaking Soda\nChoco Chips\nEkstrak Vanila & Garam"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 8 sdm Mentega Tawar\n6 sdm Gula Palem\n4 sdm Gula Pasir\n1 butir Telur"
            binding.tvBahanKanan.text = "• 14 sdm Tepung Terigu\n1/2 sdt Baking Soda\n1 cangkir Choco Chips"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 115 gr Mentega Tawar\n100 gr Gula Palem\n50 gr Gula Pasir\n1 butir Telur"
            binding.tvBahanKanan.text = "• 180 gr Tepung Terigu\n3 gr Baking Soda\n150 gr Choco Chips"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepCheesecake() {
        binding.tvLangkah1.text = "Campur remahan biskuit graham, mentega cair, dan gula pasir hingga merata."
        binding.tvLangkah2.text = "Tekan adonan biskuit ke dasar loyang secara merata lalu bekukan selama 10 menit."
        binding.tvLangkah3.text = "Kocok keju krim, labu, gula merah, dan bumbu pai labu sampai halus."
        binding.tvLangkah4.text = "Tuangkan adonan labu di atas lapisan biskuit dan ratakan permukaannya."
        binding.tvLangkah5.text = "Simpan dalam kulkas minimal 4 jam sebelum dipotong dan disajikan."

        binding.tvBahanPengganti.text = "• Mentega cair dapat diganti dengan minyak kelapa cair."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Remahan biskuit graham\nMentega cair\nGula pasir\nPuree labu kuning\nKeju krim\nGula merah"
            binding.tvBahanKanan.text = "• Bumbu pai labu\nCokelat putih\nKrim kental\nGula halus\nEkstrak vanila"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 1 1/4 cangkir remahan biskuit graham\n1/3 cangkir mentega cair\n2 sendok makan gula pasir\n1 kaleng (15 ons) labu\n1 kemasan (8 ons) keju krim"
            binding.tvBahanKanan.text = "• 2 sendok teh bumbu pai labu\n8 ons cokelat putih\n3/4 cangkir krim kental\n1/4 cangkir gula halus"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 150 gr biskuit graham\n75 gr mentega cair\n30 gr gula pasir\n425 gr puree labu\n225 gr keju krim"
            binding.tvBahanKanan.text = "• 10 gr bumbu pai labu\n225 gr cokelat putih\n180 ml krim kental\n30 gr gula halus"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepNastar() {
        binding.tvLangkah1.text = "Kocok mentega, margarin, dan gula halus sebentar (sekitar 1-2 menit) hingga tercampur rata dan lembut, jangan terlalu lama."
        binding.tvLangkah2.text = "Masukkan kuning telur satu per satu sambil terus dikocok rata, lalu matikan mikser."
        binding.tvLangkah3.text = "Masukkan tepung terigu, susu bubuk, dan tepung maizena yang sudah diayak. Aduk perlahan menggunakan spatula hingga adonan bisa dibentuk."
        binding.tvLangkah4.text = "Ambil sedikit adonan, isi dengan selai nanas bulat, bulatkan kembali dan tata di atas loyang yang sudah dialasi kertas roti."
        binding.tvLangkah5.text = "Panggang dalam oven suhu 140°C selama 25 menit. Keluarkan sebentar untuk dioles kuning telur, lalu panggang lagi hingga kuning keemasan."

        binding.tvBahanPengganti.text = "• Selai nanas homemade dapat diganti dengan selai nanas instan siap pakai khusus nastar."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Mentega (Butter)\nMargarin\nGula Halus\nKuning Telur"
            binding.tvBahanKanan.text = "• Tepung Terigu Protein Sedang\nTepung Maizena\nSusu Bubuk\nSelai Nanas (Isian)"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 10 sdm Mentega\n6 sdm Margarin\n4 sdm Gula Halus\n3 butir Kuning Telur"
            binding.tvBahanKanan.text = "• 35 cangkir Tepung Terigu\n3 sdm Tepung Maizena\n2 sdm Susu Bubuk\nSelai Nanas Secukupnya"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 100 gr Mentega\n100 gr Margarin\n50 gr Gula Halus\n3 butir Kuning Telur"
            binding.tvBahanKanan.text = "• 300 gr Tepung Terigu\n30 gr Tepung Maizena\n25 gr Susu Bubuk\n150 gr Selai Nanas"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepButterCookies() {
        binding.tvLangkah1.text = "Kocok mentega tawar dan gula halus dengan mikser kecepatan sedang selama 2-3 menit hingga pucat, mengembang, dan lembut."
        binding.tvLangkah2.text = "Masukkan ekstrak vanila dan kuning telur, lalu kocok sebentar lagi hingga benar-benar tercampur rata."
        binding.tvLangkah3.text = "Ayak tepung terigu protein rendah dan maizena, masukkan secara bertahap ke adonan basah sambil diaduk perlahan dengan spatula."
        binding.tvLangkah4.text = "Masukkan adonan ke dalam piping bag (kantong semprot) yang dipasangi spuit bintang, lalu semprotkan di atas loyang beralas kertas roti."
        binding.tvLangkah5.text = "Panggang dalam oven dengan suhu 160°C selama 15–18 menit hingga pinggirannya kecokelatan keemasan, lalu dinginkan di rak kawat."

        binding.tvBahanPengganti.text = "• Mentega tawar (butter) sangat disarankan agar aroma harumnya terasa, bisa dicampur margarin dengan perbandingan 1:1."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Mentega Tawar (Butter)\nGula Halus\nKuning Telur\nEkstrak Vanila"
            binding.tvBahanKanan.text = "• Tepung Terigu Kunci Biru\nTepung Maizena\nSejumput Garam"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 16 sdm Mentega Tawar\n6 sdm Gula Halus\n1 butir Kuning Telur\n1 sdt Ekstrak Vanila"
            binding.tvBahanKanan.text = "• 22 sdm Tepung Terigu\n2 sdm Tepung Maizena\n1/4 sdt Garam"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 200 gr Mentega Tawar\n75 gr Gula Halus\n1 butir Kuning Telur\n5 ml Ekstrak Vanila"
            binding.tvBahanKanan.text = "• 220 gr Tepung Terigu\n30 gr Tepung Maizena\n2 gr Garam"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepKastengel() {
        binding.tvLangkah1.text = "Kocok mentega tawar, margarin, dan kuning telur sebentar saja (sekitar 1 menit) hingga tercampur rata dan lembut."
        binding.tvLangkah2.text = "Masukkan keju Edam parut dan keju Cheddar parut ke dalam adonan, aduk perlahan menggunakan spatula."
        binding.tvLangkah3.text = "Ayak tepung terigu dan tepung maizena, lalu masukkan secara bertahap sambil diaduk sampai adonan kalis dan mudah dibentuk."
        binding.tvLangkah4.text = "Gilas adonan setebal kurang lebih 1 cm, cetak memanjang atau potong-potong, lalu tata di atas loyang."
        binding.tvLangkah5.text = "Olesi permukaannya dengan kuning telur dan taburi sisa keju parut di atasnya. Panggang dalam oven suhu 140°C selama 30 menit hingga matang keemasan."

        binding.tvBahanPengganti.text = "• Keju Edam dapat diganti sepenuhnya dengan keju Cheddar tua atau Cheddar biasa jika ingin lebih ekonomis."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Mentega Tawar (Butter)\nMargarin\nKuning Telur\nKeju Edam Parut"
            binding.tvBahanKanan.text = "• Keju Cheddar Parut\nTepung Terigu Protein Sedang\nTepung Maizena\nKuning Telur (Olesan)"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 10 sdm Mentega Tawar\n6 sdm Margarin\n2 butir Kuning Telur\n1 cangkir Keju Edam"
            binding.tvBahanKanan.text = "• 1 cangkir Keju Cheddar\n24 sdm Tepung Terigu\n2 sdm Tepung Maizena\n2 butir Kuning Telur"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 100 gr Mentega Tawar\n100 gr Margarin\n2 butir Kuning Telur\n100 gr Keju Edam Parut"
            binding.tvBahanKanan.text = "• 100 gr Keju Cheddar Parut\n300 gr Tepung Terigu\n25 gr Tepung Maizena\n2 butir Kuning Telur (Olesan)"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepLidahKucing() {
        binding.tvLangkah1.text = "Kocok mentega tawar, margarin, dan gula halus menggunakan mikser dengan kecepatan tinggi selama 3-4 menit hingga adonan putih, pucat, dan sangat mengembang."
        binding.tvLangkah2.text = "Masukkan putih telur secara perlahan sambil terus dikocok rata, lalu tambahkan ekstrak vanila."
        binding.tvLangkah3.text = "Ayak tepung terigu protein rendah, tepung maizena, dan susu bubuk, lalu masukkan ke dalam adonan basah secara bertahap sambil diaduk balik perlahan dengan spatula."
        binding.tvLangkah4.text = "Masukkan adonan ke dalam piping bag (kantong semprot), gunting ujungnya, lalu semprotkan memanjang secara rapi di atas loyang khusus lidah kucing yang sudah dioles tipis margarin."
        binding.tvLangkah5.text = "Panggang dalam oven dengan suhu 140°C selama 20–25 menit hingga matang renyah dan bagian pinggirnya kecokelatan."

        binding.tvBahanPengganti.text = "• Penggunaan putih telur dalam keadaan fresh (segar) sangat penting agar kue bisa renyah dan tidak lembek."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Mentega Tawar (Butter)\nMargarin\nGula Halus\nPutih Telur"
            binding.tvBahanKanan.text = "• Tepung Terigu Kunci Biru\nTepung Maizena\nSusu Bubuk\nEkstrak Vanila"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 12 sdm Mentega Tawar\n4 sdm Margarin\n8 sdm Gula Halus\n3 butir Putih Telur"
            binding.tvBahanKanan.text = "• 18 sdm Tepung Terigu\n2 sdm Tepung Maizena\n2 sdm Susu Bubuk\n1 sdt Ekstrak Vanila"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 125 gr Mentega Tawar\n75 gr Margarin\n120 gr Gula Halus\n60 ml Putih Telur"
            binding.tvBahanKanan.text = "• 180 gr Tepung Terigu\n25 gr Tepung Maizena\n20 gr Susu Bubuk\n5 ml Ekstrak Vanila"
        }

        binding.btnTabBahanUtama.performClick()
    }

    private fun muatResepKupingGajah() {
        binding.tvLangkah1.text = "Kocok telur ayam, gula halus, vanili, dan sejumput garam menggunakan whisk hingga gula larut dan merata."
        binding.tvLangkah2.text = "Masukkan tepung terigu dan margarin cair ke dalam adonan telur, uleni perlahan hingga kalis."
        binding.tvLangkah3.text = "Bagi adonan menjadi dua bagian: satu bagian dibiarkan warna putih/original, dan bagian lainnya ditambahkan bubuk kakao lalu uleni hingga rata."
        binding.tvLangkah4.text = "Tipiskan kedua adonan, tumpuk adonan cokelat di atas adonan putih, lalu gulung rapat padat dan simpan di kulkas selama 1 jam agar set."
        binding.tvLangkah5.text = "Iris tipis-tipis gulungan adonan secara melintang, lalu goreng dalam minyak panas dengan api sedang hingga matang renyah kecokelatan."

        binding.tvBahanPengganti.text = "• Margarin cair dapat diganti dengan mentega cair untuk hasil yang lebih wangi dan gurih."

        binding.btnTabBahanUtama.setOnClickListener {
            setActiveTab(binding.btnTabBahanUtama, binding.btnTabTakaranSendok, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• Tepung Terigu Protein Sedang\nGula Halus\nMargarin (dicairkan)\nTelur Ayam"
            binding.tvBahanKanan.text = "• Bubuk Kakao (untuk warna cokelat)\nEkstrak Vanila\nGaram & Air Secukupnya"
        }

        binding.btnTabTakaranSendok.setOnClickListener {
            setActiveTab(binding.btnTabTakaranSendok, binding.btnTabBahanUtama, binding.btnTabTakaranGram)
            binding.tvBahanKiri.text = "• 30 sdm Tepung Terigu\n8 sdm Gula Halus\n4 sdm Margarin Cair\n1 butir Telur Ayam"
            binding.tvBahanKanan.text = "• 2 sdm Bubuk Kakao\n1 sdt Ekstrak Vanila\n1/4 sdt Garam"
        }

        binding.btnTabTakaranGram.setOnClickListener {
            setActiveTab(binding.btnTabTakaranGram, binding.btnTabBahanUtama, binding.btnTabTakaranSendok)
            binding.tvBahanKiri.text = "• 300 gr Tepung Terigu\n75 gr Gula Halus\n50 gr Margarin Cair\n1 butir Telur Ayam"
            binding.tvBahanKanan.text = "• 15 gr Bubuk Kakao\n5 ml Ekstrak Vanila\n2 gr Garam"
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