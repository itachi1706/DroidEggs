package com.itachi1706.droideggs;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Kenneth on 1/6/2015
 * for DroidEggs in package com.itachi1706.droideggs
 */
public class PopulateSelector {

    public static ArrayList<SelectorObject> populateSelectors(Context context){
        String[] version = context.getResources().getStringArray(R.array.android_ver);
        String[] range = context.getResources().getStringArray(R.array.android_ver_range);
        String[] require = context.getResources().getStringArray(R.array.android_egg_require);
        int[] minSDK = context.getResources().getIntArray(R.array.android_egg_require_min_sdk);

        ArrayList<SelectorObject> populatedObject = new ArrayList<>();

        for (int i = 0; i < version.length; i++){
            SelectorObject o = new SelectorObject(version[i], range[i], require[i], minSDK[i]);
            populatedObject.add(o);
        }

        return populatedObject;
    }
}
