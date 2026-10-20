# View 360 - Automotive Surround View Monitor (AVM) Suite

An automotive in-vehicle infotainment (IVI / Android Auto / Head Unit) 360° Surround View Monitor and 4-Channel DVR suite built with modern Android (Kotlin + Jetpack Compose and Material 3).

## Features

### 1. 360° Bird's Eye View (AVM Dual-Pane Monitor)
- **Top-Down Composite Birdview**: Realistic overhead vehicle projection with active steering guidelines (0.5m red stop bar, 1.5m yellow caution bar, 3.0m green safe line).
- **16-Zone Ultrasonic Radar Proximity**: Real-time obstacle detection waves with green/yellow/red distance alerts (cm) and audio beeping toggle.
- **Directional Camera Perspectives**:
  - **Front Camera**: Wide-angle road camera with horizon projection and dynamic front guide tracks.
  - **Rear Camera**: Reversing camera with auto-activation on **R** gear, bumper edge guide, and obstacle distance alarms.
  - **Left & Right Cameras**: Blind spot & curb parking alignment feeds with automated activation on turn signals.
  - **3D Orbit Panoramic View**: Interactive 360° vehicle orbit with drag rotation, yaw/pitch azimuth ticks, and ground reflection.
  - **Quad-Split View**: 4-channel synchronized mosaic view (Front, Rear, Left, Right).
- **Interactive Vehicle Telemetry Cockpit**:
  - Real-time PRND gear shifter.
  - Left / Right turn blinkers & Hazard warning.
  - Steering wheel angle slider (-540° to +540°) with dynamic wheel turning and guide arc curving.
  - Speed simulation (0-120 km/h) with automatic high-speed driving standby mode (> 30 km/h).
  - Door Open Warning (DOW) toggles for driver and passenger doors.

### 2. Vehicle Model Garage & Customizer
- **Multiple Vehicle Classes**: Sedan (A-Class, BMW 5-Series), SUV (A-Class, Mercedes-Benz G-Class), Hatchback (A-Class), MPV (Buick GL8), and Heavy Commercial (Futon Truck, Transit Coach).
- **Paint & Finish**: Glacier White, Onyx Black, Titanium Silver, Deep Sea Blue, Crimson Red, Emerald Green, and Cyber Amber with metallic reflection factors.
- **License Plate Customizer**: Editable plate number with standard Blue, New Energy Green (EV), Commercial Yellow, Consular Black, and Official White styles.
- **Vehicle Dimensions**: Exact wheelbase, length, width, and overhang metrics.

### 3. 4-Channel DVR & Dashcam Center
- **Synchronized 4-Channel Recording**: Live recording status, bitrates, resolutions, and blinking REC indicator.
- **Storage Management**: SD card storage usage meter (e.g., 48.2 GB / 64.0 GB) with Loop Overwrite and card format utility.
- **Video Library**: Event classification (Emergency Locked, Normal Loop, Parking Guard).
- **Instant Snapshots**: Save high-resolution camera snapshots to the gallery.

### 4. Camera Calibration & Stitching Tuning
- **8-Point Checkerboard Calibration Canvas**: Visual alignment targets around the vehicle footprint.
- **Fine Camera Tuning**: Offset X & Y nudges (-10px, -1px, +1px, +10px), pitch angle, yaw bias, and FOV scaling per camera channel.
- **Clean Leak Minimization**: Seam overlap width, blind spot leak margin, and mesh balance optimization.

### 5. AVM Settings
- Toggle dynamic steering guidelines, fixed distance markers, radar overlays, and proximity audio beeps.
- Blind Spot Detection (BSD) speed limit configuration (10 to 60 km/h).
- Door Open Warning (DOW) and 3D panoramic scan on startup.
- Video adjustments for Brightness, Contrast, and Saturation.
- Factory reset utility.

## Build Requirements
- Android SDK 36, JDK 21, Gradle 9.3.1, AGP 9.1.1
- Jetpack Compose with Material 3
