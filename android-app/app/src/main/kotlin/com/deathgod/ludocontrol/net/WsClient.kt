package com.deathgod.ludocontrol.net

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

/**
 * WsClient - WebSocket client for connecting to admin server
 * Handles messages: {"type":"roll"} and {"type":"force","value":n}
 */
class WsClient(serverUri: String, private val clientId: String) {
    
    interface MessageListener {
        fun onConnected()
        fun onDisconnected()
        fun onRollCommand()
        fun onForceCommand(value: Int)
        fun onError(error: String)
    }
    
    private var listener: MessageListener? = null
    private var webSocketClient: WebSocketClient? = null
    private val uri = URI(serverUri)
    
    fun setListener(listener: MessageListener) {
        this.listener = listener
    }
    
    fun connect() {
        try {
            webSocketClient = object : WebSocketClient(uri) {
                override fun onOpen(handshakedata: ServerHandshake?) {
                    Log.d(TAG, "WebSocket Connected")
                    
                    // Register with server
                    val registerMsg = JSONObject().apply {
                        put("type", "register")
                        put("clientId", clientId)
                        put("clientType", "android")
                    }
                    send(registerMsg.toString())
                    
                    listener?.onConnected()
                }
                
                override fun onMessage(message: String?) {
                    message?.let { handleMessage(it) }
                }
                
                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.d(TAG, "WebSocket Closed: $reason")
                    listener?.onDisconnected()
                }
                
                override fun onError(ex: Exception?) {
                    Log.e(TAG, "WebSocket Error", ex)
                    listener?.onError(ex?.message ?: "Unknown error")
                }
            }
            
            webSocketClient?.connect()
            
        } catch (e: Exception) {
            Log.e(TAG, "Connection error", e)
            listener?.onError(e.message ?: "Connection failed")
        }
    }
    
    private fun handleMessage(message: String) {
        try {
            val json = JSONObject(message)
            val type = json.optString("type")
            
            Log.d(TAG, "Received message: $type")
            
            when (type) {
                "roll" -> {
                    listener?.onRollCommand()
                }
                "force" -> {
                    val value = json.optInt("value", 1)
                    if (value in 1..6) {
                        listener?.onForceCommand(value)
                    }
                }
                "registered" -> {
                    Log.d(TAG, "Successfully registered with server")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message", e)
        }
    }
    
    fun disconnect() {
        webSocketClient?.close()
        webSocketClient = null
    }
    
    fun isConnected(): Boolean {
        return webSocketClient?.isOpen ?: false
    }
    
    companion object {
        private const val TAG = "WsClient"
    }
}
