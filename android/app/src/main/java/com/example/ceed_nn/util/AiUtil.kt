package com.example.ceed_nn.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.util.Log
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtException
import ai.onnxruntime.OrtSession
import ai.onnxruntime.OrtUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

data class Result(
    var outputBitmap: Bitmap,
    var outputBox: Array<FloatArray>
)

object AiUtil {
    private lateinit var ortEnv: OrtEnvironment
    private lateinit var ortSession: OrtSession

    fun loadModel(onnxFilePath: String) {
        ortEnv = OrtEnvironment.getEnvironment()
        val modelFile = File(onnxFilePath)
        ortSession = 
    }

    fun detect(inputStream: InputStream, ortEnv: OrtEnvironment, ortSession: OrtSession): Result {
        // Step 1: convert image into byte array (raw image bytes)
        val rawImageBytes = inputStream.readBytes()

        // Step 2: get the shape of the byte array and make ort tensor
        val shape = longArrayOf(rawImageBytes.size.toLong())

        val inputTensor = OnnxTensor.createTensor(
            ortEnv,
            ByteBuffer.wrap(rawImageBytes),
            shape,
            OnnxJavaType.UINT8
        )
        inputTensor.use {
            // Step 3: call ort inferenceSession run
            val output = ortSession.run(Collections.singletonMap("image", inputTensor),
                setOf("image_out","scaled_box_out_next")
            )

            // Step 4: output analysis
            output.use {
                val rawOutput = (output?.get(0)?.value) as ByteArray
                val boxOutput = (output?.get(1)?.value) as Array<FloatArray>
                val outputImageBitmap = byteArrayToBitmap(rawOutput)

                // Step 5: set output result
                var result = Result(outputImageBitmap,boxOutput)
                return result
            }
        }
    }
}