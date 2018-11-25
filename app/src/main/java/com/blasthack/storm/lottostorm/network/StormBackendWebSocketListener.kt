package com.blasthack.storm.lottostorm.network

import android.util.Log
import com.blasthack.storm.lottostorm.map.StormEventListener
import com.blasthack.storm.lottostorm.dto.LotteryWinner
import com.blasthack.storm.lottostorm.dto.Storm
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Response
import okhttp3.WebSocketListener
import okhttp3.WebSocket
import okio.ByteString

class StormBackendWebSocketListener(var listener: StormEventListener) : WebSocketListener() {

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        Log.d("SOCKET", "Receiving : " + text!!)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        if (text[0] == '[') {
            val stormAdapter = moshi.adapter(Array<Storm>::class.java)
            val storms: Array<Storm>? = stormAdapter.fromJson(text)
            storms!!.forEach {
                listener.onStormChanged(it)
            }
        } else {
            val winnerAdapter = moshi.adapter(LotteryWinner::class.java)
            val winner: LotteryWinner? = winnerAdapter.fromJson(text)
            listener.onStormWon(winner!!)
        }
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        Log.d("SOCKET", "Receiving bytes : " + bytes!!.hex())
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        webSocket!!.close(1000, null)
        Log.d("SOCKET", "Closing : $code / $reason")
        listener.onConnectionFailed()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("SOCKET", "Fail")
        listener.onConnectionFailed()
        super.onFailure(webSocket, t, response)
    }
}