package com.app.instantly;

import java.util.regex.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera Started", Toast.LENGTH_SHORT).show();
            scanCode();


        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(this,"We need camera to scan the qr code.",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }


    public void scanCode(){
        ScanOptions options  = new ScanOptions();
        options.setPrompt("Volume Up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(ScannerForSender.class);
        barLauncher.launch(options);
    }

    public  boolean isValidIP(String val){
        String zeroTo255 = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])";
        String regex= zeroTo255 + "\\."+ zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255+"\\:"+"8080";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(val);
        return m.matches();
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result -> {
        if(result.getContents()!=null){
            if(!isValidIP(result.getContents())) {
                Toast.makeText(this, "Invalid QrCode", Toast.LENGTH_SHORT).show();
                startActivity( new Intent(getApplicationContext(),CameraHandler.class));
                finish();
            }
            else{
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),Sender.class);
                i.putExtra("key",result.getContents());
                startActivity(i);
            }

        }
    });



}