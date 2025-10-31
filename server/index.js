const WebSocket = require('ws');

const PORT = process.env.PORT || 8080;

const wss = new WebSocket.Server({ port: PORT });

// Store connected clients
// clients: { clientId: { ws: WebSocketConnection, type: 'android'|'admin' } }
const clients = new Map();

wss.on('connection', (ws) => {
  console.log('New connection established');
  
  let clientId = null;
  let clientType = null;

  ws.on('message', (data) => {
    try {
      const message = JSON.parse(data.toString());
      console.log('Received:', message);

      // Handle client registration
      if (message.type === 'register') {
        clientId = message.clientId || `client-${Date.now()}`;
        clientType = message.clientType || 'android'; // 'android' or 'admin'
        clients.set(clientId, { ws, type: clientType });
        
        console.log(`Client registered: ${clientId} (${clientType})`);
        ws.send(JSON.stringify({ type: 'registered', clientId }));

        // Notify all admins of connected clients
        broadcastClientList();
        return;
      }

      // Admin commands - relay to specific Android client
      if (clientType === 'admin') {
        const targetClientId = message.targetClientId;
        
        if (message.type === 'roll' || message.type === 'force') {
          const targetClient = clients.get(targetClientId);
          
          if (targetClient && targetClient.type === 'android') {
            // Relay command to Android client
            const command = {
              type: message.type,
              value: message.value
            };
            targetClient.ws.send(JSON.stringify(command));
            console.log(`Relayed ${message.type} to ${targetClientId}`);
            
            // Acknowledge to admin
            ws.send(JSON.stringify({ 
              type: 'command_sent', 
              targetClientId,
              command: message.type 
            }));
          } else {
            ws.send(JSON.stringify({ 
              type: 'error', 
              message: 'Target client not found' 
            }));
          }
        } else if (message.type === 'list_clients') {
          sendClientList(ws);
        }
      }
    } catch (error) {
      console.error('Error processing message:', error);
      ws.send(JSON.stringify({ type: 'error', message: 'Invalid message format' }));
    }
  });

  ws.on('close', () => {
    if (clientId) {
      console.log(`Client disconnected: ${clientId}`);
      clients.delete(clientId);
      broadcastClientList();
    }
  });

  ws.on('error', (error) => {
    console.error('WebSocket error:', error);
  });
});

function sendClientList(ws) {
  const clientList = Array.from(clients.entries())
    .filter(([_, client]) => client.type === 'android')
    .map(([id, _]) => ({ clientId: id }));
  
  ws.send(JSON.stringify({ type: 'client_list', clients: clientList }));
}

function broadcastClientList() {
  const clientList = Array.from(clients.entries())
    .filter(([_, client]) => client.type === 'android')
    .map(([id, _]) => ({ clientId: id }));
  
  const message = JSON.stringify({ type: 'client_list', clients: clientList });
  
  // Send to all admin clients
  clients.forEach((client) => {
    if (client.type === 'admin' && client.ws.readyState === WebSocket.OPEN) {
      client.ws.send(message);
    }
  });
}

console.log(`WebSocket server started on port ${PORT}`);
console.log('Waiting for connections...');
