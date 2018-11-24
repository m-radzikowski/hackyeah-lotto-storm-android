package com.blasthack.storm.lottostorm

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng

class StormCircle(
    var centerPosition: LatLng
) {

    private val outerCircleOptions = CircleOptions().apply {
        radius(50000.0)
        center(centerPosition)
        strokeWidth(5.0f)
        strokeColor(Color.HSVToColor(floatArrayOf(120f, 100f, 100f)))
        fillColor(Color.HSVToColor(150, floatArrayOf(120f, 100f, 100f)))
        //strokePattern(getSelectedPattern(strokePatternSpinner.selectedItemPosition))
    }

    private val middleCircleOptions = CircleOptions().apply {
        radius(25000.0)
        center(centerPosition)
        strokeWidth(5.0f)
        strokeColor(Color.HSVToColor(floatArrayOf(60f, 100f, 100f)))
        fillColor(Color.HSVToColor(150, floatArrayOf(60f, 100f, 100f)))
        //strokePattern(getSelectedPattern(strokePatternSpinner.selectedItemPosition))
    }

    private val innerCircleOptions = CircleOptions().apply {
        radius(5000.0)
        center(centerPosition)
        strokeWidth(5.0f)
        strokeColor(Color.HSVToColor(floatArrayOf(1f, 1f, 1f)))
        fillColor(Color.HSVToColor(150, floatArrayOf(1f, 1f, 1f)))
        //strokePattern(getSelectedPattern(strokePatternSpinner.selectedItemPosition))
    }

    private lateinit var outerCircle: Circle
    private lateinit var middleCircle: Circle
    private lateinit var innerCircle: Circle

    fun setCenter(center: LatLng) {
        centerPosition = center
        outerCircle.center = center
        middleCircle.center = center
        innerCircle.center = center
    }

    fun addToMap(map: GoogleMap) {
        outerCircle = map.addCircle(this.outerCircleOptions)
        middleCircle = map.addCircle(this.middleCircleOptions)
        innerCircle = map.addCircle(this.innerCircleOptions)
    }
}