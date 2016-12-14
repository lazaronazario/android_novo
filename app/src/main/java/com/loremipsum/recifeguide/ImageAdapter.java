package com.loremipsum.recifeguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loremipsum.recifeguide.model.Local;
import com.loremipsum.recifeguide.util.AppConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Dannyllo on 18/11/2016.
 */
public class ImageAdapter extends ArrayAdapter<Local> {
    private Context mContext;
    private List<Local> mlocais = new ArrayList<Local>();


    public ImageAdapter(Context c, List<Local> l) {
        super(c, 0, l);
        mlocais = l;
        mContext = c;
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        ViewHolder viewHolder;
        TextView txtView;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_item, parent, false);
            viewHolder = new ViewHolder(convertView);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        txtView = (TextView) convertView.findViewById(R.id.grid_item_title);
        txtView.setText(mlocais.get(position).getNome());
        imageView = (ImageView) convertView.findViewById(R.id.grid_item_image);
        Glide.with(getContext()).load(AppConstants.URL_SISTEMA +  mlocais.get(position).getImagem()).into(imageView);

        return convertView;
    }


    static class ViewHolder {

        public ViewHolder(View parent) {
            ButterKnife.bind(this, parent);
            parent.setTag(this);
        }
    }
}