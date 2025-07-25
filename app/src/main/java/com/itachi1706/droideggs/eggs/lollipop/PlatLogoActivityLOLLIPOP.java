/*
 * Copyright (C) 2014 The Android Open Source Project
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
 * limitations under the License
 */

package com.itachi1706.droideggs.eggs.lollipop;

import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.itachi1706.droideggs.R;

import java.util.Random;

public class PlatLogoActivityLOLLIPOP extends AppCompatActivity {
    static final int[] FLAVORS = {
            0xFF9C27B0, 0xFFBA68C8, // grape
            0xFFFF9800, 0xFFFFB74D, // orange
            0xFFF06292, 0xFFF8BBD0, // bubblegum
            0xFFAFB42B, 0xFFCDDC39, // lime
            0xFFFFEB3B, 0xFFFFF176, // lemon
            0xFF795548, 0xFFA1887F, // mystery flavor
    };
    FrameLayout mLayout;
    int mTapCount;
    int mKeyCount;
    PathInterpolator mInterpolator = new PathInterpolator(0f, 0f, 0.5f, 1f);
    private static final Random random = new Random();

    static int newColorIndex() {
        return 2*(random.nextInt(FLAVORS.length/2));
    }

    Drawable makeRipple() {
        final int idx = newColorIndex();
        final ShapeDrawable popbg = new ShapeDrawable(new OvalShape());
        popbg.getPaint().setColor(FLAVORS[idx]);
        return new RippleDrawable(
                ColorStateList.valueOf(FLAVORS[idx + 1]),
                popbg, null);
    }

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

        final View stick = new View(this) {
            final Paint mPaint = new Paint();
            final Path mShadow = new Path();

            @Override
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                setWillNotDraw(false);
                setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRect(0, getHeight() / 2, getWidth(), getHeight());
                    }
                });
            }
            @Override
            public void onDraw(Canvas c) {
                final int w = c.getWidth();
                final int h = c.getHeight() / 2;
                c.translate(0, h);
                final GradientDrawable g = new GradientDrawable();
                g.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                g.setGradientCenter(w * 0.75f, 0);
                g.setColors(new int[] { 0xFFFFFFFF, 0xFFAAAAAA });
                g.setBounds(0, 0, w, h);
                g.draw(c);
                mPaint.setColor(0xFFAAAAAA);
                mShadow.reset();
                mShadow.moveTo(0,0);
                mShadow.lineTo(w, 0);
                mShadow.lineTo(w, (float) size /2 + 1.5f*w);
                mShadow.lineTo(0, (float) size /2);
                mShadow.close();
                c.drawPath(mShadow, mPaint);
            }
        };
        mLayout.addView(stick, new FrameLayout.LayoutParams((int) (32 * dp),
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));
        stick.setAlpha(0f);

        final ImageView im = new ImageView(this);
        im.setTranslationZ(20);
        im.setScaleX(0);
        im.setScaleY(0);
        final Drawable platlogo = getDrawable(R.drawable.lollipop_l_platlogo);
        platlogo.setAlpha(0);
        im.setImageDrawable(platlogo);
        im.setBackground(makeRipple());
        im.setClickable(true);
        final ShapeDrawable highlight = new ShapeDrawable(new OvalShape());
        highlight.getPaint().setColor(0x10FFFFFF);
        highlight.setBounds((int)(size*.15f), (int)(size*.15f),
                (int)(size*.6f), (int)(size*.6f));
        im.getOverlay().add(highlight);
        im.setOnClickListener(v -> {
            if (mTapCount == 0) {
                im.animate()
                        .translationZ(40)
                        .scaleX(1)
                        .scaleY(1)
                        .setInterpolator(mInterpolator)
                        .setDuration(700)
                        .setStartDelay(500)
                        .start();

                final ObjectAnimator a = ObjectAnimator.ofInt(platlogo, "alpha", 0, 255);
                a.setInterpolator(mInterpolator);
                a.setStartDelay(1000);
                a.start();

                stick.animate()
                        .translationZ(20)
                        .alpha(1)
                        .setInterpolator(mInterpolator)
                        .setDuration(700)
                        .setStartDelay(750)
                        .start();

                im.setOnLongClickListener(v1 -> {
                    if (mTapCount < 5) return false;

                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(PlatLogoActivityLOLLIPOP.this);
                    if (pref.getLong("L_EGG_MODE", 0) == 0) {
                        // For posterity: the moment this user unlocked the easter egg
                        pref.edit().putLong("L_EGG_MODE", System.currentTimeMillis()).apply();
                    }
                    im.post(() -> {
                        try {
                            Intent lland = new Intent(PlatLogoActivityLOLLIPOP.this, LLandActivity.class);
                            lland.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(lland);
                        } catch (ActivityNotFoundException ex) {
                            Log.e("PlatLogoActivity", "No more eggs.");
                        }
                        finish();
                    });
                    return true;
                });
            } else {
                im.setBackground(makeRipple());
            }
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

        im.animate().scaleX(0.3f).scaleY(0.3f)
                .setInterpolator(mInterpolator)
                .setDuration(500)
                .setStartDelay(800)
                .start();
    }
}
