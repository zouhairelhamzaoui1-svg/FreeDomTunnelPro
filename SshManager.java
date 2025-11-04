package com.freedomtunnel.freedom;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;
import android.util.Log;

public class SshManager {
    private static final String TAG = "SshManager";
    private Session session;

    public boolean connect(String host, int port, String user, String pass) {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(pass);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect(15000); // 15s timeout

            Log.i(TAG, "Session connected: " + session.isConnected());
            return session.isConnected();
        } catch (Exception e) {
            Log.e(TAG, "SSH connect error", e);
            return false;
        }
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}
