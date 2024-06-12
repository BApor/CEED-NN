package com.example.ceed_nn.util

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object AiUtil {
    private lateinit var model: Module

    fun loadModel( context: Context, ptLiteFile: String) {
        val file = File(context.filesDir, ptLiteFile)

        try {
            if (!file.exists()) {
                context.assets.open(ptLiteFile).use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        val buffer = ByteArray(4 * 1024)
                        var read: Int
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            outputStream.write(buffer, 0, read)
                        }
                        outputStream.flush()
                    }
                }
            }
        } catch (ex: IOException) {
            throw RuntimeException("Error process asset $ptLiteFile to file path")
        }

        model = LiteModuleLoader.load(file.absolutePath)
    }

    fun detect(imageProxy: ImageProxy) : ArrayList<Result> {
        var bitmap = imageProxy.toBitmap()
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true)

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            resizedBitmap,
            PrePostProcessor.NO_MEAN_RGB,
            PrePostProcessor.NO_STD_RGB
        )

        val outputTuple: Array<IValue> = model.forward(IValue.from(inputTensor)).toList()
        val outputTensor = outputTuple[0].toTensor()
        val outputs = outputTensor.dataAsFloatArray

        val imgScaleX = bitmap.width.toFloat() / PrePostProcessor.mInputWidth
        val imgScaleY = bitmap.height.toFloat() / PrePostProcessor.mInputHeight
        val ivScaleX = 1
        val ivScaleY = 1

        val results =  PrePostProcessor.outputsToNMSPredictions(
            outputs,
            imgScaleX,
            imgScaleY,
            ivScaleX.toFloat(),
            ivScaleY.toFloat(),
            0f,
            0f
        )

        return results
    }
}