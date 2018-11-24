package com.blasthack.storm.lottostorm

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.blasthack.storm.lottostorm.dto.Storm

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
import kotlin.collections.ArrayList


@SuppressLint("SetTextI18n")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, StormEventListener {

    private lateinit var mMap: GoogleMap

    private var storms: ArrayList<StormCircle> = arrayListOf()

    private var globalExpo = LatLng(52.2922104, 21.0023798)
    private var globalExpoLocation: CameraPosition = CameraPosition.Builder()
        .target(globalExpo)
        .zoom(8.0f)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fab.setOnClickListener {
            showDialog()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val client = OkHttpClient()
        val listener = StormBackendWebSocketListener(this)
        val request = Request.Builder().url(Config.WEBSOCKET_ADDRESS).build()
        client.newWebSocket(request, listener)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(globalExpoLocation))
    }

    override fun onStormChanged(storm: Storm) {
        if (!::mMap.isInitialized) {
            return
        }

        runOnUiThread {
            val foundStorm = storms.find { it.id == storm.id }
            if (foundStorm == null) {
                val newStormCircle = StormCircle(this, storm.id, LatLng(storm.lat, storm.lng))
                newStormCircle.addToMap(mMap)
                storms.add(newStormCircle)
            } else {
                foundStorm.setCenter(LatLng(storm.lat, storm.lng))
                foundStorm.addRotation()
            }
        }
    }

    private fun showDialog() {
        val dialogs = Dialog(this)
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setContentView(R.layout.dialog_lottery)
        dialogs.findViewById<MaterialButton>(R.id.lottery_play).setOnClickListener {
            Toast.makeText(this, "You have entered lottery!", Toast.LENGTH_LONG).show()
            blockLotteryButton()
            dialogs.dismiss()
        }
        dialogs.findViewById<MaterialButton>(R.id.lottery_cancel).setOnClickListener {
            dialogs.dismiss()
        }
        dialogs.show()
    }

    private fun blockLotteryButton() {
        var lockRemaining = 3
        updateLockedLotteryButton(lockRemaining)

        Timer("LotteryLock", false).scheduleAtFixedRate(0, (1000 * 1).toLong()) {
            lockRemaining--
            if (lockRemaining <= 0) {
                unlockLotteryButton()
                cancel()
            } else {
                updateLockedLotteryButton(lockRemaining)
            }
        }
    }

    private fun unlockLotteryButton() {
        runOnUiThread {
            fab.isEnabled = true
            fab.text = getString(R.string.lottery_enter)
            fab.icon = getDrawable(R.drawable.clover)
            fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
        }
    }

    private fun updateLockedLotteryButton(lockRemaining: Int) {
        runOnUiThread {
            fab.isEnabled = false
            fab.text = "Locked for $lockRemaining"
            fab.icon = getDrawable(R.drawable.cancel)
            fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGrey))
        }

    }
}
