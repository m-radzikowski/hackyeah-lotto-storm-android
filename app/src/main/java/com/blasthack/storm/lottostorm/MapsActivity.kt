package com.blasthack.storm.lottostorm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import okhttp3.OkHttpClient
import okhttp3.Request


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val DEFAULT_SCALE = 1000.0
    private val DEFAULT_RADIUS_METERS = 1000000.0

    private var globalExpo = LatLng(52.2922104, 21.0023798)
    private var globalExpoLocation: CameraPosition = CameraPosition.Builder()
        .target(globalExpo)
        .zoom(8.0f)
        .build()

    private lateinit var storm: StormCircle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

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
                runOnUiThread {
                    globalExpo = LatLng(globalExpo.latitude + 0.0025, globalExpo.longitude + 0.0025)
                    storm.setCenter(globalExpo)

                    Log.d("MAPS", "Updated storm location")
                }
            }
        }

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(globalExpoLocation))
    }
}
