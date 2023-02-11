package com.app.instantly;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
    ServerSocket serverSocket;
    Thread Thread1 = null;
    Thread Thread2 = null;
    Thread Thread3 = null;
    TextView serverIP, serverPort;
    TextView connectionStatus;
    TextView Message;

    Button btnSend;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever);
        serverIP = findViewById(R.id.ServerIP);
        serverPort = findViewById(R.id.ServerPort);
        Message = findViewById(R.id.OutputMessage);
        btnSend = findViewById(R.id.btnSend);
        connectionStatus = findViewById(R.id.ConnectionStatus);
        btnSelect = findViewById(R.id.btnSelectFile);
        qrIcon = findViewById(R.id.imageView3);
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
        /* Send Button Functionality*/
        btnSend.setOnClickListener(v -> {
            if (!FileName.isEmpty()) {
                Thread3 = new Thread(new Receiver.Thread3(FileName,inputStream));
                Thread3.start();
            } else {
                Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show();
            }
        });
    }



    /* Initializing Input and Output Stream*/
    protected  DataInputStream dataIS;
    protected DataOutputStream  dataOS;


    /*Initialize socket and Accept Connection */
    class Thread1 implements Runnable {
        @Override
        public void run() {
            Socket socket ;
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
                    Log.d("Dost",fileName);

                    if(fileName !=null){
                        String finalFileName1 = fileName;
                        try {
                            runOnUiThread(() -> Message.append("Sender: " + finalFileName1 + "\n"));
                        } catch (Exception e) {
                            throw e;
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

                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            int len = 0;
                            while (fileSize > 0 && (len = dataIS.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                                fos.write(buffer);

//                                Log.d("Received Data Size", String.valueOf(len));
//                                Log.d("G", String.valueOf(fos.getChannel().size()));

                            }
                            fos.flush();
                            fos.close();


                        } catch (IOException e) {

                        }
                    }

                } catch (IOException e) {

                }
            }
        }
    }

    /*Sending Files*/
    class Thread3 implements Runnable {
        private  String fileName;
        private  InputStream inputStream;
        Thread3(String fileName, InputStream inputStream) {
            this.fileName = fileName;
            this.inputStream = inputStream;
        }
        @Override
        public void run() {
            try {
                dataOS.writeUTF(fileName);
                try {
                    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    dataOS.writeUTF(fileName);
                    try {
                        int len;
                        while ((len = inputStream.read(buffer)) != -1) {
                            dataOS.write(buffer, 0, len);
                        }
                    } finally {
                        // close the stream
                        try {
                            byteBuffer.close();
                        } catch (IOException ignored) { /* do nothing */ }
                    }
                    inputStream.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                dataOS.flush();
                runOnUiThread(() -> {
                    Message.append("Sender: " + fileName + "\n");
                });


            } catch (IOException e) {
                throw new RuntimeException(e);
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

