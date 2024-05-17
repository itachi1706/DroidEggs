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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.itachi1706.droideggs.GingerbreadEgg.PlatLogoActivityGINGERBREAD
import com.itachi1706.droideggs.HoneycombEgg.PlatLogoActivityHONEYCOMB
import com.itachi1706.droideggs.IceCreamSandwichEgg.PlatLogoActivityICS
import com.itachi1706.droideggs.JellyBeanEgg.PlatLogoActivityJELLYBEAN
import com.itachi1706.droideggs.KitKatEgg.PlatLogoActivityKITKAT
import com.itachi1706.droideggs.LollipopEgg.PlatLogoActivityLOLLIPOP
import com.itachi1706.droideggs.MarshmallowEgg.PlatLogoActivityMARSHMALLOW
import com.itachi1706.droideggs.NougatEgg.PlatLogoActivityNougat
import com.itachi1706.droideggs.OreoEgg.PlatLogoActivityOreo
import com.itachi1706.droideggs.OreoMR1Egg.PlatLogoActivityOreoMR1
import com.itachi1706.droideggs.PieEgg.PlatLogoActivityPie
import com.itachi1706.droideggs.quince_tart_egg.PlatLogoActivityQuinceTart
import com.itachi1706.droideggs.red_velvet_cake_egg.PlatLogoActivityRedVelvetCake
import com.itachi1706.droideggs.snow_cone_egg.PlatLogoActivitySnowCone
import com.itachi1706.droideggs.tiramisu_egg.PlatLogoActivityTiramisu

/**
 * Created by Kenneth on 20/4/2018.
 * for com.itachi1706.droideggs in DroidEggs
 */
class CurrentEgg : AppCompatActivity() {

    // As this function will only execute for the correct version, we do not need to care about errors
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val errorIntent = Intent().apply { putExtra("class", "comingsoon") }
        val executeClass = when (Build.VERSION.SDK_INT) {
            GINGERBREAD, GINGERBREAD_MR1 -> PlatLogoActivityGINGERBREAD::class.java // Gingerbread
            HONEYCOMB, HONEYCOMB_MR1, HONEYCOMB_MR2 -> PlatLogoActivityHONEYCOMB::class.java // Honeycomb
            ICE_CREAM_SANDWICH, ICE_CREAM_SANDWICH_MR1 -> PlatLogoActivityICS::class.java // Ice Cream Sandwich
            JELLY_BEAN, JELLY_BEAN_MR1, JELLY_BEAN_MR2 -> PlatLogoActivityJELLYBEAN::class.java // Jelly Bean
            KITKAT, KITKAT_WATCH -> PlatLogoActivityKITKAT::class.java // Kit Kat
            LOLLIPOP, LOLLIPOP_MR1 -> PlatLogoActivityLOLLIPOP::class.java // Lollipop
            M -> PlatLogoActivityMARSHMALLOW::class.java // Marshmallow
            N, N_MR1 -> PlatLogoActivityNougat::class.java // Nougat
            O -> PlatLogoActivityOreo::class.java // Oreo
            O_MR1 -> PlatLogoActivityOreoMR1::class.java // Oreo
            P -> PlatLogoActivityPie::class.java // Pie
            Q -> PlatLogoActivityQuinceTart::class.java // Android 10 (Q)
            R -> PlatLogoActivityRedVelvetCake::class.java // Android 11 (R)
            S -> PlatLogoActivitySnowCone::class.java // Android 12 (S)
            TIRAMISU -> PlatLogoActivityTiramisu::class.java // Android 13 (Tiramisu)
            else -> null // Future Android Versions (As minSDK is way above GB, it does not matter to do a check before)
        }

        if (executeClass == null) setResult(Activity.RESULT_CANCELED, errorIntent)
        else {
            startActivity(Intent(this, executeClass))
            setResult(Activity.RESULT_OK)
            FirebaseLogger.logFirebase(this, "SDK: ${Build.VERSION.SDK_INT}", "current_egg_selected") // Firebase Analytics Event Logging
            Log.i("Firebase", "Logged Current Egg Selected for SDK " + Build.VERSION.SDK_INT)
        }
        finish()
    }
}
