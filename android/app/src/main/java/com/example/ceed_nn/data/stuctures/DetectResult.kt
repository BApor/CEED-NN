package com.example.ceed_nn.data.stuctures

import android.graphics.Rect

data class DetectResult(
    var boundingBox: Rect,
    var classId: Int,
    var score: Float,
    var seedArea: Float
)
