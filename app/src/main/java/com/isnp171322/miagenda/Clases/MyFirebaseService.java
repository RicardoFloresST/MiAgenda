package com.isnp171322.miagenda.Clases;

import android.app.NotificationManager;
import android.util.Log;

import androidx.annotation.NonNull;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String TAG = "MisNotificacionesFCM";
    private static final String CHANNEL_ID = "MyAgenda_Chanenel";


    @Override
    public void  onMessageReceived(@NonNull RemoteMessage message){
        Log.d(TAG, "From: " + message.getFrom());

        // Check if message contains a data payload.
        if (message.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData());

        }

        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + message.getNotification().getBody());
            sendNotification(message.getNotification().getTitle(),message.getNotification().getBody());
        }

    }

    private void sendNotification(String title, String body) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

    }
}
