package com.example.ceed_nn.data.repositories

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO

data class SeedSizeResults(
    val photo: Bitmap,
    val verDiagSize: Float,
    val horDiagSize: Float
)

class SeedSizeDetailsRepository {
    private var lengthRatio = 0f
    private lateinit var  seedDetection: SeedDetectionDTO
    private lateinit var results: SeedSizeResults

    private val verDiagPaint = Paint().apply {
        color = Color.parseColor("#FF6200EE")
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val horDiagPaint = Paint().apply {
        color = Color.parseColor("#FFA12E")
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    fun setLr(lr: Float) {
        lengthRatio = lr
    }

    fun setSeedDetection(seedDetec: SeedDetectionDTO) {
        seedDetection = seedDetec
    }

    fun getResults(): SeedSizeResults {
        return results
    }

    fun calculateSeedSizeAlongDiagonals() {
        val bitmap = seedDetection.photo
        var verDiagSeedPixels = 0
        var horDiagSeedPixels = 0


        for (y in 0 until bitmap.height) {
            val pixel = bitmap.getPixel(bitmap.width / 2, y)
            if (!((pixel.red in 151..239) &&
                    (pixel.green in 151..239) &&
                    (pixel.blue in 151..239)))
                 verDiagSeedPixels++
        }

        for (x in 0 until bitmap.width) {
            val pixel = bitmap.getPixel(x, bitmap.height / 2)
            if (!((pixel.red in 151..239) &&
                        (pixel.green in 151..239) &&
                        (pixel.blue in 151..239)))
                horDiagSeedPixels++
        }

        val canvasBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(canvasBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        canvas.drawLine(bitmap.width.toFloat() / 2f, 0f, bitmap.width.toFloat() / 2f, bitmap.height.toFloat(), verDiagPaint)
        canvas.drawLine(0f, bitmap.height.toFloat() / 2f, bitmap.width.toFloat(), bitmap.height.toFloat() / 2f, horDiagPaint)

        results = SeedSizeResults(
            photo = canvasBitmap,
            verDiagSize = verDiagSeedPixels.toFloat() * lengthRatio,
            horDiagSize = horDiagSeedPixels.toFloat() * lengthRatio
        )
    }
}