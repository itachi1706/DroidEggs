package com.itachi1706.droideggs.NougatEgg.EasterEgg.neko;

/**
 * Created by Kenneth on 8/9/2016.
 * for com.itachi1706.droideggs.NougatEgg.EasterEgg.neko in DroidEggs
 */
public class NekoTile extends TileService implements PrefsListener {
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
    public void onPrefsChanged() {
        updateState();
    }
    private void updateState() {
        Tile tile = getQsTile();
        int foodState = mPrefs.getFoodState();
        Food food = new Food(foodState);
        tile.setIcon(food.getIcon(this));
        tile.setLabel(food.getName(this));
        tile.setState(foodState != 0 ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }
    @Override
    public void onClick() {
        if (mPrefs.getFoodState() != 0) {
            // there's already food loaded, let's empty it
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
        showDialog(new NekoDialog(this));
    }
}
