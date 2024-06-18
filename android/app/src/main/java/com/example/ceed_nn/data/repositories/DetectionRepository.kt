package com.example.ceed_nn.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.camera.core.ImageProxy
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.ceed_nn.ai.DetectionEngine
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO
import com.example.ceed_nn.util.ImageUtil
import com.example.ceed_nn.util.NumUtil

class DetectionRepository(private var context: Context) {
    private lateinit var detEngine: DetectionEngine
    private lateinit var detections: List<SeedDetectionDTO>
    private lateinit var image: ImageProxy

    private var referenceScale = 0f
    private var referenceCounter = 0
    private val referenceNumber = 10
    private var newReferenceScale = 0f
    private var time = 0.0


    fun setDetEngineModel(model: String) {
        detEngine = DetectionEngine(context, model)
    }

    fun setCurrentFrame(imageProxy: ImageProxy) {
        image = imageProxy
    }

    fun fetchDetections() {
        detections = detEngine.detect(image)
        time = detEngine.getTime()
    }

    fun getDetections(): List<SeedDetectionDTO>  {
        return detections
    }

    fun getReferenceScale(): Float {
        return referenceScale
    }

    fun getTime(): Double {
        return time
    }

    private fun calculateReferencePixels() : Int{
        var max = 0f
        var maxReferenceDetection = SeedDetectionDTO(Rect(0,0,0,0), 0, 0f, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888), 0f, 0f, 0f, 0f)

        for (detection: SeedDetectionDTO in detections)
            if (detection.score > max && detection.classId == 0){
                max = detection.score
                maxReferenceDetection = detection
            }

        if (max == 0f)
            return 0

        return (maxReferenceDetection.boundingBox.right - maxReferenceDetection.boundingBox.left) * (maxReferenceDetection.boundingBox.bottom - maxReferenceDetection.boundingBox.top)
    }

    fun calculateSeedAreas() {
        val bitmap = ImageUtil.rotateBitmap(image.toBitmap(), 90f)

        for (i in 0 until detections.size) {
            val left = detections[i].boundingBox.left
            val top = detections[i].boundingBox.top
            val width = detections[i].boundingBox.width()
            val height = detections[i].boundingBox.height()

            if (left < 0 || top < 0 || width < 0 || height < 0 ||
                (left + width) > bitmap.width )
                continue

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

            detections[i].seedArea = NumUtil.floatRoundTo(nonBackgroundPixels * referenceScale,2)

            val canvas = Canvas(detectionCrop)
            val paint = Paint().apply {
                color = Color.RED
                textSize = 40f
                isAntiAlias = true
            }
            canvas.drawText(i.toString(), 10f, 50f, paint)

            detections[i].photo = detectionCrop
        }
    }

    fun calculateReferenceScale() {
        if (referenceCounter < referenceNumber){
            val newPartialReferenceScale = 100f / calculateReferencePixels()
            if (newPartialReferenceScale != Float.POSITIVE_INFINITY
                && newPartialReferenceScale  != 0f){
                newReferenceScale += newPartialReferenceScale
                referenceCounter++
            }
        } else {
            newReferenceScale /= referenceNumber
        }

        if ((newReferenceScale != 0f && newReferenceScale != Float.POSITIVE_INFINITY) &&
            (referenceCounter == referenceNumber)) {
            referenceScale = newReferenceScale / 10
            newReferenceScale = 0f
        }

        if (referenceCounter == referenceNumber) {
            referenceCounter = 0
        }
    }
}