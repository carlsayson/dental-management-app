# Firebase Setup Guide for DentalCare Management App

This guide provides step-by-step instructions for setting up Firebase for the DentalCare Management App.

## Prerequisites

- Google Account
- Android Studio with the project opened
- Internet connection

## Step 1: Create Firebase Project

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"** or **"Create a project"**
3. Enter project name: `DentalCare Management` (or any name you prefer)
4. (Optional) Enable Google Analytics if you want to track app usage
5. Click **"Continue"** and wait for the project to be created

## Step 2: Add Android App to Firebase Project

1. In the Firebase console, click the **Android icon** to add an Android app
2. Enter the following details:
   - **Android package name:** `com.dentalcare.app` (must match exactly)
   - **App nickname (optional):** `DentalCare App`
   - **Debug signing certificate SHA-1 (optional):** Leave empty for now
3. Click **"Register app"**

### Get SHA-1 Certificate (Optional - for future features)

```bash
# For debug keystore (development)
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# For release keystore (production)
keytool -list -v -keystore /path/to/your/keystore.jks -alias your-key-alias
```

## Step 3: Download google-services.json

1. After registering the app, Firebase will prompt you to download `google-services.json`
2. Click **"Download google-services.json"**
3. Move the downloaded file to your project's **`app/`** directory
4. Replace the existing placeholder file

**Important:** The file structure should look like this:
```
dental-management-app/
├── app/
│   ├── google-services.json  ← Place file here
│   ├── build.gradle.kts
│   └── src/
```

## Step 4: Enable Firebase Authentication

1. In Firebase Console, go to **Build > Authentication**
2. Click **"Get started"**
3. Go to the **"Sign-in method"** tab
4. Click on **"Email/Password"**
5. Enable **"Email/Password"** provider
6. Click **"Save"**

### Create Initial Test User (Optional)

1. Go to the **"Users"** tab in Authentication
2. Click **"Add user"**
3. Enter test credentials:
   - Email: `admin@dentalcare.com`
   - Password: `admin123456`
4. Click **"Add user"**

**Note:** You'll need to manually add this user to Firestore with role information (see Step 6).

## Step 5: Set Up Cloud Firestore

1. In Firebase Console, go to **Build > Firestore Database**
2. Click **"Create database"**
3. Select **"Start in production mode"** (we'll add rules next)
4. Choose a Cloud Firestore location (select closest to your users)
5. Click **"Enable"**

### Add Security Rules

1. Go to the **"Rules"** tab in Firestore
2. Replace the default rules with the following:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Helper functions
    function isAuthenticated() {
      return request.auth != null;
    }
    
    function getUserRole() {
      return get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role;
    }
    
    function isAdmin() {
      return isAuthenticated() && getUserRole() == 'admin';
    }
    
    function isDentist() {
      return isAuthenticated() && getUserRole() == 'dentist';
    }
    
    function isSecretary() {
      return isAuthenticated() && getUserRole() == 'secretary';
    }
    
    // Collections
    match /users/{userId} {
      allow read: if isAuthenticated();
      allow create: if isAdmin() || !exists(/databases/$(database)/documents/users/$(request.auth.uid));
      allow update, delete: if isAdmin();
    }
    
    match /patients/{patientId} {
      allow read: if isAuthenticated();
      allow create, update: if isAdmin() || isDentist() || isSecretary();
      allow delete: if isAdmin();
    }
    
    match /appointments/{appointmentId} {
      allow read: if isAuthenticated();
      allow create, update, delete: if isAdmin() || isDentist() || isSecretary();
    }
    
    match /treatments/{treatmentId} {
      allow read: if isAuthenticated();
      allow create, update: if isAdmin() || isDentist();
      allow delete: if isAdmin();
    }
    
    match /diagnostics/{diagnosticId} {
      allow read: if isAuthenticated();
      allow create, update: if isAdmin() || isDentist();
      allow delete: if isAdmin();
    }
    
    match /dentists/{dentistId} {
      allow read: if isAuthenticated();
      allow write: if isAdmin();
    }
  }
}
```

3. Click **"Publish"**

### Create Initial Collections (Optional)

You can pre-create collections or let the app create them automatically:

1. Click **"Start collection"**
2. Create these collections one by one:
   - `users`
   - `patients`
   - `appointments`
   - `treatments`
   - `diagnostics`
   - `dentists`

## Step 6: Set Up Firebase Storage

1. In Firebase Console, go to **Build > Storage**
2. Click **"Get started"**
3. Select **"Start in production mode"**
4. Choose a storage location (same as Firestore location)
5. Click **"Done"**

### Add Storage Rules

1. Go to the **"Rules"** tab in Storage
2. Replace the default rules with:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /diagnostics/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
                     request.resource.size < 10 * 1024 * 1024 && // Max 10MB
                     request.resource.contentType.matches('image/.*');
    }
  }
}
```

3. Click **"Publish"**

## Step 7: Create Initial Admin User in Firestore

After creating your first user via Firebase Authentication, you need to add their information to Firestore:

1. Go to **Firestore Database**
2. Click on the `users` collection (or create it)
3. Click **"Add document"**
4. Set **Document ID** to the user's UID from Authentication (or use Auto-ID)
5. Add the following fields:

| Field | Type | Value |
|-------|------|-------|
| userId | string | (Copy from Authentication UID) |
| email | string | admin@dentalcare.com |
| firstName | string | Admin |
| lastName | string | User |
| role | string | admin |
| isActive | boolean | true |
| createdAt | timestamp | (Click "Set to current time") |

6. Click **"Save"**

## Step 8: Add Sample Dentist Data (Optional)

1. Go to **Firestore Database**
2. Create a `dentists` collection
3. Add a document with Auto-ID:

| Field | Type | Value |
|-------|------|-------|
| dentistId | string | (use the auto-generated ID) |
| firstName | string | Josephine |
| lastName | string | Lazaro |
| specialization | string | General Dentistry |
| contactNumber | string | +63-XXX-XXX-XXXX |
| isActive | boolean | true |

## Step 9: Build and Run the App

1. Open the project in Android Studio
2. Sync Gradle: **File > Sync Project with Gradle Files**
3. Build the project: **Build > Make Project**
4. Run on device/emulator: Click the **Run** button (▶️)

### Verify Firebase Connection

When you run the app:
1. Try registering a new user
2. Check Firebase Console > Authentication to see if the user was created
3. Try logging in with the user
4. Navigate to the Dashboard
5. Try adding a patient and check Firestore Database

## Step 10: Enable Firebase Analytics (Optional)

1. Go to **Build > Analytics**
2. Click **"Enable Google Analytics"**
3. The app will automatically send analytics events

## Troubleshooting

### Issue: "google-services.json not found"
**Solution:** Ensure the file is in `app/google-services.json`, not in the root directory.

### Issue: "Firebase initialization error"
**Solution:** 
- Verify package name matches exactly: `com.dentalcare.app`
- Rebuild project: **Build > Rebuild Project**
- Clean build: **Build > Clean Project** then rebuild

### Issue: "Permission denied" errors in Firestore
**Solution:** 
- Check that Security Rules are published
- Verify user is authenticated
- Check user role in Firestore `users` collection

### Issue: "Unable to resolve dependency com.google.gms:google-services"
**Solution:**
- Check internet connection
- Sync Gradle files
- Clear Gradle cache: `./gradlew clean --refresh-dependencies`

### Issue: Authentication not working
**Solution:**
- Verify Email/Password provider is enabled in Firebase Console
- Check that `google-services.json` is present and correct
- Ensure device/emulator has internet connection

## Testing Firebase Integration

### Test Authentication
1. Launch app
2. Click "Register" on login screen
3. Fill in registration form with test credentials
4. Submit and verify user is created in Firebase Console > Authentication

### Test Firestore
1. Log in to the app
2. Navigate to Patients screen
3. Add a new patient
4. Check Firebase Console > Firestore Database to see the new patient document

### Test Storage
1. Navigate to Diagnostics screen
2. Try uploading an image
3. Check Firebase Console > Storage to see the uploaded file

## Security Recommendations

1. **Never commit google-services.json to public repositories**
   - Add it to `.gitignore` (already done in this project)

2. **Use strong passwords for all users**
   - Minimum 8 characters
   - Mix of letters, numbers, and symbols

3. **Regularly review Firestore Security Rules**
   - Test rules with Firebase Rules Simulator

4. **Monitor Firebase Usage**
   - Check Firebase Console > Usage and billing
   - Set up billing alerts

5. **Enable App Check (Production)**
   - Protects Firebase services from abuse
   - Setup in Firebase Console > Build > App Check

## Additional Resources

- [Firebase Documentation](https://firebase.google.com/docs)
- [Firestore Security Rules Guide](https://firebase.google.com/docs/firestore/security/get-started)
- [Firebase Authentication Guide](https://firebase.google.com/docs/auth)
- [Firebase Storage Guide](https://firebase.google.com/docs/storage)

---

## Quick Reference

### Firebase Console URLs
- Project Overview: `https://console.firebase.google.com/project/YOUR_PROJECT_ID`
- Authentication: `https://console.firebase.google.com/project/YOUR_PROJECT_ID/authentication/users`
- Firestore: `https://console.firebase.google.com/project/YOUR_PROJECT_ID/firestore`
- Storage: `https://console.firebase.google.com/project/YOUR_PROJECT_ID/storage`

### App Package Name
```
com.dentalcare.app
```

### Default Test Credentials (if created)
```
Email: admin@dentalcare.com
Password: admin123456
Role: admin
```

---

**Setup Complete!** 🎉

Your DentalCare Management App is now connected to Firebase and ready for use.
