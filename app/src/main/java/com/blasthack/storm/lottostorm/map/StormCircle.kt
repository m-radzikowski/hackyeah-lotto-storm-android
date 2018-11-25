package com.blasthack.storm.lottostorm.map

import android.content.Context
import android.graphics.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import androidx.core.content.ContextCompat
import com.blasthack.storm.lottostorm.Config
import com.blasthack.storm.lottostorm.R
import java.lang.Exception

class StormCircle(
    private var context: Context,
    var id: String,
    private var centerPosition: LatLng
) {

    private lateinit var storm: GroundOverlay

    fun setCenter(center: LatLng) {
        centerPosition = center
        storm.position = center
    }

    fun addRotation() {
        storm.bearing = storm.bearing + 1
    }

    fun addToMap(map: GoogleMap) {
        val bitmap = BitmapFactory
            .decodeResource(context.resources, R.drawable.cloud_2)
            .copy(Bitmap.Config.ARGB_8888, true)

        val paint = Paint()
        val filter = PorterDuffColorFilter(
            ContextCompat.getColor(context, R.color.stormColor),
            PorterDuff.Mode.SRC_IN
        )
        paint.colorFilter = filter

        val canvas = Canvas(bitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        storm = map.addGroundOverlay(GroundOverlayOptions().apply {
            image(BitmapDescriptorFactory.fromBitmap(bitmap))
            position(centerPosition, (Config.DEFAULT_GROUND_SIZE).toFloat(), (Config.DEFAULT_GROUND_SIZE).toFloat())
        })
    }

    fun removeFromMap() {
        storm.remove()
    }

    fun containsPlayer(player: LatLng): Boolean {
        return try {
            storm.bounds.contains(player)
        } catch (e: Exception) {
            false
        }
    }
}