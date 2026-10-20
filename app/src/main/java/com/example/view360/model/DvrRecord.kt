package com.example.view360.model

enum class DvrRecordType(val label: String) {
    NORMAL("Normal Loop"),
    EVENT_LOCK("Emergency Locked"),
    PARKING("Parking Guard")
}

data class DvrRecordItem(
    val id: String,
    val title: String,
    val timestamp: String,
    val durationSec: Int,
    val cameraChannel: String, // "All 4 Channels", "Front", "Rear", etc.
    val isLocked: Boolean,
    val fileSizeBytes: Long,
    val recordType: DvrRecordType
)

data class DvrSnapshotItem(
    val id: String,
    val title: String,
    val timestamp: String,
    val cameraChannel: String
)
