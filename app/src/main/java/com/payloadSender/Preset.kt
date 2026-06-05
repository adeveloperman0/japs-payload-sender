package com.payloadSender

data class Preset(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val ipAddress: String,
    val port: Int,
    val payloadFileName: String,
    val payloadUri: String
)
