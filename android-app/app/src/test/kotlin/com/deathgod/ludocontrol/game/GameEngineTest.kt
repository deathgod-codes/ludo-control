package com.deathgod.ludocontrol.game

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GameEngine
 */
class GameEngineTest {
    
    private lateinit var gameEngine: GameEngine
    
    @Before
    fun setUp() {
        gameEngine = GameEngine()
    }
    
    @Test
    fun testInitGame_validPlayers() {
        val state = gameEngine.initGame(2)
        
        assertNotNull(state)
        assertEquals(2, state.players.size)
        assertEquals(0, state.currentPlayerIndex)
        assertEquals(0, state.lastRoll)
        assertFalse(state.gameOver)
    }
    
    @Test
    fun testInitGame_fourPlayers() {
        val state = gameEngine.initGame(4)
        
        assertEquals(4, state.players.size)
        state.players.forEachIndexed { index, player ->
            assertEquals(index + 1, player.id)
            assertEquals("Player ${index + 1}", player.name)
            assertEquals(0, player.position)
            assertEquals(4, player.tokensHome)
        }
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun testInitGame_tooFewPlayers() {
        gameEngine.initGame(1)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun testInitGame_tooManyPlayers() {
        gameEngine.initGame(5)
    }
    
    @Test
    fun testProcessDiceRoll_validValue() {
        gameEngine.initGame(2)
        val state = gameEngine.processDiceRoll(6)
        
        assertEquals(6, state.lastRoll)
        assertEquals(1, state.currentPlayerIndex) // Should move to next player
    }
    
    @Test
    fun testProcessDiceRoll_forcedRoll() {
        gameEngine.initGame(3)
        val state = gameEngine.processDiceRoll(4, isForced = true)
        
        assertEquals(4, state.lastRoll)
        assertEquals(1, state.currentPlayerIndex)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun testProcessDiceRoll_invalidValue_tooLow() {
        gameEngine.initGame(2)
        gameEngine.processDiceRoll(0)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun testProcessDiceRoll_invalidValue_tooHigh() {
        gameEngine.initGame(2)
        gameEngine.processDiceRoll(7)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun testProcessDiceRoll_gameNotInitialized() {
        gameEngine.processDiceRoll(6)
    }
    
    @Test
    fun testGetGameState_initialized() {
        gameEngine.initGame(2)
        val state = gameEngine.getGameState()
        
        assertNotNull(state)
    }
    
    @Test
    fun testGetGameState_notInitialized() {
        val state = gameEngine.getGameState()
        
        assertNull(state)
    }
    
    @Test
    fun testCheckWinner_noWinner() {
        gameEngine.initGame(2)
        val winner = gameEngine.checkWinner()
        
        assertNull(winner)
    }
    
    @Test
    fun testResetGame() {
        gameEngine.initGame(2)
        assertNotNull(gameEngine.getGameState())
        
        gameEngine.resetGame()
        assertNull(gameEngine.getGameState())
    }
    
    @Test
    fun testPlayerRotation() {
        gameEngine.initGame(3)
        
        // First roll - should move to player 2
        var state = gameEngine.processDiceRoll(1)
        assertEquals(1, state.currentPlayerIndex)
        
        // Second roll - should move to player 3
        state = gameEngine.processDiceRoll(2)
        assertEquals(2, state.currentPlayerIndex)
        
        // Third roll - should wrap to player 1
        state = gameEngine.processDiceRoll(3)
        assertEquals(0, state.currentPlayerIndex)
    }
}
