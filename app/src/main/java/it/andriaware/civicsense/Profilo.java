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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Profilo extends AppCompatActivity {

    private String name, email, ip, token;
    private EditText twName, twEmail, twPassword;
    private IUsersApi iu;
    private Retrofit retrofit;
    private String password = "";
    private SharedPreferences.Editor editor;
    private Boolean modifica=false;
    private Button button;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_segnalazoni:
                    startActivity(new Intent(Profilo.this, MainActivity.class));
                    return true;
                case R.id.navigation_cerca:
                    startActivity(new Intent(Profilo.this, Cerca.class));
                    return true;
                case R.id.navigation_profilo:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        SharedPreferences pref = getSharedPreferences("mp", MODE_PRIVATE);
        ip=pref.getString("ip", "non definito");
        email=pref.getString("email", "def");
        name=pref.getString("name", "def");
        token=pref.getString("token", "anonimo");

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


        BottomNavigationView nav = (BottomNavigationView) findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_profilo);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        button=(Button) findViewById(R.id.buttonModifica);
        twEmail=(EditText) findViewById(R.id.editTextMailProfilo);
        twName=(EditText) findViewById(R.id.editTextNomeProfilo);
        twPassword=(EditText) findViewById(R.id.editTextPasswordModifica);

        twName.setText(name);
        twEmail.setText(email);

        twName.setEnabled(false);
        twEmail.setEnabled(false);
        twPassword.setEnabled(false);
    }

    public void modificaProfilo(View view){
      if(modifica) {

          email = twEmail.getText().toString();
          name = twName.getText().toString();
          password = twPassword.getText().toString();


          final CharSequence[] items = {"Si", "No"};
          final AlertDialog.Builder builder = new AlertDialog.Builder(Profilo.this);
          builder.setTitle("Conferma modifica profilo?");
          builder.setItems(items, new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialogInterface, int i) {


                  if (items[i].equals("Si")) {
                      Matcher matcher = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(email);
                      if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                          Toast.makeText(Profilo.this, "Uno o più campi non inseriti", Toast.LENGTH_SHORT).show();
                      } else if(password.length()<6){
                          Toast.makeText(Profilo.this, "La password deve contenere almeno 6 caratteri",
                                  Toast.LENGTH_SHORT).show();
                      }
                      else if(!matcher.find()){
                          Toast.makeText(Profilo.this, "Inserire un indirizzo email valido",
                                  Toast.LENGTH_SHORT).show();
                      }

                      else {
                          Call<User> call = iu.editUser(new User(name, email, password, password));
                          call.enqueue(new Callback<User>() {
                              @Override
                              public void onResponse(Call<User> call, Response<User> response) {
                                  if (response.isSuccessful()) {
                                      modifica=false;
                                      Toast.makeText(Profilo.this, "Campi modificati con successo.", Toast.LENGTH_SHORT).show();
                                      editor = getSharedPreferences("mp", MODE_PRIVATE).edit();
                                      editor.putString("email", email);
                                      editor.putString("name", name);
                                      editor.apply();
                                      button.setText("Modifica");
                                  } else {
                                      Toast.makeText(Profilo.this, "Campi non modificati, errore numero:" + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();

                                  }
                              }

                              @Override
                              public void onFailure(Call<User> call, Throwable t) {
                                  Toast.makeText(Profilo.this, "Verifica la tua connessione", Toast.LENGTH_SHORT).show();

                              }
                          });
                      }

                  } else if (items[i].equals("No")) {

                      dialogInterface.dismiss();
                  }
              }
          });
          builder.show();
      }
      else{
          modifica=true;
          twPassword.setEnabled(true);
          twEmail.setEnabled(true);
          twName.setEnabled(true);
          button.setText("Salva");


      }
    }

    public void logout(View view){


        final CharSequence[] items={"Si","No"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Profilo.this);
        builder.setTitle("Conferma logout?");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Si")) {
                    SharedPreferences.Editor editor;
                    editor=getSharedPreferences("mp", MODE_PRIVATE).edit();
                    editor.putString("token", "anonimo");
                    editor.putInt("user_id", -1);
                    editor.apply();
                    startActivity(new Intent(Profilo.this, LoginActivity.class));
                }else if(items[i].equals("No")){

                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

}
