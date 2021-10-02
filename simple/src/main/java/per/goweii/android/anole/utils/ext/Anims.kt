package per.goweii.android.anole.utils.ext

import android.view.animation.Animation

fun Animation.listener(block: AnimListener.() -> Unit) {
    setAnimationListener(AnimListener(block))
}

class AnimListener(block: AnimListener.() -> Unit) : Animation.AnimationListener {
    var onStart: ((Animation) -> Unit)? = null
    var onEnd: ((Animation) -> Unit)? = null
    var onRepeat: ((Animation) -> Unit)? = null

    init {
        block.invoke(this)
    }

    override fun onAnimationStart(animation: Animation) {
        onStart?.invoke(animation)
    }

    override fun onAnimationEnd(animation: Animation) {
        onEnd?.invoke(animation)
    }

    override fun onAnimationRepeat(animation: Animation) {
        onRepeat?.invoke(animation)
    }
}