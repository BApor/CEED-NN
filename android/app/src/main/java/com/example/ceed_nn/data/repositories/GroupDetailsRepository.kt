package com.example.ceed_nn.data.repositories

import android.content.Context
import com.example.ceed_nn.data.stuctures.SeedClassDTO
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO
import com.example.ceed_nn.data.stuctures.SeedGroupDTO
import com.example.ceed_nn.util.JsonUtil

class GroupDetailsRepository(private val context: Context) {
    private var detections: List<SeedDetectionDTO> = emptyList()
    private var classes: List<SeedClassDTO> = emptyList()
    private var seedGroups: List<SeedGroupDTO> = emptyList()

    private var totalArea: Float = 0f
    private var totalMass: Float = 0f
    private var totalSeedNumber: Int = 0

    fun getSeedGroups(): List<SeedGroupDTO>  {
        return seedGroups
    }

    fun getTotalArea(): Float {
        return totalArea
    }

    fun getTotalMass(): Float {
        return totalMass
    }

    fun getTotalSeedNumber(): Int {
        return totalSeedNumber
    }

    fun setDetections(detec: List<SeedDetectionDTO>) {
        totalArea = 0f
        totalMass = 0f
        totalSeedNumber = 0
        detections = detec
        // .toMutableList().filter { it.classId != 0 }
    }

    fun fetchSeedClassesFromJSON() {
        classes = JsonUtil.parseSeedClassesJSON(context)
        for (i in 0 until classes.size){
            if(classes[i].areaScale.size == 0)
                continue

            val pairs = classes[i].areaScale.zip(classes[i].massScale)
            val sortedPairs = pairs.sortedBy { it.first }

            classes[i].areaScale = sortedPairs.map { it.first }.toList()
            classes[i].massScale = sortedPairs.map { it.second }.toList()
        }
    }

    fun calculateSeedMassByScale(detection: SeedDetectionDTO): Float {
        var areaReference = 0f
        var massReference = 0f
        val scaleSize = classes[detection.classId].massScale.size
        if (scaleSize == 0)
            return 0f
        if (detection.seedArea <= classes[detection.classId].areaScale[0]) {
            areaReference = classes[detection.classId].areaScale[0]
            massReference = classes[detection.classId].massScale[0]
        } else if (detection.seedArea >= classes[detection.classId].areaScale[scaleSize - 1]) {
            areaReference = classes[detection.classId].areaScale[scaleSize - 1]
            massReference = classes[detection.classId].massScale[scaleSize - 1]
        } else
            for (i in 0 until (scaleSize - 1)) {
                val lowerAreaScaleValue = classes[detection.classId].areaScale[i]
                val upperAreaScaleValue = classes[detection.classId].areaScale[i + 1]
                val lowerMassScaleValue = classes[detection.classId].massScale[i]
                val upperMassScaleValue = classes[detection.classId].massScale[i + 1]
                if (detection.seedArea in lowerAreaScaleValue..upperAreaScaleValue){
                    if ((detection.seedArea - lowerAreaScaleValue) <= (upperAreaScaleValue - detection.seedArea)) {
                        areaReference = lowerAreaScaleValue
                        massReference = lowerMassScaleValue
                    } else {
                        areaReference = upperAreaScaleValue
                        massReference = upperMassScaleValue
                    }
                    break
                }
            }
        return (detection.seedArea * (massReference / areaReference))
    }

    fun calculateSeedMassByAvg(detection: SeedDetectionDTO): Float {
        return (detection.seedArea * (classes[detection.classId].avgMass / classes[detection.classId].avgArea))
    }

    fun calculatePhysicalPropertiesToGroups() {
        val result = mutableListOf<SeedGroupDTO>()
        for (i in 1 until classes.size) { // Visszaváltani egyre ha nem kell referencia osztály!!!!!!!
            val classDetections = detections.filter { it.classId == classes[i].index }
            if (classDetections.size <= 0)
                continue
            var groupArea = 0f
            var groupMass = 0f
            for (j in 0 until classDetections.size){
                val seedArea = classDetections[j].seedArea
//                val seedMass = calculateSeedMassByScale(classDetections[j])
                val seedMass = calculateSeedMassByAvg(classDetections[j])

                classDetections[j].seedMass = seedMass

                groupArea += seedArea
                groupMass += seedMass
            }

            val newSeedGroup = SeedGroupDTO(
                id = classes[i].index,
                name = classes[i].name,
                seeds = classDetections,
                groupArea = groupArea,
                groupMass = groupMass,
                percentageArea = 0f,
                percentageMass = 0f,
                percentageSeedNumber = 0f
            )

            result.add(newSeedGroup)

            if (i > 0){
                totalArea += groupArea
                totalMass += groupMass
            }
        }
        seedGroups = result
        for (i in 0 until seedGroups.size)
            totalSeedNumber += seedGroups[i].seeds.size
    }

    fun fetchPercentageRatios() {
        for (i in 0 until seedGroups.size) {
            seedGroups[i].percentageArea = seedGroups[i].groupArea / totalArea * 100f
            seedGroups[i].percentageMass = seedGroups[i].groupMass / totalMass * 100f
            seedGroups[i].percentageSeedNumber = seedGroups[i].seeds.size.toFloat() / totalSeedNumber.toFloat() * 100f
        }
    }
}