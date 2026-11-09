package com.example.bakerys

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bakerys.databinding.FragmentProfilBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

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

        loadSavedPreferences()

        // 1. Listener Tombol Edit Profile
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        // 2. Listener Navigasi Fragment (Draft & Disukai)
        binding.btnDraftResep.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, DraftFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        binding.btnDisukai.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, DisukaiFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        // 3. Listener Log Out
        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    SupabaseClient.instance.auth.signOut()
                } catch (e: Exception) {
                    // Ignore exception
                }

                Toast.makeText(requireContext(), "Berhasil Log Out", Toast.LENGTH_SHORT).show()

                val currentActivity = activity ?: return@launch
                val intent = Intent(currentActivity, WelcomeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                currentActivity.startActivity(intent)
                currentActivity.finish()
            }
        }

        // 4. Listener Preferensi Pemanggangan
        setupPreferensiListeners()
    }

    override fun onResume() {
        super.onResume()
        // Mengisi & merefresh data profil dari lokal & Supabase setiap kali fragment ini aktif
        loadProfilUser()
    }

    private fun loadProfilUser() {
        // 1. Muat Foto Profil & Data Teks dari SharedPreferences Lokal (Instan)
        val savedUriStr = sharedPreferences.getString("profile_image_uri", null)
        if (!savedUriStr.isNullOrEmpty()) {
            try {
                binding.ivFotoProfil.setImageURI(Uri.parse(savedUriStr))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val localNama = sharedPreferences.getString("profile_name", null)
        val localBio = sharedPreferences.getString("profile_bio", null)
        if (!localNama.isNullOrEmpty()) binding.tvNamaUser.text = localNama
        if (!localBio.isNullOrEmpty()) binding.tvBioUser.text = localBio

        // 2. Sinkronkan dengan Supabase DB
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val currentUser = SupabaseClient.instance.auth.currentUserOrNull() ?: return@launch
                val userId = currentUser.id
                val userEmailAuth = currentUser.email ?: ""

                val result = SupabaseClient.instance.from("users")
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }.decodeList<JsonObject>()

                var username = userEmailAuth.substringBefore("@")
                var bioText = "Selamat datang di Bakerys!"

                if (result.isNotEmpty()) {
                    val userObj = result.first()

                    val dbUsername = userObj["username"]?.jsonPrimitive?.content
                    val dbNamaLengkap = userObj["nama_lengkap"]?.jsonPrimitive?.content

                    if (!dbUsername.isNullOrEmpty()) {
                        username = dbUsername
                    } else if (!dbNamaLengkap.isNullOrEmpty()) {
                        username = dbNamaLengkap
                    }

                    val dbBio = userObj["bio"]?.jsonPrimitive?.content
                    if (!dbBio.isNullOrEmpty()) {
                        bioText = dbBio
                    }
                }

                if (_binding != null) {
                    binding.tvNamaUser.text = username
                    binding.tvBioUser.text = bioText
                }

            } catch (e: Exception) {
                android.util.Log.e("BAKERYS_DEBUG", "Error load profil: ${e.message}")
            }
        }
    }

    private fun setupPreferensiListeners() {
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
        selectedBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#802C3E"))
        selectedTv.setTextColor(Color.WHITE)
        selectedTv.setTypeface(null, Typeface.BOLD)

        unselectedBtn.backgroundTintList = null
        unselectedTv.setTextColor(Color.parseColor("#802C3E"))
        unselectedTv.setTypeface(null, Typeface.NORMAL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}