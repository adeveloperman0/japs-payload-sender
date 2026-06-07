package com.payloadSender

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object PresetManager {

    private const val PREFS_NAME = "netcat_presets"
    private const val PRESETS_KEY = "presets_list"
    private const val CACHE_DIR = "payload_cache"

    fun savePreset(context: Context, preset: Preset): Boolean {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val presets = getPresets(context).toMutableList()
            presets.add(preset)
            savePresetsToJson(prefs, presets)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getPresets(context: Context): List<Preset> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(PRESETS_KEY, "[]") ?: "[]"
        return try {
            val jsonArray = JSONArray(jsonStr)
            (0 until jsonArray.length()).map { i ->
                val obj = jsonArray.getJSONObject(i)
                Preset(
                    id = obj.getLong("id"),
                    name = obj.getString("name"),
                    ipAddress = obj.getString("ipAddress"),
                    port = obj.getInt("port"),
                    payloadFileName = obj.getString("payloadFileName"),
                    cachedPayloadFile = obj.getString("cachedPayloadFile")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getPayloadBytes(context: Context, preset: Preset): ByteArray? {
        return try {
            val cacheDir = File(context.cacheDir, CACHE_DIR)
            val payloadFile = File(cacheDir, preset.cachedPayloadFile)
            if (payloadFile.exists()) {
                payloadFile.readBytes()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun savePayloadBytes(context: Context, bytes: ByteArray, filename: String): String? {
        return try {
            val cacheDir = File(context.cacheDir, CACHE_DIR)
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val cachedFile = File(cacheDir, filename)
            cachedFile.writeBytes(bytes)
            filename
        } catch (e: Exception) {
            null
        }
    }

    fun deletePreset(context: Context, presetId: Long) {
        try {
            val preset = getPresets(context).find { it.id == presetId }
            if (preset != null) {
                // Delete cached payload file
                val cacheDir = File(context.cacheDir, CACHE_DIR)
                val payloadFile = File(cacheDir, preset.cachedPayloadFile)
                payloadFile.delete()
            }

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val presets = getPresets(context).filter { it.id != presetId }
            savePresetsToJson(prefs, presets)
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun savePresetsToJson(prefs: SharedPreferences, presets: List<Preset>) {
        val jsonArray = JSONArray()
        presets.forEach { preset ->
            val obj = JSONObject().apply {
                put("id", preset.id)
                put("name", preset.name)
                put("ipAddress", preset.ipAddress)
                put("port", preset.port)
                put("payloadFileName", preset.payloadFileName)
                put("cachedPayloadFile", preset.cachedPayloadFile)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(PRESETS_KEY, jsonArray.toString()).apply()
    }
}
