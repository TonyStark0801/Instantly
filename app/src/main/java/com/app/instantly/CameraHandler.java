package com.app.instantly;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


public class CameraHandler extends AppCompatActivity   {

    private static final int PERMISSION_REQUEST_CAMERA = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_handler);
        Bundle extras = getIntent().getExtras();
        String s = extras.getString("key");

        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera Started", Toast.LENGTH_SHORT).show();
                scanCode(s);


        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(this,"We need camera to scan the qr code.",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }



    public void scanCode(String s){
        ScanOptions options  = new ScanOptions();
        options.setPrompt("Volume Up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(ScannerForSender.class);
        if(Objects.equals(s, "WEB"))
            websiteLauncher.launch(options);
        else
            barLauncher.launch(options);

    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result -> {
        String val = result.getContents();
        if(val!=null){
            String[] TOKENS = val.split(":");
            if (TOKENS.length == 3 || TOKENS.length == 5) {
                Intent i = new Intent(getApplicationContext(),Sender.class);
                i.putExtra("key",TOKENS);
                startActivity(i);
//                Toast.makeText(this, Arrays.toString(TOKENS), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Invalid QrCode", Toast.LENGTH_SHORT).show();
                startActivity( new Intent(getApplicationContext(),CameraHandler.class));
                finish();
            }

        }
    });
    ActivityResultLauncher<ScanOptions> websiteLauncher = registerForActivityResult(new ScanContract(),result -> {
        String val = result.getContents();
        if(val!=null){
           InternetShareActivity.inputText.setText(val);
           finish();
        }
    });



}