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

package com.itachi1706.droideggs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import com.itachi1706.appupdater.EasterEggResMusicPrefFragment
import com.itachi1706.appupdater.SettingsInitializer
import com.itachi1706.helperlib.helpers.PrefHelper
import me.jfenn.attribouter.Attribouter


class MainSettings : AppCompatActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        supportFragmentManager.beginTransaction().replace(android.R.id.content, GeneralPreferenceFragment()).commit()
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    class GeneralPreferenceFragment : EasterEggResMusicPrefFragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val view = super.onCreateView(inflater, container, savedInstanceState)

            view.fitsSystemWindows = true

            return view;
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_general)

            SettingsInitializer().setFullscreen(true).explodeUpdaterSettings(activity, R.mipmap.ic_launcher, CommonVariables.BASE_SERVER_URL,
                    resources.getString(R.string.update_link), resources.getString(R.string.link_updates), this)
                    .setAboutApp(true) { Attribouter.from(requireContext()).show(); true; }
                    .setIssueTracking(true, "https://itachi1706.atlassian.net/browse/DEGGAND")
                    .setBugReporting(true, "https://itachi1706.atlassian.net/servicedesk/customer/portal/3")
                    .explodeInfoSettings(this)

            super.init()
            findPreference<Preference>("app_theme")?.setOnPreferenceChangeListener{ _, newValue -> PrefHelper.handleDefaultThemeSwitch(newValue.toString()); true }
        }

        override fun getMusicResource(): Int { return R.raw.hatsune_miku_romeo_and_cinderella }

        override fun getStartEggMessage(): String { return "Hope you like Vocaloid! xD" }

        override fun getEndEggMessage(): String { return "Aww okay... :(" }

        override fun getStopEggButtonText(): String { return "NO I DON'T! D:" }
    }
}
