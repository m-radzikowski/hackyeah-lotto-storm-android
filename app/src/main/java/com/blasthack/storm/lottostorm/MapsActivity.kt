package com.blasthack.storm.lottostorm

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.androidadvance.topsnackbar.TSnackbar
import com.blasthack.storm.lottostorm.dto.LotteryTicket
import com.blasthack.storm.lottostorm.dto.LotteryWinner
import com.blasthack.storm.lottostorm.dto.Storm
import com.blasthack.storm.lottostorm.map.StormCircle
import com.blasthack.storm.lottostorm.map.StormEventListener
import com.blasthack.storm.lottostorm.network.StormBackendWebSocketListener

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
import okhttp3.WebSocket
import kotlin.collections.ArrayList
import com.squareup.moshi.Moshi
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CircleOptions


@SuppressLint("SetTextI18n")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, StormEventListener {

    private lateinit var mMap: GoogleMap
    private lateinit var socket: WebSocket

    private var playerLockRemainingTime = 0

    var statusSnackBar: TSnackbar? = null

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
            //showDialog()
            participateInLottery()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        connectToWebSocket()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(globalExpoLocation))
        mMap.addCircle(
            CircleOptions()
                .center(globalExpo)
                .radius(20.0)
                .strokeColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .fillColor(ContextCompat.getColor(this, R.color.colorPrimary))
        )
    }

    override fun onStormChanged(storm: Storm) {
        if (!::mMap.isInitialized) {
            return
        }

        runOnUiThread {
            val foundStorm = storms.find { it.id == storm.id }
            if (foundStorm == null) {
                val newStormCircle =
                    StormCircle(this, storm.id, LatLng(storm.lat, storm.lng))
                newStormCircle.addToMap(mMap)
                storms.add(newStormCircle)
            } else {
                foundStorm.setCenter(LatLng(storm.lat, storm.lng))
                foundStorm.addRotation()
            }
        }

        if (playerLockRemainingTime <= 0) {
            if (canPlayerParticipate()) {
                unlockLotteryButton()
            } else {
                updateLockedLotteryButton(getString(R.string.lottery_invalid))
            }
        }
    }

    override fun onStormWon(winner: LotteryWinner) {
        if (!::mMap.isInitialized) {
            return
        }

        runOnUiThread {
            val foundStorm = storms.find { it.id == winner.storm.id }
            if (foundStorm != null) {
                foundStorm.removeFromMap()
                storms.remove(foundStorm)
            }

            showInfoSnackBar(getString(R.string.lottery_finished))
        }
    }

    override fun onConnectionFailed() {
        showStatusSnackBar(getString(R.string.connection_failed), getString(R.string.reconnect))
    }

    private fun connectToWebSocket() {
        val client = OkHttpClient()
        val listener = StormBackendWebSocketListener(this)
        val request = Request.Builder().url(Config.WEBSOCKET_ADDRESS).build()
        socket = client.newWebSocket(request, listener)
    }

    private fun showDialog() {
        val dialogs = Dialog(this)
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setContentView(R.layout.dialog_lottery)
        dialogs.findViewById<MaterialButton>(R.id.lottery_play).setOnClickListener {
            participateInLottery()
            dialogs.dismiss()
        }
        dialogs.findViewById<MaterialButton>(R.id.lottery_cancel).setOnClickListener {
            dialogs.dismiss()
        }
        dialogs.show()
    }

    private fun participateInLottery() {
        // TODO: hardcoded data for now
        val ticket = LotteryTicket(1, 52.2922104, 21.0023798)
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(LotteryTicket::class.java)
        val json = jsonAdapter.toJson(ticket)
        socket.send(json)

        blockLotteryButton()
    }

    private fun blockLotteryButton() {
        playerLockRemainingTime = 3
        updateLockedLotteryButton("Locked for $playerLockRemainingTime")

        Timer("LotteryLock", false).scheduleAtFixedRate(0, (1000 * 1).toLong()) {
            playerLockRemainingTime--
            if (playerLockRemainingTime <= 0) {
                unlockLotteryButton()
                cancel()
            } else {
                updateLockedLotteryButton("Locked for $playerLockRemainingTime")
            }
        }
    }

    private fun unlockLotteryButton() {
        runOnUiThread {
            if (canPlayerParticipate()) {
                fab.isEnabled = true
                fab.text = getString(R.string.lottery_enter)
                fab.icon = getDrawable(R.drawable.clover)
                fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent))
            } else {
                updateLockedLotteryButton(getString(R.string.lottery_invalid))
            }
        }
    }

    private fun canPlayerParticipate(): Boolean {
        storms.forEach {
            if (it.containsPlayer(globalExpo)) {
                return true
            }
        }
        return false
    }

    private fun updateLockedLotteryButton(text: String) {
        runOnUiThread {
            fab.isEnabled = false
            fab.text = text
            fab.icon = getDrawable(R.drawable.cancel)
            fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGrey))
        }
    }

    private fun showInfoSnackBar(text: String) {
        val snackBar = TSnackbar.make(root, text, TSnackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        snackBarView.setPadding(4, 4, 4, 4)
        val textView = snackBarView.findViewById<TextView>(com.androidadvance.topsnackbar.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        textView.textSize = 16f
        snackBar.show()
    }

    private fun showStatusSnackBar(text: String, action: String) {
        statusSnackBar = TSnackbar.make(root, text, TSnackbar.LENGTH_INDEFINITE).setAction(action) {
            connectToWebSocket()
        }
        statusSnackBar!!.setActionTextColor(Color.WHITE)
        val snackBarView = statusSnackBar!!.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        snackBarView.setPadding(4, 4, 4, 4)
        val textView = snackBarView.findViewById<TextView>(com.androidadvance.topsnackbar.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        textView.textSize = 16f
        statusSnackBar!!.show()
    }
}
