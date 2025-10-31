package com.deathgod.ludocontrol.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.deathgod.ludocontrol.R
import com.deathgod.ludocontrol.game.DiceManager
import com.deathgod.ludocontrol.game.GameEngine
import com.deathgod.ludocontrol.net.WsClient

/**
 * UIActivity - Main activity with basic game UI
 * This is a placeholder implementation
 */
class UIActivity : AppCompatActivity() {
    
    private lateinit var gameEngine: GameEngine
    private lateinit var diceManager: DiceManager
    private var wsClient: WsClient? = null
    
    private lateinit var statusText: TextView
    private lateinit var diceValueText: TextView
    private lateinit var rollButton: Button
    private lateinit var connectButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize game components
        gameEngine = GameEngine()
        diceManager = DiceManager()
        
        // Initialize UI (would reference real views in complete app)
        initViews()
        setupListeners()
        
        // Initialize game
        gameEngine.initGame(2)
        updateUI("Game initialized with 2 players")
    }
    
    private fun initViews() {
        // Placeholder - in real app would use findViewById
        // statusText = findViewById(R.id.statusText)
        // diceValueText = findViewById(R.id.diceValueText)
        // rollButton = findViewById(R.id.rollButton)
        // connectButton = findViewById(R.id.connectButton)
    }
    
    private fun setupListeners() {
        diceManager.setListener(object : DiceManager.DiceListener {
            override fun onDiceRolling() {
                runOnUiThread {
                    updateUI("Rolling dice...")
                }
            }
            
            override fun onDiceRolled(value: Int, isForced: Boolean) {
                runOnUiThread {
                    gameEngine.processDiceRoll(value, isForced)
                    val forcedMsg = if (isForced) " (Remote)" else ""
                    updateUI("Rolled: $value$forcedMsg")
                }
            }
        })
    }
    
    private fun connectToServer() {
        val serverUrl = "ws://10.0.2.2:8080" // Default for Android emulator
        val clientId = "android-${System.currentTimeMillis()}"
        
        wsClient = WsClient(serverUrl, clientId).apply {
            setListener(object : WsClient.MessageListener {
                override fun onConnected() {
                    runOnUiThread {
                        updateUI("Connected to server")
                    }
                }
                
                override fun onDisconnected() {
                    runOnUiThread {
                        updateUI("Disconnected from server")
                    }
                }
                
                override fun onRollCommand() {
                    runOnUiThread {
                        diceManager.rollDice()
                    }
                }
                
                override fun onForceCommand(value: Int) {
                    runOnUiThread {
                        // Force roll is hidden from player
                        diceManager.rollDice(value)
                    }
                }
                
                override fun onError(error: String) {
                    runOnUiThread {
                        updateUI("Error: $error")
                    }
                }
            })
            connect()
        }
    }
    
    private fun updateUI(message: String) {
        // Placeholder - would update real views
        // statusText.text = message
    }
    
    override fun onDestroy() {
        super.onDestroy()
        diceManager.cleanup()
        wsClient?.disconnect()
    }
}
