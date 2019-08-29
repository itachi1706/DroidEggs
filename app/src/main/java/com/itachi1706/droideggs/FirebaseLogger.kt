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

import android.content.Context
import android.os.Bundle
import android.util.Log

import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Created by Kenneth on 20/4/2018.
 * for com.itachi1706.droideggs in DroidEggs
 */
object FirebaseLogger {

    fun logFirebase(context: Context, id: String, type: String) {
        val analytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type)
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun count(context: Context, name: String, value: Int) {
        // Log to Firebase with SELECT_CONTENT, where type = counter and id = name, ignore value
        logFirebase(context, name, "counter")
        Log.i("Firebase", "Logged MetricsLogger Count: $name")
    }

    fun histogram(context: Context, name: String, bucket: Int) {
        // Log to Firebase with SELECT_CONTENT, where type = name and id = bucket
        logFirebase(context, bucket.toString() + "", name)
        Log.i("Firebase", "Logged MetricsLogger Histogram: $bucket in $name")
    }
}
