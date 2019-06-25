package net.raquezha.buttonindicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import kotlinx.android.synthetic.main.tab_item.view.*
import net.raquezha.buttonindicator.ext.getColorInt
import net.raquezha.buttonindicator.ext.getDrawableById
import net.raquezha.roundedtabbuttonindicator.lib.R

/**
 *  @author raquezha
 *  @date   06/25/19
 */
class ButtonIndicator(ctx: Context, var attr: AttributeSet) : ViewGroup(ctx, attr) {

    private var paint: Paint = Paint()
    private var index = 0
    private var precision = 0f
    private var buttonCount = 0

    private var selectedTextColorInt = getColor(R.color.tabSelectedTextColorInt)
    private var normalTextColorInt = getColor(R.color.tabNormalTextColorInt)
    private var selectedBackgroundInt = normalTextColorInt

    private var viewNameList = arrayListOf<String>()
    private var viewLabelList = arrayListOf<String>()

    fun initializeButtons(viewLabelList: ArrayList<String>) {
        this.viewLabelList = viewLabelList
        buttonCount = viewLabelList.size

        initEssentials()
        initializeTransactionButton()
    }

    private fun initializeTransactionButton() {

        val attributes = context.theme.obtainStyledAttributes(attr, R.styleable.ButtonIndicator, 0, 0)

        val count = attributes.indexCount
        for (i in 0 until count) {
            when (val attrs = attributes.getIndex(i)) {
                R.styleable.ButtonIndicator_cornerRadiusBottomLeft -> {

                }
                R.styleable.ButtonIndicator_cornerRadiusBottomRight-> {

                }
                R.styleable.ButtonIndicator_cornerRadiusTopLeft -> {

                }
                R.styleable.ButtonIndicator_cornerRadiusTopRight-> {

                }
            }
        }

        attributes.recycle()


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
        return when (index) {
            0 -> getDrawable(R.drawable.tab_button_start)!!
            viewLabelList.lastIndex -> getDrawable(R.drawable.tab_button_end)!!
            else -> getDrawable(R.drawable.tab_button_middle)!!
        }
    }

    private fun getViewDrawableSelected(index: Int): Drawable {
        return when (index) {
            0 -> getDrawable(R.drawable.tab_button_start_selected)!!
            viewLabelList.lastIndex -> getDrawable(R.drawable.tab_button_end_selected)!!
            else -> getDrawable(R.drawable.tab_button_middle_selected)!!
        }
    }

    private fun getInitialViewColor(index: Int): Int {
        return if (index == 0) getColor(R.color.tabSelectedTextColorInt) else getColor(R.color.tabNormalTextColorInt)
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