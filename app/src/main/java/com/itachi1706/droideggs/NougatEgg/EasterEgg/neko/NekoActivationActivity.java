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

package com.itachi1706.droideggs.NougatEgg.EasterEgg.neko;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.itachi1706.droideggs.FirebaseLogger;
import com.itachi1706.droideggs.NotificationUtils;

/**
 * Created by Kenneth on 8/9/2016.
 * for com.itachi1706.droideggs.NougatEgg.EasterEgg.neko in DroidEggs
 */
public class NekoActivationActivity extends AppCompatActivity {

    private void toastUp(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        if (toast.getView() != null)
            toast.getView().setBackgroundDrawable(null);
        toast.show();
    }

    private static final String TAG = "Neko";

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.i(TAG, "Notification permission granted: " + isGranted);
                if (Boolean.FALSE.equals(isGranted)) {
                    Toast.makeText(this, "Notification permission not granted. You will not know when a cat visits",
                            Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public void onStart() {
        super.onStart();

        final PackageManager pm = getPackageManager();
        final ComponentName cn = new ComponentName(this, NekoTile.class);
        if (pm.getComponentEnabledSetting(cn) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            if (NekoLand.DEBUG) {
                Log.v(TAG, "Disabling tile.");
            }
            pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            FirebaseLogger.INSTANCE.histogram(this, "egg_neko_enable", 0);
            toastUp("\uD83D\uDEAB");
        } else {
            if (NekoLand.DEBUG) {
                Log.v(TAG, "Enabling tile.");
            }
            // Check for notification permission
            var notificationutils = NotificationUtils.INSTANCE;
            if (!notificationutils.canSendNotification(this)) {
                Log.d(TAG, "Requesting notification permission");
                notificationutils.requestNotificationPermission("Notification permission is required to show you when a cat visits", this, notificationPermissionLauncher);
            }

            pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            FirebaseLogger.INSTANCE.histogram(this, "egg_neko_enable", 1);
            toastUp("\uD83D\uDC31");
        }
        finish();
    }

}
