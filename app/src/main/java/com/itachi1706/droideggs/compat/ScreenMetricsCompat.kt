package com.itachi1706.droideggs.compat

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
object ScreenMetricsCompat {
    private val api: Api =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ApiLevel30()
        else Api()

    /**
     * Returns screen size in pixels.
     */
    fun getScreenSize(context: Context): Size = api.getScreenSize(context)
    fun getInsetsMetric(insets: WindowInsets): InsetsCompat = api.getInsetsMetric(insets);

    @Suppress("DEPRECATION")
    private open class Api {
        open fun getScreenSize(context: Context): Size {
            val wm = ContextCompat.getSystemService(context, WindowManager::class.java)
            val display = wm?.defaultDisplay
            val metrics = if (display != null) {
                DisplayMetrics().also { display.getRealMetrics(it) }
            } else {
                Resources.getSystem().displayMetrics
            }
            return Size(metrics.widthPixels, metrics.heightPixels)
        }

        open fun getInsetsMetric(insets: WindowInsets): InsetsCompat {
            return if (insets.hasStableInsets()) {
                InsetsCompat(insets.stableInsetTop, insets.stableInsetBottom, insets.stableInsetLeft, insets.stableInsetRight)
            } else {
                InsetsCompat(insets.systemWindowInsetTop, insets.systemWindowInsetBottom, insets.systemWindowInsetLeft, insets.systemWindowInsetRight)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private class ApiLevel30 : Api() {
        override fun getScreenSize(context: Context): Size {
            val metrics: WindowMetrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics
            return Size(metrics.bounds.width(), metrics.bounds.height())
        }

        override fun getInsetsMetric(insets: WindowInsets): InsetsCompat {
            val inset = insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            return InsetsCompat(inset.top, inset.bottom, inset.left, inset.right)
        }
    }

    data class InsetsCompat(val top: Int, val bottom: Int, val left: Int, val right: Int)
}