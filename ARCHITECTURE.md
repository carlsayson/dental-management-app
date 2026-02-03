# DentalCare App - Architecture Documentation

## Overview

The DentalCare Management App follows Clean Architecture principles with MVVM (Model-View-ViewModel) pattern, ensuring separation of concerns, testability, and maintainability.

## Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer (UI)         │
│  ┌─────────────┐      ┌──────────────┐ │
│  │  Composables │◄────►│  ViewModels  │ │
│  │   (Views)    │      │   (State)    │ │
│  └─────────────┘      └──────────────┘ │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│          Domain Layer (Business)        │
│  ┌─────────────┐      ┌──────────────┐ │
│  │   Use Cases │      │   Entities   │ │
│  │  (optional) │      │   (Models)   │ │
│  └─────────────┘      └──────────────┘ │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│           Data Layer (Data)             │
│  ┌─────────────┐      ┌──────────────┐ │
│  │ Repositories│◄────►│  Data Sources│ │
│  │             │      │  (Firebase)  │ │
│  └─────────────┘      └──────────────┘ │
└─────────────────────────────────────────┘
```

## Layer Responsibilities

### 1. Presentation Layer

**Location:** `ui/` package

**Components:**
- **Composables (Views):** UI components built with Jetpack Compose
- **ViewModels:** Hold UI state and handle user interactions
- **Navigation:** Type-safe navigation with Navigation Compose
- **Theme:** Material Design 3 theming

**Responsibilities:**
- Render UI based on state
- Handle user input
- Navigate between screens
- Display loading/error states
- NO business logic
- NO direct data access

**Example:**
```kotlin
// PatientListScreen.kt
@Composable
fun PatientsScreen(
    viewModel: PatientsViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val patients by viewModel.patients.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    when {
        isLoading -> LoadingIndicator()
        patients.isEmpty() -> EmptyStateComponent()
        else -> PatientList(patients, onNavigateToDetail)
    }
}
```

### 2. Domain Layer

**Location:** `data/model/` package

**Components:**
- **Entities (Models):** Core business objects (User, Patient, Appointment, etc.)
- **Use Cases:** Business logic operations (optional - could be in ViewModels for simpler apps)

**Responsibilities:**
- Define data structures
- Contain business rules
- Platform-independent
- Reusable across different data sources

**Example:**
```kotlin
// Patient.kt
data class Patient(
    val patientId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val contactNumber: String = "",
    val address: String = "",
    val medicalHistory: String = "",
    val dentalHistory: String = "",
    val dateRegistered: Timestamp = Timestamp.now(),
    val createdBy: String = ""
) {
    fun getFullName() = "$firstName $lastName"
}
```

### 3. Data Layer

**Location:** `data/repository/` package

**Components:**
- **Repositories:** Abstract data access and coordinate between data sources
- **Data Sources:** Firebase (Firestore, Auth, Storage)

**Responsibilities:**
- Provide clean API for data access
- Handle data operations (CRUD)
- Manage caching (if implemented)
- Error handling and mapping
- Convert between data formats

**Example:**
```kotlin
// PatientRepository.kt
class PatientRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getPatients(): Flow<Resource<List<Patient>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val subscription = firestore.collection("patients")
            .orderBy("dateRegistered", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message))
                    return@addSnapshotListener
                }
                
                val patients = snapshot?.toObjects(Patient::class.java) ?: emptyList()
                trySend(Resource.Success(patients))
            }
        
        awaitClose { subscription.remove() }
    }
}
```

## State Management

### Resource Wrapper

**Location:** `util/Resource.kt`

Sealed class for handling different states of data:

```kotlin
sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String?) : Resource<T>()
}
```

**Usage:**
```kotlin
// In ViewModel
private val _patients = MutableStateFlow<Resource<List<Patient>>>(Resource.Loading())
val patients: StateFlow<Resource<List<Patient>>> = _patients.asStateFlow()

// In Composable
when (val result = patients.value) {
    is Resource.Loading -> LoadingIndicator()
    is Resource.Success -> PatientList(result.data)
    is Resource.Error -> ErrorComponent(result.message)
}
```

## Dependency Injection

### Hilt Setup

**Location:** `di/AppModule.kt`

Provides dependencies throughout the app:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}
```

**Injection in ViewModels:**
```kotlin
@HiltViewModel
class PatientsViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : ViewModel() {
    // ViewModel implementation
}
```

## Navigation Architecture

### Screen Definitions

**Location:** `ui/navigation/Screen.kt`

```kotlin
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Patients : Screen("patients")
    object PatientDetail : Screen("patient_detail/{patientId}") {
        fun createRoute(patientId: String) = "patient_detail/$patientId"
    }
}
```

### Navigation Graph

**Location:** `ui/navigation/NavGraph.kt`

Defines navigation flow and connections between screens:

```kotlin
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        // More routes...
    }
}
```

## UI Components

### Reusable Components

**Location:** `ui/components/`

**Principle:** DRY (Don't Repeat Yourself)

Common components that are reused across the app:

- `DentalButton.kt` - Branded buttons with consistent styling
- `DentalTextField.kt` - Text inputs with validation support
- `LoadingIndicator.kt` - Loading animations
- `EmptyStateComponent.kt` - Empty list placeholder
- `ErrorComponent.kt` - Error display with retry
- `ConfirmationDialog.kt` - Reusable confirmation dialogs
- `DatePickerDialog.kt` - Date selection
- `TimePickerDialog.kt` - Time selection

**Example:**
```kotlin
@Composable
fun DentalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
```

## Theme & Styling

### Material Design 3

**Location:** `ui/theme/`

**Components:**
- `Color.kt` - Color palette definition
- `Theme.kt` - Theme configuration (light/dark)
- `Type.kt` - Typography scale

**Color Scheme:**
```kotlin
// Professional medical blue and teal
val PrimaryBlue = Color(0xFF1565C0)
val PrimaryTeal = Color(0xFF00897B)
val BackgroundLight = Color(0xFFFAFAFA)
val SurfaceLight = Color(0xFFFFFFFF)
```

**Dynamic Theming:**
```kotlin
@Composable
fun DentalCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## Data Flow

### Typical Data Flow Example

1. **User Action:** User taps "Save Patient" button
2. **UI Layer:** Composable calls ViewModel function
   ```kotlin
   viewModel.savePatient(patient)
   ```
3. **ViewModel:** Validates input and calls repository
   ```kotlin
   fun savePatient(patient: Patient) {
       viewModelScope.launch {
           _saveState.value = Resource.Loading()
           repository.addPatient(patient)
               .collect { result ->
                   _saveState.value = result
               }
       }
   }
   ```
4. **Repository:** Performs Firebase operation
   ```kotlin
   suspend fun addPatient(patient: Patient): Flow<Resource<Boolean>> = flow {
       emit(Resource.Loading())
       try {
           firestore.collection("patients")
               .add(patient)
               .await()
           emit(Resource.Success(true))
       } catch (e: Exception) {
           emit(Resource.Error(e.message))
       }
   }
   ```
5. **UI Layer:** Observes state changes and updates UI
   ```kotlin
   when (val state = saveState.value) {
       is Resource.Loading -> ShowLoading()
       is Resource.Success -> ShowSuccess()
       is Resource.Error -> ShowError(state.message)
   }
   ```

## Error Handling

### Layered Error Handling

**1. Repository Layer:**
- Catch exceptions from data sources
- Convert to user-friendly messages
- Wrap in Resource.Error

**2. ViewModel Layer:**
- Observe repository errors
- Update UI state
- Log errors for debugging

**3. UI Layer:**
- Display error messages
- Provide retry mechanisms
- Show fallback UI

**Example:**
```kotlin
// Repository
try {
    firestore.collection("patients").get().await()
} catch (e: FirebaseFirestoreException) {
    Resource.Error("Unable to load patients. Please check your connection.")
} catch (e: Exception) {
    Resource.Error("An unexpected error occurred.")
}

// UI
if (state is Resource.Error) {
    ErrorComponent(
        message = state.message ?: "Unknown error",
        onRetry = { viewModel.retryLoad() }
    )
}
```

## Testing Strategy

### Unit Tests

**ViewModels:**
```kotlin
@Test
fun `savePatient with valid data should emit success`() = runTest {
    // Arrange
    val patient = Patient(firstName = "John", lastName = "Doe")
    
    // Act
    viewModel.savePatient(patient)
    
    // Assert
    val state = viewModel.saveState.value
    assert(state is Resource.Success)
}
```

**Repositories:**
```kotlin
@Test
fun `getPatients should return patient list`() = runTest {
    // Arrange
    val mockFirestore = mock<FirebaseFirestore>()
    val repository = PatientRepository(mockFirestore)
    
    // Act
    val result = repository.getPatients().first()
    
    // Assert
    assert(result is Resource.Success)
}
```

### UI Tests

**Composables:**
```kotlin
@Test
fun `patient list displays correctly`() {
    composeTestRule.setContent {
        PatientList(
            patients = listOf(
                Patient(firstName = "John", lastName = "Doe")
            )
        )
    }
    
    composeTestRule.onNodeWithText("John Doe").assertExists()
}
```

## Performance Considerations

### Optimization Techniques

1. **Lazy Loading:**
   - Use LazyColumn for lists
   - Load data on demand

2. **State Hoisting:**
   - Keep state at appropriate level
   - Minimize recompositions

3. **Remember:**
   - Use `remember` for computed values
   - Use `derivedStateOf` for derived state

4. **Coroutines:**
   - Use appropriate dispatchers (IO, Main)
   - Cancel operations when not needed

5. **Firebase:**
   - Use pagination for large datasets
   - Implement offline persistence
   - Use real-time listeners sparingly

## Security Architecture

### Authentication Flow

1. User enters credentials
2. Firebase Auth validates
3. App receives user token
4. Token stored securely
5. Token included in all requests
6. Firestore rules validate token

### Data Access Control

**Firestore Rules:**
- Role-based access (admin, dentist, secretary, assistant)
- Document-level permissions
- Field-level validation

**Storage Rules:**
- Authenticated users only
- File type validation
- Size limits

## Best Practices

### Do's ✅

- ✅ Use sealed classes for state management
- ✅ Handle all error cases
- ✅ Validate user input
- ✅ Use Kotlin Coroutines for async operations
- ✅ Follow Material Design guidelines
- ✅ Write meaningful commit messages
- ✅ Keep ViewModels free of Android framework dependencies
- ✅ Use dependency injection
- ✅ Implement proper navigation
- ✅ Add loading states

### Don'ts ❌

- ❌ Don't perform database operations on main thread
- ❌ Don't hardcode strings (use strings.xml)
- ❌ Don't expose mutable state from ViewModels
- ❌ Don't use GlobalScope for coroutines
- ❌ Don't ignore errors
- ❌ Don't commit sensitive data
- ❌ Don't use magic numbers
- ❌ Don't create God classes
- ❌ Don't skip input validation
- ❌ Don't forget to cancel coroutines

## Future Architectural Improvements

1. **Room Database:** Local caching for offline support
2. **Use Cases:** Extract business logic from ViewModels
3. **Multi-module:** Separate features into modules
4. **Pagination:** Implement for large datasets
5. **Worker:** Background sync with WorkManager
6. **Preferences DataStore:** User preferences storage

---

This architecture ensures:
- ✅ Separation of concerns
- ✅ Testability
- ✅ Scalability
- ✅ Maintainability
- ✅ Performance
- ✅ Security
