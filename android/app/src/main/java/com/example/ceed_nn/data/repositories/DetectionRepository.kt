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

    private lateinit var bestReference: SeedDetectionDTO

    private var referenceCounter = 0
    private val referenceNumber = 10

    private var areaRatio = 0f
    private var newAreaRatio = 0f

    private var lengthRatio = 0f
    private var newLengthRatio = 0f

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

    fun getAreaRatio(): Float {
        return areaRatio
    }

    fun getLengthRatio(): Float {
        return lengthRatio;
    }

    fun getTime(): Double {
        return time
    }

    private fun determineBestReferenceDetection() {
        if (detections.size <= 0)
            return
        val referenceDetections = detections.filter { it.classId == 0 }
        var maxScore = 0f
        var maxPixels = 0

        for (detection: SeedDetectionDTO in referenceDetections) {
            val pixels = (detection.boundingBox.right - detection.boundingBox.left) * (detection.boundingBox.bottom - detection.boundingBox.top)
            if (detection.score > maxScore &&
                pixels > maxPixels &&
                pixels > 9000) {
                maxPixels = pixels
                maxScore = detection.score
                bestReference = detection
            }
        }
    }

    private fun calculateReferencePixels() : Int{
        detections = detections.filter { it.classId != 0 }
        val mutDetections = detections.toMutableList()
        mutDetections.add(bestReference)
        detections = mutDetections

        return (bestReference.boundingBox.right - bestReference.boundingBox.left) * (bestReference.boundingBox.bottom - bestReference.boundingBox.top)
    }

    private fun calculateReferenceSidePixels(): Int {
        return ((bestReference.boundingBox.right - bestReference.boundingBox.left) +
                (bestReference.boundingBox.bottom - bestReference.boundingBox.top)) / 2
    }

    fun calculatePhysicalRatios() {
        if (referenceCounter < referenceNumber){
            determineBestReferenceDetection()
            val newPartialAreaRatio = 100f / calculateReferencePixels()
            val newPartialLengthRatio = 10f / calculateReferenceSidePixels()
            if (newPartialAreaRatio != Float.POSITIVE_INFINITY
                && newPartialAreaRatio  != 0f){
                newAreaRatio += newPartialAreaRatio
                newLengthRatio += newPartialLengthRatio
                referenceCounter++
            }
        } else {
            newAreaRatio /= referenceNumber
            newLengthRatio /= referenceNumber
        }

        if ((newAreaRatio != 0f && newAreaRatio != Float.POSITIVE_INFINITY) &&
            (referenceCounter == referenceNumber)) {
            areaRatio = newAreaRatio
            lengthRatio = newLengthRatio
            newAreaRatio = 0f
        }

        if (referenceCounter == referenceNumber) {
            referenceCounter = 0
        }
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
            var nonBackgroundPixels = 0
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

            detections[i].seedArea = NumUtil.floatRoundTo(nonBackgroundPixels * areaRatio,5)

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
}