package com.blasthack.storm.lottostorm.service

import android.app.NotificationChannel
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.blasthack.storm.lottostorm.MapsActivity
import com.blasthack.storm.lottostorm.PreferencesHelper
import com.blasthack.storm.lottostorm.R
import com.blasthack.storm.lottostorm.database.AppDatabase
import com.blasthack.storm.lottostorm.database.token.Token
import com.blasthack.storm.lottostorm.database.token.TokenDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NotificationMessageService : FirebaseMessagingService() {

    private val tag = "EXPO_FIREBASE"
    private val defaultChannel = "default_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(tag, "From: " + remoteMessage!!.from)
        Log.d(tag, "Notification Message Body: " + remoteMessage.notification!!.body!!)

        sendNotification(remoteMessage)
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        val preferencesHelper = PreferencesHelper(this)
      if (token != null ) {
          preferencesHelper.deviceToken = token
      }
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", token).apply();

    }


    private fun sendNotification(remoteMessage: RemoteMessage) {
        val app = application
        val notification = remoteMessage.notification
        val intent = Intent(this,MapsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, defaultChannel)
            .setSmallIcon(R.drawable.calendar_grey_24dp)
                .setContentTitle(notification!!.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(defaultChannel)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "jakis"
            val descriptionText = "inny"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("3", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}