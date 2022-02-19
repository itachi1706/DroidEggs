/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itachi1706.droideggs.s_egg.easter_egg.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * Launched from the PlatLogoActivity. Enables everything else in this easter egg.
 */
@TargetApi(Build.VERSION_CODES.Q)
public class WidgetActivationActivity extends Activity {
    private static final String TAG = "EasterEgg";

    private static final String S_EGG_UNLOCK_SETTING = "egg_mode_s";

    @Override
    public void onStart() {
        super.onStart();

        final PackageManager pm = getPackageManager();
        final ComponentName[] cns = new ComponentName[] {
                new ComponentName(this, PaintChipsActivity.class),
                new ComponentName(this, PaintChipsWidget.class)
        };
        SharedPreferences sp = this.getSharedPreferences("com.itachi1706.droideggs_preferences", MODE_MULTI_PROCESS);
        final long unlockValue = sp.getLong(S_EGG_UNLOCK_SETTING, 0);
        for (ComponentName cn : cns) {
            final boolean componentEnabled = pm.getComponentEnabledSetting(cn)
                    == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            if (unlockValue == 0) {
                if (componentEnabled) {
                    Log.v(TAG, "Disabling component: " + cn);
                    pm.setComponentEnabledSetting(cn,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                } else {
                    Log.v(TAG, "Already disabled: " + cn);
                }
            } else {
                if (!componentEnabled) {
                    Log.v(TAG, "Enabling component: " + cn);
                    pm.setComponentEnabledSetting(cn,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                } else {
                    Log.v(TAG, "Already enabled: " + cn);
                }
            }
        }

        finish();
    }
}
