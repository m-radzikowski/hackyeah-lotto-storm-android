package com.blasthack.storm.lottostorm

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.blasthack.storm.lottostorm.service.NotifyFriendActivity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import okhttp3.OkHttpClient
import okhttp3.Request

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val DEFAULT_SCALE = 1000.0

    private var globalExpo = LatLng(52.2922104, 21.0023798)
    private var globalExpoLocation: CameraPosition = CameraPosition.Builder()
        .target(globalExpo)
        .zoom(8.0f)
        .build()

    private lateinit var storm: StormCircle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fab.setOnClickListener {
            showDialog()
        }
        notifyStorm.setOnClickListener {
            val intent = Intent(applicationContext, NotifyFriendActivity::class.java)
            startActivity(intent)
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val client = OkHttpClient()
        val listener = StormBackendWebSocketListener()
        val request = Request.Builder().url(Config.WEBSOCKET_ADDRESS).build()
        client.newWebSocket(request, listener)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        storm = StormCircle(globalExpo)
        storm.addToMap(mMap)

        Timer("StormMockTimer", false).scheduleAtFixedRate(0, (1000 * 0.2).toLong()) {
            if (::storm.isInitialized) {
                globalExpo = LatLng(globalExpo.latitude + 0.0025, globalExpo.longitude + 0.0025)
                runOnUiThread {
                    storm.setCenter(globalExpo)
                    Log.d("MAPS", "Updated storm location")
                }
            }
        }

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(globalExpoLocation))
    }

    private fun showDialog() {
        val dialogs = Dialog(this)
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setContentView(R.layout.dialog_lottery)
        dialogs.findViewById<MaterialButton>(R.id.lottery_play).setOnClickListener {
            Toast.makeText(this, "You have entered lottery!", Toast.LENGTH_LONG).show()
        }
        dialogs.findViewById<MaterialButton>(R.id.lottery_cancel).setOnClickListener {
            dialogs.dismiss()
        }
        dialogs.show()
    }
}
