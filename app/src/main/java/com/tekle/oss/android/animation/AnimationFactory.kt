/**
 * Copyright (c) 2012 Ephraim Tekle genzeb@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Ephraim A. Tekle
 */
package com.tekle.oss.android.animation

import android.view.View
import android.view.animation.*
import com.tekle.oss.android.animation.AnimationFactory.FlipDirection
import com.tekle.oss.android.animation.FlipAnimation
import android.widget.ViewAnimator
import com.tekle.oss.android.animation.AnimationFactory
import android.view.animation.Animation.AnimationListener

/**
 * This class contains methods for creating [Animation] objects for some of the most common animation, including a 3D flip animation, [FlipAnimation].
 * Furthermore, utility methods are provided for initiating fade-in-then-out and flip animations.
 *
 * @author Ephraim A. Tekle
 */
object AnimationFactory {
    /**
     * Create a pair of [FlipAnimation] that can be used to flip 3D transition from `fromView` to `toView`. A typical use case is with [ViewAnimator] as an out and in transition.
     *
     * NOTE: Avoid using this method. Instead, use [.flipTransition].
     *
     * @param fromView the view transition away from
     * @param toView the view transition to
     * @param dir the flip direction
     * @param duration the transition duration in milliseconds
     * @param interpolator the interpolator to use (pass `null` to use the [AccelerateInterpolator] interpolator)
     * @return
     */
    fun flipAnimation(
        fromView: View,
        toView: View?,
        dir: FlipDirection?,
        duration: Long,
        interpolator: Interpolator?
    ): Array<Animation?> {
        val result = arrayOfNulls<Animation>(2)
        val centerX: Float
        val centerY: Float
        centerX = fromView.width / 2.0f
        centerY = fromView.height / 2.0f
        val outFlip: Animation = FlipAnimation(
            dir!!.startDegreeForFirstView,
            dir.endDegreeForFirstView,
            centerX,
            centerY,
            FlipAnimation.SCALE_DEFAULT,
            FlipAnimation.ScaleUpDownEnum.SCALE_DOWN
        )
        outFlip.duration = duration
        outFlip.fillAfter = true
        outFlip.interpolator = interpolator ?: AccelerateInterpolator()
        val outAnimation = AnimationSet(true)
        outAnimation.addAnimation(outFlip)
        result[0] = outAnimation

        // Uncomment the following if toView has its layout established (not the case if using ViewFlipper and on first show)
        //centerX = toView.getWidth() / 2.0f;
        //centerY = toView.getHeight() / 2.0f; 
        val inFlip: Animation = FlipAnimation(
            dir.startDegreeForSecondView,
            dir.endDegreeForSecondView,
            centerX,
            centerY,
            FlipAnimation.SCALE_DEFAULT,
            FlipAnimation.ScaleUpDownEnum.SCALE_UP
        )
        inFlip.duration = duration
        inFlip.fillAfter = true
        inFlip.interpolator = interpolator ?: AccelerateInterpolator()
        inFlip.startOffset = duration
        val inAnimation = AnimationSet(true)
        inAnimation.addAnimation(inFlip)
        result[1] = inAnimation
        return result
    }

    /**
     * Flip to the next view of the `ViewAnimator`'s subviews. A call to this method will initiate a [FlipAnimation] to show the next View.
     * If the currently visible view is the last view, flip direction will be reversed for this transition.
     *
     * @param viewAnimator the `ViewAnimator`
     * @param dir the direction of flip
     */
    fun flipTransition(viewAnimator: ViewAnimator, dir: FlipDirection) {
        val fromView = viewAnimator.currentView
        val currentIndex = viewAnimator.displayedChild
        val nextIndex = (currentIndex + 1) % viewAnimator.childCount
        val toView = viewAnimator.getChildAt(nextIndex)
        val animc = flipAnimation(
            fromView,
            toView,
            if (nextIndex < currentIndex) dir.theOtherDirection() else dir,
            250,
            null
        )
        viewAnimator.outAnimation = animc[0]
        viewAnimator.inAnimation = animc[1]
        viewAnimator.showNext()
    }
    //////////////
    /**
     * Slide animations to enter a view from left.
     *
     * @param duration the animation duration in milliseconds
     * @param interpolator the interpolator to use (pass `null` to use the [AccelerateInterpolator] interpolator)
     * @return a slide transition animation
     */
    fun inFromLeftAnimation(duration: Long, interpolator: Interpolator?): Animation {
        val inFromLeft: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromLeft.duration = duration
        inFromLeft.interpolator = interpolator ?: AccelerateInterpolator() //AccelerateInterpolator
        return inFromLeft
    }

    /**
     * Slide animations to hide a view by sliding it to the right
     *
     * @param duration the animation duration in milliseconds
     * @param interpolator the interpolator to use (pass `null` to use the [AccelerateInterpolator] interpolator)
     * @return a slide transition animation
     */
    fun outToRightAnimation(duration: Long, interpolator: Interpolator?): Animation {
        val outtoRight: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        outtoRight.duration = duration
        outtoRight.interpolator = interpolator ?: AccelerateInterpolator()
        return outtoRight
    }

    /**
     * Slide animations to enter a view from right.
     *
     * @param duration the animation duration in milliseconds
     * @param interpolator the interpolator to use (pass `null` to use the [AccelerateInterpolator] interpolator)
     * @return a slide transition animation
     */
    fun inFromRightAnimation(duration: Long, interpolator: Interpolator?): Animation {
        val inFromRight: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromRight.duration = duration
        inFromRight.interpolator = interpolator ?: AccelerateInterpolator()
        return inFromRight
    }

    /**
     * Slide animations to hide a view by sliding it to the left.
     *
     * @param duration the animation duration in milliseconds
     * @param interpolator the interpolator to use (pass `null` to use the [AccelerateInterpolator] interpolator)
     * @return a slide transition animation
     */
    fun outToLeftAnimation(duration: Long, interpolator: Interpolator?): Animation {
        val outtoLeft: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        outtoLeft.duration = duration
        outtoLeft.interpolator = interpolator ?: AccelerateInterpolator()
        return outtoLeft
    }

    /**
     * Slide animations to enter a view from top.
     *
     * @param duration the animation duration in milliseconds
     * @param interpolator the interpolator to use (pass `null` to use the [AccelerateInterpolator] interpolator)
     * @return a slide transition animation
     */
    fun inFromTopAnimation(duration: Long, interpolator: Interpolator?): Animation {
        val infromtop: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        infromtop.duration = duration
        infromtop.interpolator = interpolator ?: AccelerateInterpolator()
        return infromtop
    }

    /**
     * Slide animations to hide a view by sliding it to the top
     *
     * @param duration the animation duration in milliseconds
     * @param interpolator the interpolator to use (pass `null` to use the [AccelerateInterpolator] interpolator)
     * @return a slide transition animation
     */
    fun outToTopAnimation(duration: Long, interpolator: Interpolator?): Animation {
        val outtotop: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f
        )
        outtotop.duration = duration
        outtotop.interpolator = interpolator ?: AccelerateInterpolator()
        return outtotop
    }

    /**
     * A fade animation that will fade the subject in by changing alpha from 0 to 1.
     *
     * @param duration the animation duration in milliseconds
     * @param delay how long to wait before starting the animation, in milliseconds
     * @return a fade animation
     * @see .fadeInAnimation
     */
    fun fadeInAnimation(duration: Long, delay: Long): Animation {
        val fadeIn: Animation = AlphaAnimation(0F, 1F)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = duration
        fadeIn.startOffset = delay
        return fadeIn
    }

    /**
     * A fade animation that will fade the subject out by changing alpha from 1 to 0.
     *
     * @param duration the animation duration in milliseconds
     * @param delay how long to wait before starting the animation, in milliseconds
     * @return a fade animation
     * @see .fadeOutAnimation
     */
    fun fadeOutAnimation(duration: Long, delay: Long): Animation {
        val fadeOut: Animation = AlphaAnimation(1F, 0F)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.startOffset = delay
        fadeOut.duration = duration
        return fadeOut
    }

    /**
     * A fade animation that will ensure the View starts and ends with the correct visibility
     * @param view the View to be faded in
     * @param duration the animation duration in milliseconds
     * @return a fade animation that will set the visibility of the view at the start and end of animation
     */
    fun fadeInAnimation(duration: Long, view: View): Animation {
        val animation = fadeInAnimation(500, 0)
        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {
                view.visibility = View.GONE
            }
        })
        return animation
    }

    /**
     * A fade animation that will ensure the View starts and ends with the correct visibility
     * @param view the View to be faded out
     * @param duration the animation duration in milliseconds
     * @return a fade animation that will set the visibility of the view at the start and end of animation
     */
    fun fadeOutAnimation(duration: Long, view: View): Animation {
        val animation = fadeOutAnimation(500, 0)
        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {
                view.visibility = View.VISIBLE
            }
        })
        return animation
    }

    /**
     * Creates a pair of animation that will fade in, delay, then fade out
     * @param duration the animation duration in milliseconds
     * @param delay how long to wait after fading in the subject and before starting the fade out
     * @return a fade in then out animations
     */
    fun fadeInThenOutAnimation(duration: Long, delay: Long): Array<Animation> {
        return arrayOf(fadeInAnimation(duration, 0), fadeOutAnimation(duration, duration + delay))
    }

    /**
     * Fades the view in. Animation starts right away.
     * @param v the view to be faded in
     */
    fun fadeOut(v: View?) {
        if (v == null) return
        v.startAnimation(fadeOutAnimation(500, v))
    }

    /**
     * Fades the view out. Animation starts right away.
     * @param v the view to be faded out
     */
    fun fadeIn(v: View?) {
        if (v == null) return
        v.startAnimation(fadeInAnimation(500, v))
    }

    /**
     * Fades the view in, delays the specified amount of time, then fades the view out
     * @param v the view to be faded in then out
     * @param delay how long the view will be visible for
     */
    fun fadeInThenOut(v: View?, delay: Long) {
        if (v == null) return
        v.visibility = View.VISIBLE
        val animation = AnimationSet(true)
        val fadeInOut = fadeInThenOutAnimation(500, delay)
        animation.addAnimation(fadeInOut[0])
        animation.addAnimation(fadeInOut[1])
        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                v.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {
                v.visibility = View.VISIBLE
            }
        })
        v.startAnimation(animation)
    }

    /**
     * The `FlipDirection` enumeration defines the most typical flip view transitions: left-to-right and right-to-left. `FlipDirection` is used during the creation of [FlipAnimation] animations.
     *
     * @author Ephraim A. Tekle
     */
    enum class FlipDirection {
        LEFT_RIGHT, RIGHT_LEFT;

        val startDegreeForFirstView: Float
            get() = 0F
        val startDegreeForSecondView: Float
            get() = when (this) {
                LEFT_RIGHT -> -90F
                RIGHT_LEFT -> 90F
                else -> 0F
            }
        val endDegreeForFirstView: Float
            get() = when (this) {
                LEFT_RIGHT -> 90F
                RIGHT_LEFT -> -90F
                else -> 0F
            }
        val endDegreeForSecondView: Float
            get() = 0F

        fun theOtherDirection(): FlipDirection? {
            return when (this) {
                LEFT_RIGHT -> RIGHT_LEFT
                RIGHT_LEFT -> LEFT_RIGHT
                else -> null
            }
        }
    }
}