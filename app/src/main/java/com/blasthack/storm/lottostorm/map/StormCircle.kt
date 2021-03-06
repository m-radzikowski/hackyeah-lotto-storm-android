package com.blasthack.storm.lottostorm.map

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.blasthack.storm.lottostorm.Config
import com.blasthack.storm.lottostorm.R
import com.blasthack.storm.lottostorm.utils.BitmapUtils

class StormCircle(
    private var context: Context,
    var id: String,
    var index: Number,
    private var centerPosition: LatLng
) {

    private lateinit var storm: GroundOverlay

    var width = (Config.DEFAULT_GROUND_SIZE).toFloat()

    fun setCenter(center: LatLng) {
        centerPosition = center
        storm.position = center
    }

    fun addRotation() {
        storm.bearing = storm.bearing + 1
    }

    fun addToMap(map: GoogleMap) {
        var bitmap = BitmapUtils.drawableToBitmap(context, R.drawable.cloud_2, R.color.stormColor)

        if (index == 0) {
            width *= 2
            bitmap = BitmapUtils.drawableToBitmap(context, R.drawable.cloud_2, R.color.colorOrange)
        } else if (index == 1) {
            width *= 1.5f
            bitmap = BitmapUtils.drawableToBitmap(context, R.drawable.cloud_2, R.color.colorGreen)
        }
        storm = map.addGroundOverlay(GroundOverlayOptions().apply {
            image(BitmapDescriptorFactory.fromBitmap(bitmap))
            position(centerPosition, width, width)
        })
    }

    fun removeFromMap() {
        storm.remove()
    }

    fun containsPlayer(player: LatLng): Boolean {
        val distance = FloatArray(2)
        Location.distanceBetween(
            player.latitude,
            player.longitude,
            centerPosition.latitude,
            centerPosition.longitude,
            distance
        )
        return distance[0] <= (width / 2)
    }
}