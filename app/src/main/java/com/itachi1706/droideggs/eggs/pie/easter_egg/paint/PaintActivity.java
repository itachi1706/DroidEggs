/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.itachi1706.droideggs.eggs.pie.easter_egg.paint;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Magnifier;

import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.itachi1706.droideggs.R;

import java.util.Arrays;
import java.util.stream.IntStream;

@RequiresApi(24)
public class PaintActivity extends Activity {
    private static final float MAX_BRUSH_WIDTH_DP = 100f;
    private static final float MIN_BRUSH_WIDTH_DP = 1f;
    private static final int NUM_BRUSHES = 6;
    private static final int NUM_COLORS = 6;
    private Painting painting = null;
    private CutoutAvoidingToolbar toolbar = null;
    private LinearLayout brushes = null;
    private LinearLayout colors = null;
    private boolean sampling = false;
    private View.OnClickListener buttonHandler = view -> {
        if (view.getId() == R.id.btnBrush) {
            view.setSelected(true);
            hideToolbar(colors);
            toggleToolbar(brushes);
        } else if (view.getId() == R.id.btnColor) {
            view.setSelected(true);
            hideToolbar(brushes);
            toggleToolbar(colors);
        } else if (view.getId() == R.id.btnClear) {
            painting.clear();
        } else if (view.getId() == R.id.btnSample) {
            sampling = true;
            view.setSelected(true);
        } else if (view.getId() == R.id.btnZen) {
            painting.setZenMode(!painting.getZenMode());
            view.animate()
                    .setStartDelay(200)
                    .setInterpolator(new OvershootInterpolator())
                    .rotation(painting.getZenMode() ? 0f : 90f);
        }
    };
    private void showToolbar(View bar) {
        if (bar.getVisibility() != View.GONE) return;
        bar.setVisibility(View.VISIBLE);
        bar.setTranslationY(toolbar.getHeight()/2f);
        bar.animate()
                .translationY(toolbar.getHeight())
                .alpha(1f)
                .setDuration(220)
                .start();
    }
    private void hideToolbar(View bar) {
        if (bar.getVisibility() != View.VISIBLE) return;
        bar.animate()
                .translationY(toolbar.getHeight()/2f)
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> bar.setVisibility(View.GONE))
                .start();
    }
    private void toggleToolbar(View bar) {
        if (bar.getVisibility() == View.VISIBLE) {
            hideToolbar(bar);
        } else {
            showToolbar(bar);
        }
    }
    private BrushPropertyDrawable widthButtonDrawable;
    private BrushPropertyDrawable colorButtonDrawable;
    private float maxBrushWidth;
    private float minBrushWidth;
    private int nightMode = Configuration.UI_MODE_NIGHT_UNDEFINED;
    static float lerp(float f, float a, float b) {
        return a + (b-a) * f;
    }
    @SuppressLint("ClickableViewAccessibility")
    void setupViews(Painting oldPainting) {
        setContentView(R.layout.pie_activity_paint);
        painting = oldPainting != null ? oldPainting : new Painting(this);
        ((FrameLayout) findViewById(R.id.contentView)).addView(painting,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        painting.setPaperColor(ContextCompat.getColor(getApplicationContext(), R.color.paper_color));
        painting.setPaintColor(ContextCompat.getColor(getApplicationContext(),R.color.paint_color));
        toolbar = findViewById(R.id.toolbar);
        brushes = findViewById(R.id.brushes);
        colors = findViewById(R.id.colors);
        Magnifier magnifier;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) magnifier = new Magnifier(painting);
        else {
            magnifier = null;
        }
        painting.setOnTouchListener(
                (view, event) -> {
                    switch (event.getActionMasked()) {
                        case ACTION_DOWN:
                        case ACTION_MOVE:
                            if (sampling) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) magnifier.show(event.getX(), event.getY());
                                colorButtonDrawable.setWellColor(
                                        painting.sampleAt(event.getX(), event.getY()));
                                return true;
                            }
                            break;
                        case ACTION_CANCEL:
                            if (sampling) {
                                findViewById(R.id.btnSample).setSelected(false);
                                sampling = false;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) magnifier.dismiss();
                            }
                            break;
                        case ACTION_UP:
                            if (sampling) {
                                findViewById(R.id.btnSample).setSelected(false);
                                sampling = false;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) magnifier.dismiss();
                                painting.setPaintColor(
                                        painting.sampleAt(event.getX(), event.getY()));
                                refreshBrushAndColor();
                            }
                            break;
                    }
                    return false; // allow view to continue handling
                });
        findViewById(R.id.btnBrush).setOnClickListener(buttonHandler);
        findViewById(R.id.btnColor).setOnClickListener(buttonHandler);
        findViewById(R.id.btnClear).setOnClickListener(buttonHandler);
        findViewById(R.id.btnSample).setOnClickListener(buttonHandler);
        findViewById(R.id.btnZen).setOnClickListener(buttonHandler);
        findViewById(R.id.btnColor).setOnLongClickListener(view -> {
            colors.removeAllViews();
            showToolbar(colors);
            refreshBrushAndColor();
            return true;
        });
        findViewById(R.id.btnClear).setOnLongClickListener(view -> {
            painting.invertContents();
            return true;
        });
        widthButtonDrawable = new BrushPropertyDrawable(this);
        widthButtonDrawable.setFrameColor(ContextCompat.getColor(getApplicationContext(),R.color.toolbar_icon_color));
        colorButtonDrawable = new BrushPropertyDrawable(this);
        colorButtonDrawable.setFrameColor(ContextCompat.getColor(getApplicationContext(),R.color.toolbar_icon_color));
        ((ImageButton) findViewById(R.id.btnBrush)).setImageDrawable(widthButtonDrawable);
        ((ImageButton) findViewById(R.id.btnColor)).setImageDrawable(colorButtonDrawable);
        refreshBrushAndColor();
    }
    private void refreshBrushAndColor() {
        final LinearLayout.LayoutParams buttonLp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLp.weight = 1f;
        if (brushes.getChildCount() == 0) {
            for (int i = 0; i < NUM_BRUSHES; i++) {
                final BrushPropertyDrawable icon = new BrushPropertyDrawable(this);
                icon.setFrameColor(ContextCompat.getColor(getApplicationContext(),R.color.toolbar_icon_color));
                // exponentially increasing brush size
                final float width = lerp(
                        (float) Math.pow((float) i / NUM_BRUSHES, 2f), minBrushWidth,
                        maxBrushWidth);
                icon.setWellScale(width / maxBrushWidth);
                icon.setWellColor(ContextCompat.getColor(getApplicationContext(),R.color.toolbar_icon_color));
                final ImageButton button = new ImageButton(this);
                button.setImageDrawable(icon);
                button.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.pie_toolbar_button_bg));
                button.setOnClickListener(
                        view -> {
                            brushes.setSelected(false);
                            hideToolbar(brushes);
                            painting.setBrushWidth(width);
                            refreshBrushAndColor();
                        });
                brushes.addView(button, buttonLp);
            }
        }
        if (colors.getChildCount() == 0) {
            final Palette pal = new Palette(NUM_COLORS);
            for (final int c : IntStream.concat(
                    IntStream.of(Color.BLACK, Color.WHITE),
                    Arrays.stream(pal.getColors(), 0, pal.getColors().length)
            ).toArray()) {
                final BrushPropertyDrawable icon = new BrushPropertyDrawable(this);
                icon.setFrameColor(ContextCompat.getColor(getApplicationContext(),R.color.toolbar_icon_color));
                icon.setWellColor(c);
                final ImageButton button = new ImageButton(this);
                button.setImageDrawable(icon);
                button.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.pie_toolbar_button_bg));
                button.setOnClickListener(
                        view -> {
                            colors.setSelected(false);
                            hideToolbar(colors);
                            painting.setPaintColor(c);
                            refreshBrushAndColor();
                        });
                colors.addView(button, buttonLp);
            }
        }
        widthButtonDrawable.setWellScale(painting.getBrushWidth() / maxBrushWidth);
        widthButtonDrawable.setWellColor(painting.getPaintColor());
        colorButtonDrawable.setWellColor(painting.getPaintColor());
    }
    private void refreshNightMode(Configuration config) {
        int newNightMode =
                (config.uiMode & Configuration.UI_MODE_NIGHT_MASK);
        if (nightMode != newNightMode) {
            if (nightMode != Configuration.UI_MODE_NIGHT_UNDEFINED) {
                painting.invertContents();
                ((ViewGroup) painting.getParent()).removeView(painting);
                setupViews(painting);
                final View decorView = getWindow().getDecorView();
                int decorSUIV = decorView.getSystemUiVisibility();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (newNightMode == Configuration.UI_MODE_NIGHT_YES) {
                        decorView.setSystemUiVisibility(
                                decorSUIV & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                    } else {
                        decorView.setSystemUiVisibility(
                                decorSUIV | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                    }
                }
            }
            nightMode = newNightMode;
        }
    }
    public PaintActivity() {
        // NO-OP
    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        painting.onTrimMemory();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refreshNightMode(newConfig);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags = lp.flags
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        getWindow().setAttributes(lp);
        maxBrushWidth = MAX_BRUSH_WIDTH_DP * getResources().getDisplayMetrics().density;
        minBrushWidth = MIN_BRUSH_WIDTH_DP * getResources().getDisplayMetrics().density;
        setupViews(null);
        refreshNightMode(getResources().getConfiguration());
    }
    @Override
    public void onPostResume() {
        super.onPostResume();
    }
}