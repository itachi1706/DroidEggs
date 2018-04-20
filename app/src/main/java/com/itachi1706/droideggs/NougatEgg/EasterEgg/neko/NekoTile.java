package com.itachi1706.droideggs.NougatEgg.EasterEgg.neko;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

/**
 * Created by Kenneth on 8/9/2016.
 * for com.itachi1706.droideggs.NougatEgg.EasterEgg.neko in DroidEggs
 */
@TargetApi(Build.VERSION_CODES.N)
public class NekoTile extends TileService implements PrefState.PrefsListener {
    private static final String TAG = "NekoTile";

    private PrefState mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = new PrefState(this);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        mPrefs.setListener(this);
        updateState();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        mPrefs.setListener(null);
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        MetricsLogger.count(this, "egg_neko_tile_added", 1);
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        MetricsLogger.count(this, "egg_neko_tile_removed", 1);
    }

    @Override
    public void onPrefsChanged() {
        updateState();
    }

    private void updateState() {
        Tile tile = getQsTile();
        int foodState = mPrefs.getFoodState();
        Food food = new Food(foodState);
        if (foodState != 0) {
            NekoService.registerJobIfNeeded(this, food.getInterval(this));
        }
        tile.setIcon(food.getIcon(this));
        tile.setLabel(food.getName(this));
        tile.setState(foodState != 0 ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onClick() {
        if (mPrefs.getFoodState() != 0) {
            // there's already food loaded, let's empty it
            MetricsLogger.count(this, "egg_neko_empty_food", 1);
            mPrefs.setFoodState(0);
            NekoService.cancelJob(this);
        } else {
            // time to feed the cats
            if (isLocked()) {
                if (isSecure()) {
                    Log.d(TAG, "startActivityAndCollapse");
                    Intent intent = new Intent(this, NekoLockedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityAndCollapse(intent);
                } else {
                    unlockAndRun(new Runnable() {
                        @Override
                        public void run() {
                            showNekoDialog();
                        }
                    });
                }
            } else {
                showNekoDialog();
            }
        }
    }

    private void showNekoDialog() {
        Log.d(TAG, "showNekoDialog");
        MetricsLogger.count(this, "egg_neko_select_food", 1);
        showDialog(new NekoDialog(this));
    }
}
