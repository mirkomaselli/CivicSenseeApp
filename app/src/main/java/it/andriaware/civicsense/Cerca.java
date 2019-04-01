package it.andriaware.civicsense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Cerca extends AppCompatActivity {

    private String token;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_segnalazoni:
                    startActivity(new Intent(Cerca.this, MainActivity.class));
                    return true;
                case R.id.navigation_cerca:
                    return true;
                case R.id.navigation_profilo:
                    startActivity(new Intent(Cerca.this, Profilo.class));
                    return true;
            }
            return false;
        }
    };

    private EditText cdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerca);

        SharedPreferences pref = getSharedPreferences("mp", MODE_PRIVATE);
        token=pref.getString("token", "anonimo");

        BottomNavigationView nav = (BottomNavigationView) findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_cerca);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        if(token.equals("anonimo")){
            nav.setVisibility(View.GONE);
            navigation.setVisibility(View.GONE);
        }
        else {
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        }
        cdt=(EditText)findViewById(R.id.editTextCTD);
    }
    public void cerca(View view){
        int cdt1 = 0;
        if(!cdt.getText().toString().isEmpty()) {
            cdt1 = Integer.parseInt(cdt.getText().toString());
        }
        if(cdt1==0){
            Toast.makeText(Cerca.this, "Inserisci il CDT", Toast.LENGTH_SHORT).show();
        }else {
            SharedPreferences.Editor editor;
            editor = getSharedPreferences("mp", MODE_PRIVATE).edit();
            editor.putInt("id_segnalazione", cdt1);
            editor.apply();
            startActivity(new Intent(Cerca.this, DettaglioSegnalazione.class));
        }
    }

}
