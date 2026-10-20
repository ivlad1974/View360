package com.example.view360.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.view360.data.CarRepository
import com.example.view360.model.AVMPreferences
import com.example.view360.model.CameraCalibOffset
import com.example.view360.model.CameraViewMode
import com.example.view360.model.CarModelItem
import com.example.view360.model.CarPaint
import com.example.view360.model.DvrRecordItem
import com.example.view360.model.DvrRecordType
import com.example.view360.model.DvrSnapshotItem
import com.example.view360.model.LicensePlateStyle
import com.example.view360.model.StitchingSeamConfig
import com.example.view360.model.TurnSignalState
import com.example.view360.model.UltrasonicRadarState
import com.example.view360.model.VehicleGear
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AVMUiState(
    val currentCarModel: CarModelItem = CarRepository.vehicleModels[0],
    val currentPaint: CarPaint = CarRepository.defaultPaintList[0],
    val licensePlate: String = "粤A·360BV",
    val licensePlateStyle: LicensePlateStyle = LicensePlateStyle.BLUE,
    val currentGear: VehicleGear = VehicleGear.PARK,
    val cameraViewMode: CameraViewMode = CameraViewMode.FRONT,
    val steeringAngleDeg: Float = 0f, // -540° to +540°
    val speedKmh: Float = 0f, // 0 to 120 km/h
    val turnSignal: TurnSignalState = TurnSignalState.OFF,
    val leftDoorOpen: Boolean = false,
    val rightDoorOpen: Boolean = false,
    val trunkOpen: Boolean = false,
    val radarState: UltrasonicRadarState = UltrasonicRadarState(),
    val preferences: AVMPreferences = AVMPreferences(),
    val dvrRecording: Boolean = true,
    val dvrRecords: List<DvrRecordItem> = listOf(
        DvrRecordItem("rec_001", "20261020_064500_4CH.mp4", "2026-10-20 06:45:00", 180, "4-CH Sync", false, 480_000_000L, DvrRecordType.NORMAL),
        DvrRecordItem("rec_002", "20261020_064200_4CH.mp4", "2026-10-20 06:42:00", 180, "4-CH Sync", false, 480_000_000L, DvrRecordType.NORMAL),
        DvrRecordItem("rec_003", "20261020_063900_EVENT.mp4", "2026-10-20 06:39:00", 180, "4-CH Sync", true, 480_000_000L, DvrRecordType.EVENT_LOCK),
        DvrRecordItem("rec_004", "20261020_063600_4CH.mp4", "2026-10-20 06:36:00", 180, "4-CH Sync", false, 480_000_000L, DvrRecordType.NORMAL),
        DvrRecordItem("rec_005", "20261020_062000_PARK.mp4", "2026-10-20 06:20:00", 60, "Front + Rear", false, 160_000_000L, DvrRecordType.PARKING)
    ),
    val dvrSnapshots: List<DvrSnapshotItem> = listOf(
        DvrSnapshotItem("snap_01", "SNAP_20261020_064522.jpg", "2026-10-20 06:45:22", "360° Composite"),
        DvrSnapshotItem("snap_02", "SNAP_20261020_064115.jpg", "2026-10-20 06:41:15", "Rear Camera")
    ),
    val cameraCalibs: Map<Int, CameraCalibOffset> = mapOf(
        1 to CameraCalibOffset(1, "Front Fisheye", 0f, 0f, 0f, 0f, 1.0f),
        2 to CameraCalibOffset(2, "Right Mirror", 0f, 0f, -10f, -30f, 1.0f),
        3 to CameraCalibOffset(3, "Rear Fisheye", 0f, 0f, 0f, 0f, 1.0f),
        4 to CameraCalibOffset(4, "Left Mirror", 0f, 0f, -10f, 30f, 1.0f)
    ),
    val seamConfig: StitchingSeamConfig = StitchingSeamConfig(),
    val orbitYaw: Float = 45f,
    val orbitPitch: Float = 35f,
    val toastMessage: String? = null
)

class AVMViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AVMUiState())
    val uiState: StateFlow<AVMUiState> = _uiState.asStateFlow()

    fun setGear(gear: VehicleGear) {
        _uiState.update { current ->
            val nextCam = when (gear) {
                VehicleGear.REVERSE -> CameraViewMode.REAR
                VehicleGear.DRIVE -> CameraViewMode.FRONT
                else -> current.cameraViewMode
            }
            current.copy(
                currentGear = gear,
                cameraViewMode = nextCam,
                // Adjust radar alert simulation based on reverse/drive
                radarState = if (gear == VehicleGear.REVERSE) {
                    current.radarState.copy(rearCenterLeft = 32, rearCenterRight = 38)
                } else {
                    current.radarState.copy(rearCenterLeft = 75, rearCenterRight = 80)
                }
            )
        }
    }

    fun setCameraViewMode(mode: CameraViewMode) {
        _uiState.update { it.copy(cameraViewMode = mode) }
    }

    fun setSteeringAngle(angleDeg: Float) {
        _uiState.update { it.copy(steeringAngleDeg = angleDeg.coerceIn(-540f, 540f)) }
    }

    fun setVehicleSpeed(speedKmh: Float) {
        _uiState.update { it.copy(speedKmh = speedKmh.coerceIn(0f, 140f)) }
    }

    fun setTurnSignal(signal: TurnSignalState) {
        _uiState.update { current ->
            val autoView = if (current.preferences.bsdEnabled) {
                when (signal) {
                    TurnSignalState.LEFT -> CameraViewMode.LEFT
                    TurnSignalState.RIGHT -> CameraViewMode.RIGHT
                    else -> current.cameraViewMode
                }
            } else {
                current.cameraViewMode
            }
            current.copy(turnSignal = signal, cameraViewMode = autoView)
        }
    }

    fun toggleLeftDoor() {
        _uiState.update { it.copy(leftDoorOpen = !it.leftDoorOpen) }
    }

    fun toggleRightDoor() {
        _uiState.update { it.copy(rightDoorOpen = !it.rightDoorOpen) }
    }

    fun toggleTrunk() {
        _uiState.update { it.copy(trunkOpen = !it.trunkOpen) }
    }

    fun toggleRadarMute() {
        _uiState.update { it.copy(radarState = it.radarState.copy(isMuted = !it.radarState.isMuted)) }
    }

    fun setOrbitAngles(yaw: Float, pitch: Float) {
        _uiState.update { it.copy(orbitYaw = (yaw % 360f + 360f) % 360f, orbitPitch = pitch.coerceIn(10f, 85f)) }
    }

    fun takeSnapshot() {
        val timeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val nameStr = "SNAP_${System.currentTimeMillis()}.jpg"
        val newSnap = DvrSnapshotItem(
            id = "snap_${System.currentTimeMillis()}",
            title = nameStr,
            timestamp = timeStr,
            cameraChannel = _uiState.value.cameraViewMode.label
        )
        _uiState.update {
            it.copy(
                dvrSnapshots = listOf(newSnap) + it.dvrSnapshots,
                toastMessage = "Snapshot saved: $nameStr"
            )
        }
    }

    fun toggleEmergencyLock() {
        _uiState.update { current ->
            val first = current.dvrRecords.firstOrNull()
            if (first != null) {
                val updatedRecords = current.dvrRecords.mapIndexed { index, item ->
                    if (index == 0) item.copy(isLocked = !item.isLocked, recordType = if (!item.isLocked) DvrRecordType.EVENT_LOCK else DvrRecordType.NORMAL) else item
                }
                current.copy(
                    dvrRecords = updatedRecords,
                    toastMessage = if (!first.isLocked) "Current clip locked to Emergency" else "Clip unlocked"
                )
            } else {
                current
            }
        }
    }

    fun toggleDvrRecording() {
        _uiState.update { it.copy(dvrRecording = !it.dvrRecording) }
    }

    fun formatStorage() {
        _uiState.update {
            it.copy(
                dvrRecords = emptyList(),
                dvrSnapshots = emptyList(),
                toastMessage = "SD Card formatted successfully"
            )
        }
    }

    fun selectCarModel(model: CarModelItem) {
        _uiState.update { it.copy(currentCarModel = model, currentPaint = model.defaultPaint) }
    }

    fun selectPaint(paint: CarPaint) {
        _uiState.update { it.copy(currentPaint = paint) }
    }

    fun updateLicensePlate(plate: String, style: LicensePlateStyle) {
        _uiState.update { it.copy(licensePlate = plate, licensePlateStyle = style) }
    }

    fun updateCalib(cameraId: Int, dx: Float, dy: Float, dPitch: Float, dYaw: Float, dFov: Float) {
        _uiState.update { current ->
            val existing = current.cameraCalibs[cameraId] ?: CameraCalibOffset(cameraId, "Cam $cameraId")
            val updated = existing.copy(
                offsetX = (existing.offsetX + dx).coerceIn(-100f, 100f),
                offsetY = (existing.offsetY + dy).coerceIn(-100f, 100f),
                pitchDeg = (existing.pitchDeg + dPitch).coerceIn(-45f, 45f),
                yawDeg = (existing.yawDeg + dYaw).coerceIn(-45f, 45f),
                fovScale = (existing.fovScale + dFov).coerceIn(0.5f, 1.8f)
            )
            val newMap = current.cameraCalibs.toMutableMap()
            newMap[cameraId] = updated
            current.copy(cameraCalibs = newMap)
        }
    }

    fun resetCalib(cameraId: Int) {
        _uiState.update { current ->
            val newMap = current.cameraCalibs.toMutableMap()
            newMap[cameraId] = CameraCalibOffset(cameraId, "Camera $cameraId")
            current.copy(cameraCalibs = newMap, toastMessage = "Camera $cameraId calibration reset")
        }
    }

    fun updateSeamConfig(seam: StitchingSeamConfig) {
        _uiState.update { it.copy(seamConfig = seam) }
    }

    fun updatePreferences(transform: (AVMPreferences) -> AVMPreferences) {
        _uiState.update { it.copy(preferences = transform(it.preferences)) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
