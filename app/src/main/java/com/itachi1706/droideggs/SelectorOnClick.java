package com.itachi1706.droideggs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.itachi1706.droideggs.GingerbreadEgg.PlatLogoActivityGINGERBREAD;
import com.itachi1706.droideggs.HoneycombEgg.PlatLogoActivityHONEYCOMB;
import com.itachi1706.droideggs.IceCreamSandwichEgg.PlatLogoActivityICS;
import com.itachi1706.droideggs.JellyBeanEgg.PlatLogoActivityJELLYBEAN;
import com.itachi1706.droideggs.KitKatEgg.PlatLogoActivityKITKAT;
import com.itachi1706.droideggs.LollipopEgg.PlatLogoActivityLOLLIPOP;
import com.itachi1706.droideggs.MNCEgg.PlatLogoActivityMNC;
import com.itachi1706.droideggs.MarshmallowEgg.PlatLogoActivityMARSHMALLOW;
import com.itachi1706.droideggs.NDPEgg.PlatLogoActivityNDP;
import com.itachi1706.droideggs.NougatEgg.PlatLogoActivityNougat;
import com.itachi1706.droideggs.OreoEgg.PlatLogoActivityOreo;
import com.itachi1706.droideggs.OreoMR1Egg.PlatLogoActivityOreoMR1;

import java.util.LinkedList;

/**
 * Created by Kenneth on 1/6/2015
 * for DroidEggs in package com.itachi1706.droideggs
 */
public class SelectorOnClick implements AdapterView.OnItemClickListener {

    public SelectorOnClick() {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String[] version_code = view.getContext().getResources().getStringArray(R.array.legacy_version_with_egg_code);
        String[] version_name = view.getContext().getResources().getStringArray(R.array.android_ver);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        boolean debugMode = sp.getBoolean("debug", false);

        Log.d("Selected Version", position + "");

        if (!(position < version_code.length && position >= 0)){
            Log.e("ERROR", "Invalid Position");
            return;
        }
        String version = version_code[position];
        Intent selectedEgg = null;
        switch (version){
            case "GB": selectedEgg = new Intent(view.getContext(), PlatLogoActivityGINGERBREAD.class); break;
            case "HC": selectedEgg = new Intent(view.getContext(), PlatLogoActivityHONEYCOMB.class); break;
            case "ICS":
                if (Build.VERSION.SDK_INT >= 16)
                    selectedEgg = new Intent(view.getContext(), PlatLogoActivityICS.class);
                else
                    MainScreen.unableToAccessEasterEgg("JELLYBEAN");
                break;
            case "JB":
                if (Build.VERSION.SDK_INT >= 16)
                    selectedEgg = new Intent(view.getContext(), PlatLogoActivityJELLYBEAN.class);
                else
                    MainScreen.unableToAccessEasterEgg("JELLYBEAN");
                break;
            case "KK":
                if (Build.VERSION.SDK_INT >= 19)
                    selectedEgg = new Intent(view.getContext(), PlatLogoActivityKITKAT.class);
                else
                    MainScreen.unableToAccessEasterEgg("KITKAT");
                break;
            case "L":
                if (Build.VERSION.SDK_INT >= 21)
                    selectedEgg = new Intent(view.getContext(), PlatLogoActivityLOLLIPOP.class);
                else
                    MainScreen.unableToAccessEasterEgg("LOLLIPOP");
                break;
            case "MNC":
                if (Build.VERSION.SDK_INT >= 21)
                    selectedEgg = new Intent(view.getContext(), PlatLogoActivityMNC.class);
                else
                    MainScreen.unableToAccessEasterEgg("LOLLIPOP");
                break;
            case "MM":
                if (Build.VERSION.SDK_INT >= 21)
                    selectedEgg = new Intent(view.getContext(), PlatLogoActivityMARSHMALLOW.class);
                else
                    MainScreen.unableToAccessEasterEgg("LOLLIPOP");
                break;
            case "NDP":
                if (Build.VERSION.SDK_INT >= 21)
                    selectedEgg = new Intent(view.getContext(), PlatLogoActivityNDP.class);
                else
                    MainScreen.unableToAccessEasterEgg("LOLLIPOP");
                break;
            case "N":
                if (Build.VERSION.SDK_INT >= 24) {
                    selectedEgg = new Intent(view.getContext(), PlatLogoActivityNougat.class);
                    selectedEgg.putExtra("setting", sp.getBoolean("actual_neko_egg", false));
                } else
                    MainScreen.unableToAccessEasterEgg("NOUGAT");
                break;
            case "O":
                if (Build.VERSION.SDK_INT >= 21)
                    selectedEgg = new Intent(view.getContext(), PlatLogoActivityOreo.class);
                else
                    MainScreen.unableToAccessEasterEgg("OREO");
                break;
            case "O_MR1":
                if (Build.VERSION.SDK_INT >= 21)
                    selectedEgg = new Intent(view.getContext(), PlatLogoActivityOreoMR1.class);
                else
                    MainScreen.unableToAccessEasterEgg("OREO");
                break;
        }
        if (selectedEgg != null) {
            view.getContext().startActivity(selectedEgg);

            // Firebase Analytics Event Logging
            FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(view.getContext());
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, version_name[position]);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "egg_select");
            analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            Log.i("Firebase", "Logged Egg Selected: " + version_name[position]);

            // Add dynamic shortcuts
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutManager shortcutManager = view.getContext().getSystemService(ShortcutManager.class);
                LinkedList<ShortcutInfo> infos = new LinkedList<>(shortcutManager.getDynamicShortcuts());
                final int shortcutCount = shortcutManager.getMaxShortcutCountPerActivity() - 2;
                if (infos.size() >= shortcutCount) {
                    Log.i("ShortcutManager", "Dynamic Shortcuts more than " + shortcutCount
                            + ". Removing extras");
                    do {
                        infos.removeLast();
                    } while (infos.size() > shortcutCount);
                }
                selectedEgg.setAction(Intent.ACTION_VIEW);
                ShortcutInfo newShortcut = new ShortcutInfo.Builder(view.getContext(), "egg-" + version)
                        .setShortLabel(version_name[position]).setLongLabel("Launch this egg directly")
                        .setIcon(Icon.createWithResource(view.getContext(), R.mipmap.ic_launcher_round))
                        .setIntent(selectedEgg).build();

                infos.add(newShortcut);
                shortcutManager.setDynamicShortcuts(infos);
            }
        }
    }
}
