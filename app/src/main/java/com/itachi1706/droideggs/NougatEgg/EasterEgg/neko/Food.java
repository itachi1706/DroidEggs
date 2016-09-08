package com.itachi1706.droideggs.NougatEgg.EasterEgg.neko;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Icon;
import android.os.Build;

import com.itachi1706.droideggs.R;

/**
 * Created by Kenneth on 8/9/2016.
 * for com.itachi1706.droideggs.NougatEgg.EasterEgg.neko in DroidEggs
 */
@TargetApi(Build.VERSION_CODES.M)
public class Food {
    private final int mType;
    private static int[] sIcons;
    private static String[] sNames;
    public Food(int type) {
        mType = type;
    }
    public Icon getIcon(Context context) {
        if (sIcons == null) {
            TypedArray icons = context.getResources().obtainTypedArray(R.array.nougat_food_icons);
            sIcons = new int[icons.length()];
            for (int i = 0; i < sIcons.length; i++) {
                sIcons[i] = icons.getResourceId(i, 0);
            }
            icons.recycle();
        }
        return Icon.createWithResource(context, sIcons[mType]);
    }
    public String getName(Context context) {
        if (sNames == null) {
            sNames = context.getResources().getStringArray(R.array.nougat_food_names);
        }
        return sNames[mType];
    }
    public long getInterval(Context context) {
        return context.getResources().getIntArray(R.array.nougat_food_intervals)[mType];
    }
    public int getType() {
        return mType;
    }
}
