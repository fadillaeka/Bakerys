package com.example.bakerys

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin
import kotlin.random.Random

class BakerySparkleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private data class Particle(
        var x: Float,
        var y: Float,
        val radius: Float,
        val speedY: Float,
        val swayOffset: Float,
        val color: Int,
        var alphaPhase: Float
    )

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val colors = listOf(
        Color.parseColor("#FFD54F"), // Emas karamel
        Color.parseColor("#F48FB1"), // Pink pastel
        Color.parseColor("#FFAB91"), // Peach hangat
        Color.parseColor("#FFF59D")  // Krim vanilla
    )

    init {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                updateParticles()
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        particles.clear()
        if (w > 0 && h > 0) {
            repeat(24) {
                particles.add(
                    Particle(
                        x = Random.nextFloat() * w,
                        y = Random.nextFloat() * h,
                        radius = Random.nextFloat() * 7f + 3f,
                        speedY = Random.nextFloat() * 1.4f + 0.6f,
                        swayOffset = Random.nextFloat() * 6.28f,
                        color = colors.random(),
                        alphaPhase = Random.nextFloat() * 6.28f
                    )
                )
            }
        }
    }

    private fun updateParticles() {
        val h = height.toFloat()
        val w = width.toFloat()
        if (h <= 0 || w <= 0) return

        for (p in particles) {
            p.y -= p.speedY
            p.x += sin(p.alphaPhase) * 0.6f
            p.alphaPhase += 0.05f

            if (p.y < -20f) {
                p.y = h + 20f
                p.x = Random.nextFloat() * w
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (p in particles) {
            paint.color = p.color
            val alpha = ((sin(p.alphaPhase) + 1f) * 0.5f * 170 + 40).toInt().coerceIn(0, 255)
            paint.alpha = alpha
            canvas.drawCircle(p.x, p.y, p.radius, paint)
        }
    }
}