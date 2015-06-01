package com.itachi1706.droideggs.KitKatEgg;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.dreams.DreamService;

/**
 * Created by Kenneth on 1/6/2015
 * for DroidEggs in package com.itachi1706.droideggs.KitKatEgg
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)  //API 17
public class DessertCaseDream extends DreamService {

    private DessertCaseView mView;
    private DessertCaseView.RescalingContainer mContainer;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);

        mView = new DessertCaseView(this);

        mContainer = new DessertCaseView.RescalingContainer(this);

        mContainer.setView(mView);

        setContentView(mContainer);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        mView.postDelayed(new Runnable() {
            public void run() {
                mView.start();
            }
        }, 1000);
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        mView.stop();
    }
}
