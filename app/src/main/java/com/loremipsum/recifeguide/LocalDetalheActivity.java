package com.loremipsum.recifeguide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class LocalDetalheActivity extends AppCompatActivity {

    ShareActionProvider mActionProvider;
    private String mNome;
    private String mDescricao;
    private String mImagem;
    private String mHorarioAbertura;
    private String mHorarioFechamento;
    private Double mLatitude;
    private Double mLongitude;
    private Double mLatitudeFinal;
    private Double mLongitudeFinal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_detalhe);

        Intent it = getIntent();

        mNome = it.getStringExtra("nome");
        mDescricao = it.getStringExtra("descricao");
        mImagem = it.getStringExtra("imagem");
        mHorarioAbertura = it.getStringExtra("horarioAbertura");
        mHorarioFechamento = it.getStringExtra("horarioFechamento");

        mLatitude = it.getDoubleExtra("Latitude",0);
        mLongitude = it.getDoubleExtra("Longitude",0);
        mLatitudeFinal = it.getDoubleExtra("LatitudeFinal",0);
        mLongitudeFinal = it.getDoubleExtra("LongitudeFinal",0);

        getSupportActionBar().setTitle(mNome);

            TextView textViewNome = (TextView) findViewById(R.id.nomeLocal);
            TextView textViewDescricao = (TextView) findViewById(R.id.descricaoLocal);
            ImageView imageViewImagem = (ImageView) findViewById(R.id.imagemLocal);
            TextView textViewHorarioAbertura = (TextView) findViewById(R.id.horarioAbertura);
            TextView textViewHorarioFechamento = (TextView) findViewById(R.id.horarioFechamento);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chamarGoogleMaps(mLatitude,mLongitude,mLatitudeFinal,mLongitudeFinal);
                    //chamarGoogleMaps("-8.2042511","-34.9341232","-8.1754226","-34.934184");
                }
            });
            textViewNome.setText(mNome);
            textViewDescricao.setText(mDescricao);

            try {
            textViewHorarioAbertura.setText(mHorarioAbertura.split(" ")[1]);
            textViewHorarioFechamento.setText(mHorarioFechamento.split(" ")[1]);
            }catch(Exception ex){}

            Glide.with(this).load("http://198.245.60.221:8085/Recife_Guide/" + mImagem).into(imageViewImagem);





    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detalhe_activity_menu, menu);

        MenuItem shared = menu.findItem(R.id.action_shared);
        mActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shared);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, mNome);
        mActionProvider.setShareIntent(i);

        return super.onCreateOptionsMenu(menu);
    }*/

    //TODO: Latitude Inicial e Longitude Inicial é o local em que o celular se encontra, e o final é o destino
    public void chamarGoogleMaps(Double p_LatitudeInicial,Double p_LongitudeInicial ,Double p_LatitudeFinal,Double LongitudeFinal){
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?"
                        + "saddr="+ p_LatitudeInicial+","+p_LongitudeInicial + "&daddr="+p_LatitudeFinal+","+LongitudeFinal+""));

        startActivity(intent);
    }

}
