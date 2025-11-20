package com.futuremind.recyclerviewfastscroll.viewprovider

/**
 * Created by Michal on 11/08/16.
 */
class DefaultBubbleBehavior(private val animationManager: VisibilityAnimationManager?) : ViewBehavior {
    override fun onHandleGrabbed() {
        animationManager!!.show()
    }

    override fun onHandleReleased() {
        animationManager!!.hide()
    }

    override fun onScrollStarted() {}
    override fun onScrollFinished() {}
}