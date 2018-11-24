package com.blasthack.storm.lottostorm

import android.util.Log
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
        val stormAdapter = moshi.adapter(Array<Storm>::class.java)
        val storms: Array<Storm>? = stormAdapter.fromJson(text)
        storms!!.forEach {
            listener.onStormChanged(it)
        }
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        Log.d("SOCKET", "Receiving bytes : " + bytes!!.hex())
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        webSocket!!.close(1000, null)
        Log.d("SOCKET", "Closing : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("SOCKET", "Fail")
        super.onFailure(webSocket, t, response)
    }
}