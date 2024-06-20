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

import android.animation.TimeAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.itachi1706.droideggs.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Kenneth on 1/6/2015
 * for DroidEggs in package com.itachi1706.droideggs.LollipopEgg
 */
public class LLand extends FrameLayout {
    public static final String TAG = "LLand";

    public static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);
    public static final boolean DEBUG_DRAW = false; // DEBUG

    public static final void l(String s, Object... objects) {
        if (DEBUG) {
            Log.d(TAG, String.format(s, objects));
        }
    }

    public static final boolean AUTOSTART = true;
    public static final boolean HAVE_STARS = true;

    public static final float DEBUG_SPEED_MULTIPLIER = 1f;
    public static final boolean DEBUG_IDDQD = false;

    static final int[] POPS = {
            // resid                // spinny!
            R.drawable.lollipop_pop_belt, 0,
            R.drawable.lollipop_pop_droid, 0,
            R.drawable.lollipop_pop_pizza, 1,
            R.drawable.lollipop_pop_stripes, 0,
            R.drawable.lollipop_pop_swirl, 1,
            R.drawable.lollipop_pop_vortex, 1,
            R.drawable.lollipop_pop_vortex2, 1,
    };

    private static class Params {
        private final float translationPerSec;
        private final int obstaclePeriod;
        private final int boostDv;
        private final int playerHitSize;
        private final int playerSize;
        private final int obstacleWidth;
        private final int obstacleStemWidth;
        private final int obstacleGap;
        private final int obstacleMin;
        private final int buildingWidthMin;
        private final int buildingWidthMax;
        private final int buildingHeightMin;
        private final int cloudSizeMin;
        private final int cloudSizeMax;
        private final int starSizeMin;
        private final int starSizeMax;
        private final int g;
        private final int maxV;
        private final float sceneryZ;
        private final float obstacleZ;
        private final float playerZ;
        private final float playerZBoost;
        private final float hudZ;

        public Params(Resources res) {
            translationPerSec = res.getDimension(R.dimen.translation_per_sec);
            int obstacleSpacing = res.getDimensionPixelSize(R.dimen.obstacle_spacing);
            obstaclePeriod = (int) (obstacleSpacing / translationPerSec);
            boostDv = res.getDimensionPixelSize(R.dimen.boost_dv);
            playerHitSize = res.getDimensionPixelSize(R.dimen.player_hit_size);
            playerSize = res.getDimensionPixelSize(R.dimen.player_size);
            obstacleWidth = res.getDimensionPixelSize(R.dimen.obstacle_width);
            obstacleStemWidth = res.getDimensionPixelSize(R.dimen.obstacle_stem_width);
            obstacleGap = res.getDimensionPixelSize(R.dimen.obstacle_gap);
            obstacleMin = res.getDimensionPixelSize(R.dimen.obstacle_height_min);
            buildingHeightMin = res.getDimensionPixelSize(R.dimen.building_height_min);
            buildingWidthMin = res.getDimensionPixelSize(R.dimen.building_width_min);
            buildingWidthMax = res.getDimensionPixelSize(R.dimen.building_width_max);
            cloudSizeMin = res.getDimensionPixelSize(R.dimen.cloud_size_min);
            cloudSizeMax = res.getDimensionPixelSize(R.dimen.cloud_size_max);
            starSizeMin = res.getDimensionPixelSize(R.dimen.star_size_min);
            starSizeMax = res.getDimensionPixelSize(R.dimen.star_size_max);

            g = res.getDimensionPixelSize(R.dimen.G);
            maxV = res.getDimensionPixelSize(R.dimen.max_v);

            sceneryZ = res.getDimensionPixelSize(R.dimen.scenery_z);
            obstacleZ = res.getDimensionPixelSize(R.dimen.obstacle_z);
            playerZ = res.getDimensionPixelSize(R.dimen.player_z);
            playerZBoost = res.getDimensionPixelSize(R.dimen.player_z_boost);
            hudZ = res.getDimensionPixelSize(R.dimen.hud_z);
        }
    }

    private TimeAnimator mAnim;

    private TextView mScoreField;
    private View mSplash;

    private Player mDroid;
    private ArrayList<Obstacle> mObstaclesInPlay = new ArrayList<>();

    private float t;
    private float dt;

    private int mScore;
    private float mLastPipeTime; // in sec
    private int mWidth;
    private int mHeight;
    private boolean mAnimating;
    private boolean mPlaying;
    private boolean mFrozen; // after death, a short backoff


    private int mTimeOfDay;
    private static final int DAY = 0;
    private static final int NIGHT = 1;
    private static final int TWILIGHT = 2;
    private static final int SUNSET = 3;
    private static final int[][] SKIES = {
            {0xFFc0c0FF, 0xFFa0a0FF}, // DAY
            {0xFF000010, 0xFF000000}, // NIGHT
            {0xFF000040, 0xFF000010}, // TWILIGHT
            {0xFFa08020, 0xFF204080}, // SUNSET
    };

    private static Params params;

    public LLand(Context context) {
        this(context, null);
    }

    public LLand(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LLand(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
        params = new Params(getResources());
        mTimeOfDay = irand(0, SKIES.length);
    }

    @Override
    public boolean willNotDraw() {
        return !DEBUG;
    }

    public int getGameWidth() {
        return mWidth;
    }

    public int getGameHeight() {
        return mHeight;
    }

    public float getGameTime() {
        return t;
    }

    public float getLastTimeStep() {
        return dt;
    }

    public void setScoreField(TextView tv) {
        mScoreField = tv;
        if (tv != null) {
            tv.setTranslationZ(params.hudZ);
            if (!(mAnimating && mPlaying)) {
                tv.setTranslationY(-500);
            }
        }
    }

    public void setSplash(View v) {
        mSplash = v;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        stop();
        reset();
        if (AUTOSTART) {
            start(false);
        }
    }

    final float[] hsv = {0, 0, 0};

    private final Random random = new Random();

    @SuppressWarnings("ResourceType")
    private void reset() {
        boolean mFlipped;
        l("reset");
        final Drawable sky = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                SKIES[mTimeOfDay]
        );
        sky.setDither(true);
        setBackground(sky);

        mFlipped = frand() > 0.5f;
        setScaleX(mFlipped ? -1 : 1);

        setScore(0);

        int i = getChildCount();
        while (i-- > 0) {
            final View v = getChildAt(i);
            if (v instanceof GameView) {
                removeViewAt(i);
            }
        }

        mObstaclesInPlay.clear();

        mWidth = getWidth();
        mHeight = getHeight();

        boolean showingSun = (mTimeOfDay == DAY || mTimeOfDay == SUNSET) && frand() > 0.25;
        if (showingSun) {
            final Star sunStar = new Star(getContext());
            sunStar.setBackgroundResource(R.drawable.lollipop_sun);
            final int w = getResources().getDimensionPixelSize(R.dimen.sun_size);
            sunStar.setTranslationX(frand(w, (float) mWidth - w));
            if (mTimeOfDay == DAY) {
                sunStar.setTranslationY(frand(w, (mHeight * 0.66f)));
                sunStar.getBackground().setTint(0);
            } else {
                sunStar.setTranslationY(frand(mHeight * 0.66f, (float) mHeight - w));
                sunStar.getBackground().setTintMode(PorterDuff.Mode.SRC_ATOP);
                sunStar.getBackground().setTint(0xC0FF8000);

            }
            addView(sunStar, new FrameLayout.LayoutParams(w, w));
        }
        if (!showingSun) {
            final boolean dark = mTimeOfDay == NIGHT || mTimeOfDay == TWILIGHT;
            final float ff = frand();
            if ((dark && ff < 0.75f) || ff < 0.5f) {
                final Star moon = new Star(getContext());
                moon.setBackgroundResource(R.drawable.lollipop_moon);
                moon.getBackground().setAlpha(dark ? 255 : 128);
                moon.setScaleX(frand() > 0.5 ? -1 : 1);
                moon.setRotation(moon.getScaleX() * frand(5, 30));
                final int w = getResources().getDimensionPixelSize(R.dimen.sun_size);
                moon.setTranslationX(frand(w, (float) mWidth - w));
                moon.setTranslationY(frand(w, (float) mHeight - w));
                addView(moon, new FrameLayout.LayoutParams(w, w));
            }
        }

        final int mh = mHeight / 6;
        final boolean cloudless = frand() < 0.25;
        final int N = 20;
        for (i = 0; i < N; i++) {
            final float r1 = frand();
            final Scenery s;
            if (HAVE_STARS && r1 < 0.3 && mTimeOfDay != DAY) {
                s = new Star(getContext());
            } else if (r1 < 0.6 && !cloudless) {
                s = new Cloud(getContext());
            } else {
                s = new Building(getContext());

                s.z = (float) i / N;
                s.setTranslationZ(params.sceneryZ * (1 + s.z));
                s.v = 0.85f * s.z; // buildings move proportional to their distance
                hsv[0] = 175;
                hsv[1] = 0.25f;
                hsv[2] = 1 * s.z;
                s.setBackgroundColor(Color.HSVToColor(hsv));
                s.h = irand(params.buildingHeightMin, mh);
            }
            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(s.w, s.h);
            if (s instanceof Building) {
                lp.gravity = Gravity.BOTTOM;
            } else {
                lp.gravity = Gravity.TOP;
                final float r = frand();
                if (s instanceof Star) {
                    lp.topMargin = (int) (r * r * mHeight);
                } else {
                    lp.topMargin = (int) (1 - r * r * mHeight / 2) + mHeight / 2;
                }
            }

            addView(s, lp);
            s.setTranslationX(frand(-lp.width, (float) mWidth + lp.width));
        }

        mDroid = new Player(getContext());
        mDroid.setX((float) mWidth / 2);
        mDroid.setY((float) mHeight / 2);
        addView(mDroid, new FrameLayout.LayoutParams(params.playerSize, params.playerSize));

        mAnim = new TimeAnimator();
        mAnim.setTimeListener((timeAnimator, t, dt) -> step(t, dt));
    }

    private void setScore(int score) {
        mScore = score;
        if (mScoreField != null) mScoreField.setText(String.valueOf(score));
    }

    private void addScore(int incr) {
        setScore(mScore + incr);
    }

    private void start(boolean startPlaying) {
        l("start(startPlaying=%s)", startPlaying ? "true" : "false");
        if (startPlaying) {
            mPlaying = true;

            t = 0;
            // there's a sucker born every OBSTACLE_PERIOD
            mLastPipeTime = getGameTime() - params.obstaclePeriod;

            if (mSplash != null && mSplash.getAlpha() > 0f) {
                mSplash.setTranslationZ(params.hudZ);
                mSplash.animate().alpha(0).translationZ(0).setDuration(400);

                mScoreField.animate().translationY(0)
                        .setInterpolator(new DecelerateInterpolator())
                        .setDuration(1500);
            }

            mScoreField.setTextColor(0xFFAAAAAA);
            mScoreField.setBackgroundResource(R.drawable.lollipop_scorecard);
            mDroid.setVisibility(View.VISIBLE);
            mDroid.setX((float) mWidth / 2);
            mDroid.setY((float) mHeight / 2);
        } else {
            mDroid.setVisibility(View.GONE);
        }
        if (!mAnimating) {
            mAnim.start();
            mAnimating = true;
        }
    }

    private void stop() {
        if (mAnimating) {
            mAnim.cancel();
            mAnim = null;
            mAnimating = false;
            mScoreField.setTextColor(0xFFFFFFFF);
            mScoreField.setBackgroundResource(R.drawable.lollipop_scorecard_gameover);
            mTimeOfDay = irand(0, SKIES.length); // for next reset
            mFrozen = true;
            postDelayed(() -> mFrozen = false, 250);
        }
    }

    public static final float lerp(float x, float a, float b) {
        return (b - a) * x + a;
    }

    public static final float rlerp(float v, float a, float b) {
        return (v - a) / (b - a);
    }

    public static final float clamp(float f) {
        return f < 0f ? 0f : Math.min(f, 1f);
    }

    public static final float frand() {
        return (float) Math.random();
    }

    public static final float frand(float a, float b) {
        return lerp(frand(), a, b);
    }

    public static final int irand(int a, int b) {
        return (int) lerp(frand(), a, b);
    }

    private void step(long tMs, long dtMs) {
        t = tMs / 1000f; // seconds
        dt = dtMs / 1000f;

        if (DEBUG) {
            t *= DEBUG_SPEED_MULTIPLIER;
            dt *= DEBUG_SPEED_MULTIPLIER;
        }

        // 1. Move all objects and update bounds
        final int N = getChildCount();
        int i = 0;
        for (; i < N; i++) {
            final View v = getChildAt(i);
            if (v instanceof GameView) {
                ((GameView) v).step(tMs, dtMs, t, dt);
            }
        }

        // 2. Check for altitude
        if (mPlaying && mDroid.below(mHeight)) {
            if (DEBUG_IDDQD) {
                poke();
            } else {
                l("player hit the floor");
                stop();
            }
        }

        // 3. Check for obstacles
        boolean passedBarrier = false;
        for (int j = mObstaclesInPlay.size(); j-- > 0; ) {
            final Obstacle ob = mObstaclesInPlay.get(j);
            if (mPlaying && ob.intersects(mDroid) && !DEBUG_IDDQD) {
                l("player hit an obstacle");
                stop();
            } else if (ob.cleared(mDroid)) {
                if (ob instanceof Stem) passedBarrier = true;
                mObstaclesInPlay.remove(j);
            }
        }

        if (mPlaying && passedBarrier) {
            addScore(1);
        }

        // 4. Handle edge of screen
        // Walk backwards to make sure removal is safe
        while (i-- > 0) {
            final View v = getChildAt(i);
            if (v instanceof Obstacle) {
                if (v.getTranslationX() + v.getWidth() < 0) {
                    removeViewAt(i);
                }
            } else if (v instanceof Scenery) {
                final Scenery s = (Scenery) v;
                if (v.getTranslationX() + s.w < 0) {
                    v.setTranslationX(getWidth());
                }
            }
        }

        // 3. Time for more obstacles!
        if (mPlaying && (t - mLastPipeTime) > params.obstaclePeriod) {
            mLastPipeTime = t;
            final int obstacley = (random.nextInt((mHeight - 2 * params.obstacleMin -
                    params.obstacleGap))) + params.obstacleMin;

            final int inset = (params.obstacleWidth - params.obstacleStemWidth) / 2;
            final int yinset = params.obstacleWidth / 2;

            final int d1 = irand(0, 250);
            final Obstacle s1 = new Stem(getContext(), (float) obstacley - yinset, false);
            addView(s1, new FrameLayout.LayoutParams(
                    params.obstacleStemWidth,
                    (int) s1.h,
                    Gravity.TOP | Gravity.LEFT));
            s1.setTranslationX((float) mWidth + inset);
            s1.setTranslationY(-s1.h - yinset);
            s1.setTranslationZ(params.obstacleZ * 0.75f);
            s1.animate()
                    .translationY(0)
                    .setStartDelay(d1)
                    .setDuration(250);
            mObstaclesInPlay.add(s1);

            final Obstacle p1 = new Pop(getContext(), params.obstacleWidth);
            addView(p1, new FrameLayout.LayoutParams(
                    params.obstacleWidth,
                    params.obstacleWidth,
                    Gravity.TOP | Gravity.LEFT));
            p1.setTranslationX(mWidth);
            p1.setTranslationY(-params.obstacleWidth);
            p1.setTranslationZ(params.obstacleZ);
            p1.setScaleX(0.25f);
            p1.setScaleY(0.25f);
            p1.animate()
                    .translationY(s1.h - inset)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(d1)
                    .setDuration(250);
            mObstaclesInPlay.add(p1);

            final int d2 = irand(0, 250);
            final Obstacle s2 = new Stem(getContext(),
                    (float) mHeight - obstacley - params.obstacleGap - yinset,
                    true);
            addView(s2, new FrameLayout.LayoutParams(
                    params.obstacleStemWidth,
                    (int) s2.h,
                    Gravity.TOP | Gravity.LEFT));
            s2.setTranslationX((float) mWidth + inset);
            s2.setTranslationY((float) mHeight + yinset);
            s2.setTranslationZ(params.obstacleZ * 0.75f);
            s2.animate()
                    .translationY(mHeight - s2.h)
                    .setStartDelay(d2)
                    .setDuration(400);
            mObstaclesInPlay.add(s2);

            final Obstacle p2 = new Pop(getContext(), params.obstacleWidth);
            addView(p2, new FrameLayout.LayoutParams(
                    params.obstacleWidth,
                    params.obstacleWidth,
                    Gravity.TOP | Gravity.LEFT));
            p2.setTranslationX(mWidth);
            p2.setTranslationY(mHeight);
            p2.setTranslationZ(params.obstacleZ);
            p2.setScaleX(0.25f);
            p2.setScaleY(0.25f);
            p2.animate()
                    .translationY(mHeight - s2.h - yinset)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(d2)
                    .setDuration(400);
            mObstaclesInPlay.add(p2);
        }

        if (DEBUG_DRAW) invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (DEBUG) l("touch: %s", ev);
        return processPokeEvents(ev);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (DEBUG) l("trackball: %s", ev);
        return processPokeEvents(ev);
    }

    private boolean processPokeEvents(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            poke();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            unpoke();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent ev) {
        if (DEBUG) l("keyDown: %d", keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                poke();
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent ev) {
        if (DEBUG) l("keyDown: %d", keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                unpoke();
                return true;
        }
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent ev) {
        if (DEBUG) l("generic: %s", ev);
        return false;
    }

    private void poke() {
        l("poke");
        if (mFrozen) return;
        if (!mAnimating) {
            reset();
            start(true);
        } else if (!mPlaying) {
            start(true);
        }
        mDroid.boost();
        if (DEBUG) {
            mDroid.dv *= DEBUG_SPEED_MULTIPLIER;
            mDroid.animate().setDuration((long) (200 / DEBUG_SPEED_MULTIPLIER));
        }
    }

    private void unpoke() {
        l("unboost");
        if (mFrozen) return;
        if (!mAnimating) return;
        mDroid.unboost();
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        if (!DEBUG_DRAW) return;

        final Paint pt = new Paint();
        pt.setColor(0xFFFFFFFF);
        final int L = mDroid.corners.length;
        final int N = L / 2;
        for (int i = 0; i < N; i++) {
            final int x = (int) mDroid.corners[i * 2];
            final int y = (int) mDroid.corners[i * 2 + 1];
            c.drawCircle(x, y, 4, pt);
            c.drawLine(x, y,
                    mDroid.corners[(i * 2 + 2) % L],
                    mDroid.corners[(i * 2 + 3) % L],
                    pt);
        }

        pt.setStyle(Paint.Style.STROKE);
        pt.setStrokeWidth(getResources().getDisplayMetrics().density);

        final int M = getChildCount();
        pt.setColor(0x8000FF00);
        for (int i = 0; i < M; i++) {
            final View v = getChildAt(i);
            if (v == mDroid || !(v instanceof GameView)) continue;
            if (v instanceof Pop) {
                final Pop p = (Pop) v;
                c.drawCircle(p.cx, p.cy, p.r, pt);
            } else {
                final Rect r = new Rect();
                v.getHitRect(r);
                c.drawRect(r, pt);
            }
        }

        pt.setColor(Color.BLACK);
        final StringBuilder sb = new StringBuilder("obstacles: ");
        for (Obstacle ob : mObstaclesInPlay) {
            sb.append(ob.hitRect.toShortString());
            sb.append(" ");
        }
        pt.setTextSize(20f);
        c.drawText(sb.toString(), 20, 100, pt);
    }

    static final Rect sTmpRect = new Rect();

    private interface GameView {
        public void step(long tMs, long dtMs, float t, float dt);
    }

    private class Player extends ImageView implements GameView {
        protected float dv;

        private boolean mBoosting;

        private final float[] sHull = new float[]{
                0.3f, 0f,    // left antenna
                0.7f, 0f,    // right antenna
                0.92f, 0.33f, // off the right shoulder of Orion
                0.92f, 0.75f, // right hand (our right, not his right)
                0.6f, 1f,    // right foot
                0.4f, 1f,    // left foot BLUE!
                0.08f, 0.75f, // sinistram
                0.08f, 0.33f,  // cold shoulder
        };
        public final float[] corners = new float[sHull.length];

        public Player(Context context) {
            super(context);

            setBackgroundResource(R.drawable.lollipop_android);
            getBackground().setTintMode(PorterDuff.Mode.SRC_ATOP);
            getBackground().setTint(0xFF00FF00);
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    final int w = view.getWidth();
                    final int h = view.getHeight();
                    final int ix = (int) (w * 0.3f);
                    final int iy = (int) (h * 0.2f);
                    outline.setRect(ix, iy, w - ix, h - iy);
                }
            });
        }

        public void prepareCheckIntersections() {
            final int inset = (params.playerSize - params.playerHitSize) / 2;
            final int scale = params.playerHitSize;
            final int N = sHull.length / 2;
            for (int i = 0; i < N; i++) {
                corners[i * 2] = scale * sHull[i * 2] + inset;
                corners[i * 2 + 1] = scale * sHull[i * 2 + 1] + inset;
            }
            final Matrix m = getMatrix();
            m.mapPoints(corners);
        }

        public boolean below(int h) {
            final int N = corners.length / 2;
            for (int i = 0; i < N; i++) {
                final int y = (int) corners[i * 2 + 1];
                if (y >= h) return true;
            }
            return false;
        }

        public void step(long tMs, long dtMs, float t, float dt) {
            if (getVisibility() != View.VISIBLE) return; // not playing yet

            if (mBoosting) {
                dv = -params.boostDv;
            } else {
                dv += params.g;
            }
            if (dv < -params.maxV) dv = -params.maxV;
            else if (dv > params.maxV) dv = params.maxV;

            final float y = getTranslationY() + dv * dt;
            setTranslationY(y < 0 ? 0 : y);
            setRotation(
                    90 + lerp(clamp(rlerp(dv, params.maxV, -1 * (float) params.maxV)), 90, -90));

            prepareCheckIntersections();
        }

        public void boost() {
            mBoosting = true;
            dv = -params.boostDv;

            animate().cancel();
            animate()
                    .scaleX(1.25f)
                    .scaleY(1.25f)
                    .translationZ(params.playerZBoost)
                    .setDuration(100);
            setScaleX(1.25f);
            setScaleY(1.25f);
        }

        public void unboost() {
            mBoosting = false;

            animate().cancel();
            animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationZ(params.playerZ)
                    .setDuration(200);
        }
    }

    private class Obstacle extends View implements GameView {
        protected float h;

        public final Rect hitRect = new Rect();

        public Obstacle(Context context, float h) {
            super(context);
            setBackgroundColor(0xFFFF0000);
            this.h = h;
        }

        public boolean intersects(Player p) {
            final int N = p.corners.length / 2;
            for (int i = 0; i < N; i++) {
                final int x = (int) p.corners[i * 2];
                final int y = (int) p.corners[i * 2 + 1];
                if (hitRect.contains(x, y)) return true;
            }
            return false;
        }

        public boolean cleared(Player p) {
            final int N = p.corners.length / 2;
            for (int i = 0; i < N; i++) {
                final int x = (int) p.corners[i * 2];
                if (hitRect.right >= x) return false;
            }
            return true;
        }

        @Override
        public void step(long tMs, long dtMs, float t, float dt) {
            setTranslationX(getTranslationX() - params.translationPerSec * dt);
            getHitRect(hitRect);
        }
    }

    private class Pop extends Obstacle {
        int mRotate;
        int cx;
        int cy;
        int r;

        public Pop(Context context, float h) {
            super(context, h);
            int idx = 2 * irand(0, POPS.length / 2);
            setBackgroundResource(POPS[idx]);
            setScaleX(frand() < 0.5f ? -1 : 1);
            mRotate = POPS[idx + 1] == 0 ? 0 : (frand() < 0.5f ? -1 : 1);
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    final int pad = (int) (getWidth() * 0.02f);
                    outline.setOval(pad, pad, getWidth() - pad, getHeight() - pad);
                }
            });
        }

        @Override
        public boolean intersects(Player p) {
            final int N = p.corners.length / 2;
            for (int i = 0; i < N; i++) {
                final int x = (int) p.corners[i * 2];
                final int y = (int) p.corners[i * 2 + 1];
                if (Math.hypot((double) x - cx, (double) y - cy) <= r) return true;
            }
            return false;
        }

        @Override
        public void step(long tMs, long dtMs, float t, float dt) {
            super.step(tMs, dtMs, t, dt);
            if (mRotate != 0) {
                setRotation(getRotation() + dt * 45 * mRotate);
            }

            cx = (hitRect.left + hitRect.right) / 2;
            cy = (hitRect.top + hitRect.bottom) / 2;
            r = getWidth() / 2;
        }
    }

    private class Stem extends Obstacle {
        Paint mPaint = new Paint();
        Path mShadow = new Path();
        boolean mDrawShadow;

        public Stem(Context context, float h, boolean drawShadow) {
            super(context, h);
            mDrawShadow = drawShadow;
            mPaint.setColor(0xFFAAAAAA);
            setBackground(null);
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            setWillNotDraw(false);
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRect(0, 0, getWidth(), getHeight());
                }
            });
        }

        @Override
        public void onDraw(Canvas c) {
            final int w = c.getWidth();
            final int h = c.getHeight();
            final GradientDrawable g = new GradientDrawable();
            g.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            g.setGradientCenter(w * 0.75f, 0);
            g.setColors(new int[]{0xFFFFFFFF, 0xFFAAAAAA});
            g.setBounds(0, 0, w, h);
            g.draw(c);
            if (!mDrawShadow) return;
            mShadow.reset();
            mShadow.moveTo(0, 0);
            mShadow.lineTo(w, 0);
            mShadow.lineTo(w, (float) params.obstacleWidth / 2 + w * 1.5f);
            mShadow.lineTo(0, (float) params.obstacleWidth / 2);
            mShadow.close();
            c.drawPath(mShadow, mPaint);
        }
    }

    private static class Scenery extends FrameLayout implements GameView {
        protected float z;
        protected float v;
        protected int h;
        protected int w;

        public Scenery(Context context) {
            super(context);
        }

        @Override
        public void step(long tMs, long dtMs, float t, float dt) {
            setTranslationX(getTranslationX() - params.translationPerSec * dt * v);
        }
    }

    private class Building extends Scenery {
        public Building(Context context) {
            super(context);

            w = irand(params.buildingWidthMin, params.buildingWidthMax);
            h = 0; // will be setup later, along with z

            setTranslationZ(params.sceneryZ);
        }
    }

    private class Cloud extends Scenery {
        public Cloud(Context context) {
            super(context);
            setBackgroundResource(frand() < 0.01f ? R.drawable.lollipop_cloud_off : R.drawable.lollipop_cloud);
            getBackground().setAlpha(0x40);
            w = h = irand(params.cloudSizeMin, params.cloudSizeMax);
            z = 0;
            v = frand(0.15f, 0.5f);
        }
    }

    private class Star extends Scenery {
        public Star(Context context) {
            super(context);
            setBackgroundResource(R.drawable.lollipop_star);
            w = h = irand(params.starSizeMin, params.starSizeMax);
            v = z = 0;
        }
    }
}
