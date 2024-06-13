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

package com.itachi1706.droideggs.eggs.pie.easter_egg.paint

import java.util.LinkedList
import android.view.MotionEvent

class SpotFilter(internal var mBufSize: Int, posDecay: Float, pressureDecay: Float, internal var mPlotter: Plotter) {
    val spots = LinkedList<MotionEvent.PointerCoords>() // newest at front
    val tmpSpot = MotionEvent.PointerCoords()
    var lastTool = MotionEvent.TOOL_TYPE_UNKNOWN
    val posDecay: Float
    val pressureDecay: Float
    interface Plotter {
        fun plot(s: MotionEvent.PointerCoords)
    }
    init {
        this.posDecay = if (posDecay in 0f..1f) posDecay else 1f
        this.pressureDecay = if (pressureDecay in 0f..1f) pressureDecay else 1f
    }
    fun filterInto(out: MotionEvent.PointerCoords, tool: Int): MotionEvent.PointerCoords {
        lastTool = tool
        var wi = 1f // weight for ith component (position)
        var w = 0f // total weight
        var wiPress = 1f // weight for ith component (pressure)
        var wPress = 0f // total weight (pressure)
        var x = 0f
        var y = 0f
        var pressure = 0f
        var size = 0f
        for (pi in spots) {
            x += pi.x * wi
            y += pi.y * wi
            pressure += pi.pressure * wiPress
            size += pi.size * wiPress
            w += wi
            wi *= posDecay // exponential backoff
            wPress += wiPress
            wiPress *= pressureDecay
            if (PRECISE_STYLUS_INPUT && tool == MotionEvent.TOOL_TYPE_STYLUS) {
                // just take the newest one, no need to average
                break
            }
        }
        out.x = x / w
        out.y = y / w
        out.pressure = pressure / wPress
        out.size = size / wPress
        return out
    }
    protected fun addInternal(c: MotionEvent.PointerCoords, tool: Int) {
        val coord =
                if (spots.size == mBufSize) {
                    spots.removeLast()
                } else {
                    MotionEvent.PointerCoords()
                }
        coord.copyFrom(c)
        spots.add(0, coord)
        filterInto(tmpSpot, tool)
        mPlotter.plot(tmpSpot)
    }
    fun add(cv: List<MotionEvent.PointerCoords>, tool: Int) {
        for (c in cv) {
            addInternal(c, tool)
        }
    }
    fun add(evt: MotionEvent) {
        val tool = evt.getToolType(0)
        for (i in 0 until evt.historySize) {
            evt.getHistoricalPointerCoords(0, i, tmpSpot)
            addInternal(tmpSpot, tool)
        }
        evt.getPointerCoords(0, tmpSpot)
        addInternal(tmpSpot, tool)
    }
    fun finish() {
        while (spots.isNotEmpty()) {
            filterInto(tmpSpot, lastTool)
            spots.removeLast()
            mPlotter.plot(tmpSpot)
        }
        spots.clear()
    }
    companion object {
        var PRECISE_STYLUS_INPUT = true
    }
}