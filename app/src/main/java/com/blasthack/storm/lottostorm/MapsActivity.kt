package com.blasthack.storm.lottostorm

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.blasthack.storm.lottostorm.service.NotifyFriendActivity
import com.blasthack.storm.lottostorm.service.StormBackendService
import com.blasthack.storm.lottostorm.service.StormRepository
import com.blasthack.storm.lottostorm.utils.BitmapUtils

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
import com.shawnlin.numberpicker.NumberPicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


@SuppressLint("SetTextI18n")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, StormEventListener {

    private lateinit var mMap: GoogleMap
    private lateinit var socket: WebSocket

    private var playerLockRemainingTime = 0

    var statusSnackBar: TSnackbar? = null

    private var storms: ArrayList<StormCircle> = arrayListOf()

    private var playerPosition = LatLng(52.2922104, 21.0023798)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val myId = intent.getIntExtra("myId", -1)

        fab.setOnClickListener {
            participateInLottery()
        }

        tickets.setOnClickListener {
            showTicketsDialog()
        }

        center.setOnClickListener {
            if (::mMap.isInitialized) {
                mMap.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(playerPosition)
                            .zoom(12.0f)
                            .build()
                    )
                )
            }
        }

        notifyStorm.setOnClickListener {
            val intent = Intent(applicationContext, NotifyFriendActivity::class.java)
            startActivity(intent)
        }

        tickets_count.text = Config.client.balance.toString()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // TODO: not always working (get balance)
        getBalance()
        connectToWebSocket()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(playerPosition)
                    .zoom(10.0f)
                    .build()
            )
        )
        startLocalization()
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

        if (playerLockRemainingTime <= 0) {
            unlockLotteryButton()
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
            if (Config.client.id.toLong() == winner.ticket.userId) {
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle("Wygrana")
                alertDialog.setMessage("Gratulacje! Burza skumuluwała sie i zalała cię hajsem. Zgarnąłeś X PLN!")
                alertDialog.show()
            } else {
                showInfoSnackBar(getString(R.string.lottery_finished))
            }

            val bitmap = BitmapUtils.drawableToBitmap(this, R.drawable.flash, android.R.color.holo_red_dark)
            mMap.addGroundOverlay(GroundOverlayOptions().apply {
                image(BitmapDescriptorFactory.fromBitmap(bitmap))
                position(LatLng(winner.storm.lat, winner.storm.lng), 1500f, 1500f)
            })
        }
    }

    override fun onConnectionFailed() {
        showStatusSnackBar(getString(R.string.connection_failed), getString(R.string.reconnect))
    }

    private fun startLocalization() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationChangeListener { playerPosition = LatLng(it.latitude, it.longitude) }
        } else {
            showStatusSnackBar("Lokalizacja wyłączona", "Włącz")
        }
    }

    private fun connectToWebSocket() {
        val client = OkHttpClient()
        val listener = StormBackendWebSocketListener(this)
        val request = Request.Builder().url(Config.WEBSOCKET_ADDRESS).build()
        socket = client.newWebSocket(request, listener)
    }

    private fun showTicketsDialog() {
        val dialogs = Dialog(this)

        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setContentView(R.layout.dialog_lottery)
        dialogs.findViewById<MaterialButton>(R.id.lottery_play).setOnClickListener { _ ->
            val couponSelector = dialogs.findViewById<NumberPicker>(R.id.number_picker)
            StormBackendService.create(StormRepository::class.java)
                .updateCoupons(Config.client.id, couponSelector.value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        showInfoSnackBar("Doładowano kupony")

                        Config.client.balance = it.balance
                        tickets_count.text = Config.client.balance.toString()
                    },
                    { _: Throwable? ->
                        Log.d("COUPONS", "Failed to update coupons!")
                    }
                )

            dialogs.dismiss()
        }
        dialogs.findViewById<MaterialButton>(R.id.lottery_cancel).setOnClickListener {
            dialogs.dismiss()
        }
        dialogs.show()
    }

    private fun participateInLottery() {
        if (Config.client.balance <= 0) {
            showInfoSnackBar("Brak kuponów! Zakup więcej aby zagrać.")
            return
        }

        val ticket = LotteryTicket(1, playerPosition.longitude, playerPosition.latitude)
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(LotteryTicket::class.java)
        val json = jsonAdapter.toJson(ticket)
        socket.send(json)

        Config.client.balance = Config.client.balance--
        tickets_count.text = Config.client.balance.toString()

        blockLotteryButton()
    }

    private fun blockLotteryButton() {
        playerLockRemainingTime = 3
        updateLockedLotteryButton("Graj dalej za $playerLockRemainingTime")

        Timer("LotteryLock", false).scheduleAtFixedRate(0, (1000 * 1).toLong()) {
            playerLockRemainingTime--
            if (playerLockRemainingTime <= 0) {
                unlockLotteryButton()
                cancel()
            } else {
                updateLockedLotteryButton("Graj dalej za $playerLockRemainingTime")
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
            if (it.containsPlayer(playerPosition)) {
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

    private fun getBalance() {
        StormBackendService.create(StormRepository::class.java)
            .getCoupons(Config.client.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Config.client.balance = it.balance
                    tickets_count.text = Config.client.balance.toString()
                },
                { _: Throwable? ->
                    Log.d("COUPONS", "Failed to update coupons!")
                }
            )
    }

    private fun showInfoSnackBar(text: String) {
        val snackBar = TSnackbar.make(root, text, TSnackbar.LENGTH_SHORT)
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
