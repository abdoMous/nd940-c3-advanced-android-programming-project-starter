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
    private var progressWidth = 0
    private var angle = 0

    private var rectangleAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when(new){
            ButtonState.Loading -> {
                rectangleAnimator = ValueAnimator.ofInt(0, widthSize).apply {
                    duration = 2000
                    addUpdateListener { valueAnimator ->
                        progressWidth = animatedValue as Int
                        valueAnimator.repeatCount = ValueAnimator.INFINITE
                        valueAnimator.repeatMode = ValueAnimator.REVERSE
                        invalidate()
                    }
                    start()
                }
                circleAnimator = ValueAnimator.ofInt(0, 360).apply {
                    duration  = 2000
                    addUpdateListener { valueAnimator ->
                        angle = valueAnimator.animatedValue as Int
                        valueAnimator.repeatCount = ValueAnimator.INFINITE
                        invalidate()
                    }
                    start()
                }
            }
            ButtonState.Completed -> {
                rectangleAnimator.end()
                progressWidth = 0
                circleAnimator.end()
                angle = 0
                invalidate()
            }
        }
    }

    init {
        isClickable = true
    }


    private val paint = Paint()
    override fun onDraw(canvas: Canvas) {

        // draw fixed rectangle

        paint.color = getColor(context, R.color.colorPrimary)
        canvas.drawRect(
                0f,
                0f,
                widthSize.toFloat(),
                heightSize.toFloat(), paint)

        //draw dynamic rectangle

        paint.color = getColor(context, R.color.colorPrimaryDark)
        canvas.drawRect(
                0f,
                0f,
                widthSize.toFloat() * progressWidth /100,
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

        paint.color=Color.YELLOW
        canvas.drawArc((widthSize - 100f),(heightSize / 2) - 50f, (widthSize-50f),
                (heightSize / 2) + 50f, 0F,angle.toFloat(), true,paint)

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