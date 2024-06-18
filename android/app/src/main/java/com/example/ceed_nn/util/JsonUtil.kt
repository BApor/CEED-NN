package com.example.ceed_nn.util

import android.content.Context
import com.example.ceed_nn.data.stuctures.SeedClassDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object JsonUtil {
    fun loadJSONFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun parseSeedClassesJSON(context: Context): List<SeedClassDTO> {
        val jsonString = loadJSONFromAsset(context, "seedClasses.json")
        return if (jsonString != null) {
            val gson = Gson()
            val listType = object : TypeToken<List<SeedClassDTO>>() {}.type
            gson.fromJson(jsonString, listType)
        } else {
            emptyList()
        }
    }
}