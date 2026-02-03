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

### ✅ Complete Implementation

#### 🔐 Authentication Module
- User Login with email/password
- User Registration with role selection (Admin, Dentist, Secretary, Assistant)
- Password reset functionality
- Email validation
- Animated splash screen with auto-navigation

#### 📊 Dashboard
- Personalized greeting with user name and role
- Real-time statistics cards:
  - Today's appointments count
  - Total patients count
  - Pending appointments count
  - Completed treatments this week
- Quick action buttons for common tasks
- Today's appointments list
- Material Design 3 cards with animations

#### 👥 Patient Management
- Patient list with search functionality
- Add/Edit patient with validation:
  - Personal information (name, contact, address)
  - Medical history
  - Dental history
- Patient detail screen with 3 tabs:
  - Overview: Complete patient information
  - Appointments: Patient's appointment history
  - Treatments: Patient's treatment records
- Quick actions: Schedule appointment, add treatment
- Delete patient with confirmation dialog
- Empty state handling

#### 📅 Appointment Management
- Appointment list with filter options
- Add/Edit appointment with:
  - Patient selector (searchable)
  - Dentist selector
  - 9 service types (Cleaning, Extraction, Filling, Root Canal, etc.)
  - Date and time pickers (Material 3)
  - Walk-in toggle
  - Notes field
- Double-booking prevention
- Appointment detail with status updates:
  - Confirm, Complete, Cancel actions
  - Status change confirmation dialogs
- Color-coded status chips

#### 🦷 Treatment Records
- Treatment list with filtering
- Add/Edit treatment with:
  - Patient and dentist selection
  - Treatment type dropdown
  - Treatment date picker
  - Detailed notes
  - Follow-up support with date picker
- Treatment detail screen
- Link to patient profile

#### 📷 Diagnostic Management
- Diagnostic records list (grid/list view)
- Upload diagnostic with:
  - Patient selection
  - Type selection (Radiograph, Photo, Cast)
  - Image picker integration
  - Notes field
  - Firebase Storage upload
- Full-screen image viewer with:
  - Pinch-to-zoom (1x-5x)
  - Diagnostic details overlay
  - Share/Download options

#### 📈 Reports & Settings
- Reports dashboard (ready for implementation)
- Settings screen with:
  - User profile display
  - Theme toggle (Light/Dark/System)
  - Change password
  - Notification preferences
  - About section
  - Logout with confirmation

#### 🎨 UI/UX Features
- Material Design 3 throughout
- Dynamic color theming
- Smooth animations and transitions
- Loading states with indicators
- Error handling with user-friendly messages
- Empty state illustrations
- Confirmation dialogs for destructive actions
- Form validation on all inputs
- Responsive layouts

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
6. Enable the following in Firebase Console:
   - **Authentication:** Enable Email/Password provider
   - **Firestore Database:** Create database in production mode
   - **Storage:** Set up Firebase Storage

### Firestore Security Rules

Add these security rules in Firebase Console > Firestore Database > Rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Helper function to check if user is authenticated
    function isAuthenticated() {
      return request.auth != null;
    }
    
    // Helper function to get user role
    function getUserRole() {
      return get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role;
    }
    
    // Helper function to check if user is admin
    function isAdmin() {
      return isAuthenticated() && getUserRole() == 'admin';
    }
    
    // Helper function to check if user is dentist
    function isDentist() {
      return isAuthenticated() && getUserRole() == 'dentist';
    }
    
    // Helper function to check if user is secretary
    function isSecretary() {
      return isAuthenticated() && getUserRole() == 'secretary';
    }
    
    // Users collection
    match /users/{userId} {
      allow read: if isAuthenticated();
      allow create: if isAdmin() || !exists(/databases/$(database)/documents/users/$(request.auth.uid));
      allow update, delete: if isAdmin();
    }
    
    // Patients collection
    match /patients/{patientId} {
      allow read: if isAuthenticated();
      allow create, update: if isAdmin() || isDentist() || isSecretary();
      allow delete: if isAdmin();
    }
    
    // Appointments collection
    match /appointments/{appointmentId} {
      allow read: if isAuthenticated();
      allow create, update, delete: if isAdmin() || isDentist() || isSecretary();
    }
    
    // Treatments collection
    match /treatments/{treatmentId} {
      allow read: if isAuthenticated();
      allow create, update: if isAdmin() || isDentist();
      allow delete: if isAdmin();
    }
    
    // Diagnostics collection
    match /diagnostics/{diagnosticId} {
      allow read: if isAuthenticated();
      allow create, update: if isAdmin() || isDentist();
      allow delete: if isAdmin();
    }
    
    // Dentists collection
    match /dentists/{dentistId} {
      allow read: if isAuthenticated();
      allow write: if isAdmin();
    }
  }
}
```

### Firebase Storage Rules

Add these rules in Firebase Console > Storage > Rules:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /diagnostics/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
                     (request.resource.size < 10 * 1024 * 1024) && // Max 10MB
                     request.resource.contentType.matches('image/.*');
    }
  }
}
```

## Role-Based Access Control

| Feature | Admin | Dentist | Secretary | Assistant |
|---------|-------|---------|-----------|-----------|
| Register Users | ✓ | ✗ | ✗ | ✗ |
| View Patients | ✓ | ✓ | ✓ | ✓ |
| Add/Edit Patients | ✓ | ✓ | ✓ | ✗ |
| Delete Patients | ✓ | ✗ | ✗ | ✗ |
| Manage Appointments | ✓ | ✓ | ✓ | ✗ |
| Add Treatments | ✓ | ✓ | ✗ | ✗ |
| View Treatments | ✓ | ✓ | ✓ | ✓ |
| Upload Diagnostics | ✓ | ✓ | ✗ | ✓ |
| View Diagnostics | ✓ | ✓ | ✓ | ✓ |
| Generate Reports | ✓ | ✓ | ✓ | ✗ |
| App Settings | ✓ | ✓ | ✓ | ✓ |

### Build and Run
```bash
# Build the project
./gradlew build

# Run on connected device/emulator
./gradlew installDebug
```

## Data Models

### Firestore Collections

#### users
```kotlin
{
  userId: String (Firebase Auth UID)
  email: String
  firstName: String
  lastName: String
  role: String (admin/dentist/secretary/assistant)
  linkedDentistId: String? (optional)
  createdAt: Timestamp
  isActive: Boolean
}
```

#### patients
```kotlin
{
  patientId: String (auto-generated)
  firstName: String
  lastName: String
  contactNumber: String
  address: String
  medicalHistory: String
  dentalHistory: String
  dateRegistered: Timestamp
  createdBy: String (userId)
}
```

#### dentists
```kotlin
{
  dentistId: String
  firstName: String
  lastName: String
  specialization: String
  contactNumber: String
  isActive: Boolean
}
```

#### appointments
```kotlin
{
  appointmentId: String (auto-generated)
  patientId: String
  dentistId: String
  appointmentDate: Timestamp
  appointmentTime: String
  serviceType: String
  status: String (pending/confirmed/completed/cancelled)
  isWalkIn: Boolean
  notes: String
  createdAt: Timestamp
  createdBy: String (userId)
}
```

#### treatments
```kotlin
{
  treatmentId: String (auto-generated)
  patientId: String
  dentistId: String
  treatmentDate: Timestamp
  treatmentType: String
  treatmentNotes: String
  followUpRequired: Boolean
  followUpDate: Timestamp? (optional)
  createdAt: Timestamp
  createdBy: String (userId)
}
```

#### diagnostics
```kotlin
{
  diagnosticId: String (auto-generated)
  patientId: String
  dentistId: String
  fileUrl: String
  fileName: String
  type: String (panoramic/periapical/photo/cast)
  notes: String
  dateUploaded: Timestamp
  uploadedBy: String (userId)
}
```

## Development Guidelines

### Code Organization
- Follow MVVM architecture pattern
- Use Repository pattern for data access
- Keep ViewModels free of Android framework dependencies
- Use sealed classes for state management (Resource.kt)
- Implement proper error handling in repositories
- Use Kotlin Coroutines and Flow for asynchronous operations

### UI Development
- Use Jetpack Compose for all UI
- Follow Material Design 3 guidelines
- Implement proper loading and error states
- Add empty state handling for lists
- Use confirmation dialogs for destructive actions
- Validate all user inputs

### Firebase Integration
- Use Firebase BOM for version management
- Implement real-time listeners for live data updates
- Handle offline scenarios with proper error messages
- Use coroutines extensions for Firebase operations
- Properly clean up listeners to avoid memory leaks

### Testing
- Write unit tests for ViewModels and repositories
- Test navigation flows
- Validate form inputs
- Test error handling scenarios

## Building the Project

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Run Tests
```bash
./gradlew test
```

### Clean Build
```bash
./gradlew clean build
```

## Known Limitations

- Image upload requires Firebase Storage to be properly configured
- Real-time updates require active internet connection
- Some features require specific user roles (see RBAC table above)
- Date/time pickers use Material 3 APIs (requires proper theme setup)

## Future Enhancements

- [ ] PDF report generation and export
- [ ] Push notifications for appointment reminders
- [ ] Calendar integration
- [ ] Multi-language support
- [ ] Offline data caching with Room database
- [ ] Appointment reminder notifications
- [ ] Patient portal access
- [ ] Integration with payment gateways
- [ ] Advanced analytics dashboard
- [ ] Backup and restore functionality

## Troubleshooting

### Build Issues
- **Google Services error:** Ensure `google-services.json` is in the `app/` directory
- **Dependency resolution:** Run `./gradlew --refresh-dependencies`
- **Sync issues:** File > Invalidate Caches / Restart in Android Studio

### Runtime Issues
- **Firebase Authentication fails:** Check internet connection and Firebase console setup
- **Data not loading:** Verify Firestore rules are correctly configured
- **Image upload fails:** Check Firebase Storage rules and permissions

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is created for **Dra. Josephine B. Lazaro Dental Care** clinic.

## Support

For issues and questions, please create an issue in the repository.

---

**Built with ❤️ using Kotlin and Jetpack Compose**