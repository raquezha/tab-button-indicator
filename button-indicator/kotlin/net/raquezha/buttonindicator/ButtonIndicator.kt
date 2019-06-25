package net.raquezha.buttonindicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import kotlinx.android.synthetic.main.tab_item.view.*
import net.raquezha.buttonindicator.ext.getColorInt
import net.raquezha.buttonindicator.ext.getDrawableById
import net.raquezha.roundedtabbuttonindicator.lib.R
import android.util.StateSet
import android.util.Log
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.widget.TextViewCompat


/**
 *  @author raquezha
 *  @date   06/25/19
 */
class ButtonIndicator(ctx: Context, attrs: AttributeSet) : ViewGroup(ctx, attrs) {

    private var paint: Paint = Paint()
    private var index = 0
    private var precision = 0f
    private var buttonCount = 0

    private var selectedTextColorInt = DEFAULT_SELECTED_TEXT_COLOR
    private var normalTextColorInt = DEFAULT_TEXT_COLOR
    private var selectedBackgroundInt = DEFAULT_SELECTED_BACKGROUND_COLOR
    private var strokeColorInt = DEFAULT_STROKE_COLOR
    private var strokeWidth = DEFAULT_STROKE_WIDTH
    private var textAppearance = 0
    private var textSize = DEFAULT_TEXT_SIZE


    private var viewNameList = arrayListOf<String>()
    private var viewLabelList = arrayListOf<String>()

    private var cornerRadius = 0f
    private var cornerRadiusBottomLeft = 8f
    private var cornerRadiusBottomRight = 8f
    private var cornerRadiusTopLeft = 8f
    private var cornerRadiusTopRight = 8f

    companion object{
        private val DEFAULT_COLOR = Color.GRAY
        private val DEFAULT_CORNER_RADIUS = 0f
        private val DEFAULT_STROKE_WIDTH = 1f
        private val DEFAULT_SELECTED_TEXT_COLOR = Color.WHITE
        private val DEFAULT_SELECTED_BACKGROUND_COLOR = DEFAULT_COLOR
        private val DEFAULT_STROKE_COLOR = DEFAULT_COLOR
        private val DEFAULT_TEXT_COLOR= DEFAULT_COLOR
        private val DEFAULT_TEXT_SIZE = 16
        private val TRANSPARENT = Color.parseColor("#00FFFFFF")
    }

    init {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ButtonIndicator)

        val count = attributes.indexCount
        Log.e("ButtonIndicator", "attribute count: $count")
        for (i in 0 until count) {
            when (val attr = attributes.getIndex(i)) {
                R.styleable.ButtonIndicator_cornerRadius-> {
                    Log.e("ButtonIndicator", "getting corner radius")
                    cornerRadius = attributes.getFloat(attr, DEFAULT_CORNER_RADIUS)
                }
                R.styleable.ButtonIndicator_cornerRadiusBottomLeft -> {
                    cornerRadiusBottomLeft = attributes.getFloat(attr,  DEFAULT_CORNER_RADIUS)
                }
                R.styleable.ButtonIndicator_cornerRadiusBottomRight-> {
                    cornerRadiusBottomRight = attributes.getFloat(attr, DEFAULT_CORNER_RADIUS)
                }
                R.styleable.ButtonIndicator_cornerRadiusTopLeft -> {
                    cornerRadiusTopLeft = attributes.getFloat(attr, DEFAULT_CORNER_RADIUS)
                }
                R.styleable.ButtonIndicator_cornerRadiusTopRight -> {
                    cornerRadiusTopRight = attributes.getFloat(attr, DEFAULT_CORNER_RADIUS)
                }
                R.styleable.ButtonIndicator_selectedTextColor -> {
                    selectedTextColorInt = attributes.getColor(attr, DEFAULT_SELECTED_TEXT_COLOR)
                }
                R.styleable.ButtonIndicator_normalTextColor-> {
                    normalTextColorInt = attributes.getColor(attr, DEFAULT_TEXT_COLOR)
                }
                R.styleable.ButtonIndicator_selectedBackground-> {
                    selectedBackgroundInt = attributes.getColor(attr, DEFAULT_SELECTED_BACKGROUND_COLOR)
                }
                R.styleable.ButtonIndicator_strokeColor-> {
                    strokeColorInt = attributes.getColor(attr, DEFAULT_STROKE_COLOR)
                }
                R.styleable.ButtonIndicator_strokeWidth-> {
                    strokeWidth = attributes.getFloat(attr, DEFAULT_STROKE_WIDTH)
                }
                R.styleable.ButtonIndicator_android_textAppearance -> {
                    textAppearance = attributes.getResourceId(attr, 0)
                }
                R.styleable.ButtonIndicator_android_textSize-> {
                    textSize = attributes.getDimensionPixelSize(attr, DEFAULT_TEXT_SIZE)
                }
            }
        }

        Log.e("ButtonIndicator", "textSize: $textSize")
        Log.e("ButtonIndicator", "cornerRadius: $cornerRadius")

        if(cornerRadius != 0f) {
            cornerRadiusBottomLeft = cornerRadius
            cornerRadiusBottomRight = cornerRadius
            cornerRadiusTopRight = cornerRadius
            cornerRadiusTopLeft = cornerRadius
        }

        attributes.recycle()
    }

    fun initializeButtons(viewLabelList: ArrayList<String>) {
        this.viewLabelList = viewLabelList
        buttonCount = viewLabelList.size

        initEssentials()
        initializeTransactionButton()
    }

    private fun initializeTransactionButton() {

        for ((index, viewName) in viewLabelList.withIndex()) {
            val view = createViewTab(viewName, getViewDrawable(index), getInitialViewColor(index))
            addView(view)
            viewNameList.add(viewName.toLowerCase())
        }
    }

    interface TransactionIndicatorListener {
        fun onClickButton(name: String, index: Int)
    }

    private fun initEssentials() {
        setWillNotDraw(false)
        paint = Paint()
        paint.color = selectedBackgroundInt
        viewNameList = arrayListOf()
    }

    fun setListener(listener: TransactionIndicatorListener) {
        for (index in 0 until childCount) {
            val view = getChildAt(index)
            view.setOnClickListener {
                listener.onClickButton(viewLabelList[index], index)
            }
        }
    }

    private fun selectView(view: View, drawable: Drawable) {
        val tvTab = view.tvTabName
        tvTab.setTextColor(selectedTextColorInt)
        view.background = drawable
    }

    private fun unSelectView(view: View, drawable: Drawable) {
        val tvTab = view.tvTabName
        tvTab.setTextColor(normalTextColorInt)
        view.background = drawable
    }

    private fun createViewTab(tabName: String, drawable: Drawable, colorInt: Int = normalTextColorInt): View {
        val view = View.inflate(this.context, R.layout.tab_item, null)
        val tvTab = view.tvTabName
        tvTab.setTextColor(colorInt)
        tvTab.text = tabName
        view.background = drawable

        if(textAppearance != 0) {
            TextViewCompat.setTextAppearance(tvTab, textAppearance)
        }

        if(textSize != DEFAULT_TEXT_SIZE) {
            tvTab.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }


        return view
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val childWidth = width / buttonCount
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        for (i in 0 until childCount) {
            getChildAt(i).measure(
                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childW = (right - left) / buttonCount
        val childH = (bottom - top)
        for (i in 0 until childCount) {
            getChildAt(i).layout(i * childW, 0, (i + 1) * childW, childH)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val preciseIndex = if (buttonCount == 2) {
            if (index == 1) 3f else 0f
        } else if (buttonCount == 3) {
            if (index == 2) 3f else index + precision
        } else {
            index + precision
        }

        when {
            preciseIndex < 1f -> {
                canvas?.drawRect(
                    ((index + precision) * width / buttonCount) + 24, 0f, ((1 + index + precision) *
                            width / buttonCount), height.toFloat(), paint
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas?.drawRoundRect(
                        (index + precision) * width / buttonCount,
                        0f,
                        (1 + index + precision) * width / buttonCount,
                        height.toFloat(),
                        24f,
                        24f,
                        paint
                    )
                }
            }
            preciseIndex in 1f..2f -> canvas?.drawRect(
                ((index + precision) * width / buttonCount),
                0f,
                ((1 + index + precision) * width / buttonCount),
                height.toFloat(),
                paint
            )
            preciseIndex > 2 -> {
                canvas?.drawRect(
                    ((index + precision) * width / buttonCount), 0f, ((1 + index + precision) * width /
                            buttonCount) - 24, height.toFloat(), paint
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas?.drawRoundRect(
                        (index + precision) * width / buttonCount, 0f, ((1 + index + precision) * width
                                / buttonCount), height.toFloat(), 24f, 24f, paint
                    )
                }
            }
        }
    }

    private fun getViewDrawable(index: Int): Drawable {
        return getTabButtonDrawable(when (index) {
            0 -> TabButtonType.START
            viewLabelList.lastIndex -> TabButtonType.END
            else -> TabButtonType.MIDDLE
        }, TRANSPARENT, strokeColorInt)
    }

    private fun getViewDrawableSelected(index: Int): Drawable {
        return getTabButtonDrawable(when (index) {
            0 -> TabButtonType.START
            viewLabelList.lastIndex -> TabButtonType.END
            else -> TabButtonType.MIDDLE
        }, selectedBackgroundInt, strokeColorInt)
    }

    enum class TabButtonType {
        START,
        MIDDLE,
        END
    }

    private fun getTabButtonDrawable(
        type: TabButtonType,
        solidColor: Int,
        strokeColor: Int
    ): StateListDrawable {
        val out = StateListDrawable()
        var paddingLeft= -1
        var paddingRight= -1
        val layerDrawable =
            when(type) {
                TabButtonType.START -> {
                    paddingLeft = 0
                    paddingRight = -1
                    createStrokeDrawable(solidColor,
                        strokeColor,
                        strokeWidth,
                        cornerRadiusTopLeft,
                        0f,
                        0f,
                        cornerRadiusBottomLeft)
                }
                TabButtonType.MIDDLE -> {
                    paddingLeft = 0
                    paddingRight = 0
                    createStrokeDrawable(solidColor,
                        strokeColor,
                        strokeWidth,
                        0f,
                        0f,
                        0f,
                        0f)
                }

                TabButtonType.END -> {
                    paddingLeft = -1
                    paddingRight = 0
                    createStrokeDrawable(solidColor,
                        strokeColor,
                        strokeWidth,
                        0f,
                        cornerRadiusTopRight,
                        cornerRadiusBottomRight,
                        0f)
                }

            }

        layerDrawable.setLayerInset(0,paddingLeft, 0,paddingRight, 0)
        out.addState(StateSet.NOTHING, layerDrawable)
        return out
    }

    private fun createStrokeDrawable(
        solidColor: Int,
        strokeColor: Int,
        strokeWidth: Float,
        cornerRadiusTopLeft: Float,
        cornerRadiusTopRight: Float,
        cornerRadiusBottomRight: Float,
        cornerRadiusBottomLeft: Float): LayerDrawable {

        val cornerRadius = FloatArray(8)
        cornerRadius[0] = cornerRadiusTopLeft
        cornerRadius[1] = cornerRadiusTopLeft
        cornerRadius[2] = cornerRadiusTopRight
        cornerRadius[3] = cornerRadiusTopRight
        cornerRadius[4] = cornerRadiusBottomRight
        cornerRadius[5] = cornerRadiusBottomRight
        cornerRadius[6] = cornerRadiusBottomLeft
        cornerRadius[7] = cornerRadiusBottomLeft

        val gradientDrawable = GradientDrawable()

        gradientDrawable.setStroke(strokeWidth.toInt(), strokeColor)

        gradientDrawable.gradientType = GradientDrawable.RECTANGLE
        gradientDrawable.setColor(solidColor)
        gradientDrawable.cornerRadii = cornerRadius

        return LayerDrawable(arrayOf(gradientDrawable))
    }


    private fun getInitialViewColor(index: Int): Int {
        return if (index == 0) selectedTextColorInt else normalTextColorInt
    }

    fun selectView(index: Int) {
        for (indexedValue in children.iterator().withIndex()) {
            if (index == indexedValue.index) {
                selectView(indexedValue.value, getViewDrawableSelected(indexedValue.index))
            } else {
                unSelectView(indexedValue.value, getViewDrawable(indexedValue.index))
            }
        }

        invalidate()
    }

    fun setProgress(index: Int, precision: Float = 0f) {
        this.index = index
        this.precision = precision
        invalidate()
    }

    private fun getColor(id: Int): Int {
        return context.getColorInt(id)
    }

    private fun getString(id: Int): String {
        return context.getString(id)
    }

    private fun getDrawable(id: Int): Drawable? {
        return context.getDrawableById(id)
    }
}