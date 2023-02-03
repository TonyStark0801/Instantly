//package com.app.instantly;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class FileActivity extends AppCompatActivity {
//
//    static final int REQUEST_GET=1;
//    public static String file_path;
////    public static int choose =1;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_file);
////
////        Button image = (Button) findViewById(R.id.IMAGE);
////
////        image.setOnClickListener(new View.OnClickListener() {
////            // The code in this method will be executed when the numbers View is clicked on.
////            @Override
////            public void onClick(View view) {
////
////                Intent myimageIntent = new Intent(Intent.ACTION_GET_CONTENT);
////                myimageIntent.setType("image/*");
////                startActivityForResult(myimageIntent, REQUEST_GET);
////
////
////            }
////        });
////
////        Button vid = (Button) findViewById(R.id.video);
////
////        vid.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Intent myvideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
////                myvideoIntent.setType("video/*");
////                startActivityForResult(myvideoIntent, REQUEST_GET);
////            }
////        });
////
////        Button other = (Button) findViewById(R.id.files);
////
////        other.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Intent myfileIntent = new Intent(Intent.ACTION_GET_CONTENT);
////                myfileIntent.setType("*/*");
////                startActivity(myfileIntent);
////            }
////        });
////
////        Button audio = (Button) findViewById(R.id.audio);
////        audio.setOnClickListener(view -> {
////            Intent myaudioIntent = new Intent(Intent.ACTION_GET_CONTENT);
////            myaudioIntent.setType("audio/*");
////            startActivity(myaudioIntent);
////
////        });
////    }
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////
////        file_path="";
////        if(requestCode==REQUEST_GET && resultCode==RESULT_OK){
////            Uri fullPhoto = data.getData();
////            file_path=fullPhoto.getPath()+"";
////            Intent intent = new Intent(FileActivity.this,Server.class);
////            intent.putExtra("file",file_path);
////            startActivity(intent);
////        }
////    }
////
////}