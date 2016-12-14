package com.loremipsum.recifeguide.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loremipsum.recifeguide.MainActivity;
import com.loremipsum.recifeguide.R;
import com.loremipsum.recifeguide.model.Local;
import com.loremipsum.recifeguide.model.Rota;

import java.util.ArrayList;

/**
 * Created by Christian.Palmer on 18/11/2016.
 */

public class CarregarRoteiroTask {

    Routing routing = null;

    private MainActivity activity = null;
    private Rota rota = null;
    private Context context = null;
    ProgressDialog progress = null;

    public CarregarRoteiroTask(MainActivity mainActivity, Rota rota, Context taskContext) {
        activity = mainActivity;
        this.rota = rota;
        this.context = taskContext;

        progress = new ProgressDialog(taskContext);
    }

    public void setRota(Rota rota) {
        this.rota = rota;
    }

    public AsyncTask.Status getStatus() {
        return routing.getStatus();

    }

    public void cancelar() {
        if (routing != null) {
            routing.cancel(true);
        }

        if (progress != null) {
            progress.dismiss();
        }
    }

    public void executar(Location localizacaoAtual) {

        progress.setTitle(R.string.loadRota);
        //progress.setMessage("Carregando Rota..");
        progress.setMessage(context.getResources().getString(R.string.loadRotaDois));
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (CarregarRoteiroTask.this.getStatus() == AsyncTask.Status.RUNNING)
                    CarregarRoteiroTask.this.cancelar();
                progress.dismiss();
            }
        });
        progress.show();


        Local[] locais = rota.obterMelhorTrajeto(localizacaoAtual);


        LatLng start = new LatLng(localizacaoAtual.getLatitude(), localizacaoAtual.getLongitude());
        ArrayList<LatLng> waypoints = new ArrayList<>();
        waypoints.add(start);

        if (locais != null) {
            for (int i = 0; i < locais.length - 1; i++) {

                Local local = locais[i];
                LatLng latLng = new LatLng(local.getLat(), local.getLng());
                waypoints.add(latLng);
            }

            waypoints.add(new LatLng(locais[locais.length - 1].getLat(), (locais[locais.length - 1].getLng())));


            Routing.Builder routingBuilder = new Routing.Builder()
                    .travelMode(Routing.TravelMode.WALKING)
                    .withListener(routingListener)
                    .waypoints(waypoints);


            routing = routingBuilder.build();
            routing.execute();
        }
    }

    RoutingListener routingListener = new RoutingListener() {
        @Override
        public void onRoutingFailure(RouteException e) {
            CarregarRoteiroTask.this.cancelar();
        }

        @Override
        public void onRoutingStart() {

        }

        @Override
        public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
            ArrayList<Polyline> polylines = new ArrayList<>();
            final int[] COLORS = new int[]{R.color.colorPrimaryDark, R.color.colorPrimary, R.color.com_facebook_blue, R.color.colorAccent, R.color.primary_dark_material_light};

            if (polylines.size() > 0) {
                for (Polyline poly : polylines) {
                    poly.remove();
                }
            }

            polylines = new ArrayList<>();
            //add route(s) to the map.

            for (int i = 0; i < route.size(); i++) {

                //In case of more than 5 alternative routes
                int colorIndex = i % COLORS.length;

                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(activity.getResources().getColor(COLORS[colorIndex]));
                polyOptions.width(10 + i * 3);
                polyOptions.addAll(route.get(i).getPoints());
                Polyline polyline = activity.map.addPolyline(polyOptions);
                polylines.add(polyline);

                // Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            }

            rota.setPolyLines(polylines);
            activity.startRoute(rota);
            progress.dismiss();
        }

        @Override
        public void onRoutingCancelled() {

            CarregarRoteiroTask.this.cancelar();
        }

    };

}
