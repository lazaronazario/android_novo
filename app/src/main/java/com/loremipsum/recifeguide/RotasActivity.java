package com.loremipsum.recifeguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loremipsum.recifeguide.model.Rota;
import com.loremipsum.recifeguide.model.Usuario;
import com.loremipsum.recifeguide.tasks.CarregarRoteiroTask;
import com.loremipsum.recifeguide.util.VisibilidadeRota;

import java.util.ArrayList;

import static com.loremipsum.recifeguide.MainActivity.getCurrentLocation;

/**
 * Created by BREVE DEUS VEM on 21/11/2016.
 */

public class RotasActivity extends AppCompatActivity {

    private ArrayList<Rota> mRoteiro = MainActivity.listaRotas;
    static ListView mListView;
    private RotasAdapter mAdapter;
    private ArrayList<Rota> mRoteiroFiltrados = new ArrayList<>();
    VisibilidadeRota visibilidadeRota;
    Usuario usuario;
    private static final String PREF_NAME = "LoginPreference";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String strTipoLogin;
    String strVerificado;
    String id;
    String nome;
    String email;

    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotas);
        getSupportActionBar().setTitle(R.string.lista_roteiros);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        nome = sharedPreferences.getString("NOME", "");
        email = sharedPreferences.getString("EMAIL", "");
        id = sharedPreferences.getString("ID", "");
        strTipoLogin = sharedPreferences.getString("TIPO", "");
        strVerificado = sharedPreferences.getString("VERIFICADO", "");

        carregarLocais();

        initViews();


    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.list_view_roteiro);
        mAdapter = new RotasAdapter(this, mRoteiroFiltrados);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (getCurrentLocation() != null) {
                    Rota rota = mRoteiroFiltrados.get(i);

                    final CarregarRoteiroTask task = new CarregarRoteiroTask(MainActivity.self, rota, MainActivity.self);

                    task.executar(MainActivity.self.mLastLocation);
                    RotasActivity.this.finish();
                } else {
                    Toast.makeText(RotasActivity.this, R.string.check_gps, Toast.LENGTH_LONG).show();
                }


                //Intent it = new Intent(getBaseContext(), MainActivity.class);
                //startActivity(it);
            }
        });

    }

    private void carregarLocais() {

        if (mRoteiro != null) {
            for (Rota rota : mRoteiro) {
                if (rota.getVisibilidadeRota().valor == 0) {
                    mRoteiroFiltrados.add(rota);
                } else if ((rota.getVisibilidadeRota().valor == 1 && (rota.getUsuario().getEmail().equals(email)))) {
                    mRoteiroFiltrados.add(rota);
                }
            }
            // mAdapter.notifyDataSetChanged();
        }
    }

}
