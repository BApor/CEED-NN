package com.example.ceed_nn.ai

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO
import org.pytorch.Tensor

object PostProcess {

    fun nms(
        x: Tensor,
        threshold: Float,
        imgToUiRatio: Float
    ): List<SeedDetectionDTO> {
        // x: [0:4] - box, [4] - score, [5] - class
        val data = x.dataAsFloatArray
        val numElem = x.shape()[0].toInt()
        val innerShape = x.shape()[1].toInt()
        val selected_indices = (0 until numElem).toMutableList()

        val scores =  data.sliceArray( (0 until numElem).flatMap { r->(r*innerShape)+4 until (r*innerShape)+5 } )
        val boxes = data.sliceArray( (0 until numElem).flatMap { r->(r*innerShape) until (r*innerShape)+4 } )
        val classes = data.sliceArray( (0 until numElem).flatMap { r->(r*innerShape)+5 until (r*innerShape)+6 } )

        for (i in 0 until numElem) {
            val current_class = classes[i].toInt()
            for (j in i+1 until numElem) {
                val box_i = boxes.sliceArray(i*4 until (i*4)+4)
                val box_j = boxes.sliceArray(j*4 until (j*4)+4)
                val iou = calculate_iou(box_i, box_j)
                if (iou > threshold && classes[j].toInt() == current_class) {
                    if (scores[j] > scores[i]) {
                        selected_indices.remove(i)
                        break
                    } else {
                        selected_indices.remove(j)
                    }
                }
            }
        }

        val result = mutableListOf<SeedDetectionDTO>()
        for (i in 0 until numElem) {
            if (selected_indices.contains(i)) {
                val box = boxes.slice((i*4) until (i*4)+4)
                val detection = SeedDetectionDTO(
                    id = i,
                    boundingBox = Rect(
                        (box[0] * imgToUiRatio).toInt(),
                        (box[1] * imgToUiRatio).toInt(),
                        (box[2] * imgToUiRatio).toInt(),
                        (box[3] * imgToUiRatio).toInt()
                    ),
                    score = scores[i],
                    classId = classes[i].toInt(),
                    seedArea = 0f,
                    photo = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                    seedMass = 0f
                    )
                result.add(detection)
            }
        }

        return result
    }

    fun calculate_iou(box1: FloatArray, box2: FloatArray): Float {
        val x1 = maxOf(box1[0], box2[0])
        val y1 = maxOf(box1[1], box2[1])
        val x2 = minOf(box1[2], box2[2])
        val y2 = minOf(box1[3], box2[3])

        val intersection = maxOf(0f, x2 - x1) * maxOf(0f, y2 - y1)
        val area1 = (box1[2] - box1[0]) * (box1[3] - box1[1])
        val area2 = (box2[2] - box2[0]) * (box2[3] - box2[1])
        val union = area1 + area2 - intersection

        return intersection / union
    }
}