package it.andriaware.civicsense;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import retrofit2.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private String email1, password1, token;
    private  IUsersApi iu;
    private String ip = "http://192.168.1.24/api/";
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editor = getSharedPreferences("mp", MODE_PRIVATE).edit();
        editor.putString("ip", ip);
        editor.apply();

        SharedPreferences pref = getSharedPreferences("mp", MODE_PRIVATE);
        token=pref.getString("token", "anonimo");

        if(!token.equals("anonimo")){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        Gson gson = new GsonBuilder().serializeNulls().create();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ip)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        iu= retrofit.create(IUsersApi.class);
    }

    public void registrati(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void anonimo(View view) {

        editor.putString("token", "anonimo");
        editor.apply();

        final CharSequence[] items={"Cerca","Segnala", "Chiudi"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Accesso Anonimo");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Cerca")) {
                    startActivity(new Intent(LoginActivity.this, Cerca.class));
                }else if(items[i].equals("Segnala")){
                    startActivity(new Intent(LoginActivity.this, CreaSegnalazioneActivity.class));
                } else if(items[i].equals("Chiudi")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

    public void accedi(View view){
        email=(EditText) findViewById(R.id.editTextEmailAccedi);
        password= (EditText) findViewById(R.id.editTextPasswordAccedi);
        email1 = email.getText().toString();
        password1=password.getText().toString();

        setContentView(R.layout.splash);

        if(email1.isEmpty()||password1.isEmpty()){
            Toast.makeText(this, "Uno dei campi non è stato inserito",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Login login = new Login(email1, password1);
            Call<Login> call= iu.loginUser(login);

            call.enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    Login logRes = response.body();
                    if(response.isSuccessful()&&logRes!=null){
                        Toast.makeText(LoginActivity.this, "Login Confermato", Toast.LENGTH_SHORT).show();
                        token=logRes.getApi_token();

                        editor=getSharedPreferences("mp", MODE_PRIVATE).edit();
                        editor.putString("token", token);
                        editor.putString("email", logRes.getEmail());
                        editor.putString("name", logRes.getName());
                        editor.putInt("user_id", logRes.getId());
                        editor.apply();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    else {
                            Toast.makeText(LoginActivity.this,
                                    String.format("Credenziali errate", String.valueOf(response.code()))
                                    , Toast.LENGTH_LONG).show();
                            setContentView(R.layout.activity_login);
                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
                    Toast.makeText(LoginActivity.this,
                            "Error is " + t.getMessage()
                            , Toast.LENGTH_LONG).show();
                    setContentView(R.layout.activity_login);
                }
            });

        }
    }

    public void passwordDimenticata(View view){
        email=(EditText) findViewById(R.id.editTextEmailAccedi);
        email1 = email.getText().toString();

        setContentView(R.layout.splash);
        
        if(email1.isEmpty()){
            Toast.makeText(this, "Inserire la mail su cui effettuare il reset password",
                    Toast.LENGTH_SHORT).show();
        }else{

            Call<Email> call1 = iu.forgotPassword(new Email(email1));
            call1.enqueue(new Callback<Email>() {
                @Override
                public void onResponse(Call<Email> call, Response<Email> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Verifica la tua casella email per il reset password",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "La mail inserita non è iscritta al sistema",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Email> call, Throwable t) {

                }
            });
        }


    }


}
