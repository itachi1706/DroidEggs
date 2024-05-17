package com.itachi1706.droideggs.tiramisu_egg;

import static android.graphics.PixelFormat.TRANSLUCENT;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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

import androidx.preference.PreferenceManager;

import com.itachi1706.droideggs.PlatLogoCommon;
import com.itachi1706.droideggs.R;
import com.itachi1706.droideggs.forwardPortedCode.AnalogClock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@TargetApi(Build.VERSION_CODES.Q)
public class PlatLogoActivityTiramisu extends Activity {
    private static final String TAG = "PlatLogoActivityT";

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
        mLogo.setImageResource(R.drawable.tiramisu_platlogo);
        layout.addView(mLogo, lp);

        mBg = new BubblesDrawable();
        mBg.setLevel(0);
        mBg.avoid = widgetSize / 2;
        mBg.padding = 0.5f * dp;
        mBg.minR = 1 * dp;
        layout.setBackground(mBg);
        layout.setOnLongClickListener(mBg);

        setContentView(layout);
    }

    private void launchNextStage(boolean locked) {
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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        PlatLogoCommon.syncTouchPressure(TOUCH_STATS, this);
        // For posterity: the moment this user unlocked the easter egg
        pref.edit().putLong("T_EGG_MODE", System.currentTimeMillis()).apply();

        // Lets not activate the easter egg again

        // No more activation

        //finish(); // no longer finish upon unlock; it's fun to frob the dial
    }

    static final String TOUCH_STATS = "t.touch.stats";
    double mPressureMin = 0, mPressureMax = -1;

    @Override
    public void onStart() {
        super.onStart();
        PlatLogoCommon.syncTouchPressure(TOUCH_STATS, this);
    }

    @Override
    public void onStop() {
        PlatLogoCommon.syncTouchPressure(TOUCH_STATS, this);
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
                    if (mOverrideMinute == 0 && (mOverrideHour % 12) == 1) {
                        Log.v(TAG, "13:00");
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        launchNextStage(false);
                    }
                    return true;
            }
            return false;
        }
    }

    private static final String[][] EMOJI_SETS = {
            {"🍇", "🍈", "🍉", "🍊", "🍋", "🍌", "🍍", "🥭", "🍎", "🍏", "🍐", "🍑",
                    "🍒", "🍓", "🫐", "🥝"},
            {"😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾"},
            {"😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃", "🫠", "😉", "😊",
                    "😇", "🥰", "😍", "🤩", "😘", "😗", "☺️", "😚", "😙", "🥲", "😋", "😛", "😜",
                    "🤪", "😝", "🤑", "🤗", "🤭", "🫢", "🫣", "🤫", "🤔", "🫡", "🤐", "🤨", "😐",
                    "😑", "😶", "🫥", "😏", "😒", "🙄", "😬", "🤥", "😌", "😔", "😪", "🤤", "😴",
                    "😷"},
            { "🤩", "😍", "🥰", "😘", "🥳", "🥲", "🥹" },
            { "🫠" },
            {"💘", "💝", "💖", "💗", "💓", "💞", "💕", "❣", "💔", "❤", "🧡", "💛",
                    "💚", "💙", "💜", "🤎", "🖤", "🤍"},
            // {"👁", "️🫦", "👁️"}, // this one is too much
            {"👽", "🛸", "✨", "🌟", "💫", "🚀", "🪐", "🌙", "⭐", "🌍"},
            {"🌑", "🌒", "🌓", "🌔", "🌕", "🌖", "🌗", "🌘"},
            {"🐙", "🪸", "🦑", "🦀", "🦐", "🐡", "🦞", "🐠", "🐟", "🐳", "🐋", "🐬", "🫧", "🌊",
                    "🦈"},
            {"🙈", "🙉", "🙊", "🐵", "🐒"},
            {"♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓"},
            {"🕛", "🕧", "🕐", "🕜", "🕑", "🕝", "🕒", "🕞", "🕓", "🕟", "🕔", "🕠", "🕕", "🕡",
                    "🕖", "🕢", "🕗", "🕣", "🕘", "🕤", "🕙", "🕥", "🕚", "🕦"},
            {"🌺", "🌸", "💮", "🏵️", "🌼", "🌿"},
            {"🐢", "✨", "🌟", "👑"}
    };

    static class Bubble {
        public float x, y, r;
        public int color;
        public String text = null;
    }

    class BubblesDrawable extends Drawable implements View.OnLongClickListener {
        private static final int MAX_BUBBS = 2000;

        private final int[] mColorIds = {
                R.color.system_accent3_400,
                R.color.system_accent3_500,
                R.color.system_accent3_600,

                R.color.system_accent2_400,
                R.color.system_accent2_500,
                R.color.system_accent2_600,
        };

        private int[] mColors = new int[mColorIds.length];

        private int mEmojiSet = -1;

        private final Bubble[] mBubbs = new Bubble[MAX_BUBBS];
        private int mNumBubbs;

        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public float avoid = 0f;
        public float padding = 0f;
        public float minR = 0f;

        BubblesDrawable() {
            for (int i = 0; i < mColorIds.length; i++) {
                mColors[i] = getColor(mColorIds[i]);
            }
            for (int j = 0; j < mBubbs.length; j++) {
                mBubbs[j] = new Bubble();
            }
        }

        @Override
        public void draw(Canvas canvas) {
            if (getLevel() == 0) return;
            final float f = getLevel() / 10000f;
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextAlign(Paint.Align.CENTER);
            int drawn = 0;
            for (int j = 0; j < mNumBubbs; j++) {
                if (mBubbs[j].color == 0 || mBubbs[j].r == 0) continue;
                if (mBubbs[j].text != null) {
                    mPaint.setTextSize(mBubbs[j].r * 1.75f);
                    canvas.drawText(mBubbs[j].text, mBubbs[j].x,
                            mBubbs[j].y  + mBubbs[j].r * f * 0.6f, mPaint);
                } else {
                    mPaint.setColor(mBubbs[j].color);
                    canvas.drawCircle(mBubbs[j].x, mBubbs[j].y, mBubbs[j].r * f, mPaint);
                }
                drawn++;
            }
        }

        public void chooseEmojiSet() {
            mEmojiSet = (int) (Math.random() * EMOJI_SETS.length);
            final String[] emojiSet = EMOJI_SETS[mEmojiSet];
            for (int j = 0; j < mBubbs.length; j++) {
                mBubbs[j].text = emojiSet[(int) (Math.random() * emojiSet.length)];
            }
            invalidateSelf();
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
        public void setAlpha(int alpha) { }

        @Override
        public void setColorFilter(ColorFilter colorFilter) { }

        @Override
        public int getOpacity() {
            return TRANSLUCENT;
        }

        @Override
        public boolean onLongClick(View v) {
            if (getLevel() == 0) return false;
            chooseEmojiSet();
            return true;
        }
    }
}
