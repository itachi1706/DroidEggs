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

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.itachi1706.droideggs.GingerbreadEgg.PlatLogoActivityGINGERBREAD
import com.itachi1706.droideggs.HoneycombEgg.PlatLogoActivityHONEYCOMB
import com.itachi1706.droideggs.IceCreamSandwichEgg.PlatLogoActivityICS
import com.itachi1706.droideggs.JellyBeanEgg.PlatLogoActivityJELLYBEAN
import com.itachi1706.droideggs.KitKatEgg.PlatLogoActivityKITKAT
import com.itachi1706.droideggs.LollipopEgg.PlatLogoActivityLOLLIPOP
import com.itachi1706.droideggs.MNCEgg.PlatLogoActivityMNC
import com.itachi1706.droideggs.MarshmallowEgg.PlatLogoActivityMARSHMALLOW
import com.itachi1706.droideggs.NDPEgg.PlatLogoActivityNDP
import com.itachi1706.droideggs.NougatEgg.PlatLogoActivityNougat
import com.itachi1706.droideggs.OreoEgg.PlatLogoActivityOreo
import com.itachi1706.droideggs.OreoMR1Egg.PlatLogoActivityOreoMR1
import com.itachi1706.droideggs.PieEgg.PlatLogoActivityPie
import com.itachi1706.droideggs.quince_tart_egg.PlatLogoActivityQuinceTart
import com.itachi1706.droideggs.red_velvet_cake_egg.PlatLogoActivityRedVelvetCake
import com.itachi1706.droideggs.snow_cone_egg.PlatLogoActivitySnowCone
import com.itachi1706.droideggs.tiramisu_egg.PlatLogoActivityTiramisu
import com.itachi1706.helperlib.helpers.PrefHelper
import java.util.LinkedList

/**
 * Created by Kenneth on 1/6/2015
 * for DroidEggs in package com.itachi1706.droideggs
 */
class SelectorOnClick(private val act: MainScreen) : AdapterView.OnItemClickListener {

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val versionCode =
            view.context.resources.getStringArray(R.array.legacy_version_with_egg_code)
        val versionName = view.context.resources.getStringArray(R.array.android_ver)
        val sp = PrefHelper.getDefaultSharedPreferences(view.context)
        Log.d("Selected Version", position.toString() + "")

        if (!(position < versionCode.size && position >= 0)) {
            Log.e("ERROR", "Invalid Position")
            return
        }
        val version = versionCode[position]
        var selectedEgg: Intent? = null
        when (version) {
            "GB" -> selectedEgg = Intent(view.context, PlatLogoActivityGINGERBREAD::class.java)
            "HC" -> selectedEgg = Intent(view.context, PlatLogoActivityHONEYCOMB::class.java)
            "ICS" -> selectedEgg = Intent(view.context, PlatLogoActivityICS::class.java)
            "JB" -> selectedEgg = Intent(view.context, PlatLogoActivityJELLYBEAN::class.java)
            "KK" -> selectedEgg = Intent(view.context, PlatLogoActivityKITKAT::class.java)
            "L" -> selectedEgg = Intent(view.context, PlatLogoActivityLOLLIPOP::class.java)
            "MNC" -> selectedEgg = Intent(view.context, PlatLogoActivityMNC::class.java)
            "MM" -> selectedEgg = Intent(view.context, PlatLogoActivityMARSHMALLOW::class.java)
            "NDP" -> selectedEgg = Intent(view.context, PlatLogoActivityNDP::class.java)
            "N" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                selectedEgg = Intent(view.context, PlatLogoActivityNougat::class.java)
                selectedEgg.putExtra("setting", sp.getBoolean("actual_neko_egg", false))
            } else act.unableToAccessEasterEgg("NOUGAT")

            "O" -> selectedEgg = Intent(view.context, PlatLogoActivityOreo::class.java)
            "O_MR1" -> selectedEgg = Intent(view.context, PlatLogoActivityOreoMR1::class.java)
            "P" ->
                // Do check and make sure you can access as some part of the egg requires Nougat (24)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N || sp.getBoolean(
                        "access_partial_egg", false
                    )
                ) selectedEgg = Intent(
                    view.context, PlatLogoActivityPie::class.java
                ) else act.limitedAccessToEgg("NOUGAT")

            "Q" -> if (sp.getBoolean(
                    "access_partial_egg", false
                ) || Build.VERSION.SDK_INT >= 23
            ) selectedEgg = Intent(view.context, PlatLogoActivityQuinceTart::class.java)
            else act.unableToAccessEasterEgg("MARSHMALLOW")

            "R" -> selectedEgg = Intent(view.context, PlatLogoActivityRedVelvetCake::class.java)
            "S" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) selectedEgg = Intent(
                view.context, PlatLogoActivitySnowCone::class.java
            ) else act.unableToAccessEasterEgg("Q")

            "T" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) selectedEgg = Intent(
                view.context, PlatLogoActivityTiramisu::class.java
            ) else act.unableToAccessEasterEgg("Q")
        }
        activateEgg(selectedEgg, view, position, versionName, version)
    }

    private fun activateEgg(
        selectedEgg: Intent?, view: View, position: Int, versionName: Array<String>, version: String
    ) {
        if (selectedEgg != null) {
            view.context.startActivity(selectedEgg)

            // Firebase Analytics Event Logging
            FirebaseLogger.logFirebase(view.context, versionName[position], "egg_select")
            Log.i("Firebase", "Logged Egg Selected: " + versionName[position])

            // Add dynamic shortcuts
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                val shortcutManager = view.context.getSystemService(ShortcutManager::class.java)
                val infos = LinkedList(shortcutManager!!.dynamicShortcuts)
                val shortcutCount = shortcutManager.maxShortcutCountPerActivity - 2
                if (infos.size >= shortcutCount) {
                    Log.i(
                        "ShortcutManager",
                        "Dynamic Shortcuts more than $shortcutCount. Removing extras"
                    )
                    do {
                        infos.removeLast()
                    } while (infos.size > shortcutCount)
                }
                selectedEgg.action = Intent.ACTION_VIEW
                val newShortcut = ShortcutInfo.Builder(view.context, "egg-$version")
                    .setShortLabel(versionName[position] + " Egg")
                    .setLongLabel(versionName[position] + " Egg")
                    .setIcon(Icon.createWithResource(view.context, R.mipmap.ic_launcher_round))
                    .setIntent(selectedEgg).build()
                infos.add(newShortcut)
                shortcutManager.dynamicShortcuts = infos
            }
        }
    }
}
