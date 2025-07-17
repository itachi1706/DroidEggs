/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.itachi1706.droideggs.eggs.nougat.easter_egg.neko;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itachi1706.droideggs.FirebaseLogger;
import com.itachi1706.droideggs.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 8/9/2016.
 * for com.itachi1706.droideggs.NougatEgg.EasterEgg.neko in DroidEggs
 */
@TargetApi(Build.VERSION_CODES.M)
public class NekoLand extends Activity implements PrefState.PrefsListener {
    public static final String CHAN_ID = "EGG";

    public static final boolean DEBUG = false;
    public static final boolean DEBUG_NOTIFICATIONS = false;

    private static final int EXPORT_BITMAP_SIZE = 600;

    private static final boolean CAT_GEN = false;
    private PrefState mPrefs;
    private CatAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.nougat_neko_activity);
        View rootView = findViewById(R.id.nougat_neko_root);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setLogo(Cat.create(this));
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mPrefs = new PrefState(this);
        mPrefs.setListener(this);
        final RecyclerView recyclerView = findViewById(R.id.holder);
        mAdapter = new CatAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        int numCats = updateCats();
        FirebaseLogger.INSTANCE.histogram(this, "egg_neko_visit_gallery", numCats);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPrefs.setListener(null);
    }

    private int updateCats() {
        Cat[] cats;
        if (CAT_GEN) {
            cats = new Cat[50];
            for (int i = 0; i < cats.length; i++) {
                cats[i] = Cat.create(this);
            }
        } else {
            final float[] hsv = new float[3];
            List<Cat> list = mPrefs.getCats();
            Collections.sort(list, (cat, cat2) -> {
                Color.colorToHSV(cat.getBodyColor(), hsv);
                float bodyH1 = hsv[0];
                Color.colorToHSV(cat2.getBodyColor(), hsv);
                float bodyH2 = hsv[0];
                return Float.compare(bodyH1, bodyH2);
            });
            cats = list.toArray(new Cat[0]);
        }
        mAdapter.setCats(cats);
        return cats.length;
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
    }

    private void onCatRemove(Cat cat) {
        cat.logRemove(this);
        mPrefs.removeCat(cat);
    }

    private void showNameDialog(final Cat cat) {
        final Context context = new ContextThemeWrapper(this,
                android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(context).inflate(R.layout.nougat_edit_text, null);
        final EditText text = view.findViewById(android.R.id.edit);
        text.setText(cat.getName());
        text.setSelection(cat.getName().length());
        final int size = context.getResources()
                .getDimensionPixelSize(android.R.dimen.app_icon_size);
        Drawable catIcon = cat.createIcon(size, size).loadDrawable(this);
        new AlertDialog.Builder(context)
                .setTitle(" ")
                .setIcon(catIcon)
                .setView(view)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    cat.logRename(context);
                    cat.setName(text.getText().toString().trim());
                    mPrefs.addCat(cat);
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

        private void setContextGroupVisible(final CatHolder holder, boolean vis) {
            final View group = holder.contextGroup;
            if (vis && group.getVisibility() != View.VISIBLE) {
                group.setAlpha(0);
                group.setVisibility(View.VISIBLE);
                group.animate().alpha(1.0f).setDuration(333);
                Runnable hideAction = () -> setContextGroupVisible(holder, false);
                group.setTag(hideAction);
                group.postDelayed(hideAction, 5000);
            } else if (!vis && group.getVisibility() == View.VISIBLE) {
                group.removeCallbacks((Runnable) group.getTag());
                group.animate().alpha(0f).setDuration(250).withEndAction(() -> group.setVisibility(View.INVISIBLE));
            }
        }

        @Override
        public void onBindViewHolder(final CatHolder holder, int position) {
            Context context = holder.itemView.getContext();
            final int size = context.getResources().getDimensionPixelSize(R.dimen.neko_display_size);
            holder.imageView.setImageIcon(mCats[position].createIcon(size, size));
            holder.textView.setText(mCats[position].getName());
            holder.itemView.setOnClickListener(v -> onCatClick(mCats[holder.getAdapterPosition()]));
            holder.itemView.setOnLongClickListener(v -> {
                setContextGroupVisible(holder, true);
                return true;
            });
            holder.delete.setOnClickListener(v -> {
                setContextGroupVisible(holder, false);
                new AlertDialog.Builder(NekoLand.this)
                        .setTitle(getString(R.string.confirm_delete, mCats[position].getName()))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> onCatRemove(mCats[holder.getAdapterPosition()]))
                        .show();
            });
            holder.share.setOnClickListener(v -> {
                setContextGroupVisible(holder, false);
                Cat cat = mCats[holder.getAdapterPosition()];
                shareCat(cat);
            });
        }

        @Override
        public int getItemCount() {
            return mCats.length;
        }
    }

    private void shareCat(Cat cat) {
        Bitmap bitmap = cat.createBitmap(EXPORT_BITMAP_SIZE, EXPORT_BITMAP_SIZE);
        if (bitmap != null) {
            String filename = cat.getName().replaceAll("[/ #:]+", "_");
            String uriString = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, filename, "Android EasterEgg Neko " + cat.getName());
            if (uriString != null) {
                Uri uri = Uri.parse(uriString);
                Log.v("Neko", "cat uri: " + uri);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, cat.getName());
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setType("image/jpeg");
                startActivity(Intent.createChooser(intent, null));
                cat.logShare(this);
            } else Log.e("NekoLand", "error saving to media store");
        } else Log.e("NekoLand", "error generating bitmap");
    }

    private static class CatHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final View contextGroup;
        private final View delete;
        private final View share;

        public CatHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(android.R.id.icon);
            textView = itemView.findViewById(android.R.id.title);
            contextGroup = itemView.findViewById(R.id.contextGroup);
            delete = itemView.findViewById(android.R.id.closeButton);
            share = itemView.findViewById(R.id.shareText);
        }
    }
}
