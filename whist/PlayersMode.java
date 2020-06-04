import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

/**
 * Interface with functions for different level AI players to select card.
 * Now have to static function NPCSelection and SmartSelection.
 * Can be extended in the future.
 */
public interface PlayersMode {
    /**
     * New Function used for normal NPC to follow the rules
     */
    abstract Card NPCSelection(Hand hand, CardsInformation.Suit lead, CardsInformation.Suit trumps);

    /**
     * New Function used for Smart AI to select cards.
     */
    abstract Card SmartSelection(Hand hand, CardsInformation.Suit lead,
                                 CardsInformation.Suit trumps, boolean hasTrumpOnBoard,
                                 Card winningCard, Card largestTrump);
}
