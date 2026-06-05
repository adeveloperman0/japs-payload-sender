package com.payloadSender

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object PresetManager {

    private const val PREFS_NAME = "netcat_presets"
    private const val PRESETS_KEY = "presets_list"

    fun savePreset(context: Context, preset: Preset) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val presets = getPresets(context).toMutableList()
        presets.add(preset)
        savePresetsToJson(prefs, presets)
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
                    payloadUri = obj.getString("payloadUri")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun deletePreset(context: Context, presetId: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val presets = getPresets(context).filter { it.id != presetId }
        savePresetsToJson(prefs, presets)
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
                put("payloadUri", preset.payloadUri)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(PRESETS_KEY, jsonArray.toString()).apply()
    }
}
