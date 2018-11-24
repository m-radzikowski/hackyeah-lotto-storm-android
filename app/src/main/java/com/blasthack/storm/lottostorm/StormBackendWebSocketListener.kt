package com.blasthack.storm.lottostorm

import android.util.Log
import okhttp3.WebSocketListener
import okhttp3.WebSocket
import okio.ByteString

class StormBackendWebSocketListener : WebSocketListener() {

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        Log.d("MAPS", "Receiving : " + text!!)
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        Log.d("MAPS", "Receiving bytes : " + bytes!!.hex())
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        webSocket!!.close(1000, null)
        Log.d("MAPS", "Closing : $code / $reason")
    }

}