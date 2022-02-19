package com.itachi1706.droideggs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;

import org.json.JSONObject;

public class PlatLogoCommon {
    private PlatLogoCommon() {
        throw new IllegalStateException("Utility class");
    }

    static double mPressureMin = 0, mPressureMax = -1;

    public static void measureTouchPressure(MotionEvent event) {
        final float pressure = event.getPressure();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mPressureMax < 0) {
                    mPressureMin = mPressureMax = pressure;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pressure < mPressureMin) mPressureMin = pressure;
                if (pressure > mPressureMax) mPressureMax = pressure;
                break;
        }
    }

    public static void syncTouchPressure(String touchStats, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        final String KEY = touchStats;
        try {
            final String touchDataJson = pref.getString(KEY, null);
            final JSONObject touchData = new JSONObject(
                    touchDataJson != null ? touchDataJson : "{}");
            if (touchData.has("min")) {
                mPressureMin = Math.min(mPressureMin, touchData.getDouble("min"));
            }
            if (touchData.has("max")) {
                mPressureMax = Math.max(mPressureMax, touchData.getDouble("max"));
            }
            if (mPressureMax >= 0) {
                touchData.put("min", mPressureMin);
                touchData.put("max", mPressureMax);
                pref.edit().putString(touchStats, touchData.toString()).apply();
            }
        } catch (Exception e) {
            Log.e("PlatLogoActivity", "Can't write touch settings", e);
        }
    }

}
