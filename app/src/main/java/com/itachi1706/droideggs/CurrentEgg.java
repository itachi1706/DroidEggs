package com.itachi1706.droideggs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.itachi1706.droideggs.GingerbreadEgg.PlatLogoActivityGINGERBREAD;
import com.itachi1706.droideggs.HoneycombEgg.PlatLogoActivityHONEYCOMB;
import com.itachi1706.droideggs.JellyBeanEgg.PlatLogoActivityJELLYBEAN;
import com.itachi1706.droideggs.KitKatEgg.PlatLogoActivityKITKAT;
import com.itachi1706.droideggs.LollipopEgg.PlatLogoActivityLOLLIPOP;
import com.itachi1706.droideggs.MarshmallowEgg.PlatLogoActivityMARSHMALLOW;
import com.itachi1706.droideggs.NougatEgg.PlatLogoActivityNougat;
import com.itachi1706.droideggs.OreoEgg.PlatLogoActivityOreo;
import com.itachi1706.droideggs.OreoMR1Egg.PlatLogoActivityOreoMR1;

/**
 * Created by Kenneth on 20/4/2018.
 * for com.itachi1706.droideggs in DroidEggs
 */
public class CurrentEgg extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent errorIntent = new Intent();
        if (Build.VERSION.SDK_INT >= 99999) // ??? (Future P)
            errorIntent.putExtra("class", "comingsoon");
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) // Nougat
            startActivity(new Intent(this, PlatLogoActivityOreoMR1.class));
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) // Nougat
            startActivity(new Intent(this, PlatLogoActivityOreo.class));
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // Nougat
            Intent i = new Intent(this, PlatLogoActivityNougat.class);
            i.putExtra("setting", PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("actual_neko_egg", false));
            startActivity(i);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //Marshmallow
            startActivity(new Intent(this, PlatLogoActivityMARSHMALLOW.class));
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) //Lollipop (21-22)
            startActivity(new Intent(this, PlatLogoActivityLOLLIPOP.class));
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) //KitKat
            startActivity(new Intent(this, PlatLogoActivityKITKAT.class));
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) //Jelly Bean
            startActivity(new Intent(this, PlatLogoActivityJELLYBEAN.class));
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) { //ICS
            //weird("ICE CREAM SANDWICH", "JELLY BEAN");
            errorIntent.putExtra("class", "weird");
            errorIntent.putExtra("title", "ICE CREAM SANDWICH"); // ICS with JB usage
            errorIntent.putExtra("body", "JELLY BEAN");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) //Honeycomb
            startActivity(new Intent(this, PlatLogoActivityHONEYCOMB.class));
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)  //Gingerbread
            startActivity(new Intent(this, PlatLogoActivityGINGERBREAD.class));
        else {
            // No Egg
            errorIntent.putExtra("class", "noegg");
        }

        if (errorIntent.hasExtra("class"))
            setResult(RESULT_CANCELED, errorIntent);
        else {
            setResult(RESULT_OK);
            // Firebase Analytics Event Logging
            FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "SDK: " + Build.VERSION.SDK_INT);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "current_egg_selected");
            analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            Log.i("Firebase", "Logged Current Egg Selected for SDK " + Build.VERSION.SDK_INT);
        }
        finish();
    }
}
