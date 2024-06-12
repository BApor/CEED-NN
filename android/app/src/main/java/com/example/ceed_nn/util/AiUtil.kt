package com.example.ceed_nn.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import org.pytorch.Module
import org.pytorch.LiteModuleLoader
import java.io.InputStream

object AiUtil {
    private lateinit var model: Module

    fun loadModel( context: Context, ptLiteFile: String) {
        val file = File(context.filesDir, ptLiteFile)
        if (!file.exists()) {
            context.assets.open(ptLiteFile).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    copyFile(inputStream, outputStream)
                }
            }
        }

        model = Module.load(file.absolutePath)


    }


    private fun copyAssetToFile(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (!file.exists()) {
            context.assets.open(assetName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    copyFile(inputStream, outputStream)
                }
            }
        }
        return file.absolutePath
    }

    private fun copyFile(inputStream: InputStream, outputStream: FileOutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
    }
}