package com.blasthack.storm.lottostorm

import com.blasthack.storm.lottostorm.dto.Client

class Config {
    companion object {
        const val SERVER_ADDRESS = "192.168.43.245:90"
        const val WEBSOCKET_ADDRESS = "ws://$SERVER_ADDRESS/storm?client"

        const val DEFAULT_GROUND_SIZE = 1000 * 50 * 2

        var client = Client("", 0, 0)
    }
}