/*
 * Copyright (C) 2015 Kenneth Soh (itachi1706)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.itachi1706.droideggs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*

/**
 * Created by Kenneth on 1/6/2015
 * for DroidEggs in package com.itachi1706.droideggs
 */
class SelectorAdapter(context: Context, textViewResourceId: Int, private val items: ArrayList<SelectorObject>) : ArrayAdapter<SelectorObject>(context, textViewResourceId, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.listview_selector, parent, false)

        val (name, range, required, minSDK) = items[position]
        val title = v.findViewById<TextView>(R.id.tvTitle)
        val rangeView = v.findViewById<TextView>(R.id.tvRange)
        val requiredView = v.findViewById<TextView>(R.id.tvRequired)

        title?.text = name
        rangeView?.text = "Android $range" //Format: Android %RANGE%
        if (minSDK == 1) requiredView?.text = "Requires: None" else requiredView?.text = "Requires: Android $required (SDK $minSDK)" //Format: Requires: NONE/Android %REQUIRED% (SDK %SDKINT%)
        return v
    }

    override fun getCount(): Int {
        return items.size
    }
}
