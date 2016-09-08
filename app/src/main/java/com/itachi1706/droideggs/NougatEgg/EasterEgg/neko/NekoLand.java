package com.itachi1706.droideggs.NougatEgg.EasterEgg.neko;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itachi1706.droideggs.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Kenneth on 8/9/2016.
 * for com.itachi1706.droideggs.NougatEgg.EasterEgg.neko in DroidEggs
 */
public class NekoLand extends Activity implements PrefState.PrefsListener {
    public static boolean DEBUG = false;
    public static boolean DEBUG_NOTIFICATIONS = false;
    private static final int STORAGE_PERM_REQUEST = 123;
    private static boolean CAT_GEN = false;
    private PrefState mPrefs;
    private CatAdapter mAdapter;
    private Cat mPendingShareCat;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nougat_neko_activity);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setLogo(Cat.create(this));
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        mPrefs = new PrefState(this);
        mPrefs.setListener(this);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.holder);
        mAdapter = new CatAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        updateCats();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPrefs.setListener(null);
    }
    private void updateCats() {
        Cat[] cats;
        if (CAT_GEN) {
            cats = new Cat[50];
            for (int i = 0; i < cats.length; i++) {
                cats[i] = Cat.create(this);
            }
        } else {
            cats = mPrefs.getCats().toArray(new Cat[0]);
        }
        mAdapter.setCats(cats);
    }
    private void onCatClick(Cat cat) {
        if (CAT_GEN) {
            mPrefs.addCat(cat);
            new AlertDialog.Builder(NekoLand.this)
                    .setTitle("Cat added")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            showNameDialog(cat);
        }
//      noman.notify(1, cat.buildNotification(NekoLand.this).build());
    }
    private void onCatRemove(Cat cat) {
        mPrefs.removeCat(cat);
    }
    private void showNameDialog(final Cat cat) {
        Context context = new ContextThemeWrapper(this, R.style.AppTheme_Dialog_NoActionBar);
        // TODO: Move to XML, add correct margins.
        View view = LayoutInflater.from(context).inflate(R.layout.nougat_edit_text, null);
        final EditText text = (EditText) view.findViewById(android.R.id.edit);
        text.setText(cat.getName());
        text.setSelection(cat.getName().length());
        Drawable catIcon = cat.createLargeIcon(this).loadDrawable(this);
        new AlertDialog.Builder(context)
                .setTitle(" ")
                .setIcon(catIcon)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cat.setName(text.getText().toString().trim());
                        mPrefs.addCat(cat);
                    }
                }).show();
    }
    @Override
    public void onPrefsChanged() {
        updateCats();
    }
    private class CatAdapter extends RecyclerView.Adapter<CatHolder> {
        private Cat[] mCats;
        public void setCats(Cat[] cats) {
            mCats = cats;
            notifyDataSetChanged();
        }
        @Override
        public CatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CatHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.nougat_cat_view, parent, false));
        }
        @Override
        public void onBindViewHolder(final CatHolder holder, int position) {
            Context context = holder.itemView.getContext();
            holder.imageView.setImageIcon(mCats[position].createLargeIcon(context));
            holder.textView.setText(mCats[position].getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCatClick(mCats[holder.getAdapterPosition()]);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.contextGroup.removeCallbacks((Runnable) holder.contextGroup.getTag());
                    holder.contextGroup.setVisibility(View.VISIBLE);
                    Runnable hideAction = new Runnable() {
                        @Override
                        public void run() {
                            holder.contextGroup.setVisibility(View.INVISIBLE);
                        }
                    };
                    holder.contextGroup.setTag(hideAction);
                    holder.contextGroup.postDelayed(hideAction, 5000);
                    return true;
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.contextGroup.setVisibility(View.INVISIBLE);
                    holder.contextGroup.removeCallbacks((Runnable) holder.contextGroup.getTag());
                    onCatRemove(mCats[holder.getAdapterPosition()]);
                }
            });
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cat cat = mCats[holder.getAdapterPosition()];
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        mPendingShareCat = cat;
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                STORAGE_PERM_REQUEST);
                        return;
                    }
                    shareCat(cat);
                }
            });
        }
        @Override
        public int getItemCount() {
            return mCats.length;
        }
    }
    private void shareCat(Cat cat) {
        final File dir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getString(R.string.nougat_directory_name));
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e("NekoLand", "save: error: can't create Pictures directory");
            return;
        }
        final File png = new File(dir, cat.getName().replaceAll("[/ #:]+", "_") + ".png");
        Bitmap bitmap = cat.createBitmap(512, 512);
        if (bitmap != null) {
            try {
                OutputStream os = new FileOutputStream(png);
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
                os.close();
                MediaScannerConnection.scanFile(
                        this,
                        new String[] {png.toString()},
                        new String[] {"image/png"},
                        null);
                Uri uri = Uri.fromFile(png);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, cat.getName());
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent, null));
            } catch (IOException e) {
                Log.e("NekoLand", "save: error: " + e);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == STORAGE_PERM_REQUEST) {
            if (mPendingShareCat != null) {
                shareCat(mPendingShareCat);
                mPendingShareCat = null;
            }
        }
    }
    private static class CatHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final View contextGroup;
        private final View delete;
        private final View share;
        public CatHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(android.R.id.icon);
            textView = (TextView) itemView.findViewById(android.R.id.title);
            contextGroup = itemView.findViewById(R.id.contextGroup);
            delete = itemView.findViewById(android.R.id.closeButton);
            share = itemView.findViewById(android.R.id.shareText);
        }
    }
}
