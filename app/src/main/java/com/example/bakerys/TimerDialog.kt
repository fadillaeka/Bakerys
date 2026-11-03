package com.example.bakerys

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.Locale

class TimerDialog : DialogFragment() {

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false

    // Uji coba di emulator gunakan 5 * 1000, Uji coba 5 detik. Jika sudah oke, ganti ke 5 * 60 * 1000 (5 menit)
    private val startTimeInMillis: Long = 5 * 1000
    private var timeLeftInMillis: Long = startTimeInMillis

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var tvCountdown: TextView
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
        btnStart = view.findViewById(R.id.btnStartTimer)
        btnRestart = view.findViewById(R.id.btnRestartTimer)
        btnClose = view.findViewById(R.id.btnCloseTimer)

        updateCountDownText()

        btnStart.setOnClickListener {
            stopAlarmSound()
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        btnRestart.setOnClickListener {
            stopAlarmSound()
            resetTimer()
        }

        btnClose.setOnClickListener {
            stopAlarmSound()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun startTimer() {
        if (timeLeftInMillis == 0L) {
            timeLeftInMillis = startTimeInMillis
        }

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                isTimerRunning = false
                btnStart.text = "Start"

                // Tetap menampilkan 00:00 saat alarm bunyi
                timeLeftInMillis = 0L
                updateCountDownText()

                playAlarmSound()
                Toast.makeText(context, "Timer Selesai! Waktu Memasak Habis.", Toast.LENGTH_LONG).show()
            }
        }.start()

        isTimerRunning = true
        btnStart.text = "Pause"
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        btnStart.text = "Start"
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        timeLeftInMillis = startTimeInMillis
        updateCountDownText()
        btnStart.text = "Start"
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        tvCountdown.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun playAlarmSound() {
        stopAlarmSound()
        mediaPlayer = MediaPlayer.create(context, R.raw.timer_sound)
        mediaPlayer?.start()
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