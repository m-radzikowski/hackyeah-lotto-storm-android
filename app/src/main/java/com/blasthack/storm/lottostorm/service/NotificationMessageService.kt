package com.blasthack.storm.lottostorm.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationMessageService : FirebaseMessagingService() {

    private val tag = "EXPO_FIREBASE"
    private val defaultChannel = "default_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(tag, "From: " + remoteMessage!!.from)
        Log.d(tag, "Notification Message Body: " + remoteMessage.notification!!.body!!)

        //sendNotification(remoteMessage)
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)

/*        if (token != null ) {
            registerForNotifications("cos", token)
        }*/
    }

/*
    private fun registerForNotifications(username: String, token: String) {
        StormRepository.registerToken(username, token).execute()
    }
*/

/*    private fun sendNotification(remoteMessage: RemoteMessage) {
        val app = application as Expo
        val notification = remoteMessage.notification
        val intent = Intent(this, app.currentActivity!!.javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, defaultChannel)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.launcher_48dp))
                .setSmallIcon(R.drawable.expo_icon_24dp)
                .setContentTitle(notification!!.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(defaultChannel)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }*/
}