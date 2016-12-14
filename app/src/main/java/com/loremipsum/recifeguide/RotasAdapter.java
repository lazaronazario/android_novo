package com.loremipsum.recifeguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loremipsum.recifeguide.model.Rota;

import java.util.ArrayList;

/**
 * Created by BREVE DEUS VEM on 21/11/2016.
 */

public class RotasAdapter extends ArrayAdapter<Rota> {

    public RotasAdapter(Context context, ArrayList<Rota> rotas) {
        super(context,0, rotas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            if (getContext() != null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_rota, parent, false);
            }
          }



        Rota rota = getItem(position);
        TextView textView = (TextView) convertView.findViewById(R.id.nome_roteiro);


        textView.setText(rota.getNome());
        return convertView;

    }
}
