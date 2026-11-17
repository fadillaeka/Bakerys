package com.example.bakerys

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin

class WavyBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Layer gelombang belakang (putih semi-transparan sebagai highlight)
    private val backWavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#80FFFFFF") // Putih transparansi 50%
        style = Paint.Style.FILL
    }

    // Layer gelombang utama (putih solid)
    private val frontWavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFF8F9") // Putih hangat senada tema Bakery's
        style = Paint.Style.FILL
    }

    private val wavePath = Path()
    private var phase = 0f

    init {
        // Animasi aliran gelombang terus-menerus
        ValueAnimator.ofFloat(0f, (2 * Math.PI).toFloat()).apply {
            duration = 3500 // 3.5 detik per siklus gelombang
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                phase = animation.animatedValue as Float
                invalidate() // Gambar ulang setiap frame
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()

        // 1. Gambar highlight gelombang belakang
        drawWave(canvas, width, height, amplitude = 28f, frequency = 1.2f, phaseOffset = phase + 1.5f, baseHeight = 55f, paint = backWavePaint)

        // 2. Gambar gelombang putih utama di depan
        drawWave(canvas, width, height, amplitude = 22f, frequency = 1.0f, phaseOffset = phase, baseHeight = 75f, paint = frontWavePaint)
    }

    private fun drawWave(
        canvas: Canvas,
        width: Float,
        height: Float,
        amplitude: Float,
        frequency: Float,
        phaseOffset: Float,
        baseHeight: Float,
        paint: Paint
    ) {
        wavePath.reset()
        wavePath.moveTo(0f, height)
        wavePath.lineTo(0f, baseHeight)

        var x = 0f
        while (x <= width) {
            val y = baseHeight + amplitude * sin((x / width * 2 * Math.PI * frequency) + phaseOffset).toFloat()
            wavePath.lineTo(x, y)
            x += 10f
        }

        wavePath.lineTo(width, height)
        wavePath.close()
        canvas.drawPath(wavePath, paint)
    }
}