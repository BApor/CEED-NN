package com.example.ceed_nn.data.stuctures

import android.graphics.Bitmap

data class SeedGroupDTO(
    var id: Int,
    var name: String,
    var seeds: List<SeedDetectionDTO>,
    var groupArea: Float,
    var groupMass: Float,
    var percentageArea: Float,
    var percentageMass: Float,
    var percentageSeedNumber: Float
)
