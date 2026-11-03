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
import androidx.lifecycle.lifecycleScope
import com.example.bakerys.databinding.FragmentProfilBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@kotlinx.serialization.InternalSerializationApi
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

        // 2. Listener Navigasi
        binding.btnResepDisimpan.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TersimpanFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnDraftResep.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DraftFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnDisukai.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DisukaiFragment())
                .addToBackStack(null)
                .commit()
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
        setupPreferensiListeners(view)
    }

    override fun onResume() {
        super.onResume()
        // Mengisi & merefresh data profil dari Supabase setiap kali fragment ini aktif/kembali dari Edit Profile
        loadProfilUser()
    }

    private fun loadProfilUser() {
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

                val rootView = view ?: return@launch
                val tvNama = rootView.findViewById<TextView>(R.id.tvNamaUser)
                    ?: rootView.findViewById<TextView>(R.id.tvNamaUser)
                    ?: rootView.findViewById<TextView>(R.id.etUsernameReg)

                val tvBio = rootView.findViewById<TextView>(R.id.tvBioUser)
                    ?: rootView.findViewById<TextView>(R.id.tvBioUser)
                    ?: rootView.findViewById<TextView>(R.id.etBioEdit)

                tvNama?.text = username
                tvBio?.text = bioText

            } catch (e: Exception) {
                android.util.Log.e("BAKERYS_DEBUG", "Error load profil: ${e.message}")
            }
        }
    }

    private fun setupPreferensiListeners(rootView: View) {
        val btnMetric = rootView.findViewById<LinearLayout>(R.id.btnMetric)
        val tvMetric = rootView.findViewById<TextView>(R.id.tvMetric)
        val btnImperial = rootView.findViewById<LinearLayout>(R.id.btnImperial)
        val tvImperial = rootView.findViewById<TextView>(R.id.tvImperial)

        val btnCelcius = rootView.findViewById<LinearLayout>(R.id.btnCelcius)
        val tvCelcius = rootView.findViewById<TextView>(R.id.tvCelcius)
        val btnFahrenheit = rootView.findViewById<LinearLayout>(R.id.btnFahrenheit)
        val tvFahrenheit = rootView.findViewById<TextView>(R.id.tvFahrenheit)

        btnMetric?.setOnClickListener {
            if (tvMetric != null && btnImperial != null && tvImperial != null) {
                setOptionSelected(btnMetric, tvMetric, btnImperial, tvImperial)
            }
            savePreference("unit_preference", "metric")
            Toast.makeText(requireContext(), "Satuan diubah ke Metric (Gram/ml)", Toast.LENGTH_SHORT).show()
        }

        btnImperial?.setOnClickListener {
            if (tvImperial != null && btnMetric != null && tvMetric != null) {
                setOptionSelected(btnImperial, tvImperial, btnMetric, tvMetric)
            }
            savePreference("unit_preference", "imperial")
            Toast.makeText(requireContext(), "Satuan diubah ke Sendok (Cangkir/ons)", Toast.LENGTH_SHORT).show()
        }

        btnCelcius?.setOnClickListener {
            if (tvCelcius != null && btnFahrenheit != null && tvFahrenheit != null) {
                setOptionSelected(btnCelcius, tvCelcius, btnFahrenheit, tvFahrenheit)
            }
            savePreference("temp_preference", "celcius")
            Toast.makeText(requireContext(), "Standar suhu diubah ke Celcius (°C)", Toast.LENGTH_SHORT).show()
        }

        btnFahrenheit?.setOnClickListener {
            if (tvFahrenheit != null && btnCelcius != null && tvCelcius != null) {
                setOptionSelected(btnFahrenheit, tvFahrenheit, btnCelcius, tvCelcius)
            }
            savePreference("temp_preference", "fahrenheit")
            Toast.makeText(requireContext(), "Standar suhu diubah ke Fahrenheit (°F)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSavedPreferences() {
        val rootView = view ?: return
        val btnMetric = rootView.findViewById<LinearLayout>(R.id.btnMetric)
        val tvMetric = rootView.findViewById<TextView>(R.id.tvMetric)
        val btnImperial = rootView.findViewById<LinearLayout>(R.id.btnImperial)
        val tvImperial = rootView.findViewById<TextView>(R.id.tvImperial)

        val btnCelcius = rootView.findViewById<LinearLayout>(R.id.btnCelcius)
        val tvCelcius = rootView.findViewById<TextView>(R.id.tvCelcius)
        val btnFahrenheit = rootView.findViewById<LinearLayout>(R.id.btnFahrenheit)
        val tvFahrenheit = rootView.findViewById<TextView>(R.id.tvFahrenheit)

        val savedUnit = sharedPreferences.getString("unit_preference", "metric")
        if (savedUnit == "imperial" && btnImperial != null && tvImperial != null && btnMetric != null && tvMetric != null) {
            setOptionSelected(btnImperial, tvImperial, btnMetric, tvMetric)
        } else if (btnMetric != null && tvMetric != null && btnImperial != null && tvImperial != null) {
            setOptionSelected(btnMetric, tvMetric, btnImperial, tvImperial)
        }

        val savedTemp = sharedPreferences.getString("temp_preference", "celcius")
        if (savedTemp == "fahrenheit" && btnFahrenheit != null && tvFahrenheit != null && btnCelcius != null && tvCelcius != null) {
            setOptionSelected(btnFahrenheit, tvFahrenheit, btnCelcius, tvCelcius)
        } else if (btnCelcius != null && tvCelcius != null && btnFahrenheit != null && tvFahrenheit != null) {
            setOptionSelected(btnCelcius, tvCelcius, btnFahrenheit, tvFahrenheit)
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