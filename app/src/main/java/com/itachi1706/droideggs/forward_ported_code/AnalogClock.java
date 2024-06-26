/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.itachi1706.droideggs.forward_ported_code;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;

import com.itachi1706.droideggs.R;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Formatter;
import java.util.Locale;

/**
 * This widget display an analogic clock with two hands for hours and
 * minutes.
 *
 * This widget has been deprecated on Android and used only for Android S's egg
 */
@RemoteView
@RequiresApi(api = Build.VERSION_CODES.Q)
public class AnalogClock extends View {
    /** How many times per second that the seconds hand advances. */
    private final int mSecondsHandFps;

    private Clock mClock;
    @Nullable
    private final ZoneId mTimeZone;

    private Drawable mHourHand;
    private Drawable mMinuteHand;
    @Nullable
    private Drawable mSecondHand;
    private Drawable mDial;

    private final int mDialWidth;
    private final int mDialHeight;

    private boolean mVisible;

    private float mSeconds;
    private float mMinutes;
    private float mHour;
    private boolean mChanged;

    private final Context mContext;

    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        mSecondsHandFps = 1; // Default Value

        mDial = AppCompatResources.getDrawable(context, R.drawable.clock_dial);

        TintInfo mDialTintInfo = new TintInfo();
        if (mDialTintInfo.mHasTintList || mDialTintInfo.mHasTintBlendMode) {
            mDial = mDialTintInfo.apply(mDial);
        }

        mHourHand = AppCompatResources.getDrawable(context, R.drawable.clock_hand_hour);

        TintInfo mHourHandTintInfo = new TintInfo();
        if (mHourHandTintInfo.mHasTintList || mHourHandTintInfo.mHasTintBlendMode) {
            mHourHand = mHourHandTintInfo.apply(mHourHand);
        }

        mMinuteHand = AppCompatResources.getDrawable(context, R.drawable.clock_hand_minute);

        TintInfo mMinuteHandTintInfo = new TintInfo();
        if (mMinuteHandTintInfo.mHasTintList || mMinuteHandTintInfo.mHasTintBlendMode) {
            mMinuteHand = mMinuteHandTintInfo.apply(mMinuteHand);
        }

        TintInfo mSecondHandTintInfo = new TintInfo();
        if (mSecondHandTintInfo.mHasTintList || mSecondHandTintInfo.mHasTintBlendMode) {
            mSecondHand = null;
        }

        mTimeZone = null;
        createClock();

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }

    /**
     * Indicates which time zone is currently used by this view.
     *
     * @return The ID of the current time zone or null if the default time zone,
     *         as set by the user, must be used
     *
     * @see java.util.TimeZone
     * @see java.util.TimeZone#getAvailableIDs()
     */
    @Nullable
    public String getTimeZone() {
        ZoneId zoneId = mTimeZone;
        return zoneId == null ? null : zoneId.getId();
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);

        if (isVisible) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();

        if (!mReceiverAttached) {
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            // OK, this is gross but needed. This class is supported by the
            // remote views mechanism and as a part of that the remote views
            // can be inflated by a context for another user without the app
            // having interact users permission - just for loading resources.
            // For example, when adding widgets from a user profile to the
            // home screen. Therefore, we register the receiver as the current
            // user not the one the context is for.
            getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
            mReceiverAttached = true;
        }

        // NOTE: It's safe to do these after registering the receiver since the receiver always runs
        // in the main thread, therefore the receiver can't run before this method returns.

        // The time zone may have changed while the receiver wasn't registered, so update the clock.
        createClock();

        // Make sure we update to the current time
        onTimeChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mReceiverAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mReceiverAttached = false;
        }
        super.onDetachedFromWindow();
    }

    private void onVisible() {
        if (!mVisible) {
            mVisible = true;
            mTick.run();
        }

    }

    private void onInvisible() {
        if (mVisible) {
            removeCallbacks(mTick);
            mVisible = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize =  MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float )heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        final Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        boolean scaled = false;

        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w,
                    (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);

        canvas.save();
        canvas.rotate(mHour / 12.0f * 360.0f, x, y);
        final Drawable hourHand = mHourHand;
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

        final Drawable minuteHand = mMinuteHand;
        if (changed) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        minuteHand.draw(canvas);
        canvas.restore();

        final Drawable secondHand = mSecondHand;
        if (secondHand != null && mSecondsHandFps > 0) {
            canvas.save();
            canvas.rotate(mSeconds / 60.0f * 360.0f, x, y);

            if (changed) {
                w = secondHand.getIntrinsicWidth();
                h = secondHand.getIntrinsicHeight();
                secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
            }
            secondHand.draw(canvas);
            canvas.restore();
        }

        if (scaled) {
            canvas.restore();
        }
    }

    /**
     * Return the current Instant to be used for drawing the clockface. Protected to allow
     * subclasses to override this to show a different time from the system clock.
     *
     * @return the Instant to be shown on the clockface
     */
    protected Instant now() {
        return mClock.instant();
    }

    protected void onTimeChanged() {
        Instant now = now();
        onTimeChanged(now.atZone(mClock.getZone()).toLocalTime(), now.toEpochMilli());
    }

    private void onTimeChanged(LocalTime localTime, long nowMillis) {
        float previousHour = mHour;
        float previousMinutes = mMinutes;

        float rawSeconds = localTime.getSecond() + localTime.getNano() / 1_000_000_000f;
        // We round the fraction of the second so that the seconds hand always occupies the same
        // n positions between two given numbers, where n is the number of ticks per second. This
        // ensures the second hand advances by a consistent distance despite our handler callbacks
        // occurring at inconsistent frequencies.
        mSeconds =
                mSecondsHandFps <= 0
                        ? rawSeconds
                        : Math.round(rawSeconds * mSecondsHandFps) / (float) mSecondsHandFps;
        mMinutes = localTime.getMinute() + mSeconds / 60.0f;
        mHour = localTime.getHour() + mMinutes / 60.0f;
        mChanged = true;

        // Update the content description only if the announced hours and minutes have changed.
        if ((int) previousHour != (int) mHour || (int) previousMinutes != (int) mMinutes) {
            updateContentDescription(nowMillis);
        }
    }

    /** Intent receiver for the time or time zone changing. */
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                createClock();
            }

            mTick.run();
        }
    };
    private boolean mReceiverAttached;

    private final Runnable mTick = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(this);
            if (!mVisible) {
                return;
            }

            Instant now = now();
            ZonedDateTime zonedDateTime = now.atZone(mClock.getZone());
            LocalTime localTime = zonedDateTime.toLocalTime();

            long millisUntilNextTick;
            if (mSecondHand == null || mSecondsHandFps <= 0) {
                // If there's no second hand, then tick at the start of the next minute.
                //
                // This must be done with ZonedDateTime as opposed to LocalDateTime to ensure proper
                // handling of DST. Also note that because of leap seconds, it should not be assumed
                // that one minute == 60 seconds.
                Instant startOfNextMinute = zonedDateTime.plusMinutes(1).withSecond(0).toInstant();
                millisUntilNextTick = Duration.between(now, startOfNextMinute).toMillis();
                if (millisUntilNextTick <= 0) {
                    // This should never occur, but if it does, then just check the tick again in
                    // one minute to ensure we're always moving forward.
                    millisUntilNextTick = Duration.ofMinutes(1).toMillis();
                }
            } else {
                // If there is a seconds hand, then determine the next tick point based on the fps.
                //
                // How many milliseconds through the second we currently are.
                long millisOfSecond = Duration.ofNanos(localTime.getNano()).toMillis();
                // How many milliseconds there are between tick positions for the seconds hand.
                double millisPerTick = 1000 / (double) mSecondsHandFps;
                // How many milliseconds we are past the last tick position.
                long millisPastLastTick = Math.round(millisOfSecond % millisPerTick);
                // How many milliseconds there are until the next tick position.
                millisUntilNextTick = Math.round(millisPerTick - millisPastLastTick);
                // If we are exactly at the tick position, this could be 0 milliseconds due to
                // rounding. In this case, advance by the full amount of millis to the next
                // position.
                if (millisUntilNextTick <= 0) {
                    millisUntilNextTick = Math.round(millisPerTick);
                }
            }

            // Schedule a callback for when the next tick should occur.
            postDelayed(this, millisUntilNextTick);

            onTimeChanged(localTime, now.toEpochMilli());

            invalidate();
        }
    };

    private void createClock() {
        ZoneId zoneId = mTimeZone;
        if (zoneId == null) {
            mClock = Clock.systemDefaultZone();
        } else {
            mClock = Clock.system(zoneId);
        }
    }

    private void updateContentDescription(long timeMillis) {
        //noinspection deprecation
        final int flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
        String contentDescription =
                DateUtils.formatDateRange(
                                mContext,
                                new Formatter(new StringBuilder(50), Locale.getDefault()),
                                timeMillis /* startMillis */,
                                timeMillis /* endMillis */,
                                flags,
                                getTimeZone())
                        .toString();
        setContentDescription(contentDescription);
    }

    private final class TintInfo {
        boolean mHasTintList;
        @Nullable ColorStateList mTintList;
        boolean mHasTintBlendMode;
        @Nullable BlendMode mTintBlendMode;

        /**
         * Returns a mutated copy of {@code drawable} with tinting applied, or null if it's null.
         */
        @Nullable
        Drawable apply(@Nullable Drawable drawable) {
            if (drawable == null) return null;

            Drawable newDrawable = drawable.mutate();

            if (mHasTintList) {
                newDrawable.setTintList(mTintList);
            }

            if (mHasTintBlendMode) {
                newDrawable.setTintBlendMode(mTintBlendMode);
            }

            // All drawables should have the same state as the View itself.
            if (drawable.isStateful()) {
                newDrawable.setState(getDrawableState());
            }

            return newDrawable;
        }
    }
}
