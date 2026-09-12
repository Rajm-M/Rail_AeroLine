The Rail_AeroLine application follows a modern Clean Architecture pattern using the MVVM (Model-ViewModel-ViewModel) design. It is built entirely in Kotlin with Jetpack Compose for a reactive UI.
Here is the detailed breakdown of the system architecture:
1. The Core Architecture Layers
1. The Core Architecture Layers
Layer
Responsibility
Key Files
UI Layer (Compose)
Handles user interaction and rendering the cinematic theme.
AdminScreen.kt, HomeScreen.kt
Presentation Layer
ViewModels manage UI state and trigger background logic.
UserViewModel.kt, CaptureViewModel.kt
Domain Layer
Repositories act as the single source of truth, merging local and remote data.
UserRepository.kt, OheDataRepository.kt
Data Layer (Storage)
Room (SQLite) for structured data and EncryptedSharedPreferences for credentials.
AppDatabase.kt
graph TD
    A[Hardware Sensors / Manual Input] -->|Capture| B[CaptureViewModel]
    B -->|Save| C[OheDataRepository]
    C -->|SQL Command| D[(Local SQLite DB)]
    
    D -->|Auto-Trigger| E[DB Triggers]
    E -->|Security| F[Protect Admin Accounts]
    
    C -->|Background Job| G[WorkManager]
    G -->|If Internet Available| H[Google Drive Cloud]
    
    D -->|Raw Export| I[Phone Downloads Folder]
    C -->|Generate PDF| J[Professional Reports]


   4. Technology Stack Summary
•
UI: Jetpack Compose (Modern Declarative UI).
•
Dependency Injection: Hilt (Standard Android DI).
•
Persistence: Room Persistence Library (SQLite Abstraction).
•
Async/Multithreading: Kotlin Coroutines & Flow.
•
Background Tasks: WorkManager.
•
Security: Jetpack Security (AES-256 Encryption).
•
Cloud API: Google Drive V3 REST API.
This architecture is designed for High Reliability in industrial environments where offline performance is critical, but cloud data safety is a priority.
