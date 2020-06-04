/**
 * This class is used for managing Cards Suit and Rank.
 */
public class CardsInformation {
    /**
     * Enum for card suit: Spades, Hearts, Diamonds and Clubs.
     */
    public enum Suit
    {
        SPADES, HEARTS, DIAMONDS, CLUBS
    }

    /**
     * Enum for card rank: From 2 to 10, J to A.
     */
    public enum Rank
    {
        // Reverse order of rank importance (see rankGreater() below)
        // Order of cards is tied to card images
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
    }

    final static String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};

}
