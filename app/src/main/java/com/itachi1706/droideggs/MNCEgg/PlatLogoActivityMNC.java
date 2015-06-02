package com.itachi1706.droideggs.MNCEgg;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
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
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.itachi1706.droideggs.LollipopEgg.LLandActivity;
import com.itachi1706.droideggs.R;

@TargetApi(21)
public class PlatLogoActivityMNC extends AppCompatActivity {

    final static int[] FLAVORS = {
            0xFF9C27B0, 0xFFBA68C8, // grape
            0xFFFF9800, 0xFFFFB74D, // orange
            0xFFF06292, 0xFFF8BBD0, // bubblegum
            0xFFAFB42B, 0xFFCDDC39, // lime
            0xFFFFEB3B, 0xFFFFF176, // lemon
            0xFF795548, 0xFFA1887F, // mystery flavor
    };

    final static int[] PRESSEDCOLOR = {
            0xFFFFFFFF, 0xFFFFFFFF, // grape
            0xFFFFFFFF, 0xFFFFFFFF, // orange
            0xFFFFFFFF, 0xFFFFFFFF, // bubblegum
            0xFFFFFFFF, 0xFFFFFFFF, // lime
            0xFFFFFFFF, 0xFFFFFFFF, // lemon
            0xFFFFFFFF, 0xFFFFFFFF, // mystery flavor
    };

    final int[] pressedColor = { 0x00000000 , 0x00000000 };

    FrameLayout mLayout;
    int mTapCount;
    int mKeyCount;
    PathInterpolator mInterpolator = new PathInterpolator(0f, 0f, 0.5f, 1f);

    static int newColorIndex() {
        return 2*((int) (Math.random()*PRESSEDCOLOR.length/2));
    }

    Drawable makeRipple() {
        final int idx = newColorIndex();
        final ShapeDrawable popbg = new ShapeDrawable(new OvalShape());
        final RippleDrawable ripple = new RippleDrawable(ColorStateList.valueOf(PRESSEDCOLOR[idx + 1]), popbg, null);
        return ripple;
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

        final ImageView im = new ImageView(this);
        im.setTranslationZ(20);
        im.setScaleX(0);
        im.setScaleY(0);
        final Drawable platlogo = getDrawable(R.drawable.mnc_platlogo);
        platlogo.setAlpha(230);
        im.setImageDrawable(platlogo);
        im.setBackground(makeRipple());
        im.setClickable(true);


        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTapCount == 0) {

                    im.setBackground(makeRipple());

                    im.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (mTapCount < 5) return false;

                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(PlatLogoActivityMNC.this);
                            if (pref.getLong("L_EGG_MODE", 0) == 0){
                                // For posterity: the moment this user unlocked the easter egg
                                pref.edit().putLong("L_EGG_MODE", System.currentTimeMillis()).apply();
                            }
                            im.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Intent lland = new Intent(PlatLogoActivityMNC.this, LLandActivity.class);
                                        lland.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                        startActivity(lland);
                                    } catch (ActivityNotFoundException ex) {
                                        Log.e("PlatLogoActivity", "No more eggs.");
                                    }
                                    finish();
                                }
                            });
                            return true;
                        }
                    });
                } else {
                    im.setBackground(makeRipple());
                }
                mTapCount++;
            }
        });

        // Enable hardware keyboard input for TV compatibility.
        im.setFocusable(true);
        im.requestFocus();
        im.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
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
            }
        });

        mLayout.addView(im, new FrameLayout.LayoutParams(size, size, Gravity.CENTER));
        //mLayout.addView(imRip, new FrameLayout.LayoutParams(size, size, Gravity.CENTER));

        //Make it appear

        im.animate().scaleX(0.3f).scaleY(0.3f)
                .setInterpolator(mInterpolator)
                .setDuration(500)
                .setStartDelay(800)
                .start();

        im.animate()
                .translationZ(40)
                .scaleX(1)
                .scaleY(1)
                .setInterpolator(mInterpolator)
                .setDuration(700)
                .setStartDelay(500)
                .start();

    }
}
