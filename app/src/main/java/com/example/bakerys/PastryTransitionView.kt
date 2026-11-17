package com.example.bakerys

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.random.Random

class PastryTransitionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private data class PastryItem(
        val xRatio: Float,
        val yRatio: Float,
        val emoji: String,
        val sizeMultiplier: Float,
        val rotation: Float,
        val stagger: Float
    )

    private val items = mutableListOf<PastryItem>()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }
    private val bgPaint = Paint().apply {
        color = Color.parseColor("#FCEBE5")
    }

    private val emojis = listOf("🥐", "🧁", "🍩", "🍪", "🍰", "🥨", "🍓", "🥧")
    private var progress = 0f

    init {
        visibility = GONE
        val cols = 6
        val rows = 13
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                items.add(
                    PastryItem(
                        xRatio = (c.toFloat() / (cols - 1)) + (Random.nextFloat() - 0.5f) * 0.14f,
                        yRatio = (r.toFloat() / (rows - 1)) + (Random.nextFloat() - 0.5f) * 0.08f,
                        emoji = emojis.random(),
                        sizeMultiplier = Random.nextFloat() * 0.35f + 1.15f,
                        rotation = Random.nextFloat() * 44f - 22f,
                        stagger = Random.nextFloat() * 0.2f
                    )
                )
            }
        }
        items.shuffle()
    }

    // Fase 1 (Di LoginActivity): Kue muncul menutupi seluruh layar Login
    fun animateCover(onCovered: () -> Unit) {
        visibility = VISIBLE
        progress = 0f
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 650
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
                if (progress >= 1f) {
                    onCovered()
                }
            }
            start()
        }
    }

    // Fase 2 (Di MainActivity): Layar sudah tertutup kue, lalu menyibak membuka Beranda
    fun animateReveal() {
        visibility = VISIBLE
        progress = 1f
        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 750
            startDelay = 120
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
                if (progress <= 0f) {
                    visibility = GONE
                }
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (visibility != VISIBLE || width == 0 || height == 0) return

        bgPaint.alpha = (progress * 255).toInt().coerceIn(0, 255)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val baseEmojiSize = width * 0.27f

        for (item in items) {
            val scale = ((progress - item.stagger * 0.3f) / 0.75f).coerceIn(0f, 1.1f)
            if (scale > 0.01f) {
                val cx = item.xRatio * width
                val cy = item.yRatio * height
                val finalSize = baseEmojiSize * item.sizeMultiplier * scale

                textPaint.textSize = finalSize
                textPaint.alpha = (scale.coerceIn(0f, 1f) * 255).toInt()

                canvas.save()
                canvas.rotate(item.rotation * scale, cx, cy)
                canvas.drawText(item.emoji, cx, cy + finalSize * 0.35f, textPaint)
                canvas.restore()
            }
        }
    }
}