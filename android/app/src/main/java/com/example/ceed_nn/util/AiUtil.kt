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

    private fun copyFile(inputStream: InputStream, outputStream: FileOutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
    }
}