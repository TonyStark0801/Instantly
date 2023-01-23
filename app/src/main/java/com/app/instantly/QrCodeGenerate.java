package com.app.instantly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Objects;

public class QrCodeGenerate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generate);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            if (Objects.equals(value, "WIFI")) showQR();
            else createHotSpot();
        }
    }

    public void showQR(){
        Toast.makeText(this, "Showing QR", Toast.LENGTH_SHORT).show();
    }

    public void createHotSpot(){
        Toast.makeText(this, "Creating Hotspot", Toast.LENGTH_SHORT).show();






    }
}