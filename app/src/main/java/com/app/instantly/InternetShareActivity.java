package com.app.instantly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class InternetShareActivity extends AppCompatActivity {

    Button Join;
    Button Create;

    ImageView QrScanner;
   static EditText inputText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_share);

        Join = findViewById(R.id.join);
        Create = findViewById(R.id.create);
        QrScanner  = findViewById(R.id.ScanQr);
        inputText = findViewById(R.id.Url);
        
        Join.setOnClickListener(v->{
            String url = String.valueOf(inputText.getText());
            if(url.equals("")) Toast.makeText(this, "Please Enter url", Toast.LENGTH_SHORT).show();
            else if (!url.contains("https://instantly-web.vercel.app/tranfer.html?room_id=")) {
                Toast.makeText(this, "Invalid url", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        Create.setOnClickListener(v->{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instantly-web.vercel.app/tranfer.html"));
            startActivity(browserIntent);
        });

        QrScanner.setOnClickListener(v->{
            Intent i = new Intent(this, CameraHandler.class);
            i.putExtra("key","WEB");
            startActivity(i);
        });


    }
}