package it.andriaware.civicsense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

public class SignUpActivity extends AppCompatActivity {
    private IUsersApi iu;
    private EditText nome, email, password;
    private String nome1, email1, password1;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Gson gson = new GsonBuilder().serializeNulls().create();
        SharedPreferences ipPref = getSharedPreferences("mp",MODE_PRIVATE);
        ip=ipPref.getString("ip", "No name defined");


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ip)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        iu= retrofit.create(IUsersApi.class);
    }

    public void registrati(View view){
        nome = (EditText) findViewById(R.id.editTextNomeRegistrati);
        email = (EditText) findViewById(R.id.editTextEmailRegistrati);
        password = (EditText) findViewById(R.id.editTextPasswordRegistrati);

        nome1=nome.getText().toString();
        email1=email.getText().toString();
        password1=password.getText().toString();

        Matcher matcher = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(email1);
        if(nome1.isEmpty() || email1.isEmpty() || password1.isEmpty()){
            Toast.makeText(this, "Uno dei campi non è stato inserito",
                    Toast.LENGTH_SHORT).show();
        }
        else if(password1.length()<6){
            Toast.makeText(this, "La password deve contenere almeno 6 caratteri",
                    Toast.LENGTH_SHORT).show();
        }
        else if(!matcher.find()){
            Toast.makeText(this, "Inserire un indirizzo email valido",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            setContentView(R.layout.splash);

            User user = new User(nome1, email1, password1, password1);

            Call<User> call= iu.createUser(user);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User respondeUser = response.body();

                    if(response.isSuccessful()&&respondeUser!=null){
                        Toast.makeText(SignUpActivity.this, "Registrazione completata" , Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    }
                    else {
                        setContentView(R.layout.activity_sign_up);
                        Toast.makeText(SignUpActivity.this,
                               "Sei già iscritto, effettua il login"
                                , Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this,
                            "Errore di rete" + t.getMessage()
                            , Toast.LENGTH_LONG).show();
                }
            });


        }



    }
}
