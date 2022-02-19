/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itachi1706.droideggs.PieEgg.EasterEgg.paint

import android.content.Context
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.itachi1706.droideggs.compat.ScreenMetricsCompat

@RequiresApi(21)
class ToolbarView(context: Context) : FrameLayout(context) {
    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        val lp = layoutParams as LayoutParams?
        if (lp != null && insets != null) {
            lp.topMargin = ScreenMetricsCompat.getInsetsMetricTop(insets)
            lp.bottomMargin = ScreenMetricsCompat.getInsetsMetricBottom(insets)
            layoutParams = lp
        }
        return super.onApplyWindowInsets(insets)
    }
}