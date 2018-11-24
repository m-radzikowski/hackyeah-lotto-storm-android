package com.blasthack.storm.lottostorm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.blasthack.storm.lottostorm.service.PushClient
import com.blasthack.storm.lottostorm.service.StormBackendService
import com.blasthack.storm.lottostorm.service.StormRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            var username = login_email.text.toString()
            if (username.isEmpty()){
                username = "tester"
            }
            StormBackendService.create(StormRepository::class.java).register(PushClient(username, "qwerty"))                .subscribeOn(
                Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d("sd", "Successfully registered new chat channel.")

                        Toast.makeText(this, it.id, Toast.LENGTH_LONG).show()

                    },
                    { _: Throwable? ->
                        Log.d("ssd", "Failed to register new chat channel!")

                        Toast.makeText(this, "jakis", Toast.LENGTH_LONG).show()
                    }
                )
            val intent = Intent(applicationContext, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}
