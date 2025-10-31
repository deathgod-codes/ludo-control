# Android App

Ludo-style game with remote control capabilities.

## Setup

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run on emulator or device

## Requirements

- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.9+

## Architecture

- **GameEngine**: Core game logic (testable)
- **DiceManager**: Handles dice animations and remote commands
- **WsClient**: WebSocket client for admin control
- **UIActivity**: Main game UI

## Testing

Run unit tests:
```bash
./gradlew test
```

## Remote Control

The app connects to a WebSocket server to receive commands from the admin panel:
- `roll`: Trigger a normal dice roll
- `force`: Force a specific dice value (hidden from player)

**Security Note**: Forced dice rolls are performed with the same animation as normal rolls,
so players cannot distinguish between regular and forced rolls.
