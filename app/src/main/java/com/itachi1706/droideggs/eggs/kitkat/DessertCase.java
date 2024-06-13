/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.itachi1706.droideggs.eggs.kitkat;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class DessertCase extends AppCompatActivity {

    DessertCaseView mView;

    @Override
    public void onStart() {
        super.onStart();

        PackageManager pm = getPackageManager();
        final ComponentName cn = new ComponentName(this, DessertCaseDream.class);
        if (pm.getComponentEnabledSetting(cn) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            Log.v("DessertCase", "ACHIEVEMENT UNLOCKED");
            pm.setComponentEnabledSetting(cn,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }

        mView = new DessertCaseView(this);

        DessertCaseView.RescalingContainer container = new DessertCaseView.RescalingContainer(this);

        container.setView(mView);

        setContentView(container);
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.postDelayed(() -> mView.start(), 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mView.stop();
    }
}
