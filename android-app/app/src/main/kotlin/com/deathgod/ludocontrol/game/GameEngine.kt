package com.deathgod.ludocontrol.game

/**
 * GameEngine - Core game logic for Ludo
 * This class is testable and contains the business logic for the game
 */
class GameEngine {
    
    data class Player(
        val id: Int,
        val name: String,
        var position: Int = 0,
        var tokensHome: Int = 4
    )
    
    data class GameState(
        val players: List<Player>,
        var currentPlayerIndex: Int = 0,
        var lastRoll: Int = 0,
        var gameOver: Boolean = false
    )
    
    private var gameState: GameState? = null
    
    /**
     * Initialize a new game with specified number of players
     */
    fun initGame(numPlayers: Int): GameState {
        require(numPlayers in 2..4) { "Number of players must be between 2 and 4" }
        
        val players = (1..numPlayers).map { 
            Player(id = it, name = "Player $it")
        }
        
        gameState = GameState(players = players)
        return gameState!!
    }
    
    /**
     * Process a dice roll
     * @param value The dice value (1-6)
     * @param isForced Whether this roll was forced by admin (hidden from player)
     * @return Updated game state
     */
    fun processDiceRoll(value: Int, isForced: Boolean = false): GameState {
        require(value in 1..6) { "Dice value must be between 1 and 6" }
        require(gameState != null) { "Game not initialized" }
        
        val state = gameState!!
        state.lastRoll = value
        
        // Basic game logic - move to next player after roll
        // In a real game, this would be more complex with token movement
        state.currentPlayerIndex = (state.currentPlayerIndex + 1) % state.players.size
        
        return state
    }
    
    /**
     * Get current game state
     */
    fun getGameState(): GameState? = gameState
    
    /**
     * Check if a player has won
     */
    fun checkWinner(): Player? {
        val state = gameState ?: return null
        return state.players.firstOrNull { it.tokensHome == 0 && it.position >= 57 }
    }
    
    /**
     * Reset the game
     */
    fun resetGame() {
        gameState = null
    }
}
