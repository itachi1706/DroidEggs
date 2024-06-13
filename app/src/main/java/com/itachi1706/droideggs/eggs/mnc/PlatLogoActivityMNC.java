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

package com.itachi1706.droideggs.eggs.mnc;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.itachi1706.droideggs.R;
import com.itachi1706.droideggs.eggs.marshmallow.Color;

public class PlatLogoActivityMNC extends AppCompatActivity {
    FrameLayout mLayout;
    int mTapCount;
    int mKeyCount;
    PathInterpolator mInterpolator = new PathInterpolator(0f, 0f, 0.5f, 1f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayout = new FrameLayout(this);
        setContentView(mLayout);
    }

    @Override
    public void onAttachedToWindow() {
        final DisplayMetrics dm = getResources().getDisplayMetrics();
        final float dp = dm.density;
        final int size = (int)
                (Math.min(Math.min(dm.widthPixels, dm.heightPixels), 600*dp) - 100*dp);

        final View im = new View(this);
        im.setTranslationZ(20);
        im.setScaleX(0.5f);
        im.setScaleY(0.5f);
        im.setAlpha(0f);
        im.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                final int pad = (int) (8 * dp);
                outline.setOval(pad, pad, view.getWidth() - pad, view.getHeight() - pad);
            }
        });
        final float hue = (float) Math.random();
        final Paint bgPaint = new Paint();
        bgPaint.setColor(Color.HSBtoColor(hue, 0.4f, 1f));
        final Paint fgPaint = new Paint();
        fgPaint.setColor(Color.HSBtoColor(hue, 0.5f, 1f));
        final Drawable M = getDrawable(R.drawable.marshmallow_platlogo_m);
        final Drawable platlogo = new Drawable() {
            @Override
            public void setAlpha(int alpha) { }
            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) { }
            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
            @Override
            public void draw(Canvas c) {
                final float r = c.getWidth() / 2f;
                c.drawCircle(r, r, r, bgPaint);
                c.drawArc(0, 0, 2 * r, 2 * r, 135, 180, false, fgPaint);
                M.setBounds(0, 0, c.getWidth(), c.getHeight());
                M.draw(c);
            }
        };
        im.setBackground(new RippleDrawable(
                ColorStateList.valueOf(0xFFFFFFFF),
                platlogo,
                null));
        im.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        im.setClickable(true);


        im.setOnClickListener(v -> {
            im.setOnLongClickListener(v1 -> {
                if (mTapCount < 5) return false;

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(PlatLogoActivityMNC.this);
                if (pref.getLong("MNC_EGG_MODE", 0) == 0) {
                    // For posterity: the moment this user unlocked the easter egg
                    pref.edit().putLong("MNC_EGG_MODE", System.currentTimeMillis()).apply();
                }
                im.post(() -> {
                    Toast.makeText(PlatLogoActivityMNC.this, "\u00af\\_(\u30c4)_/\u00af", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return true;
            });
            mTapCount++;
        });

        // Enable hardware keyboard input for TV compatibility.
        im.setFocusable(true);
        im.requestFocus();
        im.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode != KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                ++mKeyCount;
                if (mKeyCount > 2) {
                    if (mTapCount > 5) {
                        im.performLongClick();
                    } else {
                        im.performClick();
                    }
                }
                return true;
            } else {
                return false;
            }
        });

        mLayout.addView(im, new FrameLayout.LayoutParams(size, size, Gravity.CENTER));

        //Make it appear
        im.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setInterpolator(mInterpolator)
                .setDuration(500)
                .setStartDelay(800)
                .start();

    }
}
