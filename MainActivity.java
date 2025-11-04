package com.freedomtunnel.freedom;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText hostEt, portEt, userEt, passEt;
    Button connectBtn;
    TextView statusTv;

    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hostEt = findViewById(R.id.et_host);
        portEt = findViewById(R.id.et_port);
        userEt = findViewById(R.id.et_user);
        passEt = findViewById(R.id.et_pass);
        connectBtn = findViewById(R.id.btn_connect);
        statusTv = findViewById(R.id.tv_status);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!connected) {
                    String host = hostEt.getText().toString().trim();
                    String port = portEt.getText().toString().trim();
                    String user = userEt.getText().toString().trim();
                    String pass = passEt.getText().toString();

                    if (TextUtils.isEmpty(host) || TextUtils.isEmpty(port) || TextUtils.isEmpty(user)) {
                        statusTv.setText("Fill host, port and username");
                        return;
                    }

                    Intent intent = new Intent(MainActivity.this, TunnelService.class);
                    intent.putExtra("host", host);
                    intent.putExtra("port", Integer.parseInt(port));
                    intent.putExtra("user", user);
                    intent.putExtra("pass", pass);
                    startForegroundService(intent);
                    statusTv.setText("Connecting...");
                    connectBtn.setText("Disconnect");
                    connected = true;
                } else {
                    Intent intent = new Intent(MainActivity.this, TunnelService.class);
                    intent.setAction(TunnelService.ACTION_STOP);
                    startService(intent);
                    statusTv.setText("Disconnected");
                    connectBtn.setText("Connect");
                    connected = false;
                }
            }
        });
    }
}
