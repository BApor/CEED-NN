package com.example.ceed_nn.data.stuctures

import android.graphics.Bitmap

data class SeedGroupDTO(
    var index: Int,
    var name: String,
    var photo: Bitmap,
    var totalArea: Float,
    var totalMass: Float
)