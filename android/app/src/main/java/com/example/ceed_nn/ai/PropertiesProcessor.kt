package com.example.ceed_nn.ai

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.core.ImageProxy
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

object PropertiesProcessor {

    fun calculateReferencePixels(detections: List<DetectResult>) : Int {
        var max = 0f
        var maxReferenceDetection: DetectResult = DetectResult(Rect(0,0,0,0), 0, 0f, 0f)

        for (detection: DetectResult in detections)
            if (detection.score > max && detection.classId == 0){
                max = detection.score
                maxReferenceDetection = detection
            }

        if (max == 0f)
            return 0

        return (maxReferenceDetection.boundingBox.right - maxReferenceDetection.boundingBox.left) * (maxReferenceDetection.boundingBox.bottom - maxReferenceDetection.boundingBox.top)


    }

    fun calculateSeedPixels(detections: List<DetectResult>, imageProxy: ImageProxy, referenceScale: Float) : List<DetectResult>{
        val detectionsWithSeedAreas = detections
        val bitmap = PytorchMobile.rotateBitmap(imageProxy.toBitmap(), 90f)

        for (i in 0 until detectionsWithSeedAreas.size) {
            val left = detectionsWithSeedAreas[i].boundingBox.left
            val top = detectionsWithSeedAreas[i].boundingBox.top
            val width = detectionsWithSeedAreas[i].boundingBox.width()
            val height = detectionsWithSeedAreas[i].boundingBox.height()

            val detectionCrop = Bitmap.createBitmap(bitmap, left, top, width, height)

            // Background color grey-white between RGB(150, 150, 150) and RGB(240, 240, 240)
            var nonBackgroundPixels = 0 // Seed pixels
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = detectionCrop.getPixel(x, y)
                    if (!((pixel.red in 151..239) &&
                          (pixel.green in 151..239) &&
                          (pixel.blue in 151..239))) {
                        nonBackgroundPixels++
                    }
                }
            }

            detectionsWithSeedAreas[i].seedArea = nonBackgroundPixels * referenceScale
        }

        return detectionsWithSeedAreas
    }
}