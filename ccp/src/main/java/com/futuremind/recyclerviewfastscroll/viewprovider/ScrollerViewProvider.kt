package com.futuremind.recyclerviewfastscroll.viewprovider

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.futuremind.recyclerviewfastscroll.FastScroller

/**
 * Created by Michal on 05/08/16.
 * Provides [View]s and their behaviors for the handle and bubble of the fastscroller.
 */
abstract class ScrollerViewProvider {
    protected lateinit var scroller: FastScroller
        private set
    protected var handleBehavior: ViewBehavior? = null
        get() {
            if (field == null) field = provideHandleBehavior()
            return field
        }
        private set
    protected var bubbleBehavior: ViewBehavior? = null
        get() {
            if (field == null) field = provideBubbleBehavior()
            return field
        }
        private set

    fun setFastScroller(scroller: FastScroller) {
        this.scroller = scroller
    }

    protected val context: Context
        get() = scroller.context

    /**
     * @param container The container [FastScroller] for the view to inflate properly.
     * @return A view which will be by the [FastScroller] used as a handle.
     */
    abstract fun provideHandleView(container: ViewGroup?): View

    /**
     * @param container The container [FastScroller] for the view to inflate properly.
     * @return A view which will be by the [FastScroller] used as a bubble.
     */
    abstract fun provideBubbleView(container: ViewGroup?): View?

    /**
     * Bubble view has to provide a [TextView] that will show the index title.
     * @return A [TextView] that will hold the index title.
     */
    abstract fun provideBubbleTextView(): TextView?

    /**
     * To offset the position of the bubble relative to the handle. E.g. in [DefaultScrollerViewProvider]
     * the sharp corner of the bubble is aligned with the center of the handle.
     * @return the position of the bubble in relation to the handle (according to the orientation).
     */
    abstract val bubbleOffset: Int
    protected abstract fun provideHandleBehavior(): ViewBehavior?
    protected abstract fun provideBubbleBehavior(): ViewBehavior?
    fun onHandleGrabbed() {
        if (handleBehavior != null) handleBehavior!!.onHandleGrabbed()
        if (bubbleBehavior != null) bubbleBehavior!!.onHandleGrabbed()
    }

    fun onHandleReleased() {
        if (handleBehavior != null) handleBehavior!!.onHandleReleased()
        if (bubbleBehavior != null) bubbleBehavior!!.onHandleReleased()
    }

    fun onScrollStarted() {
        if (handleBehavior != null) handleBehavior!!.onScrollStarted()
        if (bubbleBehavior != null) bubbleBehavior!!.onScrollStarted()
    }

    fun onScrollFinished() {
        if (handleBehavior != null) handleBehavior!!.onScrollFinished()
        if (bubbleBehavior != null) bubbleBehavior!!.onScrollFinished()
    }
}