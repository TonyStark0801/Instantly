package com.app.instantly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import java.util.Objects;

public class WifiOrHotspot extends AppCompatActivity {

    Button wifiMode ;
    Button hotspotMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_hotspot);
        Objects.requireNonNull(getSupportActionBar()).hide();
        wifiMode = findViewById(R.id.WifiModeBtn);
        hotspotMode = findViewById(R.id.HotspotModeBtn);


        wifiMode.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ReceiverWifiPermission.class));
        });

        hotspotMode.setOnClickListener(v -> {

            LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}

            if(!gps_enabled && !network_enabled) {
                // notify user
                new AlertDialog.Builder(this)
                        .setMessage(R.string.gps_network_not_enabled)
                        .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton(R.string.Cancel,null)
                        .show();
            }

            startActivity(new Intent(getApplicationContext(),ReceiverHotspotPermission.class));
        });

    }


}