package com.itachi1706.droideggs.NougatEgg.EasterEgg.neko;

/**
 * Created by Kenneth on 8/9/2016.
 * for com.itachi1706.droideggs.NougatEgg.EasterEgg.neko in DroidEggs
 */
public class PrefState implements OnSharedPreferenceChangeListener {
    private static final String FILE_NAME = "mPrefs";
    private static final String FOOD_STATE = "food";
    private static final String CAT_KEY_PREFIX = "cat:";
    private final Context mContext;
    private final SharedPreferences mPrefs;
    private PrefsListener mListener;
    public PrefState(Context context) {
        mContext = context;
        mPrefs = mContext.getSharedPreferences(FILE_NAME, 0);
    }
    // Can also be used for renaming.
    public void addCat(Cat cat) {
        mPrefs.edit()
                .putString(CAT_KEY_PREFIX + String.valueOf(cat.getSeed()), cat.getName())
                .commit();
    }
    public void removeCat(Cat cat) {
        mPrefs.edit()
                .remove(CAT_KEY_PREFIX + String.valueOf(cat.getSeed()))
                .commit();
    }
    public List<Cat> getCats() {
        ArrayList<Cat> cats = new ArrayList<>();
        Map<String, ?> map = mPrefs.getAll();
        for (String key : map.keySet()) {
            if (key.startsWith(CAT_KEY_PREFIX)) {
                long seed = Long.parseLong(key.substring(CAT_KEY_PREFIX.length()));
                Cat cat = new Cat(mContext, seed);
                cat.setName(String.valueOf(map.get(key)));
                cats.add(cat);
            }
        }
        return cats;
    }
    public int getFoodState() {
        return mPrefs.getInt(FOOD_STATE, 0);
    }
    public void setFoodState(int foodState) {
        mPrefs.edit().putInt(FOOD_STATE, foodState).commit();
    }
    public void setListener(PrefsListener listener) {
        mListener = listener;
        if (mListener != null) {
            mPrefs.registerOnSharedPreferenceChangeListener(this);
        } else {
            mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mListener.onPrefsChanged();
    }
    public interface PrefsListener {
        void onPrefsChanged();
    }
}
