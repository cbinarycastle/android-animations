package io.github.cbinarycastle.androidanimations.behavior

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginTop
import io.github.cbinarycastle.androidanimations.R
import kotlin.math.sign

private const val DEFAULT_ENTER_ANIMATION_DURATION_MS = 225
private const val DEFAULT_EXIT_ANIMATION_DURATION_MS = 175
private const val DEFAULT_SCROLLING_THRESHOLD = 0

class HideTopViewOnScrollBehavior<V : View>(
    context: Context,
    attrs: AttributeSet?,
) : CoordinatorLayout.Behavior<V>(context, attrs) {

    private var enterAnimDuration = DEFAULT_ENTER_ANIMATION_DURATION_MS
    private var exitAnimDuration = DEFAULT_EXIT_ANIMATION_DURATION_MS
    private var scrollingUpThreshold = DEFAULT_SCROLLING_THRESHOLD
    private var scrollingDownThreshold = DEFAULT_SCROLLING_THRESHOLD

    private var currentAnimator: ViewPropertyAnimator? = null
    private var currentScrollState: ScrollState = ScrollState.UP
    private var dyConsumed = 0

    init {
        if (attrs != null) {
            initAttrs(context, attrs)
        }
    }

    private fun initAttrs(context: Context, attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.HideTopViewOnScrollBehavior).use {
            scrollingUpThreshold = it.getInt(
                R.styleable.HideTopViewOnScrollBehavior_behavior_scrollingUpThreshold,
                DEFAULT_SCROLLING_THRESHOLD
            )
            scrollingDownThreshold = it.getInt(
                R.styleable.HideTopViewOnScrollBehavior_behavior_scrollingDownThreshold,
                DEFAULT_SCROLLING_THRESHOLD
            )
            enterAnimDuration = it.getInt(
                R.styleable.HideTopViewOnScrollBehavior_behavior_enterAnimDuration,
                DEFAULT_ENTER_ANIMATION_DURATION_MS
            )
            exitAnimDuration = it.getInt(
                R.styleable.HideTopViewOnScrollBehavior_behavior_exitAnimDuration,
                DEFAULT_EXIT_ANIMATION_DURATION_MS
            )
        }
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean = axes == CoordinatorLayout.SCROLL_AXIS_VERTICAL

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (this.dyConsumed == 0 || dyConsumed.sign == this.dyConsumed.sign) {
            this.dyConsumed += dyConsumed
        } else {
            this.dyConsumed = 0
        }

        if (dyConsumed > 0 && this.dyConsumed >= scrollingDownThreshold) {
            this.dyConsumed = 0
            slideUp(child)
        } else if (dyConsumed < 0 && this.dyConsumed <= -scrollingUpThreshold) {
            this.dyConsumed = 0
            slideDown(child)
        }

        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
    }

    private fun slideUp(child: V) {
        if (currentScrollState == ScrollState.DOWN) {
            return
        }
        currentScrollState = ScrollState.DOWN

        animateChildTo(
            child = child,
            targetY = -(child.measuredHeight + child.marginTop).toFloat(),
            duration = enterAnimDuration
        )
    }

    private fun slideDown(child: V) {
        if (currentScrollState == ScrollState.UP) {
            return
        }
        currentScrollState = ScrollState.UP

        animateChildTo(
            child = child,
            targetY = 0f,
            duration = exitAnimDuration
        )
    }

    private fun animateChildTo(child: V, targetY: Float, duration: Int) {
        currentAnimator?.cancel()
        child.clearAnimation()

        currentAnimator = child.animate()
            .translationY(targetY)
            .setDuration(duration.toLong())
            .setListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        currentAnimator = null
                    }
                }
            )
            .also { it.start() }
    }

    private enum class ScrollState {
        UP, DOWN
    }
}