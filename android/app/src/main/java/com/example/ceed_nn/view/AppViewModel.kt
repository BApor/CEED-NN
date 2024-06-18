package com.example.ceed_nn.view

import android.app.Application
import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.ceed_nn.data.repositories.DetectionDetailsRepository
import com.example.ceed_nn.data.repositories.DetectionRepository
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO
import com.example.ceed_nn.data.stuctures.SeedGroupDTO

class AppViewModel : ViewModel(){
    private lateinit var detectionRepository: DetectionRepository
    private lateinit var detectionDetailsRepository: DetectionDetailsRepository

    var detections: List<SeedDetectionDTO> = emptyList()
    var referenceScale: Float  = 0f
    var time = 0.0

    var seedGroups: List<SeedGroupDTO> = emptyList()
    var totalArea: Float = 0f
    var totalMass: Float = 0f

    fun initializeRepositories (context: Context){
        detectionRepository = DetectionRepository(context)
        detectionDetailsRepository = DetectionDetailsRepository(context)
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

    fun fetchSeedClassesFromAssets() {
        detectionDetailsRepository.fetchSeedClassesFromJSON()
    }

    fun fetchDetectionDetails() {
        detectionDetailsRepository.setDetections(detections)
        detectionDetailsRepository.calculatePhysicalPropertiesToGroups()
        seedGroups = detectionDetailsRepository.getSeedGroups()
    }

    fun fetchTotalProperties() {
        totalArea = detectionDetailsRepository.getTotalArea()
        totalMass = detectionDetailsRepository.getTotalMass()
    }
}