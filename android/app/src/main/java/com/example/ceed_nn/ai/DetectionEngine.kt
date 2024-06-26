package com.example.ceed_nn.ai

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.example.ceed_nn.data.stuctures.SeedDetectionDTO
import com.example.ceed_nn.help.PrePostProcessor
import com.example.ceed_nn.util.ImageUtil
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.pow
import kotlin.math.roundToInt

class DetectionEngine(context: Context, modelName: String) {
    private var model: Module
    private var modelTime: Double = 0.0

    init {
        var pthFile = ""
        if (modelName == "YOLOv3")
            pthFile = "yolov3_xbs.pth"
        if (modelName == "YOLOv5")
            pthFile = "yolov5_xbs.pth"
        if (modelName == "YOLOv6")
            pthFile = "yolov6_xbs.pth"
        if (modelName == "YOLOv8")
            pthFile = "yolov8_xbs.pth"

        val file = File(context.filesDir, pthFile)
        try {
            if (!file.exists()) {
                context.assets.open(pthFile).use { inputStream ->
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
            throw RuntimeException("Error process asset $pthFile to file path")
        }
        model = Module.load(file.absolutePath)
    }


    fun detect (imageProxy: ImageProxy) : List<SeedDetectionDTO>{
        val bitmap: Bitmap = ImageUtil.rotateBitmap(imageProxy.toBitmap(), 90f)

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

        val startTime = System.nanoTime()

        try{
            val (_x, _, _) = model.forward(IValue.from(inputTensor)).toTuple()
            x = _x
        } catch (exc: Exception) {
//            Log.d("PytorchMobile", "Null")
            val emptyDetectResultList: List<SeedDetectionDTO> = listOf()
            return emptyDetectResultList
        }

        val endTime = System.nanoTime()
        modelTime = ((endTime - startTime) / 1_000_000.0).roundTo(2)

        val imgToUiRatio = 1080f / 640f

        return PostProcess.nms(
            x.toTensor(),
            0.45f,
            imgToUiRatio
        )
    }

    fun Double.roundTo(numFractionDigits: Int): Double {
        val factor = 10.0.pow(numFractionDigits.toDouble())
        return (this * factor).roundToInt() / factor
    }

    fun getTime(): Double{
        return modelTime
    }
}