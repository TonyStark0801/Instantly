package com.app.instantly;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendBtn = findViewById(R.id.sendBtn);
        Button receiveBtn = findViewById(R.id.receiveBtn);
        Intent permissionActivity = new Intent(getApplicationContext(), Permission.class);



        sendBtn.setOnClickListener(view -> {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                    && Environment.isExternalStorageManager()
                    && wifiManager.isWifiEnabled()) startActivity(new Intent(getApplicationContext(), CameraHandler.class));
            else {
                //key:0 for sender
                permissionActivity.putExtra("key",0);
                startActivity(permissionActivity);
            }
        });

        receiveBtn.setOnClickListener(view ->{
            //key:1 for sender
            permissionActivity.putExtra("key",1);
            startActivity(permissionActivity);
        });
    }
}


