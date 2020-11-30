package com.itachi1706.droideggs

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.itachi1706.appupdater.AppUpdateInitializer
import com.itachi1706.appupdater.`object`.CAAnalytics
import com.itachi1706.appupdater.utils.AnalyticsHelper
import com.itachi1706.helperlib.helpers.PrefHelper
import kotlinx.android.synthetic.main.activity_main_screen.*

class MainScreenKtx : AppCompatActivity() {

    private var populatedList = ArrayList<SelectorObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        btnCurrent.setOnClickListener { v -> this.startActivityForResult(Intent(this, CurrentEgg::class.java), RC_CURRENT_EGG) }

        AppUpdateInitializer(this, PrefHelper.getDefaultSharedPreferences(applicationContext), R.mipmap.ic_launcher, CommonVariables.BASE_SERVER_URL, true).setOnlyOnWifiCheck(true).checkForUpdate()

        val mFirebaseAnalytics = Firebase.analytics
        val helper = AnalyticsHelper(this, true)
        Runnable {
            val analytics = helper.data
            if (analytics != null) setAnalyticsData(true, mFirebaseAnalytics, analytics)
            else setAnalyticsData(false, mFirebaseAnalytics, null)
        }.run()
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)

        PrefHelper.handleDefaultThemeSwitch(PrefHelper.getDefaultSharedPreferences(this).getString("app_theme", "batterydefault")!!)
    }

    override fun onResume() {
        super.onResume()

        populatedList = ArrayList()
        populatedList = PopulateSelector.populateSelectors(this)

        val sp = PrefHelper.getDefaultSharedPreferences(this)
        val newSel = sp.getBoolean("check_version", false)

        val tmpAdapter = if (newSel) ArrayAdapter(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.legacy_version_with_egg))
            else SelectorAdapter(this, R.layout.listview_selector, populatedList)
        lvEasterEggSelection.adapter = tmpAdapter
        lvEasterEggSelection.onItemClickListener = SelectorOnClick(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CURRENT_EGG) {
            if (resultCode == RESULT_CANCELED) {
                val type = data?.getStringExtra("class")
                val title = data?.getStringExtra("title")
                val body = data?.getStringExtra("body")
                when (type) {
                    "noaccess" -> unableToAccessEasterEgg(title)
                    "comingsoon" -> eggComingSoon()
                    "weird" -> weird(title, body)
                    else -> noEgg()
                }
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_screen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            startActivity(Intent(this, MainSettings::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setAnalyticsData(enabled: Boolean, firebaseAnalytics: FirebaseAnalytics, analytics: CAAnalytics?) {
        firebaseAnalytics.setUserProperty("debug_mode", if (enabled) analytics?.isDebug.toString() + "" else null)
        firebaseAnalytics.setUserProperty("device_manufacturer", if (enabled) analytics?.getdManufacturer() else null)
        firebaseAnalytics.setUserProperty("device_codename", if (enabled) analytics?.getdCodename() else null)
        firebaseAnalytics.setUserProperty("device_fingerprint", if (enabled) analytics?.getdFingerprint() else null)
        firebaseAnalytics.setUserProperty("device_cpu_abi", if (enabled) analytics?.getdCPU() else null)
        firebaseAnalytics.setUserProperty("device_tags", if (enabled) analytics?.getdTags() else null)
        firebaseAnalytics.setUserProperty("app_version_code", if (enabled) analytics?.appVerCode.toString() else null)
        firebaseAnalytics.setUserProperty("android_sec_patch", if (enabled) analytics?.sdkPatch else null)
        firebaseAnalytics.setUserProperty("AndroidOS", if (enabled) analytics?.sdkver.toString() else null)
    }

    fun unableToAccessEasterEgg(SDK_VERSION: String?) {
        Snackbar.make(this.findViewById(android.R.id.content), "Unable to launch (INVALID VERSION)", Snackbar.LENGTH_LONG)
                .setAction("WHY?") { v -> AlertDialog.Builder(v.context)
                        .setMessage("We are unable to give you access to this easter egg due to incompatible Android Version. You require at least Android $SDK_VERSION to access this activity")
                        .setPositiveButton("AWW :(", null).show()
                }.show()
    }

    fun weird(expected: String?, actual: String?) {
        Snackbar.make(findViewById(android.R.id.content), "Unable to launch (INVALID VERSION)", Snackbar.LENGTH_LONG)
                .setAction("WAIT WHAT?") { v -> AlertDialog.Builder(v.context).setMessage("We are unable to give you access to this easter egg due to incompatible Android Version. " +
                        "You require at least Android $actual to access this activity \n\n Dev Note: Interestingly... this easter egg is for $expected, so I'm confused. LOL")
                        .setPositiveButton("WAIT WTF? O.o", null).setNegativeButton("AWW :(", null).show()}.show()
    }

    fun noEgg() {
        Snackbar.make(findViewById(android.R.id.content), "No Eggs for you", Snackbar.LENGTH_LONG)
                .setAction("WAIT WHAT?") { v -> AlertDialog.Builder(v.context).setMessage("Easter Eggs are only present in Android from Android 2.3 Gingerbread. " +
                        "Your Android Version is do not have an easter egg unfortunately :(").setPositiveButton("AWW :(", null).show() }.show()
    }

    fun eggComingSoon() {
        Snackbar.make(findViewById(android.R.id.content), "Easter Egg Coming Soon", Snackbar.LENGTH_SHORT).setAction("DISMISS", null).show()
    }

    fun limitedAccessToEgg(SDK_VERSION: String) {
        Snackbar.make(findViewById(android.R.id.content), "Unable to access egg (INVALID VERSION)", Snackbar.LENGTH_LONG)
                .setAction("Get Access") { v -> AlertDialog.Builder(v.context).setMessage("At your current version of Android, you are not able to experience the full easter egg. " +
                        "You require Android $SDK_VERSION to access it fully. \n\nHowever, if you wish you are able to access a limited version of the egg by checking the \"Access Partial Egg\" setting in the app settings")
                        .setNeutralButton("App Settings") { _, _ -> startActivity(Intent(this, MainSettings::class.java)) }
                        .setPositiveButton("AWW :(", null).show()}.show()
    }

    companion object {
        private val RC_CURRENT_EGG = 2
    }

}