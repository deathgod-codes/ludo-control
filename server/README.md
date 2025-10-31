# Server

WebSocket server for relaying admin commands to Android clients.

## Setup

```bash
npm install
```

## Run

```bash
npm start
```

Server will start on port 8080 (or PORT environment variable).

## Protocol

### Client Registration
```json
{ "type": "register", "clientId": "unique-id", "clientType": "android" }
```

### Admin Commands
```json
{ "type": "roll", "targetClientId": "client-id" }
{ "type": "force", "value": 6, "targetClientId": "client-id" }
{ "type": "list_clients" }
```
