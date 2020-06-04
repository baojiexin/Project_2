import java.util.Optional;

/**
 * An interface with abstract game management functions and can be easily extended.
 */
public interface IGameManagement {
    /**
     *  Function used to set current game status.
     * @param string
     */
    abstract void setStatus(String string);

    /**
     * Function used to initialise score information for each player.
     */
    abstract void initScore();

    /**
     * New : initialise players' information
     */
    abstract void initPlayers();

    /**
     * Function used to update Score after each play round.
     * @param player
     */
    abstract void updateScore(int player);

    /**
     * Function used to initialise cards in hands.
     */
    abstract void initRound();

    /** Newly changed: The way with rules players follow to play the game
     * Now it works with Player, NPC and Smart NPC
     * */
    abstract Optional<Integer> playRound();


}
