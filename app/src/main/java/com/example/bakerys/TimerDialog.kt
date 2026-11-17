package com.example.bakerys

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import java.util.Locale

class TimerDialog : DialogFragment() {

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false

    // Default 5 menit (bisa diubah bebas oleh user)
    private var startTimeInMillis: Long = 5 * 60 * 1000L
    private var timeLeftInMillis: Long = startTimeInMillis

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var tvCountdown: TextView
    private lateinit var etMinutes: EditText
    private lateinit var etSeconds: EditText
    private lateinit var btnSetCustom: MaterialButton
    private lateinit var btnPreset1m: MaterialButton
    private lateinit var btnPreset5m: MaterialButton
    private lateinit var btnPreset15m: MaterialButton
    private lateinit var btnPreset30m: MaterialButton
    private lateinit var btnStart: Button
    private lateinit var btnRestart: Button
    private lateinit var btnClose: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_timer, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCountdown = view.findViewById(R.id.tvTimerCountdown)
        etMinutes = view.findViewById(R.id.etTimerMinutes)
        etSeconds = view.findViewById(R.id.etTimerSeconds)
        btnSetCustom = view.findViewById(R.id.btnSetTimerCustom)

        btnPreset1m = view.findViewById(R.id.btnPreset1m)
        btnPreset5m = view.findViewById(R.id.btnPreset5m)
        btnPreset15m = view.findViewById(R.id.btnPreset15m)
        btnPreset30m = view.findViewById(R.id.btnPreset30m)

        btnStart = view.findViewById(R.id.btnStartTimer)
        btnRestart = view.findViewById(R.id.btnRestartTimer)
        btnClose = view.findViewById(R.id.btnCloseTimer)

        updateCountDownText()

        // 1. Terapkan Waktu Kustom dari Input EditText
        btnSetCustom.setOnClickListener {
            stopAlarmSound()
            applyCustomTimeFromInput()
        }

        // Listener perubahan teks otomatis agar praktis
        val autoUpdateWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isTimerRunning) {
                    val m = etMinutes.text.toString().toLongOrNull() ?: 0L
                    val sec = etSeconds.text.toString().toLongOrNull() ?: 0L
                    if (m > 0 || sec > 0) {
                        startTimeInMillis = (m * 60 + sec) * 1000L
                        timeLeftInMillis = startTimeInMillis
                        updateCountDownText()
                    }
                }
            }
        }
        etMinutes.addTextChangedListener(autoUpdateWatcher)
        etSeconds.addTextChangedListener(autoUpdateWatcher)

        // 2. Tombol Cepat (Preset Chips)
        btnPreset1m.setOnClickListener { setPresetMinutes(1) }
        btnPreset5m.setOnClickListener { setPresetMinutes(5) }
        btnPreset15m.setOnClickListener { setPresetMinutes(15) }
        btnPreset30m.setOnClickListener { setPresetMinutes(30) }

        // 3. Tombol Start / Pause
        btnStart.setOnClickListener {
            stopAlarmSound()
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        // 4. Tombol Reset
        btnRestart.setOnClickListener {
            stopAlarmSound()
            resetTimer()
        }

        // 5. Tombol Tutup
        btnClose.setOnClickListener {
            stopAlarmSound()
            dismiss()
        }
    }

    private fun setPresetMinutes(minutes: Long) {
        stopAlarmSound()
        pauseTimer()
        startTimeInMillis = minutes * 60 * 1000L
        timeLeftInMillis = startTimeInMillis
        etMinutes.setText(String.format(Locale.getDefault(), "%02d", minutes))
        etSeconds.setText("00")
        updateCountDownText()
        Toast.makeText(context, "Timer diset ke $minutes menit", Toast.LENGTH_SHORT).show()
    }

    private fun applyCustomTimeFromInput() {
        pauseTimer()
        val m = etMinutes.text.toString().toLongOrNull() ?: 0L
        val sec = etSeconds.text.toString().toLongOrNull() ?: 0L

        if (m == 0L && sec == 0L) {
            Toast.makeText(context, "Masukkan waktu minimal 1 detik!", Toast.LENGTH_SHORT).show()
            return
        }

        startTimeInMillis = (m * 60 + sec) * 1000L
        timeLeftInMillis = startTimeInMillis
        updateCountDownText()
        Toast.makeText(context, "Waktu timer berhasil diset!", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun startTimer() {
        if (timeLeftInMillis == 0L) {
            val m = etMinutes.text.toString().toLongOrNull() ?: 0L
            val sec = etSeconds.text.toString().toLongOrNull() ?: 0L
            if (m > 0 || sec > 0) {
                startTimeInMillis = (m * 60 + sec) * 1000L
                timeLeftInMillis = startTimeInMillis
            } else if (startTimeInMillis > 0) {
                timeLeftInMillis = startTimeInMillis
            }
        }

        if (timeLeftInMillis == 0L) {
            Toast.makeText(context, "Silakan atur waktu timer terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                isTimerRunning = false
                btnStart.text = "Mulai Timer"

                timeLeftInMillis = 0L
                updateCountDownText()

                playAlarmSound()
                Toast.makeText(context, "⏰ Waktu Memasak Habis! Kue sudah matang.", Toast.LENGTH_LONG).show()
            }
        }.start()

        isTimerRunning = true
        btnStart.text = "Jeda (Pause)"
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        btnStart.text = "Lanjutkan"
    }

    private fun resetTimer() {
        stopAlarmSound()
        countDownTimer?.cancel()
        isTimerRunning = false
        startTimeInMillis = 0L
        timeLeftInMillis = 0L
        etMinutes.setText("")
        etSeconds.setText("")
        updateCountDownText()
        btnStart.text = "Mulai Timer"
        Toast.makeText(context, "Timer direset ke 00:00", Toast.LENGTH_SHORT).show()
    }

    private fun updateCountDownText() {
        val totalSeconds = timeLeftInMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        tvCountdown.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun playAlarmSound() {
        stopAlarmSound()
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.timer_sound)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarmSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        stopAlarmSound()
    }
}
