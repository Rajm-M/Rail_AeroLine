# Implementation Plan - Rail_AeroLine Project

This plan outlines the multi-phase implementation of the Rail_AeroLine application, focusing on security, role-based access, connectivity, kiosk mode, and data management.

## User Review Required

> [!IMPORTANT]
> The project currently uses **Jetpack Compose**. The request mentions "Fragments" (e.g., `AdminFragment.kt`). I will implement these as Fragment-wrapped Compose screens or pure Compose screens depending on your preference. For this plan, I'll assume a Compose-first approach with a Navigation Drawer in `MainActivity`.

> [!WARNING]
> **Kiosk Mode** (Phase 3) requires the app to be a Device Owner. This usually involves a one-time setup via ADB or QR code provisioning. I will provide the `KioskReceiver` and logic, but manual device configuration will be needed.

## Proposed Changes

---

### Phase 1: Core Architecture & Security
*Establish the foundation with DI, Database, Security, and Role-based UI.*

#### [MODIFY] [libs.versions.toml](file:///D:/Rail_AeroLine/gradle/libs.versions.toml)
* Add versions and library definitions for:
    * Hilt (Dependency Injection)
    * Room (Local Persistence)
    * WorkManager (Background Tasks)
    * Security-Crypto (AES-256 Encryption)

#### [MODIFY] [app/build.gradle.kts](file:///D:/Rail_AeroLine/app/build.gradle.kts)
* Apply Hilt plugin.
* Add implementation dependencies for Hilt, Room, WorkManager, and Security-Crypto.

#### [NEW] [SecurityManager.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/security/SecurityManager.kt)
* Implement AES-256 encryption using `EncryptedSharedPreferences` and `MasterKeys` for secure local storage.

#### [NEW] [UserRole.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/model/UserRole.kt)
* Define `enum class UserRole` (Admin, Operator, Super-User).

#### [NEW] [AdminScreen.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/ui/admin/AdminScreen.kt)
* Dedicated dashboard for elevated tasks (Pairing, Logs, Security).

#### [MODIFY] [MainActivity.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/MainActivity.kt)
* Implement a `ModalNavigationDrawer` with dynamic menu items based on the current user's role.

---

### Phase 2: Admin Module & Wireless Connectivity
*Secure connection to LLOMG unit.*

#### [NEW] [DeviceSetupScreen.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/ui/admin/DeviceSetupScreen.kt)
* Wizard-style UI for secure pairing.

#### [NEW] [BluetoothPairHelper.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/connectivity/BluetoothPairHelper.kt)
* Logic for "One-time secure pairing" using unique device IDs.

#### [NEW] [WifiLinkManager.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/connectivity/WifiLinkManager.kt)
* WiFi Direct / SoftAP connection logic for wireless data transfer.

---

### Phase 3: Kiosk Mode
*Device lockdown for dedicated usage.*

#### [NEW] [KioskReceiver.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/kiosk/KioskReceiver.kt)
* `DeviceAdminReceiver` implementation to handle lockdown events.

#### [MODIFY] [MainActivity.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/MainActivity.kt)
* Add `startLockTask()` and `stopLockTask()` logic governed by Admin settings.

---

### Phase 4: Data Capture & Offline Ops
*OHE parameter logging and GPS integration.*

#### [NEW] [OheDataRepository.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/data/OheDataRepository.kt)
* Room database and Repository for OHE parameters (Height, Stagger, etc.).

#### [NEW] [GpsHelper.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/location/GpsHelper.kt)
* Wrapper for `FusedLocationProviderClient` with tagging logic.

#### [NEW] [SyncWorker.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/worker/SyncWorker.kt)
* `CoroutineWorker` for background data synchronization.

---

### Phase 5: Reports & Integration
*Generating TDMS outputs.*

#### [NEW] [ReportGenerator.kt](file:///D:/Rail_AeroLine/app/src/main/java/com/example/rail_aeroline/report/ReportGenerator.kt)
* Logic for PDF/Excel generation following TDMS standards.

---

## Verification Plan

### Automated Tests
- **Unit Tests**: Test `SecurityManager` encryption/decryption and `UserRole` access logic.
- **Integration Tests**: Verify `Room` DAO operations and `WorkManager` scheduling for `SyncWorker`.

### Manual Verification
1. **Role Check**: Log in as different users and verify the Navigation Drawer updates dynamically.
2. **Kiosk Mode**: Trigger lockdown and verify hardware buttons (Home/Recents) are disabled.
3. **Connectivity**: Simulate Bluetooth pairing and WiFi handshake with the LLOMG unit.
4. **Data Export**: Generate a sample report and verify the file structure against TDMS requirements.
