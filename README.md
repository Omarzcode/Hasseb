# Digital Muhasabah (محاسبة النفس)

> A minimalist, private, local-first spiritual habit tracker and reflection companion, built with Jetpack Compose & Kotlin.

---

## Table of Contents

- [Overview & Philosophy](#overview--philosophy)
- [Core Features](#core-features)
- [Architecture & Codebase Layout](#architecture--codebase-layout)
- [Tech Stack & Dependencies](#tech-stack--dependencies)
- [Getting Started](#getting-started-for-contributors)
- [Testing & Verification](#testing--verification)
- [Contribution Guidelines](#contribution-guidelines)
- [License](#license)

---

## Overview & Philosophy

**Digital Muhasabah** facilitates mindful, consistent spiritual growth through the Islamic practice of *Muhasabah* — self-inventory and introspection.

Unlike conventional habit trackers that lean on toxic productivity and high-stress streaks, Digital Muhasabah is built around four principles:

- **Tadaaruk (Make-Up) & Compassion** — Tri-state logging (`ON_TIME`, `LATE`/Tadaaruk, `MISSED`) rewards spiritual recovery and diligence rather than punishing delayed action.
- **Local-First & Zero Tracking** — A fully offline local database built with Room. No tracking, no external analytics, no user accounts, and zero remote data harvesting.
- **Stage-Based Spiritual Progression** — Tiered habit sets, from Stage 1 (Core Fard) through Stage 3 (Quran & Adhkar), plus fully custom habits.
- **Organic, Earth-Tone Design** — A soothing Material Design 3 palette of Olive Green, Sage, Terracotta, and Desert Sand, with full Dark Theme support.

---

## Core Features

### 1. Daily Check-In & Tri-State Logging

- **Tri-state status:**
  - `ON_TIME` (حاضر / أداء) — completed within its primary prescribed or planned time.
  - `LATE` (قضاء / تدارك) — made up or completed later, acknowledged with compassion rather than penalty.
  - `MISSED` (فائت) — tracked transparently, without breaking positive momentum.
- **Reflective daily note** — an integrated space for journaling, gratitude, and daily introspection.
- **Dynamic date navigation** — browse past check-ins and review historical completion at a glance.

### 2. Spiritual Insights Engine (`InsightsEngine.kt`)

- **Compassionate guidance** — detects recovery patterns, such as catching up after consecutive misses.
- **Burnout & strain detection** — identifies sudden activity drops and responds with gentle, Quranic/hadith-grounded reminders rather than alarms.
- **Milestone recognition** — celebrates consistency at 3, 7, 14, 30, and 40 days, without streak-shaming when a streak breaks.

### 3. Spiritual Analytics Dashboard

- **7-day consistency & completion trends** — interactive bar charts tracking on-time vs. make-up performance.
- **30-day spiritual heatmap** — a visual matrix of daily activity intensity.
- **Category radar balance wheel** (`RadarBalanceWheel.kt`) — a custom Jetpack Compose canvas visualization of spiritual balance across five dimensions:
  - Prayer (الصلاة)
  - Quran (القرآن)
  - Remembrance (الذكر)
  - Sunnah (السنن)
  - Mindfulness / self-discipline (التزكية)
- **Seasonal reflections** — dedicated views highlighting spiritual seasons (Ramadan, Dhul-Hijjah, Rajab/Sha'ban).

### 4. Adaptive Reminder Suggestion (`AdaptiveReminderEngine.kt`)

Learns the user's historical check-in time patterns locally and calculates an optimal, non-intrusive reminder window, with manual override available at any time.

### 5. Settings & Customization

- **Tier progression** — switch freely between Stage 1, Stage 2, and Stage 3.
- **Custom habit builder** — add custom habits with their own categories and target frequencies.
- **Pause & resume** — temporarily pause a habit without losing its history.
- **Demo mode** — a one-click, 35-day sample history generator for quick review and testing.
- **Data reset** — a full wipe option, for complete privacy and control.

---

## Architecture & Codebase Layout

The project follows clean **MVVM** (Model-View-ViewModel) with a **Unidirectional Data Flow** (UDF).

```
app/src/main/java/com/example/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt          # Room database definition with schema migrations
│   │   ├── HabitDao.kt             # Habit queries (active, tier-filtered, custom)
│   │   ├── HabitLogDao.kt          # Daily logs and date-range queries
│   │   └── UserSettingsDao.kt      # User preferences and spiritual tier persistence
│   ├── model/
│   │   ├── Models.kt               # Entity and domain models (HabitEntity, HabitLogEntity, etc.)
│   │   └── DefaultHabits.kt        # Predefined seeds for Stages 1, 2, and 3
│   └── repository/
│       └── MuhasabahRepository.kt  # Unified repository mediating database operations
├── domain/
│   └── engine/
│       ├── AdaptiveReminderEngine.kt # Computes adaptive reflection notification windows
│       ├── InsightsEngine.kt         # Evaluates streaks, recoveries, balance & alerts
│       ├── StatisticsEngine.kt       # Aggregates weekly, monthly, and yearly radar metrics
│       └── TimeUtils.kt              # Date manipulation & formatting helpers
├── ui/
│   ├── screens/
│   │   ├── checkin/                # Daily check-in screen, habit cards, custom habit dialog
│   │   ├── dashboard/              # Weekly, monthly deep-dive, yearly reflection & radar wheel
│   │   ├── insights/               # Daily insight cards, encouragement banners, milestone chips
│   │   ├── onboarding/             # Intro and spiritual tier onboarding selector
│   │   └── settings/               # Habit management, reminder settings, backup/reset
│   ├── theme/
│   │   ├── Color.kt                # Extended theme colors (Olive, Sage, Terracotta, Sand)
│   │   ├── Theme.kt                # MaterialTheme wrapper with Light/Dark extended colors
│   │   ├── Type.kt                 # Typography and Arabic font configurations
│   │   └── Dimens.kt               # Spacing, padding, and corner radius tokens
│   └── viewmodel/
│       └── MuhasabahViewModel.kt   # Central state holder with StateFlow & UI events
└── MainActivity.kt                 # Top-level scaffold, navigation bar, and screen routing
```

---

## Tech Stack & Dependencies

| Category | Choice |
|---|---|
| Language | Kotlin 2.2.10 |
| UI Toolkit | Jetpack Compose with Material 3 (Compose BOM `2024.09.00`) |
| Architecture | Android Jetpack ViewModel, StateFlow, Coroutines |
| Local Persistence | AndroidX Room 2.7.0 (with KSP) |
| Async Processing | Kotlinx Coroutines |
| Build System | Gradle Kotlin DSL (`build.gradle.kts`) |
| SDK Target | Min SDK `24` (Android 7.0) · Target SDK `36` |

---

## Getting Started for Contributors

### Prerequisites

1. **Android Studio** — Ladybug / Meerkat or later.
2. **JDK** — Java 17 or Java 21, configured in Android Studio.
3. **Android SDK** — API 34+ installed.

### Setup & Build

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd <repository-directory>
   ```
2. Open the project in Android Studio.
3. Allow Gradle to sync dependencies.
4. Build the debug APK:
   ```bash
   gradle assembleDebug
   ```
5. Run on an Android device or emulator.

---

## Testing & Verification

**Compile verification:**
```bash
gradle :app:compileDebugKotlin
```

**Unit & JVM tests:**
```bash
gradle :app:testDebugUnitTest
```

**Roborazzi screenshot tests** (when updating UI components):
```bash
gradle :app:verifyRoborazziDebug
# Or to record updated baseline screenshots:
gradle :app:recordRoborazziDebug
```

---

## Contribution Guidelines

Contributions are welcome. Please adhere to the following principles:

1. **Local-first & privacy**
   Never add third-party tracking, analytics, crash reporting that sends personal notes, or external cloud calls — without explicit, opt-in user consent.

2. **Compassionate UX**
   Avoid aggressive notification tones or shaming language when prayers or habits are missed. The app's purpose is encouragement and *tadaaruk* (recovery), not guilt.

3. **Jetpack Compose best practices**
   - Follow Material Design 3 tokens.
   - Use `MaterialTheme.colorScheme` and `MaterialTheme.extendedColors` instead of hardcoded hex values, to preserve flawless dark mode support.
   - Ensure all touch targets meet or exceed 48dp (`Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)`).
   - Add clear `Modifier.testTag("...")` to interactive elements for test automation.

4. **State management**
   Keep composables stateless where possible; hoist state and actions into `MuhasabahViewModel.kt`.

---

## License

Distributed under the Apache 2.0 License or MIT License. See `LICENSE` for details.
