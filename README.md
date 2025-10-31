# Ludo Control

A Ludo-style game with remote admin control capabilities. This project consists of three components:

1. **Android App** - Ludo game with WebSocket client for remote control
2. **Admin Panel** - React-based web UI for controlling connected game clients
3. **WebSocket Server** - Node.js relay server for admin-to-client communication

## 🎯 Features

- **Android Game**: Ludo-style game with dice rolling and player management
- **Remote Control**: Admin can trigger dice rolls or force specific values
- **Real-time Updates**: WebSocket communication for instant command delivery
- **Pass-only Local Mode**: Game runs locally with optional remote control
- **Hidden Forced Rolls**: Players cannot distinguish between normal and admin-forced dice rolls

## 🚀 Quick Start

### Prerequisites

- Node.js 18+
- Android Studio (for Android app)
- npm or yarn

### 1. Start the WebSocket Server

```bash
cd server
npm install
npm start
```

Server runs on port 8080 by default (configurable via PORT environment variable).

### 2. Start the Admin Panel

```bash
cd admin-panel
npm install
npm run dev
```

Admin panel runs on http://localhost:3000

### 3. Build and Run Android App

Open `android-app` in Android Studio:
1. Sync Gradle files
2. Run on emulator or device
3. Click "Connect to Server" in the app
4. See the client appear in the admin panel

## 📁 Project Structure

```
ludo-control/
├── android-app/           # Android Studio project
│   ├── app/
│   │   └── src/
│   │       ├── main/
│   │       │   └── kotlin/com/deathgod/ludocontrol/
│   │       │       ├── game/
│   │       │       │   ├── GameEngine.kt      # Core game logic
│   │       │       │   └── DiceManager.kt     # Dice animations
│   │       │       ├── net/
│   │       │       │   └── WsClient.kt        # WebSocket client
│   │       │       └── ui/
│   │       │           └── UIActivity.kt      # Main UI
│   │       └── test/                          # Unit tests
│   └── README.md
├── admin-panel/           # React + Vite admin UI
│   ├── src/
│   │   ├── App.jsx                            # Main admin interface
│   │   └── ...
│   └── README.md
├── server/                # Node.js WebSocket server
│   ├── index.js                               # Server implementation
│   └── README.md
├── .github/
│   └── workflows/
│       └── ci.yml                             # CI pipeline
├── LICENSE                                     # MIT License
├── Procfile                                    # Deployment config
└── README.md                                   # This file
```

## 🔧 Configuration

### WebSocket Server

Default port: 8080
Set custom port: `PORT=3001 npm start`

### Admin Panel

Default WebSocket URL: `ws://localhost:8080`
Change in the connection panel UI.

### Android App

Default server: `ws://10.0.2.2:8080` (Android emulator localhost)
For physical devices, update the URL in `UIActivity.kt` to your server's IP.

## 📡 WebSocket Protocol

### Client Registration
```json
{
  "type": "register",
  "clientId": "unique-client-id",
  "clientType": "android"
}
```

### Admin Commands

**Roll Dice** (normal random roll):
```json
{
  "type": "roll",
  "targetClientId": "client-id"
}
```

**Force Dice Value** (hidden from player):
```json
{
  "type": "force",
  "value": 6,
  "targetClientId": "client-id"
}
```

**List Connected Clients**:
```json
{
  "type": "list_clients"
}
```

## 🧪 Testing

### Android Unit Tests

```bash
cd android-app
./gradlew test
```

### Server Syntax Check

```bash
cd server
node -c index.js
```

## 🚢 Deployment

### Deploy to Heroku/Render

The project includes a `Procfile` for easy deployment:

```bash
# Add server dependencies to root package.json (optional)
# or deploy server subdirectory

# For Heroku:
heroku create your-app-name
git push heroku main
```

### Environment Variables

- `PORT`: Server port (default: 8080)

## ⚠️ Security Note

**IMPORTANT**: This implementation has NO authentication. The admin panel can control any connected client without verification.

- Forced dice rolls are hidden from players (same animation as normal rolls)
- In production, implement:
  - Admin authentication (OAuth, JWT, etc.)
  - Client validation and authorization
  - Encrypted WebSocket connections (WSS)
  - Rate limiting
  - Audit logging

**This is a proof-of-concept. Do not use in production without proper security measures.**

## 📝 License

MIT License - see [LICENSE](LICENSE) file for details.

## 🛠️ Development

### CI/CD

GitHub Actions workflow runs on push:
- Builds server and checks syntax
- Runs Android unit tests
- Builds admin panel

### Code Quality

- Kotlin code follows Android best practices
- React components use functional style with hooks
- WebSocket server uses async/await patterns

## 🤝 Contributing

This is a scaffold project. Contributions welcome:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📚 Additional Resources

- [Android WebSocket Guide](https://developer.android.com/)
- [React Documentation](https://react.dev/)
- [ws Library](https://github.com/websockets/ws)

## 🎮 Future Enhancements

- [ ] Full Ludo game rules implementation
- [ ] Authentication and authorization
- [ ] Multiple game rooms
- [ ] Player accounts and statistics
- [ ] UI improvements and animations
- [ ] Mobile admin panel
- [ ] Game replay and history