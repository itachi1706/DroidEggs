/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.itachi1706.droideggs.eggs.red_velvet_cake.easter_egg.neko;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import com.itachi1706.droideggs.FirebaseLogger;
import com.itachi1706.droideggs.R;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** It's a cat. */
@TargetApi(Build.VERSION_CODES.R)
public class Cat extends Drawable {
    protected static final long[] PURR = {0, 40, 20, 40, 20, 40, 20, 40, 20, 40, 20, 40};

    public static final boolean ALL_CATS_IN_ONE_CONVERSATION = true;

    public static final String GLOBAL_SHORTCUT_ID = "com.android.egg.neko:allcats";
    public static final String SHORTCUT_ID_PREFIX = "com.android.egg.neko:cat:";

    private Random mNotSoRandom;
    private Bitmap mBitmap;
    private long mSeed;
    private String mName;
    private int mBodyColor;
    private int mFootType;
    private boolean mBowTie;
    private String mFirstMessage;

    private synchronized Random notSoRandom(long seed) {
        if (mNotSoRandom == null) {
            mNotSoRandom = new Random();
            mNotSoRandom.setSeed(seed);
        }
        return mNotSoRandom;
    }

    public static final float frandrange(Random r, float a, float b) {
        return (b - a) * r.nextFloat() + a;
    }

    public static final Object choose(Random r, Object... l) {
        return l[r.nextInt(l.length)];
    }

    public static final int chooseP(Random r, int[] a) {
        return chooseP(r, a, 1000);
    }

    public static final int chooseP(Random r, int[] a, int sum) {
        int pct = r.nextInt(sum);
        final int stop = a.length - 2;
        int i = 0;
        while (i < stop) {
            pct -= a[i];
            if (pct < 0) break;
            i += 2;
        }
        return a[i + 1];
    }

    public static final int getColorIndex(int q, int[] a) {
        for (int i = 1; i < a.length; i += 2) {
            if (a[i] == q) {
                return i / 2;
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

    private CatParts d;

    public static void tint(int color, Drawable... ds) {
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

        setName(context.getString(R.string.r_default_cat_name,
                String.valueOf(mSeed % 1000)));

        final Random nsr = notSoRandom(seed);

        // body color
        mBodyColor = chooseP(nsr, P_BODY_COLORS);
        if (mBodyColor == 0) mBodyColor = Color.HSVToColor(new float[]{
                nsr.nextFloat() * 360f, frandrange(nsr, 0.5f, 1f), frandrange(nsr, 0.5f, 1f)});

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

        String[] messages = context.getResources().getStringArray(
                nsr.nextFloat() < 0.1f ? R.array.r_rare_cat_messages : R.array.r_cat_messages);
        mFirstMessage = (String) choose(nsr, (Object[]) messages);
        if (nsr.nextFloat() < 0.5f) mFirstMessage = mFirstMessage + mFirstMessage + mFirstMessage;
    }

    public static Cat fromShortcutId(Context context, String shortcutId) {
        if (shortcutId.startsWith(SHORTCUT_ID_PREFIX)) {
            return new Cat(context, Long.parseLong(shortcutId.replace(SHORTCUT_ID_PREFIX, "")));
        }
        return null;
    }

    public static Cat create(Context context) {
        return new Cat(context, Math.abs(ThreadLocalRandom.current().nextInt()));
    }

    public Notification.Builder buildNotification(Context context) {
        final Bundle extras = new Bundle();
        extras.putString("android.substName", context.getString(R.string.r_notification_name));

        final Icon notificationIcon = createNotificationLargeIcon(context);

        final Intent intent = new Intent(Intent.ACTION_MAIN)
                .setClass(context, NekoLand.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ShortcutInfo shortcut = new ShortcutInfo.Builder(context, getShortcutId())
                .setActivity(intent.getComponent())
                .setIntent(intent)
                .setShortLabel(getName())
                .setIcon(createShortcutIcon(context))
                .setLongLived(true)
                .build();
        context.getSystemService(ShortcutManager.class).addDynamicShortcuts(List.of(shortcut));

        Notification.BubbleMetadata bubbs = new Notification.BubbleMetadata.Builder()
                .setIntent(
                        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE))
                .setIcon(notificationIcon)
                .setSuppressNotification(false)
                .setDesiredHeight(context.getResources().getDisplayMetrics().heightPixels)
                .build();

        return new Notification.Builder(context, NekoLand.CHAN_ID)
                .setSmallIcon(Icon.createWithResource(context, R.drawable.r_stat_icon))
                .setLargeIcon(notificationIcon)
                .setColor(getBodyColor())
                .setContentTitle(context.getString(R.string.r_notification_title))
                .setShowWhen(true)
                .setCategory(Notification.CATEGORY_STATUS)
                .setContentText(getName())
                .setContentIntent(
                        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE))
                .setAutoCancel(true)
                .setStyle(new Notification.MessagingStyle(createPerson())
                        .addMessage(mFirstMessage, System.currentTimeMillis(), createPerson())
                        .setConversationTitle(getName())
                )
                .setBubbleMetadata(bubbs)
                .setShortcutId(getShortcutId())
                .addExtras(extras);
    }

    private Person createPerson() {
        return new Person.Builder()
                .setName(getName())
                .setBot(true)
                .setKey(getShortcutId())
                .build();
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
                drawable.setBounds(x, y, x + w, y + h);
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

    public static Icon recompressIcon(Icon bitmapIcon) {
        if (bitmapIcon.getType() != Icon.TYPE_BITMAP) return bitmapIcon;
        try {
            final Bitmap bits = (Bitmap) Icon.class.getDeclaredMethod("getBitmap").invoke(bitmapIcon);
            final ByteArrayOutputStream ostream = new ByteArrayOutputStream(
                    bits.getWidth() * bits.getHeight() * 2); // guess 50% compression
            final boolean ok = bits.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            if (!ok) return null;
            return Icon.createWithData(ostream.toByteArray(), 0, ostream.size());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            return bitmapIcon;
        }
    }

    public Icon createNotificationLargeIcon(Context context) {
        final Resources res = context.getResources();
        final int w = res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
        final int h = res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
        return recompressIcon(createIcon(w, h));
    }

    public Icon createShortcutIcon(Context context) {
        // shortcuts do not support compressed bitmaps
        final Resources res = context.getResources();
        final int w = res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
        final int h = res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
        return createIcon(w, h);
    }

    public Icon createIcon(int w, int h) {
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(result);
        float[] hsv = new float[3];
        Color.colorToHSV(mBodyColor, hsv);
        hsv[2] = (hsv[2] > 0.5f)
                ? (hsv[2] - 0.25f)
                : (hsv[2] + 0.25f);

        // Adaptive bitmaps!
        canvas.drawColor(Color.HSVToColor(hsv));
        int m = w / 4;

        slowDraw(canvas, m, m, w - m - m, h - m - m);

        return Icon.createWithAdaptiveBitmap(result);
    }

    @Override
    public void setAlpha(int i) {
        // NO-OP
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // NO-OP
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
        FirebaseLogger.INSTANCE.histogram(context, prefix + "_color", getColorIndex(mBodyColor, P_BODY_COLORS));
        FirebaseLogger.INSTANCE.histogram(context, prefix + "_bowtie", mBowTie ? 1 : 0);
        FirebaseLogger.INSTANCE.histogram(context, prefix + "_feet", mFootType);
    }

    public String getShortcutId() {
        return ALL_CATS_IN_ONE_CONVERSATION
                ? GLOBAL_SHORTCUT_ID
                : (SHORTCUT_ID_PREFIX + mSeed);
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
            body = context.getDrawable(R.drawable.r_body);
            head = context.getDrawable(R.drawable.r_head);
            leg1 = context.getDrawable(R.drawable.r_leg1);
            leg2 = context.getDrawable(R.drawable.r_leg2);
            leg3 = context.getDrawable(R.drawable.r_leg3);
            leg4 = context.getDrawable(R.drawable.r_leg4);
            tail = context.getDrawable(R.drawable.r_tail);
            leftEar = context.getDrawable(R.drawable.r_left_ear);
            rightEar = context.getDrawable(R.drawable.r_right_ear);
            rightEarInside = context.getDrawable(R.drawable.r_right_ear_inside);
            leftEarInside = context.getDrawable(R.drawable.r_left_ear_inside);
            faceSpot = context.getDrawable(R.drawable.r_face_spot);
            cap = context.getDrawable(R.drawable.r_cap);
            mouth = context.getDrawable(R.drawable.r_mouth);
            foot4 = context.getDrawable(R.drawable.r_foot4);
            foot3 = context.getDrawable(R.drawable.r_foot3);
            foot1 = context.getDrawable(R.drawable.r_foot1);
            foot2 = context.getDrawable(R.drawable.r_foot2);
            leg2Shadow = context.getDrawable(R.drawable.r_leg2_shadow);
            tailShadow = context.getDrawable(R.drawable.r_tail_shadow);
            tailCap = context.getDrawable(R.drawable.r_tail_cap);
            belly = context.getDrawable(R.drawable.r_belly);
            back = context.getDrawable(R.drawable.r_back);
            rightEye = context.getDrawable(R.drawable.r_right_eye);
            leftEye = context.getDrawable(R.drawable.r_left_eye);
            nose = context.getDrawable(R.drawable.r_nose);
            collar = context.getDrawable(R.drawable.r_collar);
            bowtie = context.getDrawable(R.drawable.r_bowtie);
            drawingOrder = getDrawingOrder();
        }

        private Drawable[] getDrawingOrder() {
            return new Drawable[]{
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