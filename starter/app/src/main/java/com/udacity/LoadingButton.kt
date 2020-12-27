package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        addUpdateListener {
//            width = it.animatedValue as Float
            invalidate()
        }
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        duration = 5000
        start()
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when(new){
            ButtonState.Clicked -> {

            }
            ButtonState.Loading -> {

            }
            ButtonState.Completed -> {

            }
        }
//        invalidate()
    }


    init {
        isClickable = true
    }


    private val paint = Paint()
    override fun onDraw(canvas: Canvas) {

        paint.color = getColor(context, R.color.colorPrimary)
        canvas.drawRect(
                0f,
                0f,
                widthSize.toFloat(),
                heightSize.toFloat(), paint)

        paint.color = Color.WHITE
        paint.textSize = 60f
        paint.textAlign = Paint.Align.CENTER


        val buttonLabel = when(buttonState){
            ButtonState.Clicked -> "Clicked"
            ButtonState.Loading -> "We are loading"
            ButtonState.Completed -> "Download"
        }
        canvas.drawText(
                buttonLabel,
                (widthSize /2).toFloat(),
                (heightSize/2).toFloat(), paint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        if(super.performClick()) return true

        invalidate()
        return true
    }

}