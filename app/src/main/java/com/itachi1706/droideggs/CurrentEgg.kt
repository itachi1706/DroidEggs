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

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.itachi1706.droideggs.JellyBeanEgg.PlatLogoActivityJELLYBEAN
import com.itachi1706.droideggs.KitKatEgg.PlatLogoActivityKITKAT
import com.itachi1706.droideggs.LollipopEgg.PlatLogoActivityLOLLIPOP
import com.itachi1706.droideggs.MarshmallowEgg.PlatLogoActivityMARSHMALLOW
import com.itachi1706.droideggs.NougatEgg.PlatLogoActivityNougat
import com.itachi1706.droideggs.OreoEgg.PlatLogoActivityOreo
import com.itachi1706.droideggs.OreoMR1Egg.PlatLogoActivityOreoMR1
import com.itachi1706.droideggs.PieEgg.PlatLogoActivityPie

/**
 * Created by Kenneth on 20/4/2018.
 * for com.itachi1706.droideggs in DroidEggs
 */
class CurrentEgg : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val errorIntent = Intent()
        when {
            Build.VERSION.SDK_INT >= 99999 -> errorIntent.putExtra("class", "comingsoon") // ??? (Future P)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> startActivity(Intent(this, PlatLogoActivityPie::class.java)) // Pie
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> startActivity(Intent(this, PlatLogoActivityOreoMR1::class.java)) // Nougat
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> startActivity(Intent(this, PlatLogoActivityOreo::class.java)) // Nougat
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> { // Nougat
                val i = Intent(this, PlatLogoActivityNougat::class.java)
                i.putExtra("setting", PreferenceManager.getDefaultSharedPreferences(this).getBoolean("actual_neko_egg", false))
                startActivity(i)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> startActivity(Intent(this, PlatLogoActivityMARSHMALLOW::class.java)) //Marshmallow
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> startActivity(Intent(this, PlatLogoActivityLOLLIPOP::class.java)) //Lollipop (21-22)
            Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT -> startActivity(Intent(this, PlatLogoActivityKITKAT::class.java)) //KitKat
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN -> startActivity(Intent(this, PlatLogoActivityJELLYBEAN::class.java)) //Jelly Bean
            else -> errorIntent.putExtra("class", "noegg") // No Egg
        }

        if (errorIntent.hasExtra("class")) setResult(Activity.RESULT_CANCELED, errorIntent)
        else {
            setResult(Activity.RESULT_OK)
            FirebaseLogger.logFirebase(this, "SDK: ${Build.VERSION.SDK_INT}", "current_egg_selected") // Firebase Analytics Event Logging
            Log.i("Firebase", "Logged Current Egg Selected for SDK " + Build.VERSION.SDK_INT)
        }
        finish()
    }
}
