package com.deathgod.ludocontrol.game

import android.os.Handler
import android.os.Looper

/**
 * DiceManager - Handles dice rolling with animations
 * Accepts remote messages and performs animation API
 */
class DiceManager {
    
    interface DiceListener {
        fun onDiceRolling()
        fun onDiceRolled(value: Int, isForced: Boolean)
    }
    
    private var listener: DiceListener? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isRolling = false
    
    fun setListener(listener: DiceListener) {
        this.listener = listener
    }
    
    /**
     * Roll dice with animation
     * @param forcedValue If not null, force this value (hidden from player)
     */
    fun rollDice(forcedValue: Int? = null) {
        if (isRolling) return
        
        isRolling = true
        listener?.onDiceRolling()
        
        // Simulate animation delay
        handler.postDelayed({
            val finalValue = forcedValue ?: (1..6).random()
            isRolling = false
            listener?.onDiceRolled(finalValue, forcedValue != null)
        }, 1000) // 1 second animation
    }
    
    /**
     * Handle remote roll command
     */
    fun handleRemoteRoll(type: String, value: Int?) {
        when (type) {
            "roll" -> rollDice()
            "force" -> value?.let { 
                if (it in 1..6) {
                    rollDice(forcedValue = it)
                }
            }
        }
    }
    
    fun isRolling(): Boolean = isRolling
    
    fun cleanup() {
        handler.removeCallbacksAndMessages(null)
        listener = null
    }
}
