package com.myapp.handbook;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by SAshutosh on 3/24/2016.
 */
public class CustomNavAdapter extends ArrayAdapter<String> {
    private final Activity _context;
    private final String[] _text;
    private final Integer[] _imageId;

    public CustomNavAdapter(Activity context, String[] text, Integer[] imageId) {
        super(context, R.layout.nav_drawer_item, text);
        this._context = context;
        this._text = text;
        this._imageId = imageId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = _context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.nav_drawer_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        txtTitle.setText(_text[position]);
        imageView.setImageResource(_imageId[position]);

        return rowView;
    }

}