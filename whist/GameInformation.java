import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardGame;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameInformation extends CardGame{
    public  static int nbPlayers = 0;
    public  static int nbStartCards = 0;
    public  static int winningScore = 0;
    public  static int Interactive_Player = 0;
    public  static int Random_NPC = 0;
    public  static int Smart_NPC = 0;
    public static int Seed = 0;
    public static final int thinkingTime = 2000;
    public static boolean enforceRules = false;
    public  static Random random;

    public static Location trumpsActorLocation = new Location(50, 50);
    public static Deck deck = new Deck(CardsInformation.Suit.values(), CardsInformation.Rank.values(), "cover");
    public static ArrayList<Card> currentCards = new ArrayList<>();
    public static Map<Integer, String> players = new HashMap();
    public static Hand[] hands;
    public static final Location trickLocation = new Location(350, 350);
    public static final int trickWidth = 40;
    public static Location hideLocation = new Location(-500, - 500);
    public static int[] scores;

    public static Font bigFont = new Font("Serif", Font.BOLD, 36);
    // return random Enum value

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // return random Card from Hand
    public static Card randomCard(Hand hand){
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list){
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    public static boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }


}
