package com.example.bakerys

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bakerys.databinding.FragmentProfilBinding

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    // Preferences untuk menyimpan opsi yang dipilih
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("BakerysPrefs", Context.MODE_PRIVATE)

        // 1. Load preferensi tersimpan saat fragment dibuka
        loadSavedPreferences()

        // 2. Listener Tombol Edit Profile
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        // 3. Listener Navigasi ke TersimpanFragment (Resep Disimpan)
        binding.btnResepDisimpan.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TersimpanFragment())
                .addToBackStack(null)
                .commit()
        }

        // 4. Listener Navigasi ke DraftFragment
        binding.btnDraftResep.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DraftFragment())
                .addToBackStack(null)
                .commit()
        }

        // 4.5. Listener Navigasi ke DisukaiFragment (MENU DISUKAI BARU)
        binding.btnDisukai.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DisukaiFragment())
                .addToBackStack(null)
                .commit()
        }

        // 5. Listener Log Out
        binding.btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Berhasil Log Out", Toast.LENGTH_SHORT).show()

            val currentActivity = activity ?: return@setOnClickListener
            val intent = Intent(currentActivity, WelcomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            currentActivity.startActivity(intent)
            currentActivity.finish()
        }

        // 6. Listener Aksi Klik Preferensi Pemanggangan
        setupPreferensiListeners()
    }

    private fun setupPreferensiListeners() {
        // --- Satuan Pengukuran ---
        binding.btnMetric.setOnClickListener {
            setOptionSelected(binding.btnMetric, binding.tvMetric, binding.btnImperial, binding.tvImperial)
            savePreference("unit_preference", "metric")
            Toast.makeText(requireContext(), "Satuan diubah ke Metric (Gram/ml)", Toast.LENGTH_SHORT).show()
        }

        binding.btnImperial.setOnClickListener {
            setOptionSelected(binding.btnImperial, binding.tvImperial, binding.btnMetric, binding.tvMetric)
            savePreference("unit_preference", "imperial")
            Toast.makeText(requireContext(), "Satuan diubah ke Sendok (Cangkir/ons)", Toast.LENGTH_SHORT).show()
        }

        // --- Suhu Oven ---
        binding.btnCelcius.setOnClickListener {
            setOptionSelected(binding.btnCelcius, binding.tvCelcius, binding.btnFahrenheit, binding.tvFahrenheit)
            savePreference("temp_preference", "celcius")
            Toast.makeText(requireContext(), "Standar suhu diubah ke Celcius (°C)", Toast.LENGTH_SHORT).show()
        }

        binding.btnFahrenheit.setOnClickListener {
            setOptionSelected(binding.btnFahrenheit, binding.tvFahrenheit, binding.btnCelcius, binding.tvCelcius)
            savePreference("temp_preference", "fahrenheit")
            Toast.makeText(requireContext(), "Standar suhu diubah ke Fahrenheit (°F)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSavedPreferences() {
        val savedUnit = sharedPreferences.getString("unit_preference", "metric")
        if (savedUnit == "imperial") {
            setOptionSelected(binding.btnImperial, binding.tvImperial, binding.btnMetric, binding.tvMetric)
        } else {
            setOptionSelected(binding.btnMetric, binding.tvMetric, binding.btnImperial, binding.tvImperial)
        }

        val savedTemp = sharedPreferences.getString("temp_preference", "celcius")
        if (savedTemp == "fahrenheit") {
            setOptionSelected(binding.btnFahrenheit, binding.tvFahrenheit, binding.btnCelcius, binding.tvCelcius)
        } else {
            setOptionSelected(binding.btnCelcius, binding.tvCelcius, binding.btnFahrenheit, binding.tvFahrenheit)
        }
    }

    private fun savePreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun setOptionSelected(
        selectedBtn: LinearLayout,
        selectedTv: TextView,
        unselectedBtn: LinearLayout,
        unselectedTv: TextView
    ) {
        // Tampilan Aktif (Maroon, Teks Putih Bold)
        selectedBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#802C3E"))
        selectedTv.setTextColor(Color.WHITE)
        selectedTv.setTypeface(null, Typeface.BOLD)

        // Tampilan Tidak Aktif (Transparan, Teks Maroon Normal)
        unselectedBtn.backgroundTintList = null
        unselectedTv.setTextColor(Color.parseColor("#802C3E"))
        unselectedTv.setTypeface(null, Typeface.NORMAL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}