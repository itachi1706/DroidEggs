package com.itachi1706.droideggs.HoneycombEgg;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.itachi1706.droideggs.R;

public class PlatLogoActivityHONEYCOMB extends AppCompatActivity {

    Toast mToast;
    int BGCOLOR = 0xD0000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("preferenceggs", Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setBackgroundColor(BGCOLOR);

        mToast = Toast.makeText(this, "REZZZZZZZ...", Toast.LENGTH_SHORT);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView content = new ImageView(this);

        content.setImageResource(R.drawable.hc_platlogo);
        content.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        setContentView(content);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Boolean forcePort = getSharedPreferences("preferenceggs", Context.MODE_PRIVATE).getBoolean("hc_force_port", false);

        if (forcePort == true) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            mToast.show();
        }
        return super.dispatchTouchEvent(ev);
    }

}
