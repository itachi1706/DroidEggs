/*
 * Copyright (C) 2015 Kenneth Soh (itachi1706)
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

package com.itachi1706.droideggs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kenneth on 1/6/2015
 * for DroidEggs in package com.itachi1706.droideggs
 */
public class SelectorAdapter extends ArrayAdapter<SelectorObject> {

    private ArrayList<SelectorObject> items;


    public SelectorAdapter(Context context, int textViewResourceId, ArrayList<SelectorObject> objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_selector, parent, false);
        }

        SelectorObject i = items.get(position);

        TextView title = v.findViewById(R.id.tvTitle);
        TextView range = v.findViewById(R.id.tvRange);
        TextView required = v.findViewById(R.id.tvRequired);

        if (title != null){
            title.setText(i.getName());
        }
        if (range != null) {
            //Format: Android %RANGE%
            range.setText("Android " + i.getRange());
        }
        if (required != null) {
            //Format: Requires: NONE/Android %REQUIRED% (SDK %SDKINT%)
            if (i.getMinSDK() == 1)
                required.setText("Requires: None");
            else
                required.setText("Requires: Android " + i.getRequired() + " (SDK " + i.getMinSDK() + ")");
        }

        return v;
    }

    @Override
    public int getCount(){
        return items != null? items.size() : 0;
    }
}
