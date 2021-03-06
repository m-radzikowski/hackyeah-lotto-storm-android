package com.blasthack.storm.lottostorm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.blasthack.storm.lottostorm.service.PushClient
import com.blasthack.storm.lottostorm.service.StormBackendService
import com.blasthack.storm.lottostorm.service.StormRepository
import com.google.firebase.FirebaseApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener { _ ->
            var username = login_email.text.toString()
            if (username.isEmpty()) {
                username = "tester"
            }

            val preferencesHelper = PreferencesHelper(this)
            FirebaseApp.initializeApp(this)
            var token = preferencesHelper.deviceToken
            if (token == null) {
                token = ""
            }

            registerPush(username, token)
        }
    }

    private fun registerPush(username: String, token: String) {
        StormBackendService.create(StormRepository::class.java)
            .register(PushClient(username, token))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.d("PUSH", "Successfully registered new chat channel.")

                    Config.client.name = username
                    Config.client.id = it.id.toInt()

                    val preferencesHelper = PreferencesHelper(this)
                    preferencesHelper.myId = it.id

                    val intent = Intent(applicationContext, MapsActivity::class.java)
                    startActivity(intent)
                },
                { _: Throwable? ->
                    Log.d("PUSH", "Failed to register new chat channel!")
                }
            )
    }
}
