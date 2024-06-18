package com.example.ceed_nn.help

import android.graphics.Rect
import java.util.Arrays
import kotlin.math.max
import kotlin.math.min


class Result(var classIndex: Int, var score: Float, var rect: Rect);

object PrePostProcessor {
    // for yolov5 model, no need to apply MEAN and STD
    var NO_MEAN_RGB: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
    var NO_STD_RGB: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f)

    // model input image size
    var mInputWidth: Int = 640
    var mInputHeight: Int = 640

    // model output is of size 25200*(num_of_class+5)
    private const val mOutputRow =
        8400 // as decided by the YOLOv5 model for input image of size 640*640
    private const val mOutputColumn = 26 // left, top, right, bottom, score and 80 class probability
    private const val mThreshold = 300f // score above which a detection is generated
    private const val mNmsLimit = 60

    fun IOU(a: Rect, b: Rect): Float {
        val areaA = ((a.right - a.left) * (a.bottom - a.top)).toFloat()
        if (areaA <= 0.0) return 0.0f

        val areaB = ((b.right - b.left) * (b.bottom - b.top)).toFloat()
        if (areaB <= 0.0) return 0.0f

        val intersectionMinX = max(a.left.toDouble(), b.left.toDouble()).toFloat()
        val intersectionMinY = max(a.top.toDouble(), b.top.toDouble()).toFloat()
        val intersectionMaxX = min(a.right.toDouble(), b.right.toDouble()).toFloat()
        val intersectionMaxY = min(a.bottom.toDouble(), b.bottom.toDouble()).toFloat()
        val intersectionArea =
            (max(
                (intersectionMaxY - intersectionMinY).toDouble(),
                0.0
            ) * max((intersectionMaxX - intersectionMinX).toDouble(), 0.0)).toFloat()
        return intersectionArea / (areaA + areaB - intersectionArea)
    }


    fun nonMaxSuppression(
        boxes: ArrayList<Result>,
        limit: Int,
        threshold: Float
    ): ArrayList<Result> {

        boxes.sortWith(Comparator { o1, o2 ->
            o1.score.compareTo(o2.score)
        })

        val selected = ArrayList<Result>()
        val active = BooleanArray(boxes.size)
        Arrays.fill(active, true)
        var numActive = active.size

        var done = false
        var i = 0
        while (i < boxes.size && !done) {
            if (active[i]) {
                val boxA = boxes[i]
                selected.add(boxA)
                if (selected.size >= limit) break

                for (j in i + 1 until boxes.size) {
                    if (active[j]) {
                        val boxB = boxes[j]
                        if (IOU(boxA.rect, boxB.rect) > threshold) {
                            active[j] = false
                            numActive -= 1
                            if (numActive <= 0) {
                                done = true
                                break
                            }
                        }
                    }
                }
            }
            i++
        }
        return selected
    }

    fun outputsToNMSPredictions(
        outputs: FloatArray,
        imgScaleX: Float,
        imgScaleY: Float,
        ivScaleX: Float,
        ivScaleY: Float,
        startX: Float,
        startY: Float
    ): ArrayList<Result> {
        val results = ArrayList<Result>()
        for (i in 0 until mOutputRow) {
            if (outputs[i * mOutputColumn + 4] > mThreshold) {
                val x = outputs[i * mOutputColumn]
                val y = outputs[i * mOutputColumn + 1]
                val w = outputs[i * mOutputColumn + 2]
                val h = outputs[i * mOutputColumn + 3]

                val left = imgScaleX * (x - w / 2)
                val top = imgScaleY * (y - h / 2)
                val right = imgScaleX * (x + w / 2)
                val bottom = imgScaleY * (y + h / 2)

                var max = outputs[i * mOutputColumn + 5]
                var cls = 0
                for (j in 0 until mOutputColumn - 5) {
                    if (outputs[i * mOutputColumn + 5 + j] > max) {
                        max = outputs[i * mOutputColumn + 5 + j]
                        cls = j
                    }
                }

                val rect = Rect(
                    (startX + ivScaleX * left).toInt(),
                    (startY + top * ivScaleY).toInt(),
                    (startX + ivScaleX * right).toInt(),
                    (startY + ivScaleY * bottom).toInt()
                )
                val result = Result(cls, outputs[i * mOutputColumn + 4], rect)
                results.add(result)
            }
        }
        return nonMaxSuppression(results, mNmsLimit, mThreshold)
    }

    fun outputsToNMSPredictionsYOLO(
        outputs: FloatArray,
        imgScaleX: Float,
        imgScaleY: Float,
        ivScaleX: Float,
        ivScaleY: Float,
        startX: Float,
        startY: Float
    ): ArrayList<Result> {
        val results = ArrayList<Result>()
        for (i in 0 until mOutputRow) {
            val x = outputs[i * mOutputColumn + 22]
            val y = outputs[i * mOutputColumn + 23]
            val w = outputs[i * mOutputColumn + 24]
            val h = outputs[i * mOutputColumn + 25]

            val left = imgScaleX * (x - w / 2)
            val top = imgScaleY * (y - h / 2)
            val right = imgScaleX * (x + w / 2)
            val bottom = imgScaleY * (y + h / 2)

            var max = outputs[i * mOutputColumn]
            var cls = 0
            for (j in 0 until mOutputColumn - 4) {
                if (outputs[i * mOutputColumn + j] > max) {
                    max = outputs[i * mOutputColumn + j]
                    cls = j
                }
            }

            val rect = Rect(
                (startX + ivScaleX * left).toInt(),
                (startY + top * ivScaleY).toInt(),
                (startX + ivScaleX * right).toInt(),
                (startY + ivScaleY * bottom).toInt()
            )
            val result = Result(cls, max, rect)
            results.add(result)
        }

        return results
    }
}