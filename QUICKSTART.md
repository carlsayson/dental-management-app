# 🚀 Quick Start Guide - DentalCare Management App

Get up and running in under 10 minutes!

## ⚡ Prerequisites

- Android Studio Hedgehog (2023.1.1+)
- JDK 17
- Android SDK 34
- Google account for Firebase

## 🔥 Firebase Setup (5 minutes)

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create project and add Android app
3. **Package name:** `com.dentalcare.app`
4. Download `google-services.json` → place in `app/` directory
5. Enable: Authentication (Email/Password), Firestore, Storage
6. Add security rules from `FIREBASE_SETUP.md`

## 🏗️ Build & Run (3 minutes)

### Android Studio
```
1. Open project
2. Sync Gradle (File > Sync Project with Gradle Files)
3. Build > Make Project (Ctrl+F9)
4. Run ▶️ on device/emulator
```

### Command Line
```bash
./gradlew build
./gradlew installDebug
```

## 🎉 Test the App

1. Launch → Splash screen → Login
2. Register (use Admin role)
3. Add test patient
4. Create appointment
5. Try theme toggle in Settings

## 🔍 Verify

- Firebase Console > Authentication > See your user
- Firestore Database > See `users` and `patients` collections

## 🛠️ Troubleshooting

| Issue | Solution |
|-------|----------|
| google-services.json not found | Place in `app/` directory |
| Package mismatch | Use `com.dentalcare.app` in Firebase |
| Build errors | `./gradlew clean build --refresh-dependencies` |

## 📚 Documentation

- `FIREBASE_SETUP.md` - Detailed Firebase guide
- `ARCHITECTURE.md` - Architecture overview
- `TESTING_CHECKLIST.md` - Complete testing guide
- `SCREENSHOTS.md` - UI descriptions
- `README.md` - Main documentation

## ✅ Success Checklist

- [x] Project builds
- [x] App runs
- [x] Can register/login
- [x] Firebase connected
- [x] Can add data

**Happy Coding! 🎉**
