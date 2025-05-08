package com.vitizen.app.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.osmdroid.util.GeoPoint

class GeoPointListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromGeoPointList(value: List<GeoPoint>): String {
        val type = object : TypeToken<List<GeoPoint>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toGeoPointList(value: String): List<GeoPoint> {
        val type = object : TypeToken<List<GeoPoint>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
} 