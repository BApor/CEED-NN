package com.example.ceed_nn.view

import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.example.ceed_nn.data.repositories.GroupDetailsRepository
import com.example.ceed_nn.data.repositories.CameraDetailsRepository
import com.example.ceed_nn.data.repositories.SeedSizeDetailsRepository
import com.example.ceed_nn.data.repositories.SeedSizeResults
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO
import com.example.ceed_nn.data.stuctures.SeedGroupDTO

class AppViewModel : ViewModel(){
    private lateinit var cameraDetailsRepository: CameraDetailsRepository
    private lateinit var groupDetailsRepository: GroupDetailsRepository
    private lateinit var seedSizeDetailsRepository: SeedSizeDetailsRepository

    var detections: List<SeedDetectionDTO> = emptyList()
    var time = 0.0

    private var areaRatio: Float  = 0f
    private var lengthRatio: Float  = 0f

    var seedGroups: List<SeedGroupDTO> = emptyList()
    var totalArea: Float = 0f
    var totalMass: Float = 0f
    var totalNumber: Int = 0

    lateinit var seedSizeResults: SeedSizeResults

    fun initializeRepositories (context: Context){
        cameraDetailsRepository = CameraDetailsRepository(context)
        groupDetailsRepository = GroupDetailsRepository(context)
        seedSizeDetailsRepository = SeedSizeDetailsRepository()
    }

    // Camera Fragment Functions (Detections, BBoxes, Time etc.)

    fun setDetEngineModel(model: String) {
        cameraDetailsRepository.setDetEngineModel(model)
    }

    fun setCurrentFrame(imageProxy: ImageProxy) {
        cameraDetailsRepository.setCurrentFrame(imageProxy)
    }

    fun fetchDetections() {
        cameraDetailsRepository.fetchDetections()
        cameraDetailsRepository.calculatePhysicalRatios()
        areaRatio = cameraDetailsRepository.getAreaRatio()
        lengthRatio = cameraDetailsRepository.getLengthRatio()
        time = cameraDetailsRepository.getTime()
        if (areaRatio == 0f)
            return
        cameraDetailsRepository.calculateSeedAreas()
        detections = cameraDetailsRepository.getDetections()
    }

    // Details Fragment Functions (grouping by class, physical properties etc.)

    fun fetchSeedClassesFromAssets() {
        groupDetailsRepository.fetchSeedClassesFromJSON()
    }

    fun fetchDetectionDetails() {
        groupDetailsRepository.setDetections(detections)
        groupDetailsRepository.calculatePhysicalPropertiesToGroups()
        groupDetailsRepository.fetchPercentageRatios()
        seedGroups = groupDetailsRepository.getSeedGroups()
    }

    fun fetchTotalProperties() {
        totalArea = groupDetailsRepository.getTotalArea()
        totalMass = groupDetailsRepository.getTotalMass()
        totalNumber = groupDetailsRepository.getTotalSeedNumber()
    }

    // Seed Size Details Fragment (size on 4 diagonals)

    fun fetchSeedSizeDetails(seedDetectionId: Int, seedDetectionClassId: Int) {
        seedSizeDetailsRepository.setLr(lengthRatio)
        val seedDetection = seedGroups.find { it.id == seedDetectionClassId }!!.seeds.find {it.id == seedDetectionId}!!
        seedSizeDetailsRepository.setSeedDetection(seedDetection)
        seedSizeDetailsRepository.calculateSeedSizeAlongDiagonals()
        seedSizeResults = seedSizeDetailsRepository.getResults()
    }
}