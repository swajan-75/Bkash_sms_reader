package com.example.sms_reader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SmsPostService extends Service {

    private static final String API = "http://192.168.0.110:3000/sms";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sender = intent.getStringExtra("sender");
        String message = intent.getStringExtra("message");

        if (sender != null && sender.equalsIgnoreCase("bKash")) {
            new Thread(() -> sendSmsJson(sender, message)).start();
        }

        return START_NOT_STICKY;
    }

    private void sendSmsJson(String sender, String message) {
        HttpURLConnection conn = null;
        try {
            String jsonPayload = "{\"sms\":\"From: " + sender + " Message: " + message + "\"}";

            URL url = new URL(API);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStream os = conn.getOutputStream();
            os.write(jsonPayload.getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();



            InputStream is = (responseCode >= 200 && responseCode < 300) ?
                    conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();


        } catch (Exception e) {
            Log.e("POST", "Error sending SMS JSON to API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
