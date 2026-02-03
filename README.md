# DentalCare Management App

A comprehensive dental practice management application built with modern Android development practices.

## Project Details

- **Application ID:** com.dentalcare.app
- **Target SDK:** 34
- **Min SDK:** 24
- **Language:** Kotlin
- **Build System:** Gradle with Kotlin DSL

## Architecture & Tech Stack

### Core Technologies
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material Design 3** - Latest Material Design components and theming
- **Kotlin Coroutines** - Asynchronous programming
- **Kotlin Flow** - Reactive data streams

### Architecture Components
- **MVVM Pattern** - Model-View-ViewModel architecture
- **Repository Pattern** - Data layer abstraction
- **Hilt** (v2.48) - Dependency injection
- **Navigation Compose** - Type-safe navigation

### Firebase Integration (BOM 32.7.0)
- **Firebase Authentication** - User authentication
- **Cloud Firestore** - NoSQL cloud database
- **Firebase Storage** - File and image storage
- **Firebase Analytics** - App analytics

### Additional Libraries
- **Coil** (v2.5.0) - Image loading and caching
- **Material Icons Extended** - Comprehensive icon set

## Project Structure

```
app/src/main/java/com/dentalcare/app/
├── DentalCareApplication.kt          # Application class with Hilt
├── MainActivity.kt                    # Main activity
├── data/
│   ├── model/                        # Data models
│   │   ├── User.kt
│   │   ├── Patient.kt
│   │   ├── Appointment.kt
│   │   ├── Treatment.kt
│   │   └── Diagnostic.kt
│   └── repository/                   # Repository layer
│       ├── AuthRepository.kt
│       ├── PatientRepository.kt
│       └── AppointmentRepository.kt
├── di/
│   └── AppModule.kt                  # Hilt dependency injection
├── ui/
│   ├── theme/                        # Material Design 3 theming
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── components/                   # Reusable UI components
│   │   ├── DentalButton.kt
│   │   ├── DentalTextField.kt
│   │   └── LoadingIndicator.kt
│   ├── navigation/                   # Navigation setup
│   │   ├── Screen.kt
│   │   └── NavGraph.kt
│   └── screens/                      # Feature screens
│       ├── auth/                     # Authentication screens
│       ├── dashboard/                # Dashboard
│       ├── patients/                 # Patient management
│       ├── appointments/             # Appointment management
│       ├── treatments/               # Treatment records
│       ├── diagnostics/              # Diagnostic records
│       ├── reports/                  # Reporting
│       └── settings/                 # Settings
└── util/
    ├── Constants.kt                  # App constants
    └── Resource.kt                   # Resource wrapper
```

## Features

### Current Implementation
- ✅ User Authentication (Login/Register)
- ✅ Dashboard with quick actions
- ✅ Patient list management
- ✅ Appointment list management
- ✅ Material Design 3 theming (Light/Dark mode)
- ✅ Navigation system
- ✅ Repository pattern with Firebase
- ✅ Loading states and error handling

### Ready for Implementation
- 📋 Add/Edit/Delete Patients
- 📋 Schedule/Manage Appointments
- 📋 Treatment records and tracking
- 📋 Diagnostic imaging and records
- 📋 Report generation and export

## Setup Instructions

### Prerequisites
1. Android Studio Hedgehog (2023.1.1) or newer
2. JDK 17
3. Android SDK 34
4. Firebase project setup

### Firebase Configuration
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing one
3. Add an Android app with package name: `com.dentalcare.app`
4. Download `google-services.json`
5. Replace the placeholder file at `app/google-services.json`
6. Enable Authentication (Email/Password), Firestore, and Storage in Firebase Console

### Build and Run
```bash
# Build the project
./gradlew build

# Run on connected device/emulator
./gradlew installDebug
```

## Data Models

- **User:** id, email, name, role, phone
- **Patient:** id, name, email, phone, dateOfBirth, address, medicalHistory
- **Appointment:** id, patientId, doctorId, date, time, reason, status
- **Treatment:** id, patientId, doctorId, treatmentType, cost, medications
- **Diagnostic:** id, patientId, doctorId, diagnosticType, findings, images

## Development Guidelines

- Follow MVVM architecture pattern
- Use Repository pattern for data access
- Handle loading and error states properly
- Keep ViewModels free of Android dependencies

---

**Note:** Replace the placeholder `google-services.json` with your actual Firebase configuration file before building.