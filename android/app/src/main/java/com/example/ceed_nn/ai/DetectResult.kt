package com.example.ceed_nn.ai

import android.graphics.Rect

data class DetectResult(
    var boundingBox: Rect,
    var classId: Int,
    var score: Float,
    var seedArea: Float
)
