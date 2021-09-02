package com.popmain.droidmedia.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import kotlin.random.Random

/**
 * @FileName: TextureRandomView
 * @Date: 9/2/21 6:32 PM
 * @Description: //TODO
 */
class TextureRandomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr), TextureView.SurfaceTextureListener, Runnable {

    private val text = "这是TextureView"
    @Volatile
    private var isRunning = false
    private val rect = Rect()
    private val bgPaint = Paint()
    private val textPaint = TextPaint()
    private var textRandomX = 0
    private var textRandomY = 0
    private var textWidth = 0f
    private var surface: SurfaceTexture? = null

    init {
        surfaceTextureListener = this
        bgPaint.color = Color.BLACK
        textPaint.color = Color.WHITE
        textPaint.textSize = 24f
        textWidth = textPaint.measureText(text)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("wzx", "onMeasure $measuredWidth , $measuredHeight")
        rect.set(0, 0, measuredWidth, measuredHeight)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        Log.e("wzx", "onSurfaceTextureSizeChanged $width , $height")
        rect.set(0, 0, width, height)
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        isRunning = false
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        this.surface = surface
        Log.e("wzx", "onSurfaceTextureAvailable ${Thread.currentThread().name}")
        isRunning = true
        Thread(this).start()
//        drawContent()
    }

    override fun run() {
        Log.e("wzx", "run isRunning=$isRunning")
        while (isRunning) {
            drawContent()
            Thread.sleep(100)
        }
    }

    private fun drawContent() {
        Log.e("wzx", "drawContent isRunning=$isRunning")
        val canvas = lockCanvas()
        canvas?.let {
            try {
                textRandomX = Random.nextInt(0, rect.width())
                textRandomY = Random.nextInt(24, rect.height() - 24)
                it.drawRect(rect, bgPaint)
                it.drawText(text, rect.width() / 2f - textWidth / 2f, rect.height() / 2f, textPaint)
                it.drawText(text, textRandomX.toFloat(), textRandomY.toFloat(), textPaint)
            } catch (e: Exception) {
                Log.e("wzx", "exception")
                e.printStackTrace()
            } finally {
                unlockCanvasAndPost(it)
            }
        }
    }
}