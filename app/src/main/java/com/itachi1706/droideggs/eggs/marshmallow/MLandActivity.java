/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itachi1706.droideggs.eggs.marshmallow;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.itachi1706.droideggs.R;
import com.itachi1706.helperlib.helpers.EdgeToEdgeHelper;

/**
 * Created by Kenneth on 7/10/2015.
 * for DroidEggs in package com.itachi1706.droideggs.MarshmallowEgg
 */
public class MLandActivity extends AppCompatActivity {

    MLand mLand;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdgeHelper.setEdgeToEdgeWithContentView(R.id.mland_root, this, R.layout.mland);

        mLand = findViewById(R.id.world);
        mLand.setScoreFieldHolder(findViewById(R.id.scores));
        final View welcome = findViewById(R.id.welcome);
        mLand.setSplash(welcome);
        final int numControllers = mLand.getGameControllers().size();
        if (numControllers > 0) {
            mLand.setupPlayers(numControllers);
        }
    }
    public void updateSplashPlayers() {
        final int N = mLand.getNumPlayers();
        final View minus = findViewById(R.id.player_minus_button);
        final View plus = findViewById(R.id.player_plus_button);
        if (N == 1) {
            minus.setVisibility(View.INVISIBLE);
            plus.setVisibility(View.VISIBLE);
            plus.requestFocus();
        } else if (N == MLand.MAX_PLAYERS) {
            minus.setVisibility(View.VISIBLE);
            plus.setVisibility(View.INVISIBLE);
            minus.requestFocus();
        } else {
            minus.setVisibility(View.VISIBLE);
            plus.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onPause() {
        mLand.stop();
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        mLand.onAttachedToWindow(); // resets and starts animation
        updateSplashPlayers();
        mLand.showSplash();
    }
    public void playerMinus(View v) {
        mLand.removePlayer();
        updateSplashPlayers();
    }
    public void playerPlus(View v) {
        mLand.addPlayer();
        updateSplashPlayers();
    }
    public void startButtonPressed(View v) {
        findViewById(R.id.player_minus_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.player_plus_button).setVisibility(View.INVISIBLE);
        mLand.start(true);
    }

}
