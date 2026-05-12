# TaminX Template

This is a clean Kotlin Multiplatform (CMP) template with shared UI and core infrastructure.

## Project Overview

TaminX provides a base for building native Android, iOS & Desktop apps with a shared Compose UI.

## Tech Stack
- Kotlin Multiplatform (KMP)
- Compose Multiplatform (CMP)
- Ktor Client (Network)
- Room KMP (Database)
- Koin (Dependency Injection)
- Navigation 3 (Navigation)
- Clean Architecture

## Module Structure

- `shared/`: App entry points and shared UI logic.
- `core/core-ui/`: Shared design tokens and components.
- `core/core-network/`: Network client setup.
- `core/core-database/`: Room database setup.
- `core/core-domain/`: Base domain models and interfaces.
- `core/core-plugin/`: Plugin system infrastructure.
- `androidApp/`: Android-specific entry point and resources.
- `desktopApp/`: Desktop-specific entry point.
- `iosApp/`: iOS-specific entry point (SwiftUI wrapper for CMP).

## Navigation
Navigation is handled in `shared/src/commonMain/kotlin/com/tamin/taminhamrah/ui/navigation/TaminHamrahNavGraph.kt`. Currently it is a placeholder for your implementation.
