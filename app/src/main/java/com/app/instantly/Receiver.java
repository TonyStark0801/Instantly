package com.app.instantly;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


import android.graphics.Bitmap;
import android.widget.ImageView;

@SuppressLint("SetTextI18n")
public class Receiver extends AppCompatActivity {
    ProgressBar progressBar;
    TextView Percent;
    ServerSocket serverSocket;
    Thread Thread1 = null;
    Thread Thread2 = null;
    Thread Thread3 = null;
    TextView serverIP, serverPort;
    TextView connectionStatus;
    TextView Message;

    ImageView cancel;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    Button btnSelect;
    String FileName;
    InputStream inputStream = null;
    public static String SERVER_IP = "";
    public static final String SERVER_PORT = "8080";
    ImageView qrIcon;
    InputStream is;
    OutputStream os;
    Socket socket ;
    long size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever);
        serverIP = findViewById(R.id.ServerIP);
        serverPort = findViewById(R.id.ServerPort);
        Message = findViewById(R.id.OutputMessage);
        connectionStatus = findViewById(R.id.ConnectionStatus);
        btnSelect = findViewById(R.id.btnSelectFile);
        qrIcon = findViewById(R.id.imageView3);
        cancel = findViewById(R.id.cancel);
        progressBar = findViewById(R.id.progressBar);
//        Percent = findViewById(R.id.percent);
//        Percent.setVisibility(View.GONE);
        setProgressBarInvisible();
        Bundle extras = getIntent().getExtras();
        String val = extras.getString("key");
        WifiManager wifiManager;
        if (Objects.equals(val, "HOTSPOT")) {
            SERVER_IP = getLocalIpAddress();
            generateQrCode("HOTSPOT" + ":" + SERVER_IP + ":" + SERVER_PORT);
        } else {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            assert wifiManager != null;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipInt = wifiInfo.getIpAddress();
            try {
                SERVER_IP = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            generateQrCode("WIFI" + ":" + SERVER_IP + ":" + SERVER_PORT);
        }

        //Making connection using Thread1
        Thread1 = new Thread(new Thread1());
        Thread1.start();



        qrIcon.setOnClickListener(v->{generateQrCode("WIFI" + ":" + SERVER_IP + ":" + SERVER_PORT);});
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        //Initialize file manager to Select files
        btnSelect.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(Receiver.this, btnSelect);
            popupMenu.getMenuInflater().inflate(R.menu.file_type, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.audio:
                        fileIntent.setType("audio/*");
                        try{
                            startActivityForResult(Intent.createChooser(fileIntent , "Select a File"),100);
                        }catch (Exception e){
                            Toast.makeText(this, "Please Install a File manager", Toast.LENGTH_SHORT).show();
                        }
                        return  true;
                    case R.id.image:
                        fileIntent.setType("image/*");
                        try{
                            startActivityForResult(Intent.createChooser(fileIntent , "Select a File"),100);
                        }catch (Exception e){
                            Toast.makeText(this, "Please Install a File manager", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    case R.id.video:
                        fileIntent.setType("video/*");
                        try{
                            startActivityForResult(Intent.createChooser(fileIntent , "Select a File"),100);
                        }catch (Exception e){
                            Toast.makeText(this, "Please Install a File manager", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.files:
                        fileIntent.setType("*/*");
                        try{
                            startActivityForResult(Intent.createChooser(fileIntent , "Select a File"),100);
                        }catch (Exception e){
                            Toast.makeText(this, "Please Install a File manager", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }
            });
            popupMenu.show();

        });


        cancel.setOnClickListener(v->{

        });
    }


    protected  void onActivityResult(int reqCode , int resCode , Intent data){
        if(reqCode == 100 && resCode == RESULT_OK && data!= null) {
            Uri uri = data.getData();
            FileName = FileActivity.getFileName(this, uri);
            AssetFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = getContentResolver().openAssetFileDescriptor(uri, "r");
                size = fileDescriptor.getLength();
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }finally {
                try {
                    if (fileDescriptor != null) {
                        fileDescriptor.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                inputStream = this.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
            if (FileName!=null) {
                try{
                    Thread3 = new Thread(new Receiver.Thread3(FileName,size,inputStream));
                    Thread3.start();
                }catch (Exception e){
                    Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, FileName, Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(reqCode,resCode,data);
    }



    /* Initializing Input and Output Stream*/
    protected  DataInputStream dataIS;
    protected DataOutputStream  dataOS;


    /*Initialize socket and Accept Connection */
    class Thread1 implements Runnable {
        @Override
        public void run() {

            try {
                serverSocket = new ServerSocket(Integer.parseInt(SERVER_PORT));
                runOnUiThread(() -> {
                    connectionStatus.setText(R.string.NotConnected);
                    serverIP.setText("IP: " + SERVER_IP);
                    serverPort.setText("Port: " + SERVER_PORT);
                });
                socket = serverSocket.accept();
                socket.setReuseAddress(true);
                 is = socket.getInputStream();
               os = socket.getOutputStream();
                dataOS = new DataOutputStream(socket.getOutputStream());
                dataIS = new DataInputStream(socket.getInputStream());
                runOnUiThread(() -> connectionStatus.setText(R.string.Connected));

                Thread2 =new Thread(new Thread2());
                Thread2.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

    }

    /*Receiving Files*/
    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String fileName = dataIS.readUTF();
                    long fileSize = dataIS.readLong();
                    long max = fileSize;
                    Log.d("Dost",fileName);

                    if(fileName !=null){
                        try {
                            runOnUiThread(() -> Message.append("Sender: " + fileName + "\n"));
                        } catch (Exception e) {
                            Toast.makeText(Receiver.this, "Cannot Write the file name", Toast.LENGTH_SHORT).show();
                        }
                        byte[] buffer = new byte[1024];

                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), fileName);
                        if (file.exists()) {
                            int i = 1;
                            String newFileName;
                            while (file.exists()) {
                                newFileName = i + "_" + fileName;
                                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), newFileName);
                                i++;
                            }
                        }
                        long bytesReceived = 0;

                        long finalFileSize = fileSize;
                        runOnUiThread(() -> {
                            progressBar.setMax((int) finalFileSize);
                            progressBar.setProgress(0);
                        });

                        try (FileOutputStream fos = new FileOutputStream(file,false)) {
                            int len = 0;

                            while (fileSize > 0 && (len = dataIS.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1){
                                fos.write(buffer,0,len);
                                bytesReceived+=len;
                                fileSize -= len;
                                runOnUiThread(Receiver.this::setProgressBarVisible);
                                long finalBytesReceived = bytesReceived;
//                                String percent =  (finalBytesReceived / finalFileSize) * 100 +"%";
//                                runOnUiThread(()->{
//                                    Percent.setText(percent);
//                                });
                                runOnUiThread(() -> {
                                    progressBar.setProgress((int) finalBytesReceived);
                                });

                                Log.d("Testing", "running    "+len);
                            }
                            Log.d("Done", "Stopped");
                            fos.flush();
                            runOnUiThread(Receiver.this::setProgressBarInvisible);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } catch (IOException e) {
                    //
                }

            }
        }
    }

    //Sending File Thread
    class Thread3 implements Runnable {
        private  String fileName;
        private InputStream inputStream;
        private  long size;
        Thread3(String fileName, long size,InputStream inputStream) {
            this.fileName = fileName;
            this.inputStream = inputStream;
            this.size = size;
        }

        @Override
        public void run() {
            try {
                dataOS.writeUTF(fileName);
                dataOS.writeLong(size);
                Log.d("d",fileName);
                byte[] buffer = new byte[1024];

                try {
                    int len;
                    long bytesSent = 0;
                    long finalTotalBytes = inputStream.available();
                    runOnUiThread(() -> {
                        progressBar.setMax((int) finalTotalBytes);
                        progressBar.setProgress(0);
                    });
                    runOnUiThread(() -> Message.append("Receiver: " + fileName + "\n"));
                    while (size>0 && (len = inputStream.read(buffer,0,(int) Math.min(buffer.length,size))) > 0) {
                        dataOS.write(buffer, 0, len);
                        bytesSent += len;
                        size-=len;

                        // Update the progress bar during the file transfer
                        long finalBytesSent = bytesSent;
//                        String percent = (int) (finalBytesSent / finalTotalBytes) * 100 +"%";
                        runOnUiThread(Receiver.this::setProgressBarVisible);
                        runOnUiThread(() -> {
//                            Percent.setText(percent);
                            progressBar.setProgress((int) finalBytesSent);
                        });
                    }
                    dataOS.flush();
                }catch (IOException e) {
                    Toast.makeText(Receiver.this, "Can't Send File. Try Again", Toast.LENGTH_SHORT).show();
                }
                finally {
                    inputStream.close();
                }
                runOnUiThread(Receiver.this::setProgressBarInvisible);

            } catch (IOException e) {
                Toast.makeText(Receiver.this, "Can't Send file. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*For non wifi*/
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }



    /*Generating QR CODE*/
    public void generateQrCode(String data){
//            qrCodeIV = findViewById(R.id.IVQrcode);
            ImageView qrCodeIV = new ImageView(this);
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int dimen = Math.min(width, height);
            dimen = dimen * 3 / 4;
            qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, dimen);
            bitmap = qrgEncoder.getBitmap(0);
            qrCodeIV.setImageBitmap(bitmap);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        TextView instructions = new TextView(this);
        instructions.setText(R.string.ScanQrCode);
        instructions.setTextSize(16f);
        instructions.setPadding(40, 0, 10, 30);
        layout.addView(instructions);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        params.height = 500;
        params.width = 500;
        qrCodeIV.setLayoutParams(params);
        qrCodeIV.setPadding(0,0,10,30);
        layout.addView(qrCodeIV);

        Button closeButton = new Button(this);
        closeButton.setText("Close");
        closeButton.setOnClickListener(v -> {
            AlertDialog dialog = (AlertDialog) v.getTag();
            dialog.dismiss();
        });
        layout.addView(closeButton);

        builder.setView(layout);

        AlertDialog dialog = builder.create();
        closeButton.setTag(dialog);

        dialog.show();


//        Dialog builder = new Dialog(this);
//        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        builder.getWindow().setBackgroundDrawable(
//                new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//        builder.setOnDismissListener(dialogInterface -> {
//            //nothing;
//        });
//
//
//        qrCodeIV.setImageBitmap(bitmap);
//        builder.addContentView(qrCodeIV, new RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
//        builder.show();



    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
    public void onDestroy() {
        super.onDestroy();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (dataIS != null) {
                dataIS.close();
            }
            if (dataOS != null) {
                dataOS.close();
            }
        }
            catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  void setProgressBarInvisible(){
        progressBar.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
//        Percent.setVisibility(View.GONE);
    }
    public  void  setProgressBarVisible(){
        progressBar.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
//        Percent.setVisibility(View.VISIBLE);
    }

//    public void createHotspot(){
//
//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//        wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
//            @RequiresApi(api = Build.VERSION_CODES.R)
//            @Override
//            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
//                super.onStarted(reservation);
//                SSID +=reservation.getSoftApConfiguration().getSsid();
//                PASSWORD += reservation.getSoftApConfiguration().getPassphrase();
//                Log.d("HotSpot", "Hotspot started SSID: " + SSID + " Password: " + PASSWORD);
//                Toast.makeText(Receiver.this, "Hotspot started SSID: " + SSID + " Password: " + PASSWORD, Toast.LENGTH_SHORT).show();
//                generateQrCode("HOTSPOT"+":"+SSID+":"+PASSWORD+":"+SERVER_IP+":"+SERVER_PORT);
//            }
//
//            @Override
//            public void onStopped() {
//                super.onStopped();
//                Log.d("HotSpot", "Hotspot stopped");
//                Toast.makeText(Receiver.this, "Hotspot stopped", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailed(int reason) {
//                super.onFailed(reason);
//                Log.d("HotSpot", "Failed to start hotspot: " + reason);
//                Toast.makeText(Receiver.this, "Failed to start hotspot", Toast.LENGTH_SHORT).show();
//            }
//        }, new Handler());
//
//    }

}

