package com.blasthack.storm.lottostorm

import com.blasthack.storm.lottostorm.dto.Storm

interface StormEventListener {

    fun onStormChanged(storm: Storm)
}