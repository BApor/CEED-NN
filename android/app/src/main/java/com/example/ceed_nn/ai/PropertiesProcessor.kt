package com.example.ceed_nn.ai

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.core.ImageProxy
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

object PropertiesProcessor {

    fun calculateReferencePixels(detections: List<NonMaxSuppression.DetectResult>) : Int {
        for (i in 0 until detections.size)
            if (detections[i].classId == 0) {
                return (detections[i].boundingBox.right - detections[i].boundingBox.left) * (detections[i].boundingBox.bottom - detections[i].boundingBox.top)
            }
        return 0
    }

    fun calculateSeedPixels(detections: List<NonMaxSuppression.DetectResult>, imageProxy: ImageProxy, referenceScale: Float) : List<Float> {
        val bitmap = imageProxy.toBitmap()
        val results = mutableListOf<Float>()

        for (detection: NonMaxSuppression.DetectResult in detections) {
            val width = detection.boundingBox.width()
            val height = detection.boundingBox.height()

            val outputBitmap = Bitmap.createBitmap(width, height, bitmap.config)
            val canvas = android.graphics.Canvas(outputBitmap)
            canvas.drawBitmap(bitmap, detection.boundingBox, Rect(0, 0, width, height), null)

            // Background color grey-white between RGB(150, 150, 150) and RGB(240, 240, 240)
            var nonBackgroundPixels = 0 // Seed pixels
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pixel = outputBitmap.getPixel(x, y)
                    if (!((pixel.red in 151..239) &&
                          (pixel.green in 151..239) &&
                          (pixel.blue in 151..239))) {
                        nonBackgroundPixels++
                    }
                }
            }

            var o = 0
            if (detection.classId == 10 && detections.size == 2)
                o = 2

            results.add(nonBackgroundPixels * referenceScale)
        }

        return results
    }
}