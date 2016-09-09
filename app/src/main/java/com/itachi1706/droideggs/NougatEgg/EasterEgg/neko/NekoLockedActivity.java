package com.itachi1706.droideggs.NougatEgg.EasterEgg.neko;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * Created by Kenneth on 8/9/2016.
 * for com.itachi1706.droideggs.NougatEgg.EasterEgg.neko in DroidEggs
 */
public class NekoLockedActivity extends AppCompatActivity implements OnDismissListener {
    private NekoDialog mDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        mDialog = new NekoDialog(this);
        mDialog.setOnDismissListener(this);
        mDialog.show();
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        finish();
    }
}
