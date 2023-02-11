package com.app.instantly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
        Intent i = new Intent(getApplicationContext(), Receiver.class);


        wifiMode.setOnClickListener(v -> {
            i.putExtra("key", "WIFI");
            startActivity(i);
        });

        hotspotMode.setOnClickListener(v -> {
            i.putExtra("key", "HOTSPOT");
            startActivity(i);


//            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            for (Network network : cm.getAllNetworks()) {
//                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
//                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                    if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//                            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
//                            && !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)) {
//                        Toast.makeText(this, "HOTSPOT IS ON", Toast.LENGTH_SHORT).show();
//                        break;
//                    }
//                }
//
//            }

//            LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
//            boolean gps_enabled = false;
//            boolean network_enabled = false;
//
//            try {
//                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            } catch(Exception ex) {}
//
//            try {
//                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            } catch(Exception ex) {}
//
//            if(!gps_enabled && !network_enabled) {
//                // notify user
//                new AlertDialog.Builder(this)
//                        .setMessage(R.string.gps_network_not_enabled)
//                        .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                            }
//                        })
//                        .setNegativeButton(R.string.Cancel,null)
//                        .show();
//            }
//
//            startActivity(new Intent(getApplicationContext(),ReceiverPermission.class));
        });



        };

    

}


