package com.example.omer.project_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SimpleViewAdapterLV extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> items;
    private final List<Bitmap> logo;

    public SimpleViewAdapterLV(Context context, List<String> items, List<Bitmap> logo) {
        super(context, R.layout.simple_listview, items);
        this.context = context;
        this.items = items;
        this.logo = logo;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowview = inflater.inflate(R.layout.simple_listview, null, true);
        TextView title = (TextView) rowview.findViewById(R.id.simplels_txt);
        ImageView image = (ImageView) rowview.findViewById(R.id.simplels_img);
        title.setText(items.get(position));
        image.setImageBitmap(logo.get(position));
        return rowview;
    }
}
