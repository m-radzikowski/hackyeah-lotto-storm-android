package com.blasthack.storm.lottostorm

import android.content.Context
import android.graphics.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import androidx.core.content.ContextCompat


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
            .decodeResource(context.resources, R.drawable.cloud)
            .copy(Bitmap.Config.ARGB_8888, true)

        val paint = Paint()
        val filter = PorterDuffColorFilter(
            ContextCompat.getColor(context, android.R.color.holo_red_dark),
            PorterDuff.Mode.SRC_IN
        )
        paint.colorFilter = filter

        val canvas = Canvas(bitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        storm = map.addGroundOverlay(GroundOverlayOptions().apply {
            image(BitmapDescriptorFactory.fromBitmap(bitmap))
            position(centerPosition, 100000f, 65000f)
        })
    }
}