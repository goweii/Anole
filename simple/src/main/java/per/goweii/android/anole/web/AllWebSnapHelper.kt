package per.goweii.android.anole.web

import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class AllWebSnapHelper(
    private val viewPager2: ViewPager2
) : LinearSnapHelper() {
    private var recyclerView: RecyclerView? = null

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        return if (viewPager2.isFakeDragging) null else super.findSnapView(layoutManager)
    }

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = recyclerView
        super.attachToRecyclerView(recyclerView)
    }

    override fun createScroller(layoutManager: RecyclerView.LayoutManager?): RecyclerView.SmoothScroller? {
        if (layoutManager !is ScrollVectorProvider) return null
        val recyclerView = recyclerView ?: return null
        return LinearSmoothScrollerImpl(recyclerView)
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if (layoutManager !is ScrollVectorProvider) {
            return RecyclerView.NO_POSITION
        }
        val itemCount = layoutManager.itemCount
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }
        val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        val currentPosition = layoutManager.getPosition(currentView)
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        val targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        if (targetPos == RecyclerView.NO_POSITION) {
            return currentPosition
        }
        return targetPos
    }

    private inner class LinearSmoothScrollerImpl(
        private val recyclerView: RecyclerView
    ) : LinearSmoothScroller(recyclerView.context) {
        override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
            val layoutManager = recyclerView.layoutManager ?: return
            val snapDistances = calculateDistanceToFinalSnap(layoutManager, targetView) ?: return
            val dx = snapDistances[0]
            val dy = snapDistances[1]
            val time = calculateTimeForDeceleration(max(abs(dx), abs(dy)))
            if (time > 0) {
                action.update(dx, dy, time, mDecelerateInterpolator)
            }
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return 30F / displayMetrics.densityDpi
        }

        override fun calculateTimeForScrolling(dx: Int): Int {
            return min(
                300,
                super.calculateTimeForScrolling(dx)
            )
        }
    }
}