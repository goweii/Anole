package per.goweii.android.anole.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.content.res.use
import per.goweii.android.anole.R


class IconCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : CardView(context, attrs) {
    private val imageView = AppCompatImageView(context)

    init {
        elevation = 0F
        cardElevation = 0F
        setCardBackgroundColor(Color.TRANSPARENT)
        radius = Float.MAX_VALUE / 2F
        addViewInLayout(
            imageView.apply {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                val typedValue = TypedValue()
                if (context.theme.resolveAttribute(
                        android.R.attr.selectableItemBackground,
                        typedValue,
                        true
                    )
                ) {
                    setBackgroundResource(typedValue.resourceId)
                }
                minimumWidth = getSuggestedMinimumWidth()
                minimumHeight = getSuggestedMinimumHeight()
            },
            childCount,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.CENTER
            }
        )
        context.obtainStyledAttributes(attrs, R.styleable.IconCardView)
            .use { typedArray ->
                val resourceId = typedArray.getResourceId(R.styleable.IconCardView_android_src, 0)
                if (resourceId > 0) {
                    imageView.setImageResource(resourceId)
                }
            }
    }

    override fun getSuggestedMinimumWidth(): Int {
        return context.resources.getDimensionPixelOffset(R.dimen.dimenIconButtonSizeDefault)
    }

    override fun getSuggestedMinimumHeight(): Int {
        return context.resources.getDimensionPixelOffset(R.dimen.dimenIconButtonSizeDefault)
    }
}