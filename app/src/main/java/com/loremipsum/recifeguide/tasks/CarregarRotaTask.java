package com.loremipsum.recifeguide.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.loremipsum.recifeguide.AcessoRest;
import com.loremipsum.recifeguide.MainActivity;
import com.loremipsum.recifeguide.model.Rota;
import com.loremipsum.recifeguide.util.MapaHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class CarregarRotaTask extends AsyncTask<Void, Void, Rota[]> {


    MapaHelper mapaHelper = null;
    private MainActivity activity = null;


    private Rota mRota = null;


    public CarregarRotaTask(MainActivity context) {
        activity = context;
        mapaHelper = new MapaHelper();

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

       /* if (routing != null && routing.getStatus() == Status.RUNNING)
            routing.cancel(true);*/
    }

    @Override
    protected Rota[] doInBackground(Void... voids) {


        try {
            AcessoRest acessoRest = new AcessoRest();
            String json = acessoRest.get("fnConsultaRotas");

            Gson gson = new Gson();
            Rota[] rotas = gson.fromJson(json, Rota[].class);
            return rotas;
        } catch (Exception ex) {
         //   throw ex;
            return null;
        }

    }

    @Override
    protected void onPostExecute(Rota[] rotas) {

        if (rotas != null) {
            MainActivity.listaRotas = new ArrayList<>(Arrays.asList(rotas));
            activity.hideInitProgress();
        }

    }

}
