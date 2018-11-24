package com.blasthack.storm.lottostorm

class Config {
    companion object {
        const val SERVER_ADDRESS = "192.168.43.55:90"
        const val WEBSOCKET_ADDRESS = "ws://$SERVER_ADDRESS/storm?client"

        const val DEFAULT_SCALE = 1000.0
    }
}