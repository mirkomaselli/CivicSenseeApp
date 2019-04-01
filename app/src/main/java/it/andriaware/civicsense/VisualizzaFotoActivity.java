package it.andriaware.civicsense;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

public class VisualizzaFotoActivity extends AppCompatActivity {


    private String imgString;
    private Bitmap decodedImage;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_foto);

        image = (ImageView) findViewById(R.id.imageView2);

        SharedPreferences pref = getSharedPreferences("mp", MODE_PRIVATE);
        imgString=pref.getString("photo", "No name defined");

        byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
        decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        image.setImageBitmap(decodedImage);
    }
}
