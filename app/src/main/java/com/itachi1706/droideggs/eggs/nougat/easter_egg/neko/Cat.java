/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.itachi1706.droideggs.eggs.nougat.easter_egg.neko;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;

import com.itachi1706.droideggs.FirebaseLogger;
import com.itachi1706.droideggs.R;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Kenneth on 8/9/2016.
 * for com.itachi1706.droideggs.NougatEgg.EasterEgg.neko in DroidEggs
 */
@TargetApi(Build.VERSION_CODES.M)
public class Cat extends Drawable {
    protected static final long[] PURR = {0, 40, 20, 40, 20, 40, 20, 40, 20, 40, 20, 40};

    private Random mNotSoRandom;
    private Bitmap mBitmap;
    private final long mSeed;
    private String mName;
    private int mBodyColor;
    private int mFootType;
    private final boolean mBowTie;

    private synchronized Random notSoRandom(long seed) {
        if (mNotSoRandom == null) {
            mNotSoRandom = new Random();
            mNotSoRandom.setSeed(seed);
        }
        return mNotSoRandom;
    }

    public static float frandrange(Random r, float a, float b) {
        return (b-a)*r.nextFloat() + a;
    }

    public static Object choose(Random r, Object... l) {
        return l[r.nextInt(l.length)];
    }

    public static int chooseP(Random r, int[] a) {
        int pct = r.nextInt(1000);
        final int stop = a.length-2;
        int i=0;
        while (i<stop) {
            pct -= a[i];
            if (pct < 0) break;
            i+=2;
        }
        return a[i+1];
    }

    public static int getColorIndex(int q, int[] a) {
        for(int i = 1; i < a.length; i+=2) {
            if (a[i] == q) {
                return i/2;
            }
        }
        return -1;
    }

    protected static final int[] P_BODY_COLORS = {
            180, 0xFF212121, // black
            180, 0xFFFFFFFF, // white
            140, 0xFF616161, // gray
            140, 0xFF795548, // brown
            100, 0xFF90A4AE, // steel
            100, 0xFFFFF9C4, // buff
            100, 0xFFFF8F00, // orange
            5, 0xFF29B6F6, // blue..?
            5, 0xFFFFCDD2, // pink!?
            5, 0xFFCE93D8, // purple?!?!?
            4, 0xFF43A047, // yeah, why not green
            1, 0,          // ?!?!?!
    };

    protected static final int[] P_COLLAR_COLORS = {
            250, 0xFFFFFFFF,
            250, 0xFF000000,
            250, 0xFFF44336,
            50, 0xFF1976D2,
            50, 0xFFFDD835,
            50, 0xFFFB8C00,
            50, 0xFFF48FB1,
            50, 0xFF4CAF50,
    };

    protected static final int[] P_BELLY_COLORS = {
            750, 0,
            250, 0xFFFFFFFF,
    };

    protected static final int[] P_DARK_SPOT_COLORS = {
            700, 0,
            250, 0xFF212121,
            50, 0xFF6D4C41,
    };

    protected static final int[] P_LIGHT_SPOT_COLORS = {
            700, 0,
            300, 0xFFFFFFFF,
    };

    private final CatParts d;

    public static void tint(int color, Drawable ... ds) {
        for (Drawable d : ds) {
            if (d != null) {
                d.mutate().setTint(color);
            }
        }
    }

    public static boolean isDark(int color) {
        final int r = (color & 0xFF0000) >> 16;
        final int g = (color & 0x00FF00) >> 8;
        final int b = color & 0x0000FF;
        return (r + g + b) < 0x80;
    }

    public Cat(Context context, long seed) {
        d = new CatParts(context);
        mSeed = seed;

        setName(context.getString(R.string.nougat_default_cat_name,
                String.valueOf(mSeed % 1000)));

        final Random nsr = notSoRandom(seed);

        // body color
        mBodyColor = chooseP(nsr, P_BODY_COLORS);
        if (mBodyColor == 0) mBodyColor = Color.HSVToColor(new float[] {
                nsr.nextFloat()*360f, frandrange(nsr,0.5f,1f), frandrange(nsr,0.5f, 1f)});

        tint(mBodyColor, d.body, d.head, d.leg1, d.leg2, d.leg3, d.leg4, d.tail,
                d.leftEar, d.rightEar, d.foot1, d.foot2, d.foot3, d.foot4, d.tailCap);
        tint(0x20000000, d.leg2Shadow, d.tailShadow);
        if (isDark(mBodyColor)) {
            tint(0xFFFFFFFF, d.leftEye, d.rightEye, d.mouth, d.nose);
        }
        tint(isDark(mBodyColor) ? 0xFFEF9A9A : 0x20D50000, d.leftEarInside, d.rightEarInside);

        tint(chooseP(nsr, P_BELLY_COLORS), d.belly);
        tint(chooseP(nsr, P_BELLY_COLORS), d.back);
        final int faceColor = chooseP(nsr, P_BELLY_COLORS);
        tint(faceColor, d.faceSpot);
        if (!isDark(faceColor)) {
            tint(0xFF000000, d.mouth, d.nose);
        }

        mFootType = 0;
        if (nsr.nextFloat() < 0.25f) {
            mFootType = 4;
            tint(0xFFFFFFFF, d.foot1, d.foot2, d.foot3, d.foot4);
        } else {
            if (nsr.nextFloat() < 0.25f) {
                mFootType = 2;
                tint(0xFFFFFFFF, d.foot1, d.foot3);
            } else if (nsr.nextFloat() < 0.1f) {
                mFootType = 1;
                tint(0xFFFFFFFF, (Drawable) choose(nsr, d.foot1, d.foot2, d.foot3, d.foot4));
            }
        }

        tint(nsr.nextFloat() < 0.333f ? 0xFFFFFFFF : mBodyColor, d.tailCap);

        final int capColor = chooseP(nsr, isDark(mBodyColor) ? P_LIGHT_SPOT_COLORS : P_DARK_SPOT_COLORS);
        tint(capColor, d.cap);

        final int collarColor = chooseP(nsr, P_COLLAR_COLORS);
        tint(collarColor, d.collar);
        mBowTie = nsr.nextFloat() < 0.1f;
        tint(mBowTie ? collarColor : 0, d.bowtie);
    }

    public static Cat create(Context context) {
        return new Cat(context, Math.abs(ThreadLocalRandom.current().nextInt()));
    }

    public NotificationCompat.Builder buildNotification(Context context) {
        final Bundle extras = new Bundle();
        extras.putString("android.substName", context.getString(R.string.nougat_notification_name));
        final Intent intent = new Intent(Intent.ACTION_MAIN)
                .setClass(context, NekoLand.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return new NotificationCompat.Builder(context, NekoLand.CHAN_ID)
                .setSmallIcon(R.drawable.nougat_stat_icon)
                .setLargeIcon(createNotificationLargeIcon(context))
                .setColor(getBodyColor())
                .setPriority(Notification.PRIORITY_LOW)
                .setContentTitle(context.getString(R.string.nougat_notification_title))
                .setShowWhen(true)
                .setCategory(Notification.CATEGORY_STATUS)
                .setContentText(getName())
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE))
                .setAutoCancel(true)
                .setVibrate(PURR)
                .addExtras(extras);
    }

    public long getSeed() {
        return mSeed;
    }

    @Override
    public void draw(Canvas canvas) {
        final int w = Math.min(canvas.getWidth(), canvas.getHeight());
        final int h = w;

        if (mBitmap == null || mBitmap.getWidth() != w || mBitmap.getHeight() != h) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            final Canvas bitCanvas = new Canvas(mBitmap);
            slowDraw(bitCanvas, 0, 0, w, h);
        }
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    private void slowDraw(Canvas canvas, int x, int y, int w, int h) {
        for (int i = 0; i < d.drawingOrder.length; i++) {
            final Drawable drawable = this.d.drawingOrder[i];
            if (drawable != null) {
                drawable.setBounds(x, y, x+w, y+h);
                drawable.draw(canvas);
            }
        }

    }

    public Bitmap createBitmap(int w, int h) {
        if (mBitmap != null && mBitmap.getWidth() == w && mBitmap.getHeight() == h) {
            return mBitmap.copy(mBitmap.getConfig(), true);
        }
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        slowDraw(new Canvas(result), 0, 0, w, h);
        return result;
    }

    public Bitmap createNotificationLargeIcon(Context context) {
        final Resources res = context.getResources();
        final int w = 2*res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
        final int h = 2*res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
        return createBitmapIcon(w, h);
    }

    public Bitmap createBitmapIcon(int w, int h) {
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(result);
        final Paint pt = new Paint();
        float[] hsv = new float[3];
        Color.colorToHSV(mBodyColor, hsv);
        hsv[2] = (hsv[2]>0.5f)
                ? (hsv[2] - 0.25f)
                : (hsv[2] + 0.25f);
        pt.setColor(Color.HSVToColor(hsv));
        float r = (float) w /2;
        canvas.drawCircle(r, r, r, pt);
        int m = w/10;

        slowDraw(canvas, m, m, w-m-m, h-m-m);
        return result;
    }

    public Icon createIcon(int w, int h) {
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(result);
        final Paint pt = new Paint();
        float[] hsv = new float[3];
        Color.colorToHSV(mBodyColor, hsv);
        hsv[2] = (hsv[2]>0.5f)
                ? (hsv[2] - 0.25f)
                : (hsv[2] + 0.25f);
        pt.setColor(Color.HSVToColor(hsv));
        float r = (float) w /2;
        canvas.drawCircle(r, r, r, pt);
        int m = w/10;

        slowDraw(canvas, m, m, w-m-m, h-m-m);

        return Icon.createWithBitmap(result);
    }

    @Override
    public void setAlpha(int i) {
        // Unused Function
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // Unused Function
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getBodyColor() {
        return mBodyColor;
    }

    public void logAdd(Context context) {
        logCatAction(context, "egg_neko_add");
    }

    public void logRename(Context context) {
        logCatAction(context, "egg_neko_rename");
    }

    public void logRemove(Context context) {
        logCatAction(context, "egg_neko_remove");
    }

    public void logShare(Context context) {
        logCatAction(context, "egg_neko_share");
    }

    private void logCatAction(Context context, String prefix) {
        FirebaseLogger.INSTANCE.count(context, prefix, 1);
        FirebaseLogger.INSTANCE.histogram(context, prefix +"_color",
                getColorIndex(mBodyColor, P_BODY_COLORS));
        FirebaseLogger.INSTANCE.histogram(context, prefix + "_bowtie", mBowTie ? 1 : 0);
        FirebaseLogger.INSTANCE.histogram(context, prefix + "_feet", mFootType);
    }

    public static class CatParts {
        private final Drawable leftEar;
        private final Drawable rightEar;
        private final Drawable rightEarInside;
        private final Drawable leftEarInside;
        private final Drawable head;
        private final Drawable faceSpot;
        private final Drawable cap;
        private final Drawable mouth;
        private final Drawable body;
        private final Drawable foot1;
        private final Drawable leg1;
        private final Drawable foot2;
        private final Drawable leg2;
        private final Drawable foot3;
        private final Drawable leg3;
        private final Drawable foot4;
        private final Drawable leg4;
        private final Drawable tail;
        private final Drawable leg2Shadow;
        private final Drawable tailShadow;
        private final Drawable tailCap;
        private final Drawable belly;
        private final Drawable back;
        private final Drawable rightEye;
        private final Drawable leftEye;
        private final Drawable nose;
        private final Drawable bowtie;
        private final Drawable collar;
        private final Drawable[] drawingOrder;

        public CatParts(Context context) {

            body = AppCompatResources.getDrawable(context, R.drawable.nougat_body);
            head = AppCompatResources.getDrawable(context, R.drawable.nougat_head);
            leg1 = AppCompatResources.getDrawable(context, R.drawable.nougat_leg1);
            leg2 = AppCompatResources.getDrawable(context, R.drawable.nougat_leg2);
            leg3 = AppCompatResources.getDrawable(context, R.drawable.nougat_leg3);
            leg4 = AppCompatResources.getDrawable(context, R.drawable.nougat_leg4);
            tail = AppCompatResources.getDrawable(context, R.drawable.nougat_tail);
            leftEar = AppCompatResources.getDrawable(context, R.drawable.nougat_left_ear);
            rightEar = AppCompatResources.getDrawable(context, R.drawable.nougat_right_ear);
            rightEarInside = AppCompatResources.getDrawable(context, R.drawable.nougat_right_ear_inside);
            leftEarInside = AppCompatResources.getDrawable(context, R.drawable.nougat_left_ear_inside);
            faceSpot = AppCompatResources.getDrawable(context, R.drawable.nougat_face_spot);
            cap = AppCompatResources.getDrawable(context, R.drawable.nougat_cap);
            mouth = AppCompatResources.getDrawable(context, R.drawable.nougat_mouth);
            foot4 = AppCompatResources.getDrawable(context, R.drawable.nougat_foot4);
            foot3 = AppCompatResources.getDrawable(context, R.drawable.nougat_foot3);
            foot1 = AppCompatResources.getDrawable(context, R.drawable.nougat_foot1);
            foot2 = AppCompatResources.getDrawable(context, R.drawable.nougat_foot2);
            leg2Shadow = AppCompatResources.getDrawable(context, R.drawable.nougat_leg2_shadow);
            tailShadow = AppCompatResources.getDrawable(context, R.drawable.nougat_tail_shadow);
            tailCap = AppCompatResources.getDrawable(context, R.drawable.nougat_tail_cap);
            belly = AppCompatResources.getDrawable(context, R.drawable.nougat_belly);
            back = AppCompatResources.getDrawable(context, R.drawable.nougat_back);
            rightEye = AppCompatResources.getDrawable(context, R.drawable.nougat_right_eye);
            leftEye = AppCompatResources.getDrawable(context, R.drawable.nougat_left_eye);
            nose = AppCompatResources.getDrawable(context, R.drawable.nougat_nose);
            collar = AppCompatResources.getDrawable(context, R.drawable.nougat_collar);
            bowtie = AppCompatResources.getDrawable(context, R.drawable.nougat_bowtie);
            drawingOrder = getDrawingOrder();
        }
        private Drawable[] getDrawingOrder() {
            return new Drawable[] {
                    collar,
                    leftEar, leftEarInside, rightEar, rightEarInside,
                    head,
                    faceSpot,
                    cap,
                    leftEye, rightEye,
                    nose, mouth,
                    tail, tailCap, tailShadow,
                    foot1, leg1,
                    foot2, leg2,
                    foot3, leg3,
                    foot4, leg4,
                    leg2Shadow,
                    body, belly,
                    bowtie
            };
        }
    }
}