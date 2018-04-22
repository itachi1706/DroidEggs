package com.itachi1706.droideggs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Kenneth on 20/4/2018.
 * for com.itachi1706.droideggs in DroidEggs
 */
public class FirebaseLogger {

    public static void count(Context context, String name, int value) {
        // Log to Firebase with SELECT_CONTENT, where type = counter and id = name, ignore value
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "counter");
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        Log.i("Firebase", "Logged MetricsLogger Count: " + name);
    }

    public static void histogram(Context context, String name, int bucket) {
        // Log to Firebase with SELECT_CONTENT, where type = name and id = bucket
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, bucket + "");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, name);
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        Log.i("Firebase", "Logged MetricsLogger Histogram: " + bucket + " in " + name);
    }
}
