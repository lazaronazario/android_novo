package com.loremipsum.recifeguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loremipsum.recifeguide.model.Local;

import java.util.ArrayList;

public class PopUpActivity extends AppCompatActivity {
    int intWidith;
    int intHeight;
    ArrayList<Local> mlocais;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.popupExibido = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        MainActivity.popupExibido = true;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        intWidith = dm.widthPixels;
        intHeight = dm.heightPixels;

        getWindow().setLayout((int)(intWidith * 0.7),(int)(intHeight * 0.7));
        Intent i = getIntent();
        Bundle bundle = getIntent().getExtras();
        mlocais =  bundle.getParcelableArrayList("Locais");



        if (mlocais.size() > 1){
            GridView gridview = (GridView) findViewById(R.id.grdView);
            gridview.setAdapter(new ImageAdapter(this,mlocais));

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                    Intent it = new Intent(getBaseContext() ,LocalDetalheActivity2.class);
                     it.putExtra("nome",mlocais.get(position).getNome());
                     it.putExtra("descricao",mlocais.get(position).getDescricao());
                     //it.putExtra("descricao","Ola");
                     //it.putExtra("nome","Eaew");
                     it.putExtra("imagem",mlocais.get(position).getImagem());
                     it.putExtra("horarioAbertura",mlocais.get(position).getHorarioInicio());
                     it.putExtra("horarioFechamento",mlocais.get(position).getHorarioFim());
                     startActivity(it);
            }
        });
        }else{
            Intent it = new Intent(getBaseContext() ,LocalDetalheActivity2.class);
            it.putExtra("nome",mlocais.get(0).getNome());
            it.putExtra("descricao",mlocais.get(0).getDescricao());
            it.putExtra("imagem",mlocais.get(0).getImagem());
            it.putExtra("horarioAbertura",mlocais.get(0).getHorarioInicio());
            it.putExtra("horarioFechamento",mlocais.get(0).getHorarioFim());
            startActivity(it);
        }

    }
}
