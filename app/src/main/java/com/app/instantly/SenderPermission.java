package com.app.instantly;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;


public class SenderPermission extends AppCompatActivity {

    Button locationService;
    TextView locationServiceReason;
    Button location;
    TextView locationReasonTxt;

    Button wifi;
    TextView wifiReasonTxt;

    Button file;
    TextView fileReasonTxt;
    WifiManager wifiManager;
    LocationManager lm ;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_permission);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {ex.printStackTrace();}

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && Environment.isExternalStorageManager()
                && wifiManager.isWifiEnabled() && (gps_enabled && network_enabled))
        {
            startActivity(new Intent(getApplicationContext(), CameraHandler.class));
        }

        location = findViewById(R.id.locationBtn);
        locationReasonTxt = findViewById(R.id.locationReasonTxt);
        wifi = findViewById(R.id.wifiBtn);
        wifiReasonTxt = findViewById(R.id.wifiReasonTxt);
        file = findViewById(R.id.FileBtn);
        fileReasonTxt = findViewById(R.id.fileReasontxt);
        locationService = findViewById(R.id.locationServiceBtn);
        locationServiceReason = findViewById(R.id.locationServiceReason);

        final int  LOCATION_REQUEST_CODE = 1;
        final int  FILE_REQUEST_CODE = 2;
        final int  WIFI_REQUEST_CODE = 3;
        final int  LOCATION_SERVICE_REQUEST_CODE = 4;

        //Initially Invisible
        setInvisible(LOCATION_REQUEST_CODE);
        setInvisible(FILE_REQUEST_CODE);
        setInvisible(WIFI_REQUEST_CODE);
        setInvisible(LOCATION_SERVICE_REQUEST_CODE);

        /* Location SenderPermission */
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            location.setVisibility(View.VISIBLE);
            locationReasonTxt.setVisibility(View.VISIBLE);
            location.setOnClickListener(v -> {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this).setTitle("SenderPermission Needed").setMessage("Storage SenderPermission is need to look for the files, so you can select it.\",LOCATION_PERMISSION_CODE );\n")
                            .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE))
                            .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                            .create().show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

                }
            });
        }


        /*File SenderPermission*/
        if( !Environment.isExternalStorageManager() ){
            setVisible(2);
            file.setOnClickListener(v->{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                else{
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_REQUEST_CODE);
                }

            });
        }


        /*Wifi SenderPermission*/
        if(!wifiManager.isWifiEnabled()){
            setVisible(WIFI_REQUEST_CODE);
            wifi.setOnClickListener(v->{
                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                startActivity(panelIntent);
            });
        }

        /*Location Service check*/
        if(!gps_enabled && !network_enabled) {
            setVisible(4);
            locationService.setOnClickListener(v->{
                this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onResume() {

        LocationManager lm  = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {ex.printStackTrace();}


        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && Environment.isExternalStorageManager()
                && wifiManager.isWifiEnabled() && (gps_enabled && network_enabled))
        {

            startActivity(new Intent(getApplicationContext(), CameraHandler.class));
            finish();
        }

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) setInvisible(1); else setVisible(1);
        if(Environment.isExternalStorageManager())  setInvisible(2); else setVisible(2);
        if(wifiManager.isWifiEnabled()) setInvisible(3); else setVisible(3);
        if(gps_enabled && network_enabled) setInvisible(4); else setVisible(4);

        super.onResume();
    }

    private void setInvisible(int code){
        switch (code){
            case  1 :
                location.setVisibility(View.GONE);
                locationReasonTxt.setVisibility(View.GONE);
            case 2 :
                file.setVisibility(View.GONE);
                fileReasonTxt.setVisibility(View.GONE);
            case 3 :
                wifi.setVisibility(View.GONE);
                wifiReasonTxt.setVisibility(View.GONE);
            case 4:
                locationService.setVisibility(View.GONE);
                locationServiceReason.setVisibility(View.GONE);
        }
    }
    private void setVisible(int code){
        switch (code){
            case  1 :
                location.setVisibility(View.VISIBLE);
                locationReasonTxt.setVisibility(View.VISIBLE);
            case 2 :
                file.setVisibility(View.VISIBLE);
                fileReasonTxt.setVisibility(View.VISIBLE);
            case 3 :
                wifi.setVisibility(View.VISIBLE);
                wifiReasonTxt.setVisibility(View.VISIBLE);
            case 4:
                locationService.setVisibility(View.VISIBLE);
                locationServiceReason.setVisibility(View.VISIBLE);
        }
    }


}