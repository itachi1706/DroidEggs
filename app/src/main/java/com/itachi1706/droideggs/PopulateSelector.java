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
