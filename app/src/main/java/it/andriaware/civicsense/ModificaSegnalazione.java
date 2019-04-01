package it.andriaware.civicsense;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class ModificaSegnalazione extends AppCompatActivity {
    private String ip, token, description, photo;
    private int priority, category, categoryTemp, id, priorityTemp;
    private Spinner priorità, categorie;
    private EditText twdesc;
    private Retrofit retrofit;
    private ImageView image;
    private Bitmap decodedImage;
    private final int IMG_REQUEST =1;
    private final int IMG_REQUEST_ALBUM=2;
    private Bitmap bitmap;
    private IUsersApi iu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_segnalazione);

        twdesc=(EditText) findViewById(R.id.textViewDescriptionModifica);

        SharedPreferences pref = getSharedPreferences("mp", MODE_PRIVATE);
        ip=pref.getString("ip", "No name defined");
        token=pref.getString("token", "anonimo");
        priority= pref.getInt("priority", 0);
        priorityTemp= pref.getInt("priority", 0);

        category=pref.getInt("category", 0);
        categoryTemp=pref.getInt("category", 0);

        description=pref.getString("description", "def");
        photo=pref.getString("photo", "def");
        id=pref.getInt("id_segnalazione", 0);

        twdesc.setText(description);

        priorità = (Spinner) findViewById(R.id.spinnerPriorityModifica);

        List<String> gravità = new ArrayList<>();
        gravità.add(0, getPriorityByID(priority));
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
        categorie = (Spinner) findViewById(R.id.spinnerCategorieModifica);

        List<String> categoria = new ArrayList<>();
        categoria.add(0, getCategoryByID(category));
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

        image = (ImageView) findViewById(R.id.imageView3);
        byte[] imageBytes = Base64.decode(photo, Base64.DEFAULT);
        decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        image.setImageBitmap(decodedImage);

    }

    public String getPriorityByID(int id){
        String status="";
        switch (id){
            case 1:
                status="Bassa";
                break;
            case 2:
                status="Media";
                break;
            case 3:
                status="Alta";
                break;
        }
        return status;
    }

    public String getCategoryByID(int id){
        String status="";
        switch (id){
            case 0:
                status="Altro";
                break;
            case 1:
                status="Stradale e Aereoportuale";
                break;
            case 2:
                status="Ferroviarie e altre linee di trasporto";
                break;
            case 3:
                status="Marittime, Laucali e Fluviali";
                break;
            case 4:
                status="Idrauliche";
                break;
            case 5:
                status="Impianti Elettrici";
                break;
            case 6:
                status="Edilizia Pubblica";
                break;
            case 7:
                status="Igienico Sanitarie";
                break;
            case 8:
                status="Impianti di comunicazione";
                break;
        }
        return status;
    }
    public void modifica(View view){
        description=twdesc.getText().toString();
        if(category==0){
            category=categoryTemp;
        }
        if(priority==0){
            priority=priorityTemp;
        }
        if(description.isEmpty()){
            Toast.makeText(this, "Inserisci una descrizione",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            setContentView(R.layout.splash);
            ModificaSegnalazioneItem mod = new ModificaSegnalazioneItem(description, priority, photo, category);
            iu= retrofit.create(IUsersApi.class);
            Call<ModificaSegnalazioneItem> call= iu.updateTicket(id, mod);
            call.enqueue(new Callback<ModificaSegnalazioneItem>() {
                @Override
                public void onResponse(Call<ModificaSegnalazioneItem> call, Response<ModificaSegnalazioneItem> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(ModificaSegnalazione.this, "Segnalazione modificata correttamente", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ModificaSegnalazione.this, MainActivity.class));
                    }
                    else{
                        Toast.makeText(ModificaSegnalazione.this,
                                String.format("Response is %s", String.valueOf(response.code()))
                                , Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ModificaSegnalazione.this, MainActivity.class));
                    }
                }

                @Override
                public void onFailure(Call<ModificaSegnalazioneItem> call, Throwable t) {
                    startActivity(new Intent(ModificaSegnalazione.this, MainActivity.class));
                }
            });
        }
    }

    public void carica(View view){
        final CharSequence[] items={"Camera","Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ModificaSegnalazione.this);
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
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                image = (ImageView) findViewById(R.id.imageView3);
                image.setImageBitmap(bitmap);
                photo = imageToString();

            }catch (IOException e){

            }
        } else if(requestCode==IMG_REQUEST && data!=null){
            bitmap = (Bitmap) data.getExtras().get("data");
            image = (ImageView) findViewById(R.id.imageView3);
            image.setImageBitmap(bitmap);
            photo = imageToString();
        }
    }

    private String imageToString(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }
}
