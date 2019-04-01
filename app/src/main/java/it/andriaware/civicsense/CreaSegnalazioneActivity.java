package it.andriaware.civicsense;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.VISIBLE;

public class CreaSegnalazioneActivity extends AppCompatActivity implements LocationListener {

    private String descrizione;
    private String displayName="0";
    private TextView textViewIndirizzo, editTextDescrizione;
    private LocationManager locationManager;
    private Indirizzo address;
    private int priority;
    private int category;
    private int flagPosizione;
    private double lat = 0;
    private double lon = 0;
    private String comune = "";
    private String county="";
    private NominatimReverse nominatim1;
    private final int IMG_REQUEST =1;
    private final int IMG_REQUEST_ALBUM=2;
    private Spinner priorità, categorie;
    private Bitmap bitmap;
    private Uri path;

    private ImageView img;

    private IUsersApi iu;
    private String  token, ip;
    private Segnalazione segnalazione;
    private String image="";

    private Retrofit retrofit;

    private JSONArray jobj;
    private HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_segnalazione);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SharedPreferences pref = getSharedPreferences("mp", MODE_PRIVATE);
        ip=pref.getString("ip", "No name defined");
        token=pref.getString("token", "anonimo");

        Gson gson = new GsonBuilder().serializeNulls().create();

        if(!token.equals("anonimo")){
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request newRequest  = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(newRequest);
                }
            }).build();

            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(ip)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }else {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ip)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        iu= retrofit.create(IUsersApi.class);

        //set spinner priorità
        priorità = (Spinner) findViewById(R.id.spinnerGravitaSegnala);

        List<String> gravità = new ArrayList<>();
        gravità.add(0, "Gravità");
        gravità.add(1, "Lieve");
        gravità.add(2, "Media");
        gravità.add(3, "Alta");

        ArrayAdapter<String> dataAdapterGravità;
        dataAdapterGravità = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gravità);
        dataAdapterGravità.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorità.setAdapter(dataAdapterGravità);

        priorità.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priority = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //set spinner categorie
        categorie = (Spinner) findViewById(R.id.spinnerCategoriaSegnala);

        List<String> categoria = new ArrayList<>();
        categoria.add(0, "Categoria");
        categoria.add(1, "Stradale e Aereoportuale");
        categoria.add(2, "Ferroviarie e altre linee di trasporto");
        categoria.add(3, "Marittime, Lacuali e Fluviali");
        categoria.add(4, "Idrauliche");
        categoria.add(5, "Impianti Elettrici");
        categoria.add(6, "Edilizia Pubblica");
        categoria.add(7, "Igienico Sanitarie");
        categoria.add(8, "Impianti di comunicazione");
        categoria.add(9, "Altro");

        ArrayAdapter<String> dataAdapterCategoria;
        dataAdapterCategoria = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categoria);
        dataAdapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorie.setAdapter(dataAdapterCategoria);
        categorie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        flagPosizione=0;
        nominatim1 = new NominatimReverse();
        CheckPermission();
        getLocation();
    }

    public void posizione(View view){
        flagPosizione = 0;
        CheckPermission();
        getLocation();
    }

    public void onResume(){
        super.onResume();

    }

    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    public void onLocationChanged(Location location){

        if(flagPosizione==0) {
            if(location.getAccuracy()<100) {
                flagPosizione = 1;
                textViewIndirizzo = (TextView) findViewById(R.id.editTextIndirizzoSegnala);
                lat = location.getLatitude();
                lon = location.getLongitude();
                address = nominatim1.getAdress(lat, lon);
              //  address=nominatim1.getAdress(41.318646,16.279617);
                comune = address.getCity();
                county = address.getCounty();
                displayName = address.getDisplayName();
                textViewIndirizzo.setText(displayName);
            }
        }

    }

    public void onProviderDisabled(String provider) {
        Toast.makeText(CreaSegnalazioneActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider!" + provider,
                Toast.LENGTH_SHORT).show();
    }

    public void segnala (final View view) throws JSONException {
        editTextDescrizione = (EditText) findViewById(R.id.editTextDrescrizioneSegnala);
        descrizione = editTextDescrizione.getText().toString();

        if(descrizione.isEmpty()){
            Toast.makeText(this, "Inserisci una descrizione",
                    Toast.LENGTH_SHORT).show();
        }
        else if(category==0){
            Toast.makeText(this, "Inserisci una categoria",
                    Toast.LENGTH_SHORT).show();
        }
        else if(flagPosizione==0){
            Toast.makeText(this, "Clicca sul pulsante per la geolocalizzazione e attendi che esca il nome della via.",
                    Toast.LENGTH_SHORT).show();
        }
        else if(image.isEmpty()){
            Toast.makeText(this, "Carica un immagine",
                    Toast.LENGTH_SHORT).show();
        }
        else if(priority==0){
            Toast.makeText(this, "Inserisci una priorità",
                    Toast.LENGTH_SHORT).show();
        }
        else{

            //Prova mail
            jobj=new JSONArray(loadJSONFromAsset(this));
            map = new HashMap<>();
            for(int i=0; i<jobj.length(); i++){
                JSONObject c = jobj.getJSONObject(i);
                map.put(c.getString("Comune"),c.getString("Email"));
            }
            //Fine prova

            setContentView(R.layout.splash);
            if(category==9){ category=0;}

            segnalazione = new Segnalazione(descrizione, displayName, lat, lon, priority, image, comune, county, category);

            Call<Segnalazione> call = iu.creaSegnalazione(segnalazione);

            call.enqueue(new Callback<Segnalazione>() {
                @Override
                public void onResponse(Call<Segnalazione> call, Response<Segnalazione> response) {
                    Segnalazione segRes = response.body();
                    if(response.isSuccessful()&&segRes!=null){
                        Toast.makeText(CreaSegnalazioneActivity.this, "Segnalazione inviata correttamente  ", Toast.LENGTH_SHORT).show();
                        if(token.equals("anonimo")){
                            startActivity(new Intent(CreaSegnalazioneActivity.this, LoginActivity.class));
                        }
                        else{
                            startActivity(new Intent(CreaSegnalazioneActivity.this, MainActivity.class));
                        }
                    }
                    else {

                        final CharSequence[] items={"Si","No"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreaSegnalazioneActivity.this);
                        builder.setTitle("Comune non iscritto al sistema, vuoi inviare una mail?");

                        builder.setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (items[i].equals("Si")) {

                                    String mail = map.get(comune);
                                    String subject="Segnalazione problematica cittadina tramite CivicSense";
                                    String text="La seguente mail è stata generata in automatico dal sistema CivicSense, per segnalare un disservizio nel suo comune.\n\nDescrizione segnalazione: " +
                                            descrizione+"\n\nIndirizzo: "+displayName+"\n\n"+ getPriorityByID(priority) + "\n\n"+ getCategoryByID(category) +"\n\nPer maggiori informazioni riguardanti il nostro servizio visiti il sito civicsense.dev\n\nCordiali saluti,\nCivicSense by AndriaWare";

                                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                            "mailto",mail, null));

                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, mail); // String[] addresses
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                    emailIntent.putExtra(Intent.EXTRA_TEXT,text );
                                    startActivity(Intent.createChooser(emailIntent, "Send email..."));

                                }else if(items[i].equals("No")){

                                    if(token.equals("anonimo")){
                                        startActivity(new Intent(CreaSegnalazioneActivity.this, LoginActivity.class));
                                    }
                                    else{
                                        startActivity(new Intent(CreaSegnalazioneActivity.this, MainActivity.class));
                                    }
                                }
                            }
                        });
                        builder.show();
                    }
                }

                @Override
                public void onFailure(Call<Segnalazione> call, Throwable t) {
                    Toast.makeText(CreaSegnalazioneActivity.this,
                            "Response is " , Toast.LENGTH_LONG).show();
                    if(token.equals("anonimo")){
                        startActivity(new Intent(CreaSegnalazioneActivity.this, LoginActivity.class));
                    }
                    else{
                        startActivity(new Intent(CreaSegnalazioneActivity.this, MainActivity.class));
                    }
                }
            });

        }

    }

    public void caricaFoto(View View){

        final CharSequence[] items={"Camera","Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreaSegnalazioneActivity.this);
        builder.setTitle("Add Image");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, IMG_REQUEST);
                }else if(items[i].equals("Gallery")){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, IMG_REQUEST_ALBUM);
                } else if(items[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode==IMG_REQUEST_ALBUM && resultCode == RESULT_OK && data!=null) {
            path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                img = (ImageView) findViewById(R.id.imageView);
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);
                image = imageToString();

            }catch (IOException e){

            }
        } else if(requestCode==IMG_REQUEST && data!=null){
           bitmap = (Bitmap) data.getExtras().get("data");
           img = (ImageView) findViewById(R.id.imageView);
           img.setImageBitmap(bitmap);
           img.setVisibility(VISIBLE);
           image = imageToString();
       }
    }

    private String imageToString(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("csvjson.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

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
}


