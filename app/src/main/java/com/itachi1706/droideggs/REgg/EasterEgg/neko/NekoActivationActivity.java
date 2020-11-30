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

package com.itachi1706.droideggs.REgg.EasterEgg.neko;

import android.app.Activity;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.itachi1706.droideggs.FirebaseLogger;
import com.itachi1706.helperlib.helpers.PrefHelper;

public class NekoActivationActivity extends Activity {
    private static final String R_EGG_UNLOCK_SETTING = "egg_mode_r";

    private void toastUp(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        final PackageManager pm = getPackageManager();
        final ComponentName cn = new ComponentName(this, NekoControlsService.class);
        final boolean componentEnabled = pm.getComponentEnabledSetting(cn)
                == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        SharedPreferences sp = PrefHelper.getDefaultSharedPreferences(this);
        if (sp.getLong(R_EGG_UNLOCK_SETTING, 0) == 0) {
            if (componentEnabled) {
                Log.v("Neko", "Disabling controls.");
                pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                FirebaseLogger.INSTANCE.histogram(this, "egg_neko_enable", 0);
                toastUp("\uD83D\uDEAB");
            } else {
                Log.v("Neko", "Controls already disabled.");
            }
        } else {
            if (!componentEnabled) {
                Log.v("Neko", "Enabling controls.");
                pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                FirebaseLogger.INSTANCE.histogram(this, "egg_neko_enable", 1);
                toastUp("\uD83D\uDC31");
            } else {
                Log.v("Neko", "Controls already enabled.");
            }
        }
        finish();
    }
}
