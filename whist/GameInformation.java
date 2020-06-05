import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class hold all information during the game period like the number and type of players, cards that have already been
 * played on board and scores for each players etc.
 */
public class GameInformation{
    /**
     * Data held during the game period.
     */
    public  static int nbPlayers = 0;
    public  static int nbStartCards = 0;
    public  static int winningScore = 0;
    public  static int Interactive_Player = 0;
    public  static int Legal_NPC = 0;
    public  static int Smart_NPC = 0;
    public static int Seed = 0;
    public static final int thinkingTime = 2000;
    public static boolean enforceRules = false;
    public  static Random random;

    public static Deck deck = new Deck(CardsInformation.Suit.values(), CardsInformation.Rank.values(), "cover");
    public static ArrayList<Card> currentCards = new ArrayList<>();
    public static Map<Integer, String> players = new HashMap();
    public static Hand[] hands;

    public static Location hideLocation = new Location(-500, - 500);

    public static int[] scores;


    /**
     * return random Enum value
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    /**
     * return random Card from Hand
     * @param hand
     * @return
     */
    public static Card randomCard(Hand hand){
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    /**
     * // return random Card from ArrayList
     * @param list
     * @return
     */
    public static Card randomCard(ArrayList<Card> list){
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    /**
     * Function that used to compare whose rank has priority.
     * @param card1
     * @param card2
     * @return
     */
    public static boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    /**
     * New : initialise players' information
     */
    public static void initPlayers(){
        for(int i = 0; i < nbPlayers; i++){
            players.put(i, "NPC");
        }
        if(Interactive_Player >= 1){
            for (int i = 0; i < Interactive_Player; i++){
                players.put(i,"Player");
            }
        }
        if(Smart_NPC >= 1){
            int marked = 0;
            int i = 0;
            while(marked < Smart_NPC){
                if(players.get(i) == "NPC"){
                    players.put(i, "Smart_NPC");
                    marked++;
                    i++;
                }
                else {
                    i++;
                }
                if(i > nbPlayers){
                    break;
                }
            }
        }
    }
}
