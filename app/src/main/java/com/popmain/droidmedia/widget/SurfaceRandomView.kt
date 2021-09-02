package com.popmain.droidmedia.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.random.Random

/**
 * @FileName: SurfaceRandomView
 * @Date: 9/2/21 6:27 PM
 * @Description: //TODO
 */
class SurfaceRandomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback, Runnable {
    private val text = "这是SurfaceView"
    private val mHolder = holder
    @Volatile
    private var isRunning = false
    private val rect = Rect()
    private val bgPaint = Paint()
    private val textPaint = TextPaint()
    private var textRandomX = 0
    private var textRandomY = 0
    private var textWidth = 0f

    init {
        mHolder.addCallback(this)
        bgPaint.color = Color.BLACK
        textPaint.color = Color.WHITE
        textPaint.textSize = 24f
        textWidth = textPaint.measureText(text)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.e("PencilSurfaceView", "surfaceChanged")
        rect.set(0, 0, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.e("PencilSurfaceView", "surfaceDestroyed")
        isRunning = false
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.e("PencilSurfaceView", "surfaceCreated")
        isRunning = true
        Thread(this).start()
    }

    override fun run() {
        while (isRunning) {
            val canvas = mHolder.lockCanvas()
            canvas?.let {
                try {
                    textRandomX = Random.nextInt(0, rect.width())
                    textRandomY = Random.nextInt(24, rect.height() - 24)
                    it.drawRect(rect, bgPaint)
                    it.drawText(text, rect.width() / 2f - textWidth / 2f, rect.height() / 2f, textPaint)
                    it.drawText(text, textRandomX.toFloat(), textRandomY.toFloat(), textPaint)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    mHolder.unlockCanvasAndPost(it)
                }
            }
            Thread.sleep(100)
        }
    }

}