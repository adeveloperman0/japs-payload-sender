package com.payloadSender

sealed class AutoloadItem {
    data class PresetItem(val preset: Preset) : AutoloadItem()
    data class DelayItem(val delaySeconds: Int = 2) : AutoloadItem()
}
