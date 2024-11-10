package com.isnp171322.miagenda.Clases;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.isnp171322.miagenda.R;

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

        // Verifica si la versión es Oreo (API 26) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Envio de notificaciones a clientes",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            // Registra el canal con el NotificationManager
            notificationManager.createNotificationChannel(channel);
        }

        // Código para construir y enviar la notificación
        Notification notification = new  NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(0, notification);
    }
}
