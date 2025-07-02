/*);
 * Copyright (C) 2011 The Android Open Source Project
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

package com.itachi1706.droideggs.eggs.ice_cream_sandwich;

import android.animation.TimeAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.itachi1706.droideggs.R;

import java.util.Random;

public class Nyandroid extends AppCompatActivity {

    static final boolean DEBUG = false;
    private static final String TAG = "Nyandroid";

    public static class Board extends FrameLayout
    {
        public static final boolean FIXED_STARS = true;
        public static final int NUM_CATS = 20;

        static Random sRNG = new Random();

        static float lerp(float a, float b, float f) {
            return (b-a)*f + a;
        }

        static float randfrange(float a, float b) {
            return lerp(a, b, sRNG.nextFloat());
        }

        static int randsign() {
            return sRNG.nextBoolean() ? 1 : -1;
        }

        static <E> E pick(E[] array) {
            if (array.length == 0) return null;
            return array[sRNG.nextInt(array.length)];
        }

        public class FlyingCat extends AppCompatImageView {
            public static final float VMAX = 1000.0f;
            public static final float VMIN = 100.0f;

            private float v;
            private float z;

            public FlyingCat(Context context, AttributeSet as) {
                super(context, as);
                setImageResource(R.drawable.ics_nyandroid_anim); // @@@

                if (DEBUG) setBackgroundColor(0x80FF0000);
            }

            @Override
            public String toString() {
                return String.format("<cat (%.1f, %.1f) (%d x %d)>",
                        getX(), getY(), getWidth(), getHeight());
            }

            public void reset() {
                final float scale = lerp(0.1f,2f,z);
                setScaleX(scale); setScaleY(scale);

                setX(-scale*getWidth()+1);
                setY(randfrange(0, Board.this.getHeight()-scale*getHeight()));
                v = lerp(VMIN, VMAX, z);

            }

            public void update(float dt) {
                setX(getX() + v * dt);
            }
        }

        TimeAnimator mAnim;

        public Board(Context context, AttributeSet as) {
            super(context, as);

            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            hideSystemUI();
            setBackgroundColor(0xFF003366);
        }

        private void hideSystemUI() {
            Log.d(TAG, "Hiding system UI");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
            Log.d(TAG, "System UI hidden");

        }

        private void reset() {
            removeAllViews();

            final ViewGroup.LayoutParams wrap = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            if (FIXED_STARS) {
                for(int i=0; i<20; i++) {
                    ImageView fixedStar = new ImageView(getContext(), null);
                    if (DEBUG) fixedStar.setBackgroundColor(0x8000FF80);
                    fixedStar.setImageResource(R.drawable.ics_star_anim); // @@@
                    addView(fixedStar, wrap);
                    final float scale = randfrange(0.1f, 1f);
                    fixedStar.setScaleX(scale); fixedStar.setScaleY(scale);
                    fixedStar.setX(randfrange(0, getWidth()));
                    fixedStar.setY(randfrange(0, getHeight()));
                    final AnimationDrawable anim = (AnimationDrawable) fixedStar.getDrawable();
                    postDelayed(anim::start, (int) randfrange(0, 1000));
                }
            }

            for(int i=0; i<NUM_CATS; i++) {
                FlyingCat nv = new FlyingCat(getContext(), null);
                addView(nv, wrap);
                nv.z = ((float)i/NUM_CATS);
                nv.z *= nv.z;
                nv.reset();
                nv.setX(randfrange(0,Board.this.getWidth()));
                final AnimationDrawable anim = (AnimationDrawable) nv.getDrawable();
                postDelayed(anim::start, (int) randfrange(0, 1000));
            }

            if (mAnim != null) {
                mAnim.cancel();
            }

            //Wait what? An ICS Easter Egg requiring JELLYBEAN API? O.o
            mAnim = new TimeAnimator();
            mAnim.setTimeListener((animation, totalTime, deltaTime) -> {
                // setRotation(totalTime * 0.01f); // not as cool as you would think

                for (int i=0; i<getChildCount(); i++) {
                    View v = getChildAt(i);
                    if (!(v instanceof FlyingCat)) continue;
                    FlyingCat nv = (FlyingCat) v;
                    nv.update(deltaTime / 1000f);
                    final float catWidth = nv.getWidth() * nv.getScaleX();
                    final float catHeight = nv.getHeight() * nv.getScaleY();
                    if (   nv.getX() + catWidth < -2
                            || nv.getX() > getWidth() + 2
                            || nv.getY() + catHeight < -2
                            || nv.getY() > getHeight() + 2)
                    {
                        nv.reset();
                    }
                }
            });
        }

        @Override
        protected void onSizeChanged (int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w,h,oldw,oldh);
            post(() -> {
                reset();
                mAnim.start();
            });
        }


        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            mAnim.cancel();
        }

        @Override
        public boolean isOpaque() {
            return true;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Nyandroid onStart called");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            );
        }
    }

    private int firstTime = 2;

    @Override
    public void onResume() {
        Board mBoard;
        super.onResume();
        Log.d(TAG, "Nyandroid onResume called");
        mBoard = new Board(this, null);
        setContentView(mBoard);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            mBoard.setOnApplyWindowInsetsListener((v, insets) -> {
                Log.d(TAG, "onApplyWindowInsets called");
                if (firstTime > 0) {
                    firstTime--;
                    // If the navigation bar is visible, hide it
                    WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                            .hide(WindowInsetsCompat.Type.navigationBars());
                    return insets;
                }

                if (!insets.isVisible(WindowInsets.Type.navigationBars())) {
                    Log.d(TAG, "Navigation bar shown, finishing Nyandroid");
                    Nyandroid.this.finish();
                }
                return insets;
            });
        } else {
            mBoard.setOnSystemUiVisibilityChangeListener(vis -> {
                if (0 == (vis & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)) {
                    Nyandroid.this.finish();
                }
            });
        }

        startNyan();
    }

    @Override
    public void onUserInteraction() {
        stopNyan();
        finish();
    }

    MediaPlayer mp;
    boolean shouldNyan = true;

    @Override
    public void onPause(){
        super.onPause();
        stopNyan();
    }

    private void startNyan(){
        SharedPreferences sp = this.getSharedPreferences("com.itachi1706.droideggs_preferences", MODE_MULTI_PROCESS);
        shouldNyan = sp.getBoolean("nyannyan", true);
        Log.d("Nyandroid", "Should Nyan: " + shouldNyan);
        if (shouldNyan) {
            mp = MediaPlayer.create(this, R.raw.nyancat);
            mp.setLooping(true);
            mp.start();
        }
    }

    private void stopNyan(){
        if (mp != null && shouldNyan) {
            if (mp.isLooping()) {
                mp.setLooping(false);
            }
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            mp.release();
            mp = null;
        }
    }
}
