# 📤 JAPS (Just Another Payload Sender)

> A clean Android app for injecting binary payloads over TCP.

![Version](https://img.shields.io/badge/version-1.1-green)
![Min SDK](https://img.shields.io/badge/minSDK-21-blue)
![Target SDK](https://img.shields.io/badge/targetSDK-34-blue)

---

## ✨ Features

- **Quick Injection** — Send any `.bin` payload to a target IP and port
- **Preset System** — Save frequently used configurations (IP, port, payload)
- **One-Click Load** — Load any saved preset instantly
- **Status Feedback** — Real-time connection and transfer status
- **No Ads** — 100% open-source, privacy-focused
- **Kotlin + Coroutines** — Non-blocking TCP on background thread

---

## 📱 UI Overview

```
┌─────────────────────────────┐
│  Payload Sender             │
│      v1.1                   │
├─────────────────────────────┤
│ IP Address    │    Port     │
│ [192.168.X.X] │ [9021]      │
├─────────────────────────────┤
│ Payload Path        │  ...  │
│ [payload.bin]       │ [📁]  │
├─────────────────────────────┤
│ [Inject Payload] [Save]     │
├─────────────────────────────┤
│ Status: Idle...             │
├─────────────────────────────┤
│ Saved Presets               │
│ ✓ MyPreset [Load] [✕]      │
│ ✓ Test     [Load] [✕]      │
└─────────────────────────────┘
```

---

## 📖 How to Use

1. **Enter Target IP** — Device running bin loader (e.g., `192.168.0.1`)
2. **Enter Port** — Port bin loader is listening on (e.g., `9021`)
3. **Select Payload** — Tap `…` to choose `.bin` file
4. **Send** — Tap `Inject Payload`
5. **Monitor** — Watch status bar for success/error

### Save a Preset
1. Fill in IP, Port, and select a Payload
2. Tap `Save`
3. Enter a name 
4. Tap `Save`
5. Preset appears in list below for quick reuse

### Load a Preset
1. Tap `Load` on any saved preset
2. Fields auto-populate
3. Tap `Inject Payload` immediately

---

## 🏗️ Project Structure

```
PayloadSender/
├── app/src/main/
│   ├── java/com/payloadSender/
│   │   ├── MainActivity.kt          # UI & presets logic
│   │   ├── PresetManager.kt         # SharedPreferences storage
│   │   ├── PresetAdapter.kt         # RecyclerView adapter
│   │   ├── PayloadSender.kt         # TCP socket logic
│   │   └── Preset.kt                # Data class
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml
│   │   │   └── item_preset.xml
│   │   ├── drawable/
│   │   │   └── [button & input styles]
│   │   ├── mipmap-*/
│   │   │   └── ic_launcher.png      # App icons (5 densities)
│   │   └── values/
│   │       ├── colors.xml
│   │       ├── strings.xml
│   │       └── styles.xml
│   └── AndroidManifest.xml
├── build.gradle
├── gradle.properties
└── settings.gradle
```

---

## 🔧 Tech Stack

| Component | Version |
|-----------|---------|
| **Language** | Kotlin |
| **Min SDK** | 21 (Android 5.0) |
| **Target SDK** | 34 (Android 14) |
| **AGP** | 8.1.0 |
| **Gradle** | 8.2 |
| **AndroidX** | Latest |

---

## 📦 Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
```
Used to open TCP sockets to remote devices.

---

## 💾 Data Storage

- **Presets** stored locally using `SharedPreferences`
- No internet connection required
- No data sent to external servers
- 100% offline operation

---

## ⚠️ Disclaimer

This tool is for **legitimate use only**:
- ✅ Personal testing
- ✅ Private network testing
- ❌ Unauthorized device access
- ❌ Piracy or illegal activity

Use responsibly and in accordance with your local laws.

---

## 📄 License

GNU GPLv3

---

## 🤝 Contributing

Issues and PRs welcome! Feel free to submit bug reports or feature requests.

---
