/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.itachi1706.droideggs.eggs.jelly_bean;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itachi1706.droideggs.R;

public class PlatLogoActivityJELLYBEAN extends AppCompatActivity {

    Toast mToast;
    ImageView mContent;
    int mCount;
    final Handler mHandler = new Handler();

    boolean currentBuild;

    private View makeView() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
        final int p = (int)(8 * metrics.density);
        view.setPadding(p, p, p, p);

        Typeface light = Typeface.create("sans-serif-light", Typeface.NORMAL);
        Typeface normal = Typeface.create("sans-serif", Typeface.BOLD);

        final float size = 14 * metrics.density;
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = (int) (-4*metrics.density);

        TextView tv = new TextView(this);
        if (light != null) tv.setTypeface(light);
        tv.setTextSize(1.25f*size);
        tv.setTextColor(0xFFFFFFFF);
        tv.setShadowLayer(4*metrics.density, 0, 2*metrics.density, 0x66000000);

        if (currentBuild)
            tv.setText("Android " + Build.VERSION.RELEASE);
        else
            tv.setText("Android 4.3.1");

        view.addView(tv, lp);

        tv = new TextView(this);
        if (normal != null) tv.setTypeface(normal);
        tv.setTextSize(size);
        tv.setTextColor(0xFFFFFFFF);
        tv.setShadowLayer(4*metrics.density, 0, 2*metrics.density, 0x66000000);
        tv.setText("JELLY BEAN");
        view.addView(tv, lp);

        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the shard prefs
        SharedPreferences sp = this.getSharedPreferences("com.itachi1706.droideggs_preferences", MODE_MULTI_PROCESS);
        currentBuild = sp.getBoolean("current_num", true);

        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        mToast.setView(makeView());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        mContent = new ImageView(this);
        mContent.setImageResource(R.drawable.jb_platlogo_alt);
        mContent.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        final int p = (int)(32 * metrics.density);
        mContent.setPadding(p, p, p, p);

        mContent.setOnClickListener(v -> {
            mToast.show();
            mContent.setImageResource(R.drawable.jb_platlogo);
        });

        mContent.setOnLongClickListener(v -> {
            try {
                Intent beanbag = new Intent(PlatLogoActivityJELLYBEAN.this, BeanBag.class);
                beanbag.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(beanbag);
            } catch (ActivityNotFoundException ex) {
                android.util.Log.e("PlatLogoActivity", "Couldn't find a bag of beans.");
            }
            finish();
            return true;
        });

        setContentView(mContent);
    }
}
