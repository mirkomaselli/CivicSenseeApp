package it.andriaware.civicsense;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.PropertyValue;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.IOException;


public class DettaglioSegnalazione extends AppCompatActivity {
    private int id, id_utente, id_utente_segnalazione;
    private String ip, token;
    private Retrofit retrofit, retrofit1;
    private IUsersApi iu, iu1;
    private MapView mapView;
    private ProgressBar p;
    private double lat,lon;
    private Tickets ticket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibWlya29tYXNlbGxpIiwiYSI6ImNqdGtjY2k2dzEyNnE0OWxxNDljOXI0aHcifQ.GOnHi-A5A507RprJ9FPS_w");
        setContentView(R.layout.activity_dettaglio_segnalazione);

        p=(ProgressBar) findViewById(R.id.progressBar2);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("mp", MODE_PRIVATE);
        id=pref.getInt("id_segnalazione", 0);
        ip=pref.getString("ip", "non definito");
        id_utente=pref.getInt("user_id",0);
       // id_utente_segnalazione=pref.getInt("id_utente_segnalazione", -1);
        token=pref.getString("token", "anonimo");


        retrofit = new Retrofit.Builder()
                .baseUrl(ip)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        iu= retrofit.create(IUsersApi.class);

        Call<Tickets> call = iu.getTicket(id);
        call.enqueue(new Callback<Tickets>() {
            @Override
            public void onResponse(Call<Tickets> call, final Response<Tickets> response) {
                Tickets tic = response.body();
                if (response.isSuccessful() && response.body() != null) {
                    ticket = response.body();
                    p.setVisibility(View.GONE);
                    String prova = "CDT:" + id + "\nDescrizione: " + response.body().getDescription() + "\n" + getCategoryByID(response.body().getCategory_id()) + "\n" + getPriorityByID(response.body().getPriority()) + "\n" + getStatusByID(response.body().getStatus());
                    TextView t = (TextView) findViewById(R.id.textView);
                    t.setText(prova);
                    lat = response.body().getLatitude();
                    lon = response.body().getLatitude();
                    SharedPreferences.Editor editor;
                    editor = getSharedPreferences("mp", MODE_PRIVATE).edit();
                    editor.putString("photo", response.body().getPhoto());
                    //    editor.putInt("id_utente_segnalazione", ticket.getUser_id());
                    editor.putString("description", ticket.getDescription());
                    editor.putInt("category", ticket.getCategory_id());
                    editor.putInt("priority", ticket.getPriority());
                    editor.apply();

                    id_utente_segnalazione = ticket.getUser_id();

                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                            mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {


                                    style.addImage("marker-icon-id",
                                            BitmapFactory.decodeResource(
                                                    DettaglioSegnalazione.this.getResources(), R.drawable.mapbox_marker_icon_default));


                                    GeoJsonSource geoJsonSource = new GeoJsonSource("source-id", Feature.fromGeometry(
                                            Point.fromLngLat(response.body().getLongitude(), response.body().getLatitude())));
                                    style.addSource(geoJsonSource);


                                    SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
                                    symbolLayer.withProperties(
                                            PropertyFactory.iconImage("marker-icon-id")

                                    );
                                    style.addLayer(symbolLayer);
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(DettaglioSegnalazione.this, "CDT non valido"+response.toString(), Toast.LENGTH_SHORT).show();
                    if(token.equals("anonimo")){
                        startActivity(new Intent(DettaglioSegnalazione.this, LoginActivity.class));
                    }else{
                        startActivity(new Intent(DettaglioSegnalazione.this, MainActivity.class));
                    }


                }
            }


            @Override
            public void onFailure(Call<Tickets> call, Throwable t) {

            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public String getStatusByID(int id){
        String status="";
        switch (id){
            case 0:
            case 1:
                status="Stato: Inviata";
                break;
            case 2:
                status="Stato: Presa in carico";
                break;
            case 3:
                status="Stato: In lavorazione";
                break;
            case 4:
                status="Stato: Completata";
                break;
        }
        return status;
    }

    public String getPriorityByID(int id){
        String status="";
        switch (id){
            case 1:
                status="Priorità: Bassa";
                break;
            case 2:
                status="Priorità: Media";
                break;
            case 3:
                status="Priorità: Alta";
                break;
        }
        return status;
    }

    public String getCategoryByID(int id){
        String status="";
        switch (id){
            case 0:
                status="Categoria: Altro";
                break;
            case 1:
                status="Categoria: Stradale e Aereoportuale";
                break;
            case 2:
                status="Categoria: Ferroviarie e altre linee di trasporto";
                break;
            case 3:
                status="Categoria: Marittime, Laucali e Fluviali";
                break;
            case 4:
                status="Categoria: Idrauliche";
                break;
            case 5:
                status="Categoria: Impianti Elettrici";
                break;
            case 6:
                status="Categoria: Edilizia Pubblica";
                break;
            case 7:
                status="Categoria: Igienico Sanitarie";
                break;
            case 8:
                status="Categoria: Impianti di comunicazione";
                break;
        }
        return status;
    }

    public void foto(View view){
        startActivity(new Intent(DettaglioSegnalazione.this, VisualizzaFotoActivity.class));
    }

    public void gestione(View view) {

        if (id_utente_segnalazione == id_utente && (ticket.getStatus()==0||ticket.getStatus()==1)) {
            final CharSequence[] items = {"Modifica Segnalazione", "Elimina Segnalazione", "Chiudi"};
            AlertDialog.Builder builder = new AlertDialog.Builder(DettaglioSegnalazione.this);
            builder.setTitle("Gestione Segnalazione");

            builder.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (items[i].equals("Modifica Segnalazione")) {
                        startActivity(new Intent (DettaglioSegnalazione.this, ModificaSegnalazione.class));
                    } else if (items[i].equals("Elimina Segnalazione")) {
                        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request newRequest  = chain.request().newBuilder()
                                        .addHeader("Authorization", "Bearer " + token)
                                        .build();
                                return chain.proceed(newRequest);
                            }
                        }).build();

                        retrofit1 = new Retrofit.Builder()
                                .client(client)
                                .baseUrl(ip)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        iu1= retrofit1.create(IUsersApi.class);
                        Call<Void> call = iu1.deletePost(id);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(DettaglioSegnalazione.this, "Segnalazione eliminata correttamente ", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DettaglioSegnalazione.this, MainActivity.class));
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });

                    } else if (items[i].equals("Chiudi")) {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.show();
        }
        else{

            Toast.makeText(DettaglioSegnalazione.this, "Non hai i permessi necessari per accedere a questa funzionalità", Toast.LENGTH_SHORT).show();
        }
    }

}
