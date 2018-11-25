package com.blasthack.storm.lottostorm.map

import com.blasthack.storm.lottostorm.dto.LotteryWinner
import com.blasthack.storm.lottostorm.dto.Storm

interface StormEventListener {

    fun onConnectionFailed()

    fun onStormChanged(storm: Storm)

    fun onStormWon(winner: LotteryWinner)
}