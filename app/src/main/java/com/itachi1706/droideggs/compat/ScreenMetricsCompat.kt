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

    fun getInsetsMetricTop(insets: WindowInsets): Int = api.getInsetsMetricsTop(insets)
    fun getInsetsMetricBottom(insets: WindowInsets): Int = api.getInsetsMetricBottom(insets)

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

        open fun getInsetsMetricsTop(insets: WindowInsets): Int {
            return if (insets.hasStableInsets()) {
                insets.stableInsetTop
            } else {
                insets.systemWindowInsetTop
            }
        }

        open fun getInsetsMetricBottom(insets: WindowInsets): Int {
            return if (insets.hasStableInsets()) {
                insets.stableInsetBottom
            } else {
                insets.systemWindowInsetBottom
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private class ApiLevel30 : Api() {
        override fun getScreenSize(context: Context): Size {
            val metrics: WindowMetrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics
            return Size(metrics.bounds.width(), metrics.bounds.height())
        }

        override fun getInsetsMetricsTop(insets: WindowInsets): Int {
            val inset = insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            return inset.top
        }

        override fun getInsetsMetricBottom(insets: WindowInsets): Int {
            val inset = insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            return inset.bottom
        }
    }
}