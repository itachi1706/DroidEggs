/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.itachi1706.droideggs.eggs.nougat.easter_egg.neko;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;

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
        // Personal Edit: Check if we should use old or new icons
        int foodicon = R.array.nougat_food_icons;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("neko_food", false))
            foodicon = R.array.nougat_food_icons_new;
        if (sIcons == null) {
            TypedArray icons = context.getResources().obtainTypedArray(foodicon);
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
