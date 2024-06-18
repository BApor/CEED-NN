package com.example.ceed_nn.data.repositories

import android.content.Context
import com.example.ceed_nn.data.stuctures.SeedClassDTO
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO
import com.example.ceed_nn.data.stuctures.SeedGroupDTO
import com.example.ceed_nn.util.JsonUtil

class DetectionDetailsRepository(private val context: Context) {
    private var detections: List<SeedDetectionDTO> = emptyList()
    private var classes: List<SeedClassDTO> = emptyList()
    private var seedGroups: List<SeedGroupDTO> = emptyList()

    private var totalArea: Float = 0f
    private var totalMass: Float = 0f

    fun getSeedGroups(): List<SeedGroupDTO>  {
        return seedGroups
    }

    fun getTotalArea(): Float {
        return totalArea
    }

    fun getTotalMass(): Float {
        return totalMass
    }

    fun setDetections(detec: List<SeedDetectionDTO>) {

        // Removing reference detects

        val indexes = mutableListOf<Int>()
        for (i in 0 until detec.size)
            indexes.add(i)
        val mutableDetec = detec.toMutableList()
        for (i in indexes)
            mutableDetec.removeAt(i)

        totalArea = 0f
        totalMass = 0f
        detections = mutableDetec
    }

    fun fetchSeedClassesFromJSON() {
        classes = JsonUtil.parseSeedClassesJSON(context)
    }

    fun calculatePhysicalPropertiesToGroups() {
        val result = mutableListOf<SeedGroupDTO>()
        for (i in 1 until classes.size) {
            val classDetections = detections.filter { it.classId == classes[i].index }
            if (classDetections.size <= 0)
                continue
            var groupArea = 0f
            var groupMass = 0f
            for (j in 0 until classDetections.size){
                val seedArea = classDetections[j].seedArea
                val seedMass = seedArea * classes[i].massScale

                classDetections[j].seedMass = seedMass
                classDetections[j].seedLength = seedArea * classes[i].lengthScale
                classDetections[j].seedWidth = seedArea * classes[i].widthScale

                groupArea += seedArea
                groupMass += seedMass
            }

            val newSeedGroup = SeedGroupDTO(
                index = classes[i].index,
                name = classes[i].name,
                seeds = classDetections,
                photo = classDetections[0].photo,
                totalArea = groupArea,
                totalMass = groupMass
            )

            result.add(newSeedGroup)

            totalArea += groupArea
            totalMass += groupMass
        }
        seedGroups = result
    }
}