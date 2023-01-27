package com.app.instantly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class Sender extends AppCompatActivity {
    String IP ="";
    String PORT = "";
    Thread Thread1 = null;
    Thread Thread2 = null;
    Thread Thread3 = null;
    TextView serverIP,serverPort;
    TextView connectionStatus;
    TextView OutMessage,InputMessage;
    Button btnSend;
    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        serverIP = findViewById(R.id.ServerIP);
        serverPort = findViewById(R.id.ServerPort);
        connectionStatus = findViewById(R.id.ConnectionStatus);
        OutMessage = findViewById(R.id.OutputMessage);
        InputMessage = findViewById(R.id.InputMessage);
        btnSend = findViewById(R.id.btnSend);
        OutMessage.setText("");
        Bundle extras = getIntent().getExtras();


        if (extras != null) {
            String value = extras.getString("key");
            String[] tokens = value.split(":");
            IP+=tokens[0];
            PORT+=tokens[1];
            Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();
            Thread1 = new Thread(new Thread1());
            Thread1.start();
        }

        serverIP.setText("Server IP: "+IP);
        serverPort.setText("PORT: "+PORT);
        connectionStatus.setText(R.string.NotConnected);



        btnSend.setOnClickListener(v -> {
            String message = InputMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                Thread3 = new Thread(new Thread3(message,bytes));
                Thread3.start();
            }
//                if(flag==1) {
//                    Toast.makeText(Client.this, "Transferring", Toast.LENGTH_SHORT).show();
//                    for (int i = 0; i < 1000; i++) ;
//                    Toast.makeText(Client.this, "Failed", Toast.LENGTH_SHORT).show();
//
//                }
        });
    }




    DataOutputStream output;
    DataInputStream input;
    class Thread1 implements Runnable {

        Socket socket=null;
        public void run() {

            try {
                socket = new Socket(IP, Integer.parseInt(PORT));
                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());
                runOnUiThread(() -> connectionStatus.setText(R.string.Connected));
                Thread2 = new Thread(new Thread2());
                Thread2.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            finally{
//                try {
//                    if(socket!=null )socket.close();
//                    if(output!=null )output.close();
//                    if(input!=null )input.close();
//                    runOnUiThread(()->{Toast.makeText(Sender.this, "Closing Socket", Toast.LENGTH_SHORT).show();});
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

        }
    }
    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = input.readUTF();
                    if (message!= null) {
                        runOnUiThread(() -> OutMessage.append("Server: " + message + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class Thread3 implements Runnable {
        private String message;
        private byte[] bytes;
        Thread3(String message, byte[]bytes) {
            this.message = message;
            this.bytes = bytes;
        }
        @Override
        public void run() {
            try {
                output.writeUTF(message);
//                f_output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                output.flush();
//                f_output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                OutMessage.append("client: " + message + "\n");
                InputMessage.setText("");
            });
        }
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

            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}