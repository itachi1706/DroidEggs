package com.itachi1706.droideggs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

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
        String[] version_name = context.getResources().getStringArray(R.array.version_with_egg);
        String[] version_code = context.getResources().getStringArray(R.array.version_with_egg_code);

        Log.d("Selected Version", position + "");

        if (!(position < version_code.length && position >= 0)){
            Log.e("ERROR", "Invalid Position");
            return;
        }
        String version = version_code[position];
        switch (version){
            case "GB": MainScreen.eggComingSoon(); break;
            case "HC": MainScreen.eggComingSoon(); break;
            case "ICS": MainScreen.eggComingSoon(); break;
            case "JB": MainScreen.eggComingSoon(); break;
            case "KK": MainScreen.eggComingSoon(); break;
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
