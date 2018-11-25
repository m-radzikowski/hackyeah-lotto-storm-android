package com.blasthack.storm.lottostorm.utils

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat

class BitmapUtils {

    companion object {
        fun drawableToBitmap(context: Context, drawableId: Int, colorId: Int): Bitmap {
            val bitmap = BitmapFactory
                .decodeResource(context.resources, drawableId)
                .copy(Bitmap.Config.ARGB_8888, true)

            val paint = Paint()
            val filter = PorterDuffColorFilter(
                ContextCompat.getColor(context, colorId),
                PorterDuff.Mode.SRC_IN
            )
            paint.colorFilter = filter

            val canvas = Canvas(bitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            return bitmap
        }
    }

}