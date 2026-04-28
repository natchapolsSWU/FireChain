# FireChain - Russian Roulette Game

🎲 **โปรเจกต์ส่งงานมหาลัย** - เกม Russian Roulette บน Android

## วิธีเล่น

### Normal Mode
- สปินกระบอก → จูงเรียวบาร์เรล
- หลีกไป = ยังมีชีวิต ✓
- โดนกระสุน = GAME OVER ✗

### Party Mode (สนุกกว่า!)
- หลายผู้เล่นเล่นกันเองแบบ turn by turn
- มี **กลยุทธ์เพิ่มเติม**:
  - **Peek** = ดูกระสุนแต่มีค่าบาป (penalty)
  - **Pass** = ข่มขู่คนต่อไป เพิ่ม cumulative shots
  - **Skip** = โหลดกระสุนเพิ่ม (รักษาชีวิตครั้งนี้)

## โครงสร้าง

```
📦 FireChain
├── 📱 UI Layer (Jetpack Compose)
│   ├── HomeScreen → เลือกโหมด
│   ├── NormalModeScreen → เล่นเดี่ยว
│   └── PartyModeScreen → เล่นเป็นกลุ่ม
│
├── 🧠 ViewModel Layer
│   ├── NormalModeViewModel → จัดการ state สำหรับ Normal Mode
│   └── PartyModeViewModel → จัดการเกม multiplayer
│
├── 🎰 Game Logic
│   └── RussianRouletteEngine → เหน physicalว game engine
│       - reset() → สุ่มกระสุนในกระบอก
│       - rotate() → เปลี่ยนตำแหน่งกระบอก
│       - fire() → จูงเรียว (ตรวจว่าโดนกระสุนหรือเปล่า)
│
└── 🎨 UI Components
    ├── CylinderView → วาดกระบอก 6 ช่องแบบ 3D
    └── GameButton/ActionButton → ปุ่มหลักๆ
```

## Technology Stack

| Layer | Tool |
|-------|------|
| Frontend | **Jetpack Compose** + Material3 |
| Backend | **Kotlin** + Coroutines (Flow) |
| State Management | **ViewModel** + StateFlow |
| Build | **Gradle** (Kotlin DSL) |
| Target | Android 24+ |

## Key Libraries (จาก build.gradle.kts)

```kotlin
// UI
androidx.compose:compose-bom
androidx.compose.material3
androidx.compose.ui

// Lifecycle
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.lifecycle:lifecycle-runtime-ktx

// Core
androidx.core:core-ktx
androidx.activity:activity-compose
```

## Installation & Run

```bash
# Clone
git clone https://github.com/natchapolsSWU/FireChain.git
cd FireChain

# Build & Run
./gradlew assembleDebug
# หรือเปิด Android Studio → Run
```

## คณะผู้จัดทำ

- **Project**: FireChain (Russian Roulette Game)
- **Language**: Kotlin (28.7%) + HTML (71.3% - layout/resources)
- **University**: Srinakharinwirot University (SWU)

---

**Note**: นี่คือเกมจำลองเพื่อการศึกษาเท่านั้น 🎓