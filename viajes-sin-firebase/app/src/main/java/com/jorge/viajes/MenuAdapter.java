package com.jorge.viajes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

    private final Context context;
    private final String[] labels;
    private final int[] icons;

    public MenuAdapter(Context context, String[] labels, int[] icons) {
        this.context = context;
        this.labels = labels;
        this.icons = icons;
    }

    @Override
    public int getCount() { return labels.length; }

    @Override
    public Object getItem(int position) { return labels[position]; }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.menuText)).setText(labels[position]);
        ((ImageView) convertView.findViewById(R.id.menuIcon)).setImageResource(icons[position]);
        return convertView;
    }
}
