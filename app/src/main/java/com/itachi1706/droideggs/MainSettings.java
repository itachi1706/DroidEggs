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

import android.os.Bundle;
import android.preference.PreferenceActivity;
import androidx.appcompat.app.AppCompatActivity;

import com.itachi1706.appupdater.EasterEggResMusicPrefFragment;
import com.itachi1706.appupdater.SettingsInitializer;

import de.psdev.licensesdialog.LicensesDialog;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class MainSettings extends AppCompatActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends EasterEggResMusicPrefFragment {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_general);

            new SettingsInitializer().setFullscreen(true).explodeUpdaterSettings(getActivity(), R.mipmap.ic_launcher, CommonVariables.BASE_SERVER_URL,
                    getResources().getString(R.string.update_link), getResources().getString(R.string.link_updates), this);

            super.addEggMethods(true, preference -> {
                new LicensesDialog.Builder(getActivity()).setNotices(R.raw.notices)
                        .setIncludeOwnLicense(true).build().show();
                return true;
            });
        }

        @Override
        public int getMusicResource() {
            return R.raw.hatsune_miku_romeo_and_cinderella;
        }

        @Override
        public String getStartEggMessage() {
            return "Hope you like Vocaloid! xD";
        }

        @Override
        public String getEndEggMessage() {
            return "Aww okay... :(";
        }

        @Override
        public String getStopEggButtonText() {
            return "NO I DON'T! D:";
        }
    }
}
