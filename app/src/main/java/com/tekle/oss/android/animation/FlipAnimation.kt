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

import android.graphics.Camera
import android.view.animation.Animation
import android.view.animation.Transformation
import com.tekle.oss.android.animation.FlipAnimation.ScaleUpDownEnum
import com.tekle.oss.android.animation.FlipAnimation

/**
 * This class extends Animation to support a 3D flip view transition animation. Two instances of this class is
 * required: one for the "from" view and another for the "to" view.
 *
 * NOTE: use [AnimationFactory] to use this class.
 *
 * @author Ephraim A. Tekle
 */
class FlipAnimation(
    private val mFromDegrees: Float,
    private val mToDegrees: Float,
    private val mCenterX: Float,
    private val mCenterY: Float,
    scale: Float,
    scaleType: ScaleUpDownEnum?
) : Animation() {
    private var mCamera: Camera? = null
    private val scaleType: ScaleUpDownEnum
    private val scale: Float
    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        mCamera = Camera()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val fromDegrees = mFromDegrees
        val degrees = fromDegrees + (mToDegrees - fromDegrees) * interpolatedTime
        val centerX = mCenterX
        val centerY = mCenterY
        val camera = mCamera
        val matrix = t.matrix
        camera!!.save()
        camera.rotateY(degrees)
        camera.getMatrix(matrix)
        camera.restore()
        matrix.preTranslate(-centerX, -centerY)
        matrix.postTranslate(centerX, centerY)
        matrix.preScale(
            scaleType.getScale(scale, interpolatedTime),
            scaleType.getScale(scale, interpolatedTime),
            centerX,
            centerY
        )
    }

    /**
     * This enumeration is used to determine the zoom (or scale) behavior of a [FlipAnimation].
     *
     * @author Ephraim A. Tekle
     */
    enum class ScaleUpDownEnum {
        /**
         * The view will be scaled up from the scale value until it's at 100% zoom level (i.e. no zoom).
         */
        SCALE_UP,

        /**
         * The view will be scaled down starting at no zoom (100% zoom level) until it's at a specified zoom level.
         */
        SCALE_DOWN,

        /**
         * The view will cycle through a zoom down and then zoom up.
         */
        SCALE_CYCLE,

        /**
         * No zoom effect is applied.
         */
        SCALE_NONE;

        /**
         * The intermittent zoom level given the current or desired maximum zoom level for the specified iteration
         *
         * @param max the maximum desired or current zoom level
         * @param iter the iteration (from 0..1).
         * @return the current zoom level
         */
        fun getScale(max: Float, iter: Float): Float {
            return when (this) {
                SCALE_UP -> max + (1 - max) * iter
                SCALE_DOWN -> 1 - (1 - max) * iter
                SCALE_CYCLE -> {
                    val halfWay = iter > 0.5
                    if (halfWay) {
                        max + (1 - max) * (iter - 0.5f) * 2
                    } else {
                        1 - (1 - max) * (iter * 2)
                    }
                }
                else -> 1
            }
        }
    }

    companion object {
        /**
         * How much to scale up/down. The default scale of 75% of full size seems optimal based on testing. Feel free to experiment away, however.
         */
        const val SCALE_DEFAULT = 0.75f
    }

    /**
     * Constructs a new `FlipAnimation` object.Two `FlipAnimation` objects are needed for a complete transition b/n two views.
     *
     * @param fromDegrees the start angle in degrees for a rotation along the y-axis, i.e. in-and-out of the screen, i.e. 3D flip. This should really be multiple of 90 degrees.
     * @param toDegrees the end angle in degrees for a rotation along the y-axis, i.e. in-and-out of the screen, i.e. 3D flip. This should really be multiple of 90 degrees.
     * @param centerX the x-axis value of the center of rotation
     * @param centerY the y-axis value of the center of rotation
     * @param scale to get a 3D effect, the transition views need to be zoomed (scaled). This value must be b/n (0,1) or else the default scale [.SCALE_DEFAULT] is used.
     * @param scaleType flip view transition is broken down into two: the zoom-out of the "from" view and the zoom-in of the "to" view. This parameter is used to determine which is being done. See [ScaleUpDownEnum].
     */
    init {
        this.scale = if (scale <= 0 || scale >= 1) SCALE_DEFAULT else scale
        this.scaleType = scaleType ?: ScaleUpDownEnum.SCALE_CYCLE
    }
}