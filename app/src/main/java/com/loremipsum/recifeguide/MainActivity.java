package com.loremipsum.recifeguide;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.Gson;
import com.loremipsum.recifeguide.model.Local;
import com.loremipsum.recifeguide.model.Rota;
import com.loremipsum.recifeguide.model.Usuario;
import com.loremipsum.recifeguide.tasks.CarregarLocaisTask;
import com.loremipsum.recifeguide.tasks.CarregarRotaTask;
import com.loremipsum.recifeguide.tasks.CarregarRoteiroTask;
import com.loremipsum.recifeguide.util.AppConstants;
import com.loremipsum.recifeguide.util.ImageHelper;
import com.loremipsum.recifeguide.util.MapaHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String PREF_NAME = "LoginPreference";
    private static final String Login_Google = "G";
    private static final String Login_Facebook = "F";

    String id;
    String nome;
    String email;
    ProfilePictureView ppf;
    TextView txtNome;
    TextView txtEmail;

    String strTipoLogin;
    Uri uriFotoGoogle;
    Usuario usuario;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String retorno;
    String envioJson;
    Gson gson;
    AccessToken accessToken;
    String strVerificado;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    MapFragment mapFragment;
    public GoogleMap map;
    Marker userMarker;
    boolean mRequestingLocationUpdates = false;
    FloatingActionMenu menuFab;
    FloatingActionButton rotaMaisProximaFab;
    FloatingActionButton rotaCheckin;
    public static Rota rotaAtual = null;
    public static ArrayList<Rota> listaRotas = null;
    public static boolean popupExibido = false;

    public static ArrayList<Local> mLocais = null;
    private static HashMap<Integer, Local> mapLocais = null;
    private CarregarRoteiroTask roteiroTask = null;
    ShareActionProvider mActionProvider;
    //Rota mRota = new Rota();
    //public Local[] mLocalRota;

    public static MainActivity self = null;
    //public String nomeLocais;
    //Adicionado callback da SDK do facebook
    CallbackManager callbackManager;
    //Adicionado caixa de dialogo da SDK do facebook
    ShareDialog shareDialog;
    //Variavel que deve carregar latitude e longitudade para ser utilizada no check in
    LatLng latitudelongitude;

    CarregarRotaTask carregarRotasTask = null;
    CarregarLocaisTask carregarLocaisTask = null;
    CarregarRoteiroTask carregarRoteiroTask = null;
    MenuItem menuCompartilhar = null;
    TextView lblNomeRota;
    android.support.design.widget.FloatingActionButton fabStop;
    Toolbar toolbar = null;
    DrawerLayout drawer = null;
    public ProgressDialog progress = null;
    LatLng latLng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        self = this;

        if (savedInstanceState != null) {
            mLocais = (ArrayList<Local>) savedInstanceState.getSerializable("locais");
            listaRotas = (ArrayList<Rota>) savedInstanceState.getSerializable("rotas");


        }

        if (mLocais == null && listaRotas == null) {
            showInitProgress();
            carregarRotas();
        }

        if (mLocais != null && mapLocais == null) {
            carregarHashMapLocais();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        nome = sharedPreferences.getString("NOME", "");
        email = sharedPreferences.getString("EMAIL", "");
        id = sharedPreferences.getString("ID", "");
        strTipoLogin = sharedPreferences.getString("TIPO", "");
        strVerificado = sharedPreferences.getString("VERIFICADO", "");

        menuFab = (FloatingActionMenu) findViewById(R.id.fab);
        rotaMaisProximaFab = (FloatingActionButton) findViewById(R.id.rotaPre);
        rotaCheckin = (FloatingActionButton) findViewById(R.id.FABcheckinFacebook);

        rotaMaisProximaFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (getCurrentLocation() != null) {

                    Rota rota = MapaHelper.ObterRotaMaisProxima(MainActivity.listaRotas, mLastLocation);
                    if (rota == null)
                        return;

                    if (carregarRoteiroTask == null)
                        carregarRoteiroTask = new CarregarRoteiroTask(MainActivity.this, rota, MainActivity.this);
                    else
                        carregarRoteiroTask.setRota(rota);

                    carregarRoteiroTask.executar(mLastLocation);
                } else {
                    Toast.makeText(MainActivity.this, R.string.check_gps, Toast.LENGTH_LONG).show();
                }
            }

        });


        rotaCheckin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                shareDialog = new ShareDialog(MainActivity.this);

                callbackManager = CallbackManager.Factory.create();

                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(MainActivity.this, R.string.checkin_realizado, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, R.string.checkin_cancelado, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException e) {
                        e.printStackTrace();
                    }
                });

                SharedLocation(shareDialog);
            }

        });


        /*
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        strTipoLogin = sharedPreferences.getString("TIPO", "");
        strVerificado = sharedPreferences.getString("Verificado", "");
        */

        //carregarRotas();

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        lblNomeRota = (TextView) findViewById(R.id.lblNomeRota);
        fabStop = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fabStop);

        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRoute();
            }
        });





    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putSerializable("locais", mLocais);
        outState.putSerializable("rotas", listaRotas);

    }

    private void showInitProgress() {
        progress = new ProgressDialog(this);
        progress.setTitle(R.string.progress_titulo);
        progress.setMessage(getString(R.string.progress_descricao));
        progress.setCancelable(false);
        progress.show();
    }

    public void hideInitProgress() {

        if (mLocais != null && listaRotas != null) {
            progress.dismiss();
        }

    }

    private void carregarRotas() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (carregarRotasTask == null)
            carregarRotasTask = new CarregarRotaTask(this);

        if (networkInfo != null && networkInfo.isConnected()) {
            carregarRotasTask.execute();
        } else {
            Toast.makeText(this, R.string.sem_conexao, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        getLastLocation();

        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }


    static public Location getCurrentLocation() {
        return self.mLastLocation;
    }

    private Location getLastLocation() {

        Location location = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    AppConstants.REQUEST_FINE_LOCATION);

            return null;
        }

        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        return location;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        /*
        sharedPreferences = RecifeGuideApp.getApplication().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        editor = sharedPreferences.edit();
        strTipoLogin = sharedPreferences.getString("TIPO", "");
        strVerificado = sharedPreferences.getString("Verificado", "");
        */

        if (strTipoLogin.equals(Login_Facebook)) {
            if (AccessToken.getCurrentAccessToken() == null) {
                goLoginScreen();
            }
            // nome = sharedPreferences.getString("NOME", "");
            //email = sharedPreferences.getString("EMAIL", "");
            //id = sharedPreferences.getString("ID", "");

            txtNome = (TextView) findViewById(R.id.meu_nome);
            txtNome.setText(nome);

            txtEmail = (TextView) findViewById(R.id.meu_email);
            txtEmail.setText(email);

            ProfilePictureView ppf = (ProfilePictureView) findViewById(R.id.fbImage);
            ppf.setVisibility(View.VISIBLE);
            ppf.setProfileId(id);

            if (strVerificado.equals("OK") == false) {
                new enviarUsuarioTask().execute();
                //Log.d("enviarUsuario","TESTE");
            }

            //txtNome = (TextView) findViewById(R.id.nome);
            //txtNome.setText("Bem Vindo " + nome );
        } else if (strTipoLogin.equals(Login_Google)) {
            // nome = getIntent().getStringExtra("NOME");
            //email = getIntent().getStringExtra("EMAIL");
            //uriFotoGoogle = Uri.parse(sharedPreferences.getString("FOTO", ""));

            txtNome = (TextView) findViewById(R.id.meu_nome);
            txtNome.setText(nome);

            txtEmail = (TextView) findViewById(R.id.meu_email);
            txtEmail.setText(email);


            new DownloadImageTask((ImageView) findViewById(R.id.gglImage))
                    .execute(sharedPreferences.getString("FOTO", ""));

            if (strVerificado.equals("OK") == false) {
                new enviarUsuarioTask().execute();
                //Log.d("enviarUsuario","TESTE");
            }
            //txtNome = (TextView) findViewById(R.id.nome);
            //txtNome.setText("Bem Vindo " + nome + "   " + email);

        }


        menuCompartilhar = menu.findItem(R.id.compartilhar_rota);
        mActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuCompartilhar);
        //setShareIntent(createShareIntent());

        menuCompartilhar.setVisible(false);
        //opcao_compatilhar_rota(menu);
        return true;

    }

    private void setShareIntent(Intent shareIntent) {
        if (mActionProvider != null) {
            mActionProvider.setShareIntent(shareIntent);
        }
    }


    private Intent createShareIntent() {
        String nomes = "";
        ArrayList<Integer> idRotas = new ArrayList<>();

        for (int i = 0; i < rotaAtual.getListaLocais().size(); i++) {
            idRotas.add(rotaAtual.getListaLocais().get(i).getId());
        }

        for (int i = 0; i < mLocais.size(); i++) {
            if (idRotas.contains(mLocais.get(i).getId())) {
                nomes += "#" + mLocais.get(i).getNome() + " ";
            }
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        i.setType("text/plain");
        //i.putExtra(Intent.EXTRA_TEXT, "Estou passando por: " + nomes + ".\n Aproveite e conheça esse e outros lugares, usando o Recife Guide. Já disponível no seu GooglePlay");
        i.putExtra(Intent.EXTRA_TEXT, String.format(getBaseContext().getResources().getString(R.string.texto_do_compartilhar), nomes));
        return i;
    }


    protected void onStart() {

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }


    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.compartilhar_rota) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.home:
                Intent tela_home = new Intent(this, MainActivity.class);
                startActivity(tela_home);
                break;
            case R.id.rotas_turisticas:
                Intent tela_roteiro = new Intent(this, RotasActivity.class);
                startActivity(tela_roteiro);

                break;
            case R.id.outros_destinos:

                Intent i = new Intent(this, CategoriaLocalActivity.class);
                //i.putExtra("Latitude", mLastLocation.getLatitude());
                //i.putExtra("Longitude", mLastLocation.getLongitude());
                startActivity(i);


                break;
            case R.id.logout:
                this.logout();

                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void enviarUsuario() {
        try {
            usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setSocialId(id);
            usuario.setEmail(email);
            gson = new Gson();
            envioJson = gson.toJson(usuario);
            AcessoRest acessoRest = new AcessoRest();
            retorno = acessoRest.get("fnInserirUsuario/" + usuario.getNome() + "," + usuario.getEmail() + "," + usuario.getSocialId());
            editor.putString("VERIFICADO", "OK");
            editor.commit();
        } catch (Exception e) {

        }
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout() {
        //logout facebook
        LoginManager.getInstance().logOut();

        /*
        //logout google
        if (LoginActivity.mGoogleApiClient != null && LoginActivity.mGoogleApiClient.isConnected())
        Auth.GoogleSignInApi.signOut(LoginActivity.mGoogleApiClient);*/

        //SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences sharedPreferences = RecifeGuideApp.getApplication().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();

        goLoginScreen();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMinZoomPreference(5);
        map.setMaxZoomPreference(25);

        String locais = null;
        Bundle extras = getIntent().getExtras();
        try{
            if (extras != null) {
                locais =  extras.getString("categoriaLocal");
                carregarLocaisNoMapa( locais );
                return;
            }
        }catch (Exception e){
            Log.v("intent", e.getMessage());
        }

        if (mLocais == null) {
            carregarLocais();
        } else {
            carregarLocaisNoMapa(locais);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppConstants.REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    Location location = getLastLocation();

                    if (location != null) {
                        updateLocation(location);
                    }

                    if (!mRequestingLocationUpdates) {

                        startLocationUpdates();
                    }
                } else {

                    Toast.makeText(this, R.string.permissao_localizacao, Toast.LENGTH_LONG);
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    AppConstants.REQUEST_FINE_LOCATION);

        }
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 1000)        // 5 seconds, in milliseconds
                .setFastestInterval(5 * 1000); // 1 second, in milliseconds

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        mRequestingLocationUpdates = true;
    }

    public void updateLocation(Location location) {

        mLastLocation = location;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker == null) {
            userMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.me))
                    .icon(BitmapDescriptorFactory.fromBitmap(ImageHelper.resizeMapIcon(this, "ic_user", 100, 100))));
            updateCamera(latLng);
        } else {
            userMarker.setPosition(latLng);
        }

          if (rotaAtual != null && latLng.latitude != mLastLocation.getLatitude() && latLng.longitude != mLastLocation.getLongitude()) {

        //if (rotaAtual != null) {
            updateCamera(latLng);

            List<Local> locaisProximos = rotaAtual.obterLocaisProximos(mLastLocation);

            List<Local> locaisComDetalhes = new ArrayList<>();

            for (Local localProximo : locaisProximos)
            {
                locaisComDetalhes.add(mapLocais.get(localProximo.getId()));
            }

            if (locaisComDetalhes.size() > 0) {
                //TODO: DANYLLO, CHAMA O ABRIR POPUP AQUI :)
                ArrayList<Local> arrayIntent = new ArrayList<>(locaisComDetalhes);

                if (!popupExibido) {
                    popupExibido = true;
                    Intent itPopUp = new Intent(this, PopUpActivity.class);
                    itPopUp.putParcelableArrayListExtra("Locais", arrayIntent);
                    startActivity(itPopUp);
                }
            }
        }


    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    //Metodo para executar o compartilhamento
    private void SharedLocation(ShareDialog shareDialog) {

        //Classe interna de task (cria thread secundaria)
        Shared sharedtask = new Shared();
        //Verifica se a latitude e longitude ja foi carregada, caso nao tenha sido apresenta toast e nao continua a execução.
        /*if (latitudelongitude == null) {
            Toast.makeText(getApplicationContext(), "Aguarde enquanto o GPS atualiza sua localização", Toast.LENGTH_SHORT).show();
        }*/
        //Execuya a thread passando latitude e longitude

        try {
            sharedtask.execute(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
        } catch (Exception e) {
            Toast.makeText(this, R.string.check_gps, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void updateCamera(LatLng latLng) {

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        map.moveCamera(cameraUpdate);
    }

    public void startRoute(Rota rota) {

        if (mLastLocation != null)
            updateLocation(mLastLocation);

        rotaAtual = rota;
        menuFab.close(true);
        //  progress.dismiss();


        lblNomeRota.setText(rota.getNome());
        menuCompartilhar.setVisible(true);

        menuFab.setVisibility(View.GONE);
        fabStop.setVisibility(View.VISIBLE);
        lblNomeRota.setVisibility(View.VISIBLE);

        setShareIntent(createShareIntent());
        //Toast.makeText(getApplicationContext(), " Rota iniciada!", Toast.LENGTH_SHORT).show();
    }

    public void stopRoute() {

        menuFab.setVisibility(View.VISIBLE);
        fabStop.setVisibility(View.GONE);
        lblNomeRota.setVisibility(View.GONE);
        menuCompartilhar.setVisible(false);


        if (rotaAtual.getPolyLines() != null) {
            for (Polyline line : rotaAtual.getPolyLines()) {
                line.remove();
            }
}
rotaAtual = null;
        }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        if (location != null)
            updateLocation(location);

    }

    private void carregarLocais() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        carregarLocaisTask = new CarregarLocaisTask(this);

        if (networkInfo != null && networkInfo.isConnected()) {

            carregarLocaisTask.execute();
        } else {
            Toast.makeText(this, R.string.sem_conexao, Toast.LENGTH_LONG).show();
        }

    }

    public void carregarLocaisNoMapa(String locais) {
        for (Local local : mLocais) {

            if(locais != null && !locais.isEmpty()){
                if(!local.getCategoriaLocal().name().equals(locais)){
                    continue;
                }
            }

            LatLng latLng = new LatLng(local.getLat(), local.getLng());

            Marker localMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(local.getNome()));

            localMarker.setIcon(BitmapDescriptorFactory.fromBitmap(ImageHelper.resizeMapIcon(this, local.getCategoriaLocal().toString().toLowerCase(), 50, 50)));
            localMarker.setPosition(latLng);

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setVisibility(View.VISIBLE);
            bmImage.setImageBitmap(result);
        }
    }


    private class Shared extends AsyncTask<String, Void, JSONObject> {

        //A execução do compartilhamento começa quando busca os lugares mais proximos da posição atual
        protected JSONObject doInBackground(String... latlong) {

            String acessotoken = "EAAFDnRkudF8BAPZBep4aiV0ZClnjf9UhS29LfZAWKcpahpmHkv4Xh0AdttxbUPKx0D6IdpZBJ6bhcZBFQywVNsrQ9fJTaYa14wRSXDR3mfjFytlu1a0xfZAk1DTriYaAO1p02Ya0nIpBN6pjRMZBy4TWoNfpwwXIz8ZD";
            String ditancia = "1000";
            JSONObject retorno = null;
            //Busca direto da API facebook
            String urldisplay = "https://graph.facebook.com/search?type=place&center=" + latlong[0] + "," + latlong[1] + "&distance=" + ditancia + "&access_token=" + acessotoken;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(urldisplay);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                urlConnection.connect();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String line;
                StringBuffer sb = new StringBuffer();

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                retorno = new JSONObject(sb.toString());

            } catch (IOException e) {

                Log.e("Error", e.getMessage());

            } catch (Exception e) {

                Log.e("Error", e.getMessage());
                e.printStackTrace();

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return retorno;
        }

        protected void onPostExecute(JSONObject result) {

            //Ao completar o processo, pega o primeiro local utilizando seu ID place, e aplica ao compartilhamento
            try {
                JSONArray jsonArray = result.getJSONArray("data");
                Log.e("retorno", result.toString());

                String placeid = null;
                //Apos achar o primeiro ID, o loop é parado.
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    placeid = jsonObject.getString("id");
                    break;
                }

                //Verifica se pode mostrar a caixa de dialogo
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    //A função do compartilhamento em se
                    ShareContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(getString(R.string.checkin_titulo_sharecontent))
                            .setPlaceId(placeid)
                            .setContentDescription(getString(R.string.checkin_descricao_sharecontent))
                            .setContentUrl(Uri.parse(getString(R.string.checkin_url_recife_guide)))
                            .build();
                    shareDialog.show(linkContent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class enviarUsuarioTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {

            enviarUsuario();
            return null;
        }
    }


    public static  void carregarHashMapLocais()
    {
        mapLocais = new HashMap<>();

        for (Local local : mLocais) {
            mapLocais.put(local.getId(), local);
        }

    }
}
