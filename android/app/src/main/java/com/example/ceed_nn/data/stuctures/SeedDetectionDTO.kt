package com.example.ceed_nn.data.stuctures

import android.graphics.Bitmap
import android.graphics.Rect

data class SeedDetectionDTO(
    var boundingBox: Rect,
    var classId: Int,
    var score: Float,
    var photo: Bitmap,
    var seedArea: Float,
    var seedMass: Float
)
