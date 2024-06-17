package com.example.ceed_nn.view

import android.app.Application
import androidx.camera.core.ImageProxy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ceed_nn.data.repositories.DetectionDetailsRepository
import com.example.ceed_nn.data.repositories.DetectionRepository
import com.example.ceed_nn.data.stuctures.DetectResult
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application){
    private var detectionRepository: DetectionRepository
    private var detectionDetailsRepository: DetectionDetailsRepository

    var detections: List<DetectResult> = emptyList()
    var referenceScale: Float  = 0f
    var time = 0.0

    init {
        detectionRepository = DetectionRepository(application.applicationContext)
        detectionDetailsRepository = DetectionDetailsRepository(application.applicationContext)
    }

    // Detections

    fun setDetEngineModel(model: String) {
        detectionRepository.setDetEngineModel(model)
    }

    fun setCurrentFrame(imageProxy: ImageProxy) {
        detectionRepository.setCurrentFrame(imageProxy)
    }

    fun fetchDetections() {
        detectionRepository.fetchDetections()
        detectionRepository.calculateReferenceScale()
        detectionRepository.calculateSeedAreas()
        val reference = detectionRepository.getReferenceScale()
        if (reference != 0f)
            referenceScale = detectionRepository.getReferenceScale()
        detections = detectionRepository.getDetections()
        time = detectionRepository.getTime()
    }

    // Detection details


}