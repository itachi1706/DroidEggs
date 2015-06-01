package com.itachi1706.droideggs;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.itachi1706.droideggs.GingerbreadEgg.PlatLogoActivityGINGERBREAD;
import com.itachi1706.droideggs.HoneycombEgg.PlatLogoActivityHONEYCOMB;
import com.itachi1706.droideggs.IceCreamSandwichEgg.PlatLogoActivityICS;
import com.itachi1706.droideggs.JellyBeanEgg.PlatLogoActivityJELLYBEAN;
import com.itachi1706.droideggs.KitKatEgg.PlatLogoActivityKITKAT;
import com.itachi1706.droideggs.LollipopEgg.PlatLogoActivityLOLLIPOP;

/**
 * Created by Kenneth on 1/6/2015
 * for DroidEggs in package com.itachi1706.droideggs
 */
public class SelectorOnClick implements AdapterView.OnItemClickListener {

    Context context;

    public SelectorOnClick(Context context){
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String[] version_code = context.getResources().getStringArray(R.array.legacy_version_with_egg_code);

        Log.d("Selected Version", position + "");

        if (!(position < version_code.length && position >= 0)){
            Log.e("ERROR", "Invalid Position");
            return;
        }
        String version = version_code[position];
        switch (version){
            case "GB": context.startActivity(new Intent(context, PlatLogoActivityGINGERBREAD.class)); break;
            case "HC": context.startActivity(new Intent(context, PlatLogoActivityHONEYCOMB.class)); break;
            case "ICS":
                if (Build.VERSION.SDK_INT >= 16)
                    context.startActivity(new Intent(context, PlatLogoActivityICS.class));
                else
                    MainScreen.unableToAccessEasterEgg("JELLYBEAN");
                break;
            case "JB":
                if (Build.VERSION.SDK_INT >= 16)
                    context.startActivity(new Intent(context, PlatLogoActivityJELLYBEAN.class));
                else
                    MainScreen.unableToAccessEasterEgg("JELLYBEAN");
                break;
            case "KK":
                if (Build.VERSION.SDK_INT >= 19)
                    context.startActivity(new Intent(context, PlatLogoActivityKITKAT.class));
                else
                    MainScreen.unableToAccessEasterEgg("KITKAT");
                break;
            case "LMP": MainScreen.eggComingSoon(); break;
            case "L":
                if (Build.VERSION.SDK_INT >= 21)
                    context.startActivity(new Intent(context, PlatLogoActivityLOLLIPOP.class));
                else
                    MainScreen.unableToAccessEasterEgg("LOLLIPOP");
                break;
            case "MNC": MainScreen.eggComingSoon(); break;
        }
    }
}
