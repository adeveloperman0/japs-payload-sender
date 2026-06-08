package com.payloadSender

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object AutoloadManager {

    private const val PREFS_NAME = "autoload_prefs"
    private const val QUEUE_KEY = "autoload_queue"

    fun saveAutoloadQueue(context: Context, items: List<AutoloadItem>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()

        items.forEach { item ->
            val obj = when (item) {
                is AutoloadItem.PresetItem -> {
                    JSONObject().apply {
                        put("type", "preset")
                        put("presetId", item.preset.id)
                    }
                }
                is AutoloadItem.DelayItem -> {
                    JSONObject().apply {
                        put("type", "delay")
                        put("seconds", item.delaySeconds)
                    }
                }
            }
            jsonArray.put(obj)
        }

        prefs.edit().putString(QUEUE_KEY, jsonArray.toString()).apply()
    }

    fun getAutoloadQueue(context: Context, presets: List<Preset>): List<AutoloadItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(QUEUE_KEY, "[]") ?: "[]"

        return try {
            val jsonArray = JSONArray(jsonStr)
            val items = mutableListOf<AutoloadItem>()

            (0 until jsonArray.length()).forEach { i ->
                val obj = jsonArray.getJSONObject(i)
                val type = obj.getString("type")

                when (type) {
                    "preset" -> {
                        val presetId = obj.getLong("presetId")
                        val preset = presets.find { it.id == presetId }
                        if (preset != null) {
                            items.add(AutoloadItem.PresetItem(preset))
                        }
                    }
                    "delay" -> {
                        val seconds = obj.optInt("seconds", 2)
                        items.add(AutoloadItem.DelayItem(seconds))
                    }
                }
            }
            items
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearAutoloadQueue(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(QUEUE_KEY, "[]").apply()
    }

    fun addPresetToAutoload(context: Context, preset: Preset) {
        val current = getAutoloadQueue(context, PresetManager.getPresets(context)).toMutableList()
        current.add(AutoloadItem.PresetItem(preset))

        // Add delay after preset (except for last one)
        if (current.isNotEmpty()) {
            current.add(AutoloadItem.DelayItem(2))
        }

        saveAutoloadQueue(context, current)
    }

    fun removeFromAutoload(context: Context, index: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(QUEUE_KEY, "[]") ?: "[]"

        try {
            val jsonArray = JSONArray(jsonStr)
            val newArray = JSONArray()

            (0 until jsonArray.length()).forEach { i ->
                if (i != index) {
                    newArray.put(jsonArray.getJSONObject(i))
                }
            }

            prefs.edit().putString(QUEUE_KEY, newArray.toString()).apply()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
