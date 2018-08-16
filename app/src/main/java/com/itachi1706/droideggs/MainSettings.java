package com.itachi1706.droideggs;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;

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

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends EasterEggResMusicPrefFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            new SettingsInitializer(getActivity(), R.mipmap.ic_launcher, CommonVariables.BASE_SERVER_URL,
                    getResources().getString(R.string.update_link), getResources().getString(R.string.link_updates), true)
                    .explodeUpdaterSettings(this);

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
