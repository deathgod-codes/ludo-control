import { useState, useEffect } from 'react'
import './App.css'

function App() {
  const [ws, setWs] = useState(null)
  const [clients, setClients] = useState([])
  const [connected, setConnected] = useState(false)
  const [serverUrl, setServerUrl] = useState('ws://localhost:8080')
  const [forceValue, setForceValue] = useState(6)

  useEffect(() => {
    connectWebSocket()
    return () => {
      if (ws) {
        ws.close()
      }
    }
  }, [])

  const connectWebSocket = () => {
    const websocket = new WebSocket(serverUrl)

    websocket.onopen = () => {
      console.log('Connected to server')
      setConnected(true)
      websocket.send(JSON.stringify({ 
        type: 'register', 
        clientType: 'admin',
        clientId: `admin-${Date.now()}`
      }))
      // Request client list
      websocket.send(JSON.stringify({ type: 'list_clients' }))
    }

    websocket.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data)
        console.log('Received:', message)

        if (message.type === 'client_list') {
          setClients(message.clients)
        } else if (message.type === 'command_sent') {
          console.log(`Command sent to ${message.targetClientId}`)
        }
      } catch (error) {
        console.error('Error parsing message:', error)
      }
    }

    websocket.onclose = () => {
      console.log('Disconnected from server')
      setConnected(false)
      // Attempt reconnection after 3 seconds
      setTimeout(connectWebSocket, 3000)
    }

    websocket.onerror = (error) => {
      console.error('WebSocket error:', error)
    }

    setWs(websocket)
  }

  const sendRoll = (clientId) => {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({ 
        type: 'roll', 
        targetClientId: clientId 
      }))
    }
  }

  const sendForce = (clientId) => {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({ 
        type: 'force', 
        value: parseInt(forceValue),
        targetClientId: clientId 
      }))
    }
  }

  return (
    <div className="app">
      <header>
        <h1>🎲 Ludo Control - Admin Panel</h1>
        <div className="status">
          Status: <span className={connected ? 'connected' : 'disconnected'}>
            {connected ? '🟢 Connected' : '🔴 Disconnected'}
          </span>
        </div>
      </header>

      <main>
        <div className="connection-panel">
          <input
            type="text"
            value={serverUrl}
            onChange={(e) => setServerUrl(e.target.value)}
            placeholder="WebSocket Server URL"
            disabled={connected}
          />
          <button onClick={connectWebSocket} disabled={connected}>
            Connect
          </button>
        </div>

        <div className="clients-section">
          <h2>Connected Clients ({clients.length})</h2>
          
          {clients.length === 0 ? (
            <p className="no-clients">No Android clients connected</p>
          ) : (
            <div className="clients-grid">
              {clients.map((client) => (
                <div key={client.clientId} className="client-card">
                  <h3>{client.clientId}</h3>
                  <div className="controls">
                    <button 
                      onClick={() => sendRoll(client.clientId)}
                      className="btn-roll"
                    >
                      🎲 Roll Dice
                    </button>
                    <div className="force-control">
                      <select 
                        value={forceValue}
                        onChange={(e) => setForceValue(e.target.value)}
                      >
                        {[1, 2, 3, 4, 5, 6].map(num => (
                          <option key={num} value={num}>{num}</option>
                        ))}
                      </select>
                      <button 
                        onClick={() => sendForce(client.clientId)}
                        className="btn-force"
                      >
                        ⚡ Force Value
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="security-note">
          <h3>⚠️ Security Note</h3>
          <p>
            This admin panel has no authentication. Forced dice rolls are hidden from players.
            Deploy with appropriate security measures in production.
          </p>
        </div>
      </main>
    </div>
  )
}

export default App
