# Weather Stories

An Android application that displays current weather information based on device location and presents related city photos in an Instagram-style stories format.

## 📱 Overview

Weather Stories combines weather data from OpenWeatherMap with beautiful city images from Unsplash to create an engaging, story-driven weather experience. Users can view current weather conditions for their location and explore visual stories about their city.

## 🏗️ Architecture

The app follows **Clean Architecture** principles with clear separation of concerns across three main layers:

### Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (UI, ViewModels, Navigation)           │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│          Domain Layer                   │
│  (Use Cases, Domain Models)             │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│           Data Layer                    │
│  (Repositories, API Services, DTOs)     │
└─────────────────────────────────────────┘
```

#### 1. **Presentation Layer** (`presentation/`)
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **UI Framework**: Jetpack Compose with Material3
- **State Management**: StateFlow for reactive UI updates
- **Dependency Injection**: Hilt for ViewModels
- **Navigation**: Jetpack Navigation Compose

**Key Components:**
- `WeatherScreen` & `WeatherViewModel`: Display current weather data
- `StoriesScreen` & `StoriesViewModel`: Instagram-style photo stories
- `Navigation`: Route definitions and navigation graph

#### 2. **Domain Layer** (`domain/`)
- **Purpose**: Contains business logic and domain models
- **Models**: Pure Kotlin data classes (`Weather`, `Story`)
- **Use Cases**: Single-responsibility business operations
  - `GetCurrentWeatherUseCase`: Retrieves weather for current location
  - `GetStoriesUseCase`: Fetches random photos for a city
  - `GetWeatherByLocationUseCase`: Gets weather for specific coordinates

#### 3. **Data Layer** (`data/`)
- **Repositories**: Abstract data sources with implementations
  - `WeatherRepository`: Weather data operations
  - `StoryRepository`: Photo/story data operations
  - `LocationRepository`: Device location and permissions
- **API Services**: Retrofit interfaces
  - `WeatherApiService`: OpenWeatherMap API
  - `UnsplashApiService`: Unsplash API
- **DTOs**: Data Transfer Objects for API responses with domain mapping

#### 4. **Dependency Injection** (`di/`)
- **Framework**: Dagger Hilt
- **Modules**:
  - `AppModule`: Network clients (Retrofit, OkHttp), Location services
  - `RepoModule`: Repository implementations

### Key Architectural Principles

✅ **Single Responsibility**: Each class has one clear purpose  
✅ **Dependency Inversion**: High-level modules don't depend on low-level modules  
✅ **Separation of Concerns**: UI, business logic, and data access are isolated  
✅ **Testability**: Easy to test with clear boundaries and dependency injection  
✅ **Unidirectional Data Flow**: Data flows from data layer → domain → presentation  

## 🌤️ Weather Data Flow

### How Weather Data is Fetched and Displayed

#### Step-by-Step Flow

1. **Initialization & Permissions**
   ```
   WeatherScreen → WeatherViewModel.init()
   ↓
   Check location permissions via LocationRepository
   ↓
   If denied → Show PermissionDenied state
   If granted → Proceed to fetch weather
   ```

2. **Location Acquisition**
   ```
   GetCurrentWeatherUseCase invoked
   ↓
   LocationRepository.getCurrentLocation()
   ↓
   Uses Google Play Services FusedLocationProviderClient
   ↓
   Returns latitude & longitude coordinates
   ```

3. **API Request**
   ```
   WeatherRepository.fetchWeather(lat, lon)
   ↓
   WeatherApiService.getWeather()
   ↓
   Retrofit call to OpenWeatherMap API
   ↓
   GET https://api.openweathermap.org/data/2.5/weather
   ```

4. **Data Transformation**
   ```
   API Response (WeatherResponseDTO)
   ↓
   DTO.toDomain() mapping function
   ↓
   Domain Model (Weather)
   ↓
   Wrapped in Result<Weather>
   ```

5. **State Management**
   ```
   WeatherViewModel receives Result<Weather>
   ↓
   Updates _uiState StateFlow
   ↓
   UI observes via collectAsStateWithLifecycle()
   ↓
   Compose recomposition with new weather data
   ```

#### UI States

The `WeatherUiState` sealed class manages all possible states:

```kotlin
sealed class WeatherUiState {
    object Initial          // App just launched
    object Loading          // Fetching weather data
    data class Success      // Weather data loaded successfully
    data class Error        // API or network error
    data class PermissionDenied // Location permission not granted
}
```

#### API Configuration

- **API Provider**: OpenWeatherMap
- **Endpoint**: `/data/2.5/weather`
- **Parameters**:
  - `lat`: Latitude from device location
  - `lon`: Longitude from device location
  - `appid`: API key from BuildConfig
  - `units`: "metric" for Celsius
- **Authentication**: API key stored in `gradle.properties`

#### Network Layer

- **HTTP Client**: OkHttp with logging interceptor
- **Serialization**: Gson for JSON parsing
- **Timeouts**: 30 seconds for connect/read/write
- **Error Handling**: Try-catch with Result type wrapping

#### Weather Display

The `WeatherScreen` displays:
- **Location**: City name with location icon
- **Temperature**: Large display with °C
- **Conditions**: Weather description (e.g., "Clear sky")
- **Details Card**:
  - Feels like temperature
  - Humidity percentage
  - Wind speed (m/s)
  - Atmospheric pressure (hPa)
  - Visibility (km)
- **Actions**:
  - Refresh button to reload weather
  - "View Stories" button to navigate to stories

## 📸 Story View Implementation

### Instagram-Style Stories Feature

The app implements a **full-screen, auto-progressing story viewer** inspired by Instagram Stories.

#### Architecture

```
StoriesScreen (Composable)
    ↓
StoriesViewModel (State Management)
    ↓
GetStoriesUseCase (Business Logic)
    ↓
StoryRepository (Data Source)
    ↓
UnsplashApiService (API)
```

### Key Features

#### 1. **Auto-Progression**
- Each story displays for **3 seconds**
- Automatic transition to the next story
- Progress bar updates every 50ms for smooth animation
- Loops back to first story after the last one

**Implementation:**
```kotlin
LaunchedEffect(currentIndex) {
    val duration = 3000L // 3 seconds
    val interval = 50L   // 50ms updates
    // Gradually update progress
    // Auto-advance when progress reaches 100%
}
```

#### 2. **Progress Indicators**
- **Top bar**: Multiple segments, one per story
- **Completed stories**: Fully filled (white)
- **Current story**: Filling gradually based on time
- **Upcoming stories**: Empty (translucent white)

#### 3. **Manual Navigation**

**Swipe Gestures:**
- Swipe **right**: Go to previous story
- Swipe **left**: Go to next story
- Implemented using `detectDragGestures` modifier

**Touch Zones:**
- **Left half**: Tap to go to previous story
- **Right half**: Tap to go to next story

#### 4. **Image Loading**
- **Library**: Coil Compose
- **Features**: Async loading with placeholder and error states
- **Display**: Full-screen with `ContentScale.Crop`
- **Loading indicator**: Centered circular progress indicator

#### 5. **Close Button**
- **Position**: Top-right corner
- **Icon**: X (Close) icon
- **Action**: Navigate back to weather screen

### Data Flow

#### 1. Navigation to Stories
```
WeatherScreen → "View Stories" button clicked
↓
Navigation.navigate("stories/{city}")
↓
StoriesScreen receives city name parameter
```

#### 2. Stories Fetching
```
StoriesViewModel.init()
↓
GetStoriesUseCase(city = "CityName", count = 5)
↓
StoryRepository.fetchRandomPhotos()
↓
UnsplashApiService.getRandomPhotos()
↓
GET https://api.unsplash.com/photos/random?query={city}&count=5
```

#### 3. State Updates
```
API Response → List<UnsplashPhotoDTO>
↓
DTO.toDomain() → List<Story>
↓
StoriesUiState.Success(stories, currentIndex, progress)
↓
UI recomposition with new story
```

### Story UI States

```kotlin
sealed class StoriesUiState {
    object Loading                          // Fetching stories
    data class Success(
        val stories: List<Story>,
        val currentIndex: Int = 0,          // Current story index
        val progress: Float = 0f            // Progress (0.0 to 1.0)
    )
    data class Error(val message: String)   // Failed to load
}
```

### ViewModel Operations

The `StoriesViewModel` provides these operations:

- **`nextStory()`**: Advance to next story (or loop to first)
- **`previousStory()`**: Go back to previous story (or stay at first)
- **`updateProgress()`**: Update current story progress
- **`retry()`**: Retry fetching stories on error

### Visual Design

- **Background**: Full black for immersive experience
- **Progress bars**: Translucent white with rounded corners
- **Touch feedback**: Swipe detection for natural interaction
- **Close button**: White icon with semi-transparent background
- **Error state**: Centered white text with retry button

## 🛠️ Technology Stack

### Core
- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Build System**: Gradle with Kotlin DSL

### Libraries

#### UI & Presentation
- **Jetpack Compose**: Modern declarative UI
- **Material3**: Material Design components
- **Coil**: Image loading and caching
- **Navigation Compose**: Type-safe navigation

#### Architecture & DI
- **Hilt**: Dependency injection
- **ViewModel**: State management
- **Lifecycle**: Lifecycle-aware components
- **Kotlin Coroutines**: Asynchronous programming

#### Networking
- **Retrofit**: HTTP client
- **OkHttp**: Low-level networking
- **Gson**: JSON serialization

#### Location
- **Google Play Services Location**: Device location access

#### Testing
- **JUnit**: Unit testing
- **MockK**: Mocking framework
- **Turbine**: Flow testing
- **Coroutines Test**: Testing coroutines

## 🔧 Setup Instructions

### Prerequisites
- Android Studio (latest version)
- Android device or emulator with API 24+
- API keys for:
  - [OpenWeatherMap](https://openweathermap.org/api)
  - [Unsplash](https://unsplash.com/developers)

### Configuration

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd WeatherStories
   ```

2. **Add API Keys**

   Create or edit `gradle.properties` in the project root:
   ```properties
   OPENWEATHER_API_KEY=your_openweathermap_api_key_here
   UNSPLASH_API_KEY=your_unsplash_access_key_here
   ```

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
   - Connect a device or start an emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

### Permissions

The app requires the following permissions:
- `ACCESS_FINE_LOCATION`: For precise weather data
- `ACCESS_COARSE_LOCATION`: For approximate location (fallback)
- `INTERNET`: For API calls

Permissions are requested at runtime when needed.

## 📁 Project Structure

```
app/src/main/java/com/aram/mehrabyan/weatherstories/
├── data/
│   ├── apiservice/
│   │   ├── WeatherApiService.kt
│   │   └── UnsplashApiService.kt
│   ├── dto/
│   │   ├── WeatherResponseDTO.kt
│   │   └── UnsplashPhotoDTO.kt
│   └── repo/
│       ├── LocationRepository.kt
│       ├── WeatherRepository.kt
│       └── StoryRepository.kt
├── di/
│   ├── AppModule.kt          # Retrofit, OkHttp, Location services
│   └── RepoModule.kt         # Repository bindings
├── domain/
│   ├── model/
│   │   ├── Weather.kt
│   │   └── Story.kt
│   └── usecase/
│       ├── GetCurrentWeatherUseCase.kt
│       ├── GetWeatherByLocationUseCase.kt
│       └── GetStoriesUseCase.kt
├── presentation/
│   ├── navigation/
│   │   └── Navigation.kt
│   ├── weather/
│   │   ├── WeatherScreen.kt
│   │   ├── WeatherViewModel.kt
│   │   └── WeatherUiState.kt
│   └── stories/
│       ├── StoriesScreen.kt
│       ├── StoriesViewModel.kt
│       └── StoriesUiState.kt
├── ui/theme/
│   ├── Theme.kt
│   └── Type.kt
├── MainActivity.kt
└── WeatherStoriesApplication.kt
```

## 🧪 Testing

The app includes unit tests for ViewModels:

```bash
./gradlew test
```

Test coverage includes:
- Weather data fetching success and error cases
- State management in ViewModels
- Use case business logic

## 🚀 Features

- ✅ **Real-time Weather**: Fetch weather based on device location
- ✅ **Permission Handling**: Graceful permission request flow
- ✅ **Error Handling**: User-friendly error messages and retry options
- ✅ **Instagram Stories**: Auto-progressing photo stories
- ✅ **Swipe Navigation**: Natural gesture-based story navigation
- ✅ **Material Design 3**: Modern, beautiful UI
- ✅ **Offline Error Handling**: Clear messaging when network is unavailable
- ✅ **Pull-to-Refresh**: Update weather data on demand

## 📝 API Documentation

### OpenWeatherMap API
- **Base URL**: `https://api.openweathermap.org/`
- **Endpoint**: `/data/2.5/weather`
- **Documentation**: https://openweathermap.org/current

### Unsplash API
- **Base URL**: `https://api.unsplash.com/`
- **Endpoint**: `/photos/random`
- **Documentation**: https://unsplash.com/documentation

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write or update tests
5. Submit a pull request

## 📄 License

[Add your license information here]

## 👤 Author

Aram Mehrabyan

---

**Note**: Remember to add your API keys to `gradle.properties` and never commit them to version control!

