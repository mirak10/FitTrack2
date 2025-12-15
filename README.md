# FitTrack2

FitTrack2 is a group project developed as part of the Mobile Computing course requirements.

## Project Structure
- `flutter_app/` – Flutter application
- `android_app/` – Native Android application (Android Studio)
- `testing/` – Test plans, test cases, and test results
- `docs/` – Project documentation

## Team Responsibilities
- Flutter Development: Zainab
- Android Development (UI + Room): Nafeesah + Karim
- Testing & Integration: Karim

## How to Run

### Flutter App
1. Open `flutter_app` in VS Code or Android Studio
2. Run `flutter pub get`
3. Run on emulator or device

### Android App
1. Open `android_app` in Android Studio
2. Sync Gradle
3. Run on emulator or device

###Reference
This app's setup is built using the MVVM architecture, which is the standard way to build robust apps with Jetpack Compose. We made sure to use Kotlin Flow to keep the UI and the data talking smoothly a core pattern we learned from the community and official guides. This approach for handling state and separation of code was heavily inspired by the best practices shared on Medium and other great learning platforms.

For all the important stuff like saving your workout plans and logs we used the Room database, following standard guides for managing relationships and using Coroutines for fast, reliable storage. We also included Firebase Authentication for a simple and secure login system, ticking off the bonus goal! The detailed setup for both Room and Firebase was guided by documentation from **Android Developers** and quick solutions found on sites like **Stack Overflow.**
