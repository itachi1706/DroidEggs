/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.itachi1706.droideggs.eggs.oreo.easter_egg.octo;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

/**
 * Created by Kenneth on 2/9/2017.
 * for com.itachi1706.droideggs.OreoEgg.EasterEgg.octo in DroidEggs
 */
public class OctopusDrawable extends Drawable {
    private static final float BASE_SCALE = 100f;
    public static final boolean PATH_DEBUG = false;

    private static final int BODY_COLOR   = 0xFF101010;
    private static final int ARM_COLOR    = 0xFF101010;
    private static final int ARM_COLOR_BACK = 0xFF000000;
    private static final int EYE_COLOR    = 0xFF808080;

    private static final int[] BACK_ARMS = {1, 3, 4, 6};
    private static final int[] FRONT_ARMS = {0, 2, 5, 7};

    private final Paint mPaint = new Paint();
    private final Arm[] mArms = new Arm[8];
    final PointF point = new PointF();
    final Matrix m = new Matrix();
    final Matrix mInv = new Matrix();
    private TimeAnimator mDriftAnimation;
    private boolean mBlinking;
    private final float[] ptmp = new float[2];
    private final float[] scaledBounds = new float[2];

    public static float randfrange(float a, float b) {
        return (float) (Math.random()*(b-a) + a);
    }
    public static float clamp(float v, float a, float b) {
        return v<a?a: Math.min(v, b);
    }

    public OctopusDrawable(Context context) {
        float dp = context.getResources().getDisplayMetrics().density;
        setSizePx((int) (100*dp));
        mPaint.setAntiAlias(true);
        for (int i=0; i<mArms.length; i++) {
            final float bias = (float)i/(mArms.length-1) - 0.5f;
            mArms[i] = new Arm(
                    0,0, // arm will be repositioned on moveTo
                    10f*bias + randfrange(0,20f), randfrange(20f,50f),
                    40f*bias+randfrange(-60f,60f), randfrange(30f, 80f),
                    randfrange(-40f,40f), randfrange(-80f,40f),
                    14f, 2f);
        }
    }

    public void setSizePx(int size) {
        m.setScale(size /BASE_SCALE, size /BASE_SCALE);
        // TaperedPathStroke.setMinStep(20f*BASE_SCALE/mSizePx); // nice little floaty circles
        TaperedPathStroke.setMinStep(8f*BASE_SCALE/ size); // classic tentacles
        m.invert(mInv);
    }

    public void startDrift() {
        if (mDriftAnimation == null) {
            mDriftAnimation = new TimeAnimator();
            mDriftAnimation.setTimeListener(new TimeAnimator.TimeListener() {
                final float maxVy = 35f;
                final float jumpVy = -100f;
                final float maxVx = 15f;
                private float vx;
                private float vy;
                long nextjump = 0;
                long unblink = 0;
                @Override
                public void onTimeUpdate(TimeAnimator timeAnimator, long t, long dt) {
                    float tSec = 0.001f * t;
                    float dtSec = 0.001f * dt;
                    if (t > nextjump) {
                        vy = jumpVy;
                        nextjump = t + (long) randfrange(5000, 10000);
                    }
                    if (unblink > 0 && t > unblink) {
                        setBlinking(false);
                        unblink = 0;
                    } else if (Math.random() < 0.001f) {
                        setBlinking(true);
                        unblink = t + 200;
                    }

                    float ax = (float) (maxVx * Math.sin(tSec * .25f));

                    vx = clamp(vx + dtSec * ax, -maxVx, maxVx);
                    float ay = 30f;
                    vy = clamp(vy + dtSec * ay, -100* maxVy, maxVy);

                    // oob check
                    if (point.y - BASE_SCALE/2 > scaledBounds[1]) {
                        vy = jumpVy;
                    } else if (point.y + BASE_SCALE < 0) {
                        vy = maxVy;
                    }

                    point.x = clamp(point.x + dtSec * vx, 0, scaledBounds[0]);
                    point.y = point.y + dtSec * vy;

                    repositionArms();
                }
            });
        }
        mDriftAnimation.start();
    }

    public void stopDrift() {
        mDriftAnimation.cancel();
    }

    @Override
    public void onBoundsChange(Rect bounds) {
        final float w = bounds.width();
        final float h = bounds.height();

        lockArms(true);
        moveTo(w/2, h/2);
        lockArms(false);

        scaledBounds[0] = w;
        scaledBounds[1] = h;
        mInv.mapPoints(scaledBounds);
    }

    // real pixel coordinates
    public void moveTo(float x, float y) {
        point.x = x;
        point.y = y;
        mapPointF(mInv, point);
        repositionArms();
    }

    public boolean hitTest(float x, float y) {
        ptmp[0] = x;
        ptmp[1] = y;
        mInv.mapPoints(ptmp);
        return Math.hypot(ptmp[0] - point.x, ptmp[1] - point.y) < BASE_SCALE/2;
    }

    private void lockArms(boolean l) {
        for (Arm arm : mArms) {
            arm.setLocked(l);
        }
    }
    private void repositionArms() {
        for (int i=0; i<mArms.length; i++) {
            final float bias = (float)i/(mArms.length-1) - 0.5f;
            mArms[i].setAnchor(
                    point.x+bias*30f,point.y+26f);
        }
        invalidateSelf();
    }

    private void drawPupil(Canvas canvas, float x, float y, float size, boolean open,
                           Paint pt) {
        final float r = open ? size*.33f : size * .1f;
        canvas.drawRoundRect(x - size, y - r, x + size, y + r, r, r, pt);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        innerDraw(canvas);
        canvas.restore();
    }

    private void innerDraw(Canvas canvas) {
        canvas.concat(m);

        // arms behind
        mPaint.setColor(ARM_COLOR_BACK);
        for (int i : BACK_ARMS) {
            mArms[i].draw(canvas, mPaint);
        }

        // head/body/thing
        mPaint.setColor(EYE_COLOR);
        canvas.drawCircle(point.x, point.y, 36f, mPaint);
        mPaint.setColor(BODY_COLOR);
        canvas.save();
        saveOutRect(canvas);
        canvas.restore();

        // eyes
        mPaint.setColor(EYE_COLOR);
        if (mBlinking) {
            drawPupil(canvas, point.x - 16f, point.y - 12f, 6f, false, mPaint);
            drawPupil(canvas, point.x + 16f, point.y - 12f, 6f, false, mPaint);
        } else {
            canvas.drawCircle(point.x - 16f, point.y - 12f, 6f, mPaint);
            canvas.drawCircle(point.x + 16f, point.y - 12f, 6f, mPaint);
        }

        // too much?
        if (false) {
            mPaint.setColor(0xFF000000);
            drawPupil(canvas, point.x - 16f, point.y - 12f, 5f, true, mPaint);
            drawPupil(canvas, point.x + 16f, point.y - 12f, 5f, true, mPaint);
        }

        // arms in front
        mPaint.setColor(ARM_COLOR);
        for (int i : FRONT_ARMS) {
            mArms[i].draw(canvas, mPaint);
        }

        if (PATH_DEBUG) for (Arm arm : mArms) {
            arm.drawDebug(canvas);
        }
    }

    private void saveOutRect(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            canvas.clipOutRect(point.x - 61f, point.y + 8f,
                    point.x + 61f, point.y + 12f);
        else {
            canvas.clipRect(new RectF(point.x - 61f, point.y + 8f,
                    point.x + 61f, point.y + 12f), Region.Op.DIFFERENCE);
        }
        canvas.drawOval(point.x-40f,point.y-60f,point.x+40f,point.y+40f, mPaint);
    }

    public void setBlinking(boolean b) {
        mBlinking = b;
        invalidateSelf();
    }

    @Override
    public void setAlpha(int i) {
        // Unused Function
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        // Unused Function
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    static Path pathMoveTo(Path p, PointF pt) {
        p.moveTo(pt.x, pt.y);
        return p;
    }
    static Path pathQuadTo(Path p, PointF p1, PointF p2) {
        p.quadTo(p1.x, p1.y, p2.x, p2.y);
        return p;
    }

    static void mapPointF(Matrix m, PointF point) {
        float[] p = new float[2];
        p[0] = point.x;
        p[1] = point.y;
        m.mapPoints(p);
        point.x = p[0];
        point.y = p[1];
    }

    private class Link  // he come to town
            implements DynamicAnimation.OnAnimationUpdateListener {
        final FloatValueHolder[] coords = new FloatValueHolder[2];
        final SpringAnimation[] anims = new SpringAnimation[coords.length];
        private final float dx;
        private final float dy;
        private boolean locked = false;
        Link next;

        Link(int index, float x1, float y1, float dx, float dy) {
            coords[0] = new FloatValueHolder(x1);
            coords[1] = new FloatValueHolder(y1);
            this.dx = dx;
            this.dy = dy;
            for (int i=0; i<coords.length; i++) {
                anims[i] = new SpringAnimation(coords[i]);
                anims[i].setSpring(new SpringForce()
                        .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                        .setStiffness(
                                index == 0 ? SpringForce.STIFFNESS_LOW
                                        : index == 1 ? SpringForce.STIFFNESS_VERY_LOW
                                        : SpringForce.STIFFNESS_VERY_LOW/2)
                        .setFinalPosition(0f));
                anims[i].addUpdateListener(this);
            }
        }
        public void setLocked(boolean locked) {
            this.locked = locked;
        }
        public PointF start() {
            return new PointF(coords[0].getValue(), coords[1].getValue());
        }
        public PointF end() {
            return new PointF(coords[0].getValue()+dx,coords[1].getValue()+dy);
        }
        public PointF mid() {
            return new PointF(
                    0.5f*dx+(coords[0].getValue()),
                    0.5f*dy+(coords[1].getValue()));
        }
        public void animateTo(PointF target) {
            if (locked) {
                setStart(target.x, target.y);
            } else {
                anims[0].animateToFinalPosition(target.x);
                anims[1].animateToFinalPosition(target.y);
            }
        }
        @Override
        public void onAnimationUpdate(DynamicAnimation dynamicAnimation, float v, float v1) {
            if (next != null) {
                next.animateTo(end());
            }
            OctopusDrawable.this.invalidateSelf();
        }

        public void setStart(float x, float y) {
            coords[0].setValue(x);
            coords[1].setValue(y);
            onAnimationUpdate(null, 0, 0);
        }
    }

    private class Arm {
        final Link link1;
        final Link link2;
        final Link link3;
        float max;
        float min;

        public Arm(float x, float y, float dx1, float dy1, float dx2, float dy2, float dx3, float dy3,
                   float max, float min) {
            link1 = new Link(0, x, y, dx1, dy1);
            link2 = new Link(1, x+dx1, y+dy1, dx2, dy2);
            link3 = new Link(2, x+dx1+dx2, y+dy1+dy2, dx3, dy3);
            link1.next = link2;
            link2.next = link3;

            link1.setLocked(true);
            link2.setLocked(false);
            link3.setLocked(false);

            this.max = max;
            this.min = min;
        }

        // when the arm is locked, it moves rigidly, without physics
        public void setLocked(boolean locked) {
            link2.setLocked(locked);
            link3.setLocked(locked);
        }

        private void setAnchor(float x, float y) {
            link1.setStart(x,y);
        }

        public Path getPath() {
            Path p = new Path();
            pathMoveTo(p, link1.start());
            pathQuadTo(p, link2.start(), link2.mid());
            pathQuadTo(p, link2.end(), link3.end());
            return p;
        }

        public void draw(@NonNull Canvas canvas, Paint pt) {
            final Path p = getPath();
            TaperedPathStroke.drawPath(canvas, p, max, min, pt);
        }

        private final Paint dpt = new Paint();
        public void drawDebug(Canvas canvas) {
            dpt.setStyle(Paint.Style.STROKE);
            dpt.setStrokeWidth(0.75f);
            dpt.setStrokeCap(Paint.Cap.ROUND);

            dpt.setAntiAlias(true);
            dpt.setColor(0xFF336699);

            final Path path = getPath();
            canvas.drawPath(path, dpt);

            dpt.setColor(0xFFFFFF00);

            dpt.setPathEffect(new DashPathEffect(new float[] {2f, 2f}, 0f));

            canvas.drawLines(new float[] {
                    link1.end().x,   link1.end().y,
                    link2.start().x, link2.start().y,

                    link2.end().x,   link2.end().y,
                    link3.start().x, link3.start().y,
            }, dpt);
            dpt.setPathEffect(null);

            dpt.setColor(0xFF00CCFF);

            canvas.drawLines(new float[] {
                    link1.start().x, link1.start().y,
                    link1.end().x,   link1.end().y,

                    link2.start().x, link2.start().y,
                    link2.end().x,   link2.end().y,

                    link3.start().x, link3.start().y,
                    link3.end().x,   link3.end().y,
            }, dpt);

            dpt.setColor(0xFFCCEEFF);
            canvas.drawCircle(link2.start().x, link2.start().y, 2f, dpt);
            canvas.drawCircle(link3.start().x, link3.start().y, 2f, dpt);

            dpt.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(link1.start().x, link1.start().y, 2f, dpt);
            canvas.drawCircle(link2.mid().x,   link2.mid().y,   2f, dpt);
            canvas.drawCircle(link3.end().x,   link3.end().y,   2f, dpt);
        }

    }
}
