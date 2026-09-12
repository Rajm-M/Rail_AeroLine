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
