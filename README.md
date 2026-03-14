[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/NYuLn2p4)

# College Basketball Scores App

A mobile Android application that displays college basketball scores from the NCAA API. The app provides real-time game information, supports both Men's and Women's basketball, and includes offline functionality with local data persistence.

## Features

- **Date Selection**: Pick any date to view games using an intuitive date picker (defaults to today)
- **Gender Toggle**: Switch between Men's and Women's college basketball games
- **Game Information Display**:
  - Team names (home and away)
  - Game status (upcoming, in-progress, or finished)
  - Scores for completed or live games
  - Start time for upcoming games
  - Current period and time remaining for live games
  - Winner information for finished games
- **Offline Support**: View previously downloaded scores even without internet connection
- **Data Persistence**: All scores are stored in a local SQLite Room database
- **Manual Refresh**: Refresh button to update scores from the API
- **Loading Indicators**: Visual feedback during API calls

## Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern:

- **Data Layer**: Room database entities and API models
- **Repository**: Handles data fetching from API and local database
- **ViewModel**: Manages UI state and business logic
- **UI Layer**: Jetpack Compose screens and components

## Technologies Used

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit for Android
- **Room Database**: Local SQLite database for offline storage
- **Retrofit**: HTTP client for API calls
- **Gson**: JSON parsing
- **Coroutines**: Asynchronous programming
- **Material 3**: Modern Material Design components

## API

The app uses the NCAA API endpoint:
```
https://ncaa-api.henrygd.me/scoreboard/basketball-[gender]/d1/yyyy/mm/dd
```

Where:
- `[gender]` is either `men` or `women`
- Date format: `yyyy/mm/dd` (e.g., `2026/03/13`)

## Setup

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle dependencies
4. Build and run on an Android device or emulator

## Requirements

- Android Studio
- Minimum SDK: 27 (Android 8.1)
- Target SDK: 36
