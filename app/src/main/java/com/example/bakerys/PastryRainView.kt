package com.example.bakerys

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin
import kotlin.random.Random

class PastryRainView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private data class PastryDrop(
        var x: Float,
        var y: Float,
        val emoji: String,
        val size: Float,
        val speedY: Float,
        var rotation: Float,
        val rotationSpeed: Float,
        var swayPhase: Float
    )

    private val drops = mutableListOf<PastryDrop>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        alpha = 185 // Sedikit transparan agar teks Beranda tetap mudah dibaca
    }

    private val pastryEmojis = listOf("🥐", "🧁", "🍩", "🍪", "🍰", "🍓", "🥨")

    init {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                updateDrops()
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drops.clear()
        if (w > 0 && h > 0) {
            repeat(18) {
                drops.add(
                    PastryDrop(
                        x = Random.nextFloat() * w,
                        y = Random.nextFloat() * h - h, // Mulai tersebar dari atas layar
                        emoji = pastryEmojis.random(),
                        size = Random.nextFloat() * 26f + 32f,
                        speedY = Random.nextFloat() * 2.5f + 1.5f,
                        rotation = Random.nextFloat() * 360f,
                        rotationSpeed = Random.nextFloat() * 2.4f - 1.2f,
                        swayPhase = Random.nextFloat() * 6.28f
                    )
                )
            }
        }
    }

    private fun updateDrops() {
        val h = height.toFloat()
        val w = width.toFloat()
        if (h <= 0 || w <= 0) return

        for (d in drops) {
            d.y += d.speedY
            d.x += sin(d.swayPhase) * 0.9f
            d.swayPhase += 0.04f
            d.rotation += d.rotationSpeed

            // Jika sudah jatuh melewati batas bawah layar, muncul lagi dari atas
            if (d.y > h + 60f) {
                d.y = -60f
                d.x = Random.nextFloat() * w
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (d in drops) {
            paint.textSize = d.size
            canvas.save()
            canvas.rotate(d.rotation, d.x, d.y)
            canvas.drawText(d.emoji, d.x, d.y, paint)
            canvas.restore()
        }
    }
}