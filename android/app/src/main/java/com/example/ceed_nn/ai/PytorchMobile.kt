package com.example.ceed_nn.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Matrix
import com.example.ceed_nn.help.PrePostProcessor

object PytorchMobile {
    private lateinit var model: Module

    fun loadModel(context: Context, ptLiteFile: String) {
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


        model = Module.load(file.absolutePath)
    }


    fun detect (imageProxy: ImageProxy) : List<PostProcessor.DetectResult>{
        val bitmap: Bitmap = rotateBitmap(imageProxy.toBitmap(), 90f)
        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            640,
            640,
            true
        )

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            resizedBitmap,
            PrePostProcessor.NO_MEAN_RGB,
            PrePostProcessor.NO_STD_RGB
        )

        var x: IValue = IValue.from(0)

        try{
            val (_x, _, _) = model.forward(IValue.from(inputTensor)).toTuple()
            x = _x
        } catch (exc: Exception) {
            Log.d("PytorchUtil", "Didnt found")
            val emptyDetectResultList: List<PostProcessor.DetectResult> = listOf()
            return emptyDetectResultList
        }

        val imgScaleX = bitmap.width.toFloat() / 640
        val imgScaleY = bitmap.height.toFloat() / 640

        return PostProcessor.nmsAndResizedRects(
            x.toTensor(),
            0.45f,
            imgScaleX,
            imgScaleY
        )
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

}