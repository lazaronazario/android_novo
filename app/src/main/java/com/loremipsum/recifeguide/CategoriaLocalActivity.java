package com.loremipsum.recifeguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loremipsum.recifeguide.util.CategoriaLocal;

public class CategoriaLocalActivity extends AppCompatActivity implements CliqueiNaCategoriaListener{

    //private ContainerLocais containerLocais;
    //private ArrayList<Local> locais;
    private CategoriaLocaisAdapter mAdapter;
    private GridView gridView;
    private Double mLatitude;
    private Double mLongitude;
    Enum[] categoriaLocal ={
            CategoriaLocal.MUSEU,
            CategoriaLocal.TEATRO,
            CategoriaLocal.MERCADOPUBLICO,
            CategoriaLocal.FEIRALIVRE,
            CategoriaLocal.PONTES,
            CategoriaLocal.COMPRAS,
            CategoriaLocal.SEMCATEGORIA
    };

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] imgsCategoria ={
            R.drawable.museu,
            R.drawable.teatro,
            R.drawable.mercadopublico,
            R.drawable.feiralivre,
            R.drawable.pontes,
            R.drawable.compras,
            R.drawable.semcategoria,
    };

    Intent intentLatLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_local);

        initViews();
    }
        private void initViews() {
        gridView = (GridView) findViewById(R.id.gridview);
        //mAdapter = new CategoriaLocaisAdapter(this, categoriaLocal, imgsCategoria);
        mAdapter = new CategoriaLocaisAdapter(this, categoriaLocal, imgsCategoria);
        gridView.setAdapter(mAdapter);


           gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mAdapter.getItem(position);
                    intentLatLong = getIntent();
                    //mLatitude = intentLatLong.getDoubleExtra("Latitude",0);
                    //mLongitude = intentLatLong.getDoubleExtra("Longitude",0);
                    Enum categoria = categoriaLocal[position];
                    //Toast.makeText(getBaseContext(), "Foi clicado", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(parent.getContext(), MainActivity.class);
                    intent.putExtra("categoriaLocal", categoria.toString());
                    //intent.putExtra("Latitude",mLatitude);
                    //intent.putExtra("Longitude",mLongitude);
                    startActivity(intent);
                }
            });
    }

    @Override
    public void CategoriaFoiClicada(CategoriaLocal categoriaLocal) {

    }
}
