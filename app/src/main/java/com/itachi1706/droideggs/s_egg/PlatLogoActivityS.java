package com.itachi1706.droideggs.s_egg;

import static android.graphics.PixelFormat.TRANSLUCENT;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.itachi1706.droideggs.PlatLogoCommon;
import com.itachi1706.droideggs.R;
import com.itachi1706.droideggs.forwardPortedCode.AnalogClock;
import com.itachi1706.droideggs.s_egg.easter_egg.widget.WidgetActivationActivity;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@TargetApi(29)
public class PlatLogoActivityS extends Activity {
    private static final String TAG = "PlatLogoActivity";

    private static final String S_EGG_UNLOCK_SETTING = "egg_mode_s";

    private SettableAnalogClock mClock;
    private ImageView mLogo;
    private BubblesDrawable mBg;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setNavigationBarColor(0);
        getWindow().setStatusBarColor(0);

        final ActionBar ab = getActionBar();
        if (ab != null) ab.hide();

        final FrameLayout layout = new FrameLayout(this);

        mClock = new SettableAnalogClock(this);

        final DisplayMetrics dm = getResources().getDisplayMetrics();
        final float dp = dm.density;
        final int minSide = Math.min(dm.widthPixels, dm.heightPixels);
        final int widgetSize = (int) (minSide * 0.75);
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(widgetSize, widgetSize);
        lp.gravity = Gravity.CENTER;
        layout.addView(mClock, lp);

        mLogo = new ImageView(this);
        mLogo.setVisibility(View.GONE);
        mLogo.setImageResource(R.drawable.s_platlogo);
        layout.addView(mLogo, lp);

        mBg = new BubblesDrawable();
        mBg.setLevel(0);
        mBg.avoid = widgetSize / 2f;
        mBg.padding = 0.5f * dp;
        mBg.minR = 1 * dp;
        layout.setBackground(mBg);

        setContentView(layout);
    }

    private void launchNextStage(boolean locked) {
        SharedPreferences pref = this.getSharedPreferences("com.itachi1706.droideggs_preferences", MODE_MULTI_PROCESS);
        mClock.animate()
                .alpha(0f).scaleX(0.5f).scaleY(0.5f)
                .withEndAction(() -> mClock.setVisibility(View.GONE))
                .start();

        mLogo.setAlpha(0f);
        mLogo.setScaleX(0.5f);
        mLogo.setScaleY(0.5f);
        mLogo.setVisibility(View.VISIBLE);
        mLogo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new OvershootInterpolator())
                .start();

        mLogo.postDelayed(() -> {
                    final ObjectAnimator anim = ObjectAnimator.ofInt(mBg, "level", 0, 10000);
                    anim.setInterpolator(new DecelerateInterpolator(1f));
                    anim.start();
                },
                500
        );


        try {
            Log.v(TAG, "Saving egg unlock=" + locked);
            PlatLogoCommon.syncTouchPressure(TOUCH_STATS, getApplicationContext());
            pref.edit().putLong(S_EGG_UNLOCK_SETTING, locked ? 0 : System.currentTimeMillis()).apply();
        } catch (RuntimeException e) {
            Log.e(TAG, "Can't write settings", e);
        }

        try {
            startActivity(new Intent(this, WidgetActivationActivity.class));
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Log.e(TAG, "No more eggs.");
        }
        //finish(); // no longer finish upon unlock; it's fun to frob the dial
    }

    static final String TOUCH_STATS = "s_touch.stats";

    @Override
    public void onStart() {
        super.onStart();
        PlatLogoCommon.syncTouchPressure(TOUCH_STATS, getApplicationContext());
    }

    @Override
    public void onStop() {
        PlatLogoCommon.syncTouchPressure(TOUCH_STATS, getApplicationContext());
        super.onStop();
    }

    /**
     * Subclass of AnalogClock that allows the user to flip up the glass and adjust the hands.
     */
    public class SettableAnalogClock extends AnalogClock {
        private int mOverrideHour = -1;
        private int mOverrideMinute = -1;
        private boolean mOverride = false;

        public SettableAnalogClock(Context context) {
            super(context);
        }

        @Override
        protected Instant now() {
            final Instant realNow = super.now();
            final ZoneId tz = Clock.systemDefaultZone().getZone();
            final ZonedDateTime zdTime = realNow.atZone(tz);
            if (mOverride) {
                if (mOverrideHour < 0) {
                    mOverrideHour = zdTime.getHour();
                }
                return Clock.fixed(zdTime
                        .withHour(mOverrideHour)
                        .withMinute(mOverrideMinute)
                        .withSecond(0)
                        .toInstant(), tz).instant();
            } else {
                return realNow;
            }
        }

        double toPositiveDegrees(double rad) {
            return (Math.toDegrees(rad) + 360 - 90) % 360;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mOverride = true;
                    // pass through
                case MotionEvent.ACTION_MOVE:
                    PlatLogoCommon.measureTouchPressure(ev);

                    float x = ev.getX();
                    float y = ev.getY();
                    float cx = getWidth() / 2f;
                    float cy = getHeight() / 2f;
                    float angle = (float) toPositiveDegrees(Math.atan2(x - cx, y - cy));

                    int minutes = (75 - (int) (angle / 6)) % 60;
                    int minuteDelta = minutes - mOverrideMinute;
                    if (minuteDelta != 0) {
                        if (Math.abs(minuteDelta) > 45 && mOverrideHour >= 0) {
                            int hourDelta = (minuteDelta < 0) ? 1 : -1;
                            mOverrideHour = (mOverrideHour + 24 + hourDelta) % 24;
                        }
                        mOverrideMinute = minutes;
                        if (mOverrideMinute == 0) {
                            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                            if (getScaleX() == 1f) {
                                setScaleX(1.05f);
                                setScaleY(1.05f);
                                animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                            }
                        } else {
                            performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
                        }

                        onTimeChanged();
                        postInvalidate();
                    }

                    return true;
                case MotionEvent.ACTION_UP:
                    if (mOverrideMinute == 0 && (mOverrideHour % 12) == 0) {
                        Log.v(TAG, "12:00 let's gooooo");
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        launchNextStage(false);
                    }
                    return true;
            }
            return false;
        }
    }

    static class Bubble {
        public float x, y, r;
        public int color;
    }

    class BubblesDrawable extends Drawable {
        private static final int MAX_BUBBS = 2000;

        @SuppressLint("InlinedApi")
        private final int[] mColorIds = {
                android.R.color.system_accent1_400,
                android.R.color.system_accent1_500,
                android.R.color.system_accent1_600,

                android.R.color.system_accent2_400,
                android.R.color.system_accent2_500,
                android.R.color.system_accent2_600,
        };

        private int[] mColors = new int[mColorIds.length];

        private final Bubble[] mBubbs = new Bubble[MAX_BUBBS];
        private int mNumBubbs;

        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public float avoid = 0f;
        public float padding = 0f;
        public float minR = 0f;

        BubblesDrawable() {
            int[] mColorIdsCompat = {
                R.color.system_accent1_400,
                R.color.system_accent1_500,
                R.color.system_accent1_600,

                R.color.system_accent2_400,
                R.color.system_accent2_500,
                R.color.system_accent2_600,
            };

            for (int i = 0; i < mColorIds.length; i++) {
                mColors[i] = getColor((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ? mColorIds[i] : mColorIdsCompat[i]);
            }
            for (int j = 0; j < mBubbs.length; j++) {
                mBubbs[j] = new Bubble();
            }
        }

        @Override
        public void draw(Canvas canvas) {
            final float f = getLevel() / 10000f;
            mPaint.setStyle(Paint.Style.FILL);
            int drawn = 0;
            for (int j = 0; j < mNumBubbs; j++) {
                if (mBubbs[j].color == 0 || mBubbs[j].r == 0) continue;
                mPaint.setColor(mBubbs[j].color);
                canvas.drawCircle(mBubbs[j].x, mBubbs[j].y, mBubbs[j].r * f, mPaint);
                drawn++;
            }
        }

        @Override
        protected boolean onLevelChange(int level) {
            invalidateSelf();
            return true;
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            randomize();
        }

        private void randomize() {
            final float w = getBounds().width();
            final float h = getBounds().height();
            final float maxR = Math.min(w, h) / 3f;
            mNumBubbs = 0;
            if (avoid > 0f) {
                mBubbs[mNumBubbs].x = w / 2f;
                mBubbs[mNumBubbs].y = h / 2f;
                mBubbs[mNumBubbs].r = avoid;
                mBubbs[mNumBubbs].color = 0;
                mNumBubbs++;
            }
            for (int j = 0; j < MAX_BUBBS; j++) {
                // a simple but time-tested bubble-packing algorithm:
                // 1. pick a spot
                // 2. shrink the bubble until it is no longer overlapping any other bubble
                // 3. if the bubble hasn't popped, keep it
                int tries = 5;
                while (tries-- > 0) {
                    float x = (float) Math.random() * w;
                    float y = (float) Math.random() * h;
                    float r = Math.min(Math.min(x, w - x), Math.min(y, h - y));

                    // shrink radius to fit other bubbs
                    for (int i = 0; i < mNumBubbs; i++) {
                        r = (float) Math.min(r,
                                Math.hypot(x - mBubbs[i].x, y - mBubbs[i].y) - mBubbs[i].r
                                        - padding);
                        if (r < minR) break;
                    }

                    if (r >= minR) {
                        // we have found a spot for this bubble to live, let's save it and move on
                        r = Math.min(maxR, r);

                        mBubbs[mNumBubbs].x = x;
                        mBubbs[mNumBubbs].y = y;
                        mBubbs[mNumBubbs].r = r;
                        mBubbs[mNumBubbs].color = mColors[(int) (Math.random() * mColors.length)];
                        mNumBubbs++;
                        break;
                    }
                }
            }
            Log.v(TAG, String.format("successfully placed %d bubbles (%d%%)",
                    mNumBubbs, (int) (100f * mNumBubbs / MAX_BUBBS)));
        }

        @Override
        public void setAlpha(int alpha) {
            // Set to no-op to prevent unintended use
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            // Set to no-op to prevent unintended use
        }

        @Override
        public int getOpacity() {
            return TRANSLUCENT;
        }
    }
}
