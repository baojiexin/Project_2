// Whist.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("serial")
public class Whist extends CardGame {
	
  public enum Suit
  {
    SPADES, HEARTS, DIAMONDS, CLUBS
  }

  public enum Rank
  {
    // Reverse order of rank importance (see rankGreater() below)
	// Order of cards is tied to card images
	ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
  }
  
  final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};

  static Random random;
  
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
  
  public boolean rankGreater(Card card1, Card card2) {
	  return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
  }
	 
  private final String version = "1.0";
  /**
	 * New change here
   */
  public static int nbPlayers = 0;
  public static int nbStartCards = 0;
  public static int winningScore = 0;
  public static int Interactive_Player = 0;
  public static int Random_NPC = 0;
  public static int Smart_NPC = 0;
  public static int Seed = 0;
//  public final int nbPlayers = 4;
//  public final int nbStartCards = 13;
//  public final int winningScore = 11;
  private final int handWidth = 400;
  private final int trickWidth = 40;
  private static Map<Integer, String> players = new HashMap();
  private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
  private final Location[] handLocations = {
			  new Location(350, 625),
			  new Location(75, 350),
			  new Location(350, 75),
			  new Location(625, 350)
	  };
  private final Location[] scoreLocations = {
			  new Location(575, 675),
			  new Location(25, 575),
			  new Location(575, 25),
			  new Location(650, 575)
	  };
  private Actor[] scoreActors = {null, null, null, null };
  private final Location trickLocation = new Location(350, 350);
  private final Location textLocation = new Location(350, 450);
  private final int thinkingTime = 2000;
  private Hand[] hands;
  private Location hideLocation = new Location(-500, - 500);
  private Location trumpsActorLocation = new Location(50, 50);
  private boolean enforceRules=false;
  //New
  private ArrayList<Card> currentCards = new ArrayList<>();

  public void setStatus(String string) { setStatusText(string); }
  private int[] scores = new int[nbPlayers];
  Font bigFont = new Font("Serif", Font.BOLD, 36);
  private void initScore() {
	 for (int i = 0; i < nbPlayers; i++) {
		 scores[i] = 0;
		 scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
		 addActor(scoreActors[i], scoreLocations[i]);
	 }
  }
	/** New : initialise players' information*/
  private void initPlayers(Map<Integer, String> map){
	for(int i = 0; i < nbPlayers; i++){
		map.put(i, "NPC");
	}
	if(Interactive_Player >= 1){
		for (int i = 0; i < Interactive_Player; i++){
			map.put(i,"Player");
		}
	}
	if(Smart_NPC >= 1){
		int marked = 0;
		int i = 0;
		while(marked < Smart_NPC){
			if(map.get(i) == "NPC"){
				map.put(i, "Smart_NPC");
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
  private void updateScore(int player) {
	removeActor(scoreActors[player]);
	scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
	addActor(scoreActors[player], scoreLocations[player]);
  }
  private Card selected;
  private void initRound() {
  	hands = deck.dealingOut(nbPlayers, nbStartCards); // Last element of hands is leftover cards; these are ignored.
	  for (int i = 0; i < nbPlayers; i++) {
	  	hands[i].sort(Hand.SortType.SUITPRIORITY, true);
	  }
	  // Set up human player for interaction
	  CardListener cardListener = new CardAdapter()  // Human Player plays card
			    {
			      public void leftDoubleClicked(Card card) { selected = card; hands[0].setTouchEnabled(false); }
			    };
		hands[0].addCardListener(cardListener);
		 // graphics
	    RowLayout[] layouts = new RowLayout[nbPlayers];
	    for (int i = 0; i < nbPlayers; i++) {
	      layouts[i] = new RowLayout(handLocations[i], handWidth);
	      layouts[i].setRotationAngle(90 * i);
	      // layouts[i].setStepDelay(10);
	      hands[i].setView(this, layouts[i]);
	      hands[i].setTargetArea(new TargetArea(trickLocation));
	      hands[i].draw();
	    }
//	    for (int i = 1; i < nbPlayers; i++)  // This code can be used to visually hide the cards in a hand (make them face down)
//	      hands[i].setVerso(true);
	    // End graphics
 }
	/** Newly changed: Now it works with Player, NPC and Smart NPC*/
 private Optional<Integer> playRound() {  // Returns winner, if any
	// Select and display trump suit
		final Suit trumps = randomEnum(Suit.class);
		final Actor trumpsActor = new Actor("sprites/"+trumpImage[trumps.ordinal()]);
	    addActor(trumpsActor, trumpsActorLocation);
	// End trump suit
	Hand trick;
	int winner;
	Card winningCard = null;
	boolean hasTrumpOnBoard = false;
	Suit lead = null;

	int nextPlayer = random.nextInt(nbPlayers); // randomly select player to lead for this round
	for (int i = 0; i < nbStartCards; i++) {
		trick = new Hand(deck);
    	selected = null;

        if (players.get(nextPlayer) == "Player") {  // Select lead depending on player type
    		hands[nextPlayer].setTouchEnabled(true);
    		setStatus("Player 0 double-click on card to lead.");
    		while (null == selected) delay(100);
        }
        else if(players.get(nextPlayer) == "NPC"){
    		setStatusText("Player " + nextPlayer + " thinking...");
            delay(thinkingTime);


			selected = NPCSelection(hands[nextPlayer],lead,trumps);
			System.out.println("NPC Card" + selected);
        }
        else if(players.get(nextPlayer) == "Smart_NPC"){
			selected = SmartSelection(hands[nextPlayer],lead,trumps,hasTrumpOnBoard,winningCard);
			System.out.println("Smart Card " + selected);
		}
        //New
        currentCards.add(selected);
        if(selected.getSuit() == trumps){
        	hasTrumpOnBoard = true;
		}
        // Lead with selected card
	        trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
			trick.draw();
			selected.setVerso(false);
			// No restrictions on the card being lead
			lead = (Suit) selected.getSuit();

			selected.transfer(trick, true); // transfer to trick (includes graphic effect)
			winner = nextPlayer;
			winningCard = selected;
		// End Lead
		for (int j = 1; j < nbPlayers; j++) {
			if (++nextPlayer >= nbPlayers) nextPlayer = 0;  // From last back to first
			selected = null;
	        if (players.get(nextPlayer) == "Player") {
	    		hands[0].setTouchEnabled(true);
	    		setStatus("Player 0 double-click on card to follow.");
	    		while (null == selected) delay(100);
	        } else if(players.get(nextPlayer) == "NPC"){
		        setStatusText("Player " + nextPlayer + " thinking...");
		        delay(thinkingTime);
		        //selected = randomCard(hands[nextPlayer]);

				selected = NPCSelection(hands[nextPlayer],lead,trumps);
				System.out.println("NPC Card" + selected);
	        }
	        else if(players.get(nextPlayer) == "Smart_NPC"){
				selected = SmartSelection(hands[nextPlayer],lead,trumps,hasTrumpOnBoard,winningCard);
				System.out.println("Smart Card " + selected);
			}
	        //New
	        currentCards.add(selected);
			if(selected.getSuit() == trumps && hasTrumpOnBoard == false){
				hasTrumpOnBoard = true;
			}
	        // Follow with selected card
			trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
	        trick.draw();
	        selected.setVerso(false);  // In case it is upside down
			// Check: Following card must follow suit if possible
			if (selected.getSuit() != lead && hands[nextPlayer].getNumberOfCardsWithSuit(lead) > 0) {
						 // Rule violation
				String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
				System.out.println(violation);
				if (enforceRules)
					try {
						throw(new BrokeRuleException(violation));
					} catch (BrokeRuleException e) {
						e.printStackTrace();
						System.out.println("A cheating player spoiled the game!");
						System.exit(0);
					}
			}
			// End Check
			selected.transfer(trick, true); // transfer to trick (includes graphic effect)
			System.out.println("winning: suit = " + winningCard.getSuit() + ", rank = " + winningCard.getRankId());
			System.out.println(" played: suit = " +    selected.getSuit() + ", rank = " +    selected.getRankId());
			if ( // beat current winner with higher card
					(selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
					  // trumped when non-trump was winning
					 (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
					 System.out.println("NEW WINNER");
					 winner = nextPlayer;
					 winningCard = selected;

			}
			// End Follow
		}
		currentCards = new ArrayList<>();
		hasTrumpOnBoard = false;
		lead = null;
		delay(600);
		trick.setView(this, new RowLayout(hideLocation, 0));
		trick.draw();		
		nextPlayer = winner;
		setStatusText("Player " + nextPlayer + " wins trick.");
		scores[nextPlayer]++;
		updateScore(nextPlayer);
		if (winningScore == scores[nextPlayer]) return Optional.of(nextPlayer);
	}
	removeActor(trumpsActor);
	return Optional.empty();
}

  public Whist()
  {
    super(700, 700, 30);
    setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
    setStatusText("Initializing...");
    initScore();
    initPlayers(players);
    Optional<Integer> winner;
    do { 
      initRound();
      winner = playRound();
    } while (!winner.isPresent());
    addActor(new Actor("sprites/gameover.gif"), textLocation);
    setStatusText("Game over. Winner is player: " + winner.get());
    refresh();
  }

  public static void main(String[] args) throws IOException {
  	Properties whistPropertie = new Properties();
  	FileReader inStream = null;
  	try {
  		inStream = new FileReader("smart" +
				".properties");
  		whistPropertie.load(inStream);
  	} finally {
  		if (inStream != null) {
			  inStream.close();
  		}
  	}
  	//To show information at the beginning.
  	nbPlayers = Integer.parseInt(whistPropertie.getProperty("nbPlayers"));
	  System.out.println("The number of Players: " + nbPlayers);
  	nbStartCards = Integer.parseInt(whistPropertie.getProperty("nbStartCards"));
	  System.out.println("Player starts with "+ nbStartCards + " cards");
  	winningScore = Integer.parseInt(whistPropertie.getProperty("winningScore"));
	  System.out.println("Winning Score is " + winningScore);
  	Interactive_Player = Integer.parseInt(whistPropertie.getProperty("Interactive_Player"));
	  System.out.println("The number of Interactive Player: " + Interactive_Player);
  	Random_NPC = Integer.parseInt(whistPropertie.getProperty("Random_NPC"));
	  System.out.println("The number of Random NPC: " + Random_NPC);
  	Smart_NPC = Integer.parseInt(whistPropertie.getProperty("Smart_NPC"));
	  System.out.println("The number of Smart NPC: " + Smart_NPC);
  	Seed = Integer.parseInt(whistPropertie.getProperty("Seed"));
  	if(Seed == 30006){
		System.out.println("Play with fixed Seed: " + Seed);
  		random = new Random((long) Seed);
	}
  	else {
		System.out.println("No Seed");
	}
	// System.out.println("Working Directory = " + System.getProperty("user.dir"));
    new Whist();
  }
	/** New Function used for normal NPC to follow the rules*/
  private Card NPCSelection(Hand hand, Suit lead,Suit trumps){
	  if (lead != null) {
		  int cardNumber = hand.getNumberOfCards();
		  for (int i = 0; i < cardNumber; i++) {
			  if (hand.get(i).getSuit() == lead) {
				  return hand.get(i);
			  } else continue;
		  }
	  }
	  return randomCard(hand);

  }
	/** New Function used for Smart AI to select cards.
	 * When taking the lead, it will select the card of highest number in hand, or the trump card of highest number
	 * in hand if any.
	 * When there is no leading card, it will try to select the largest trump card in hand.
	 * When there is no card can win the game, it will select the smallest card.*/
  private Card SmartSelection(Hand hand, Suit lead, Suit trumps, boolean hasTrumpOnBoard, Card winningCard){
  	Card trumpCard = null;
  	Card maxNormalCard = null;
  	Card minNormalCard = null;
  	Card smallestCard = null;
  	//When it is the first to play
  	if(lead == null){
  		for(int i = 0; i < hand.getNumberOfCards(); i++){
  			if(hand.get(i).getSuit() == trumps){
  				if(trumpCard == null || rankGreater(hand.get(i),trumpCard)){
  					trumpCard = hand.get(i);
				}
			}
			if(maxNormalCard == null || rankGreater(hand.get(i), maxNormalCard)){
				maxNormalCard = hand.get(i);
			}
		}

  		if(trumpCard != null){
  			return trumpCard;
		}
  		else return maxNormalCard;
	}
  	//When it is not the first one to play
  	else {
		for(int i = 0; i < hand.getNumberOfCards(); i++){
			if(hand.get(i).getSuit() == trumps){
				if(trumpCard == null || rankGreater(hand.get(i),trumpCard)){
					trumpCard = hand.get(i);
				}
			}
			if(hand.get(i).getSuit() == lead){
				if(maxNormalCard == null || rankGreater(hand.get(i),maxNormalCard)){
					maxNormalCard = hand.get(i);
				}
				if(minNormalCard == null || rankGreater(minNormalCard, hand.get(i))){
					minNormalCard = hand.get(i);
				}
			}
			if(smallestCard == null || rankGreater(smallestCard,hand.get(i))){
				smallestCard = hand.get(i);
			}
		}
		//If it has leading suit cards in hand.
		if(maxNormalCard != null){
			if(hasTrumpOnBoard){
				return minNormalCard;
			}
			if(rankGreater(maxNormalCard,winningCard)){
				return maxNormalCard;
			}
			else return minNormalCard;
		}
		//If there is no leading suit card in hand
		else {
			if(trumpCard != null){
				if(!hasTrumpOnBoard){
					return trumpCard;
				}
				else {
					Card curTrumpOnBoard = largestTrumpOnBoard(currentCards,trumps);
					if (rankGreater(trumpCard,curTrumpOnBoard)){
						return trumpCard;
					}
					else {
						return smallestCard;
					}
				}
			}
			else {
				return smallestCard;
			}
		}
	}
  }

  /** New Function used to find the largest trump card that has already been played on board*/
  private Card largestTrumpOnBoard(ArrayList<Card> currentCards, Suit trump){
  	Card largestTrumpCard = null;
  	for(int i = 0; i < currentCards.size(); i++){
  		if(currentCards.get(i).getSuit() == trump){
  			if(largestTrumpCard == null || currentCards.get(i).getRankId() < largestTrumpCard.getRankId()){
  				largestTrumpCard = currentCards.get(i);
			}
		}
	}
  	return largestTrumpCard;
  }


}
