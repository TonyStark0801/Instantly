package com.app.instantly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

public class WifiOrHotspotActivity extends AppCompatActivity {

    Button wifiMode ;
    Button hotspotMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_hotspot);
        wifiMode = findViewById(R.id.WifiModeBtn);
        hotspotMode = findViewById(R.id.HotspotModeBtn);
        Intent i = new Intent(getApplicationContext(),QrCodeGenerate.class);

        wifiMode.setOnClickListener(v -> {
            i.putExtra("key", "WIFI");
            startActivity(i);
        });

        hotspotMode.setOnClickListener(v -> {
//            i.putExtra("key", "HOTSPOT");
//            startActivity(i);
            startActivity(new Intent(getApplicationContext(),Server.class));
        });

    }


}