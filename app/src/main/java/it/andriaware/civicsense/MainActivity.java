package it.andriaware.civicsense;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
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

import android.util.Base64;


public class MainActivity extends AppCompatActivity {

    private String ip;
    private String token;
    private Retrofit retrofit;
    private IUsersApi iu;
    private ArrayList<String> arrayticket = new ArrayList<>();
    private ArrayList <SegnalazioneItem> ticket;
    private ListView listView;
    private TextView tw;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_segnalazoni:
                    return true;
                case R.id.navigation_cerca:
                    startActivity(new Intent(MainActivity.this, Cerca.class));
                    return true;
                case R.id.navigation_profilo:
                    startActivity(new Intent(MainActivity.this, Profilo.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listaSegnalazioni);
        SharedPreferences pref = getSharedPreferences("mp", MODE_PRIVATE);
        ip=pref.getString("ip", "No name defined");
        token=pref.getString("token", "anonimo");
        tw=(TextView) findViewById(R.id.textViewMessage);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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

        iu= retrofit.create(IUsersApi.class);

        Call<ArrayList<SegnalazioneItem>> call = iu.getTickets();

        call.enqueue(new Callback<ArrayList<SegnalazioneItem>>() {
            @Override
            public void onResponse(Call<ArrayList<SegnalazioneItem>> call, Response<ArrayList<SegnalazioneItem>> response) {
                ticket = response.body();

                for(SegnalazioneItem tic : ticket){

                    arrayticket.add("\nCDT:"+tic.getId()+"\n\nDescrizione: "+tic.getDescription()+"\n\nIndirizzo: "+tic.getAddress()+"\n\n"+getStatusByID(tic.getStatus())+ "\n");
                }

                if(!arrayticket.isEmpty()) {

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_activated_1, arrayticket);

                    listView.setAdapter(adapter);

                    tw.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SegnalazioneItem>> call, Throwable t) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idd;
                idd= ticket.get(position).getId();
                SharedPreferences.Editor editor;
                editor = getSharedPreferences("mp", MODE_PRIVATE).edit();
                editor.putInt("id_segnalazione", idd);

                editor.apply();
                startActivity(new Intent(MainActivity.this, DettaglioSegnalazione.class));

            }
        });

    }

    public void nuovaSegnalazione(View view){
        startActivity(new Intent(MainActivity.this, CreaSegnalazioneActivity.class));
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

    @Override
    public void onBackPressed() {
        final CharSequence[] items={"Si","No"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Vuoi davvero uscire dall'app?");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Si")) {

                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);

                }else if(items[i].equals("No")){

                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }
}

