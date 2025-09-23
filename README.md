# OneClick

A cross-platform home automation application built with Kotlin Multiplatform, featuring a modern client-server architecture with support for Android, iOS, and Web platforms.

## 🏗️ Architecture

TheOneClick follows a clean architecture pattern with a modularized structure:

### Client Applications
- **Android App**: Native Android application with Material Design
- **iOS App**: Native iOS application using SwiftUI integration
- **Web App**: WebAssembly-based web application

### Server Components
- **Main Server**: Production-ready Ktor server with JWT authentication
- **Mock Server**: Development server with mock data sources
- **Shared Server Logic**: Common business logic and data models

### Shared Modules
- **Core Contracts**: Shared data models and API contracts
- **Logging**: Platform-specific logging implementations
- **Dispatchers**: Coroutine dispatchers for each platform
- **Time Provider**: System time abstraction

## 🚀 Features

- **Home Management**: Create and manage smart homes with multiple rooms
- **Device Control**: Control various smart devices across different rooms
- **User Authentication**: Secure JWT-based authentication system
- **Cross-Platform**: Single codebase targeting Android, iOS, and Web
- **Real-time Data**: Redis caching for fast data access
- **Database Support**: PostgreSQL for persistent data storage
- **Multi-language Support**: Internationalization with Spanish and English

## 🛠️ Tech Stack

### Frontend
- **Kotlin Multiplatform**: Shared business logic across platforms
- **Jetpack Compose**: Modern UI toolkit for declarative UIs
- **Compose Multiplatform**: UI framework for cross-platform development
- **Navigation Compose**: Type-safe navigation
- **ViewModel**: State management with lifecycle awareness

### Backend
- **Ktor**: Asynchronous server framework
- **PostgreSQL**: Primary database with SQLDelight for type-safe SQL
- **Redis**: Caching and session management
- **HikariCP**: Connection pooling
- **JWT**: Secure token-based authentication

### Development Tools
- **Gradle**: Build system with Kotlin DSL
- **Detekt**: Static code analysis
- **Docker Compose**: Local development environment
- **Chamaleon**: Environment configuration management

## 📋 Prerequisites

- **JDK 21** or higher
- **Android Studio** (for Android development)
- **Xcode** (for iOS development, macOS only)
- **Docker & Docker Compose** (for local server development)
- **PostgreSQL** (for production database)
- **Redis** (for caching)

## 📁 Project Structure

```
TheOneClick/
├── client/                          # Client applications
│   ├── app/                         # Main client app (Android, iOS, Web)
│   ├── features/                    # Feature modules
│   │   └── home/                    # Home management feature
│   ├── shared/                      # Shared client modules
│   │   ├── di/                      # Dependency injection
│   │   ├── navigation/              # Navigation logic
│   │   ├── network/                 # Network layer
│   │   ├── notifications/           # Notification system
│   │   └── ui/                      # Shared UI components
│   └── iosApp/                      # iOS-specific code
├── server/                          # Server applications
│   ├── app/                         # Main server application
│   ├── mock/                        # Mock server for development
│   └── shared/                      # Shared server modules
├── shared/                          # Cross-platform shared modules
│   ├── contracts/                   # API contracts and models
│   ├── dispatchers/                 # Coroutine dispatchers
│   ├── logging/                     # Logging abstractions
│   └── timeProvider/                # Time provider abstraction
└── build-logic/                     # Build configuration and plugins
```

---

Built with ❤️ using Kotlin Multiplatform
