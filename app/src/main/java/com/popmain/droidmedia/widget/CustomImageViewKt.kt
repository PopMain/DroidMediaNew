package com.popmain.droidmedia.widget
import android.content.Context
import android.graphics.*
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.popmain.droidmedia.DroidMediaApplication
import com.popmain.droidmedia.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class CustomImageViewKt : View {
    private var mBitmap: Bitmap? = null
    private val paint = Paint()
    private val dest = Rect()
    private val colorPaint = Paint()

    constructor(context: Context?) : this(context, null,  0)

    constructor(
        context: Context?,
        attributeSet: AttributeSet? = null
    ) : this(context, attributeSet, 0)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private val mainScope = MainScope()

    private fun init() {
        colorPaint.color = Color.RED
        mainScope.launch {
            loadBitmap().collect {
                setBitmapData(it)
            }
        }
    }

    private fun setBitmapData(bitmap: Bitmap) {
        mBitmap = bitmap
        if (Looper.myLooper() == Looper.getMainLooper()) {
//            invalidate()
            requestLayout()
        } else {
//            postInvalidate()
            post { requestLayout() }
        }
    }

    private suspend fun loadBitmap(): Flow<Bitmap> = flow {
        val bitmap = BitmapFactory.decodeResource(
            DroidMediaApplication.getsDroidMediaApplication().resources, R.drawable.image
        )
        Thread.sleep(3000)
        emit(bitmap)
    }.flowOn(Dispatchers.IO)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mBitmap != null) {
            val with = mBitmap!!.width / 2
            val height = mBitmap!!.height / 2
            setMeasuredDimension(with, height)
        } else {
            setMeasuredDimension(
                getSizeDefault(suggestedMinimumWidth, widthMeasureSpec),
                getSizeDefault(suggestedMinimumHeight, heightMeasureSpec)
            )
        }
    }

    private fun getSizeDefault(size: Int, measureSpec: Int): Int {
        var result: Int = size
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        when (specMode) {
            MeasureSpec.UNSPECIFIED, MeasureSpec.AT_MOST-> result = size
            MeasureSpec.EXACTLY -> result = specSize
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mBitmap != null) {
            dest[0, 0, mBitmap!!.width / 2] = mBitmap!!.height / 2
            canvas.drawBitmap(mBitmap!!, null, dest, paint)
        } else {
            dest.set(0, 0, measuredWidth, measuredHeight)
            canvas.drawRect(dest, colorPaint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mainScope.cancel()
    }

}
