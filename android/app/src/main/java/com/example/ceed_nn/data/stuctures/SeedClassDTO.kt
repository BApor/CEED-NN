package com.example.ceed_nn.data.stuctures

data class SeedClassDTO(
    val index: Int,
    val name: String,
    var areaScale: List<Float>,
    var massScale: List<Float>,
    val avgArea: Float,
    val avgMass: Float
)
