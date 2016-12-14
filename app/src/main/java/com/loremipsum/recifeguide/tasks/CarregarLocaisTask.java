package com.loremipsum.recifeguide.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.loremipsum.recifeguide.AcessoRest;
import com.loremipsum.recifeguide.MainActivity;
import com.loremipsum.recifeguide.model.ContainerLocais;

import java.util.ArrayList;

/**
 * Created by Suetonio on 27/10/2016.
 */

public class CarregarLocaisTask extends AsyncTask<Void, Void, ContainerLocais> {

    private MainActivity activity = null;

    public CarregarLocaisTask(MainActivity context) {
        this.activity = context;
    }

    @Override
    protected ContainerLocais doInBackground(Void... voids) {

        ContainerLocais containerLocais = null;
        try {
            AcessoRest acessoRest = new AcessoRest();
            String json = acessoRest.get("fnConsultaLocais");

            Gson gson = new Gson();
            containerLocais = gson.fromJson(json, ContainerLocais.class);

        } catch (Exception ex) {
         //   throw ex;
        }
        return containerLocais;
    }

    @Override
    protected void onPostExecute(ContainerLocais containerLocais) {

        if (containerLocais != null) {
            MainActivity.mLocais = new ArrayList<>(containerLocais.locais);
            String locais = null;
            activity.carregarLocaisNoMapa(locais);
            activity.hideInitProgress();
            activity.carregarHashMapLocais();
        }

    }

}
