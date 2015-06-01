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

        TextView title = (TextView) v.findViewById(R.id.tvTitle);
        TextView range = (TextView) v.findViewById(R.id.tvRange);
        TextView required = (TextView) v.findViewById(R.id.tvRequired);

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
