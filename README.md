# Android Project - MVVM & Clean Architecture

## Project Overview
This project follows **MVVM (Model-View-ViewModel)** architecture combined with **Clean Architecture** principles to ensure better code maintainability, scalability, and testability.

## Architecture Overview
The project is structured based on **Clean Architecture**, which separates concerns into different layers:

### **1. Presentation Layer**
- **UI (Activity/Fragment/Compose Screens)**: Displays data and interacts with the user.
- **ViewModel**: Acts as a bridge between UI and domain logic. It retrieves data from the UseCase and exposes it to the UI.

### **2. Domain Layer (Business Logic)**
- **UseCase (Interactors)**: Contains business logic, interacting with repositories to get or modify data.
- **Repository Interface**: Abstract layer defining how data operations should be performed (implemented in the data layer).

### **3. Data Layer (Data Management)**
- **Repository Implementation**: Handles data fetching from API, database, or other sources.
- **DataSource (Remote/Local)**: Implements the actual data operations.
- **Models (DTOs/Entities)**: Represents the data structure used in the app.

## MVVM (Model-View-ViewModel) Breakdown
- **Model**: Represents data and business logic (UseCases, Repository, DataSource).
- **View**: UI layer (Activity, Fragment, Compose components) displaying data from ViewModel.
- **ViewModel**: Maintains UI state and communicates with UseCases for data processing.

### **util Package**
Contains reusable utility classes and functions such as:
- Extension functions
- Date/time formatters
- Network utilities
- Shared preferences helpers

### **common Package**
Includes shared components that can be used across multiple features:
- Constants
- Base classes (e.g., BaseActivity, BaseFragment, BaseViewModel)
- UI-related shared components

## Technologies Used
- **Kotlin** for modern Android development
- **Jetpack Components** (ViewModel, LiveData, Navigation, Room, etc.)
- **Retrofit** for API calls
- **Coroutines & Flow** for asynchronous programming
- **Hilt/Dagger** for Dependency Injection
- **Unit & UI Testing** (JUnit, MockK, Espresso)

## Conclusion
This architecture provides a **scalable, maintainable, and testable** structure by separating concerns into clear layers. The inclusion of `util` and `common` packages enhances reusability and code organization, making development more efficient.

## 

