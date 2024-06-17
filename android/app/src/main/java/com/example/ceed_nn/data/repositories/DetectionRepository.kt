package com.example.ceed_nn.data.repositories

import android.content.Context
import com.example.ceed_nn.ai.DetectionEngine
import com.example.ceed_nn.data.stuctures.DetectResult

class DetectionRepository(context: Context, model: String) {
    private var detEngine: DetectionEngine
    private lateinit var detections: List<DetectResult>

    init {
        detEngine = DetectionEngine(context, model)
    }

//    fun setDetEngineModel(model: String) {
//        detEngine.loadModel(model)
//    }
}