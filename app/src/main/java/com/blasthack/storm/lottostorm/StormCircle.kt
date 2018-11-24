package com.blasthack.storm.lottostorm

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng

class StormCircle(
    var centerPosition: LatLng
) {
    private val circleOptions = CircleOptions().apply {
        radius(50000.0)
        center(centerPosition)
        strokeWidth(5.0f)
        strokeColor(Color.HSVToColor(floatArrayOf(120f, 100f, 100f)))
        fillColor(Color.HSVToColor(150, floatArrayOf(120f, 100f, 100f)))
        //strokePattern(getSelectedPattern(strokePatternSpinner.selectedItemPosition))
    }

    private lateinit var circle: Circle

    fun setCenter(center: LatLng) {
        centerPosition = center
        circle.center = center
    }

    fun addToMap(map: GoogleMap) {
        circle = map.addCircle(this.circleOptions)
    }
}