package com.freedomtunnel.freedom;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class TunnelService extends Service {
    public static final String ACTION_STOP = "ACTION_STOP";
    private static final String TAG = "TunnelService";
    private static final String CHANNEL_ID = "freedom_tunnel_channel";
    private Thread worker;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        final String host = intent.getStringExtra("host");
        final int port = intent.getIntExtra("port", 22);
        final String user = intent.getStringExtra("user");
        final String pass = intent.getStringExtra("pass");

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FreeDom Tunnel Pro")
                .setContentText("Starting tunnel...")
                .setSmallIcon(android.R.drawable.ic_secure)
                .build();

        startForeground(1, notification);

        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "Connecting to " + host + ":" + port);
                    SshManager manager = new SshManager();
                    boolean ok = manager.connect(host, port, user, pass);
                    if (ok) {
                        Log.i(TAG, "SSH connected");
                        // TODO: Start SOCKS5 proxy and forward traffic
                        // This is a placeholder to keep the service alive and demonstrate structure
                        while (!Thread.currentThread().isInterrupted()) {
                            Thread.sleep(2000);
                        }
                    } else {
                        Log.e(TAG, "SSH connection failed");
                    }
                } catch (InterruptedException e) {
                    Log.i(TAG, "Worker interrupted");
                } finally {
                    stopForeground(true);
                    stopSelf();
                }
            }
        });
        worker.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (worker != null && worker.isAlive()) {
            worker.interrupt();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "FreeDom Tunnel", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }
    }
}
