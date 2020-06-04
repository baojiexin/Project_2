// Whist.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * This class is used to manage the way that players follow to play the game,
 * Associated with rules.
 */

@SuppressWarnings("serial")
public class GameManagement extends CardGame implements IGameManagement,PlayersMode{

  private final String version = "1.0";
  /**
	 * New change here
   */

  private final int handWidth = 400;
  private final Deck deck = new Deck(CardsInformation.Suit.values(), CardsInformation.Rank.values(), "cover");
  private final Location[] handLocations = {
			  new Location(350, 625),
			  new Location(75, 350),
			  new Location(350, 75),
			  new Location(625, 350)
	  };
  public static final Location[] scoreLocations = {
			  new Location(575, 675),
			  new Location(25, 575),
			  new Location(575, 25),
			  new Location(650, 575)
	  };
  public static Actor[] scoreActors = {null, null, null, null };
  private final Location textLocation = new Location(350, 450);
  public static Location trumpsActorLocation = new Location(50, 50);
  Font bigFont = new Font("Serif", Font.BOLD, 36);
  public static final Location trickLocation = new Location(350, 350);
  public static final int trickWidth = 40;

	/**
   *  Function used to set current game status.
   * @param string
   */

	@Override
	public void setStatus(String string) { setStatusText(string); }
/*---------------------------------------------------------------------------------------*/
  /**
   * Function used to initialise score information for each player.
   */
  @Override
  public void initScore() {
	  GameInformation.scores = new int[GameInformation.nbPlayers];
	 for (int i = 0; i < GameInformation.nbPlayers; i++) {
		 //scores[i] = 0;
		 GameInformation.scores[i] = 0;
		 scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
		 addActor(scoreActors[i], scoreLocations[i]);
	 }
  }

	/**
	 * New : initialise players' information
	 */
	@Override
	public void initPlayers(){
		for(int i = 0; i < GameInformation.nbPlayers; i++){
			GameInformation.players.put(i, "NPC");
		}
		if(GameInformation.Interactive_Player >= 1){
			for (int i = 0; i < GameInformation.Interactive_Player; i++){
				GameInformation.players.put(i,"Player");
			}
		}
		if(GameInformation.Smart_NPC >= 1){
			int marked = 0;
			int i = 0;
			while(marked < GameInformation.Smart_NPC){
				if(GameInformation.players.get(i) == "NPC"){
					GameInformation.players.put(i, "Smart_NPC");
					marked++;
					i++;
				}
				else {
					i++;
				}
				if(i > GameInformation.nbPlayers){
					break;
				}
			}
		}
	}

	/**
	 * Function used to update Score after each play round.
	 * @param player
	 */
	@Override
	public void updateScore(int player) {
	removeActor(scoreActors[player]);
	scoreActors[player] = new TextActor(String.valueOf(GameInformation.scores[player]), Color.WHITE, bgColor, bigFont);
	addActor(scoreActors[player], scoreLocations[player]);
  }

  public static Card selected;

  /**
   * Function used to initialise cards in hands.
   */
  @Override
  public void initRound() {
  	GameInformation.hands = deck.dealingOut(GameInformation.nbPlayers, GameInformation.nbStartCards); // Last element of hands is leftover cards; these are ignored.
	  for (int i = 0; i < GameInformation.nbPlayers; i++) {
		  GameInformation.hands[i].sort(Hand.SortType.SUITPRIORITY, true);
	  }
	  // Set up human player for interaction
	  CardListener cardListener = new CardAdapter()  // Human Player plays card
			    {
			      public void leftDoubleClicked(Card card) { selected = card; GameInformation.hands[0].setTouchEnabled(false); }
			    };
	  GameInformation.hands[0].addCardListener(cardListener);
		 // graphics
	    RowLayout[] layouts = new RowLayout[GameInformation.nbPlayers];
	    for (int i = 0; i < GameInformation.nbPlayers; i++) {
	      layouts[i] = new RowLayout(handLocations[i], handWidth);
	      layouts[i].setRotationAngle(90 * i);
	      // layouts[i].setStepDelay(10);
			GameInformation.hands[i].setView(this, layouts[i]);
			GameInformation.hands[i].setTargetArea(new TargetArea(trickLocation));
			GameInformation.hands[i].draw();
	    }
//	    for (int i = 1; i < nbPlayers; i++)  // This code can be used to visually hide the cards in a hand (make them face down)
//	      hands[i].setVerso(true);
	    // End graphics
  }

/*------------------------------------------------------------------------------------------*/
 /** Newly changed: The way with rules players follow to play the game
  * Now it works with Player, NPC and Smart NPC
  * */
 @Override
 public Optional<Integer> playRound() {  // Returns winner, if any
 	final CardsInformation.Suit trumps = GameInformation.randomEnum(CardsInformation.Suit.class);
 	final Actor trumpsActor = new Actor("sprites/"+CardsInformation.trumpImage[trumps.ordinal()]);
 	addActor(trumpsActor, trumpsActorLocation);
	// End trump suit
	Hand trick;
	int winner;
	Card winningCard = null;
	boolean hasTrumpOnBoard = false;
	CardsInformation.Suit lead = null;

	int nextPlayer = GameInformation.random.nextInt(GameInformation.nbPlayers); // randomly select player to lead for this round
	for (int i = 0; i < GameInformation.nbStartCards; i++) {
		trick = new Hand(GameInformation.deck);
		selected = null;

        if (GameInformation.players.get(nextPlayer) == "Player") {  // Select lead depending on player type
    		GameInformation.hands[nextPlayer].setTouchEnabled(true);
    		setStatus("Player 0 double-click on card to lead.");
    		while (null == selected) delay(100);
        }
        else if(GameInformation.players.get(nextPlayer) == "NPC"){
    		setStatusText("Player " + nextPlayer + " thinking...");
            delay(GameInformation.thinkingTime);
			selected = NPCSelection(GameInformation.hands[nextPlayer],lead,trumps);
			System.out.println("NPC Card" + selected);
        }
        else if(GameInformation.players.get(nextPlayer) == "Smart_NPC"){
			Card largestTrump = largestTrumpOnBoard(GameInformation.currentCards,trumps);
			selected = SmartSelection(GameInformation.hands[nextPlayer],lead,trumps,hasTrumpOnBoard,winningCard,largestTrump);
			System.out.println("Smart Card " + selected);
		}
        //New
        GameInformation.currentCards.add(selected);
        if(selected.getSuit() == trumps){
        	hasTrumpOnBoard = true;
		}
        // Lead with selected card
		trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
        trick.draw();
        selected.setVerso(false);
        // No restrictions on the card being lead
		lead = (CardsInformation.Suit) selected.getSuit();

		selected.transfer(trick, true); // transfer to trick (includes graphic effect)
		winner = nextPlayer;
		winningCard = selected;
		// End Lead
		for (int j = 1; j < GameInformation.nbPlayers; j++) {
			if (++nextPlayer >= GameInformation.nbPlayers) nextPlayer = 0;  // From last back to first
			selected = null;
	        if (GameInformation.players.get(nextPlayer) == "Player") {
				GameInformation.hands[0].setTouchEnabled(true);
	    		setStatus("Player 0 double-click on card to follow.");
	    		while (null == selected) delay(100);
	        } else if(GameInformation.players.get(nextPlayer) == "NPC"){
		        setStatusText("Player " + nextPlayer + " thinking...");
		        delay(GameInformation.thinkingTime);
		        //selected = randomCard(hands[nextPlayer]);

				selected = NPCSelection(GameInformation.hands[nextPlayer],lead,trumps);
				System.out.println("NPC Card" + selected);
	        }
	        else if(GameInformation.players.get(nextPlayer) == "Smart_NPC"){
	        	Card largestTrump = largestTrumpOnBoard(GameInformation.currentCards,trumps);
				selected = SmartSelection(GameInformation.hands[nextPlayer],lead,trumps,hasTrumpOnBoard,winningCard,largestTrump);
				System.out.println("Smart Card " + selected);
			}
	        //New
	        GameInformation.currentCards.add(selected);
			if(selected.getSuit() == trumps && hasTrumpOnBoard == false){
				hasTrumpOnBoard = true;
			}
	        // Follow with selected card
			trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
	        trick.draw();
	        selected.setVerso(false);  // In case it is upside down
			// Check: Following card must follow suit if possible
			if (selected.getSuit() != lead && GameInformation.hands[nextPlayer].getNumberOfCardsWithSuit(lead) > 0) {
						 // Rule violation
				String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
				System.out.println(violation);
				if (GameInformation.enforceRules)
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
					(selected.getSuit() == winningCard.getSuit() && GameInformation.rankGreater(selected, winningCard)) ||
					  // trumped when non-trump was winning
					 (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
					 System.out.println("NEW WINNER");
					 winner = nextPlayer;
					 winningCard = selected;

			}
			// End Follow
		}
		GameInformation.currentCards = new ArrayList<>();
		hasTrumpOnBoard = false;
		lead = null;
		delay(600);
		trick.setView(this, new RowLayout(GameInformation.hideLocation, 0));
		trick.draw();
		nextPlayer = winner;
		setStatusText("Player " + nextPlayer + " wins trick.");
		GameInformation.scores[nextPlayer]++;
		updateScore(nextPlayer);
		if (GameInformation.winningScore == GameInformation.scores[nextPlayer]) return Optional.of(nextPlayer);
	}
	removeActor(trumpsActor);
	return Optional.empty();
}

  public GameManagement()
  {
    super(700, 700, 30);
    setTitle("Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
    setStatusText("Initializing...");
    initScore();
    initPlayers();
    Optional<Integer> winner;
    Whist newWhist = new Whist();
    do {
      initRound();
      winner = playRound();
    } while (!winner.isPresent());
    addActor(new Actor("sprites/gameover.gif"), textLocation);
    setStatusText("Game over. Winner is player: " + winner.get());
    refresh();
  }

/*------------------------------------------------------------------------------------------*/
  /** New Function used to find the largest trump card that has already been played on board*/
  private Card largestTrumpOnBoard(ArrayList<Card> currentCards, CardsInformation.Suit trump){
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
  
/*------------------------------------------------------------------------------------------*/
	/** New Function used for normal NPC to follow the rules*/
	@Override
	public Card NPCSelection(Hand hand, CardsInformation.Suit lead, CardsInformation.Suit trumps){
		if (lead != null) {
			int cardNumber = hand.getNumberOfCards();
			for (int i = 0; i < cardNumber; i++) {
				if (hand.get(i).getSuit() == lead) {
					return hand.get(i);
				} else continue;
			}
		}
		return GameInformation.randomCard(hand);

	}

	/** New Function used for Smart AI to select cards.
	 * When taking the lead, it will select the card of highest number in hand, or the trump card of highest number
	 * in hand if any.
	 * When there is no leading card, it will try to select the largest trump card in hand.
	 * When there is no card can win the game, it will select the smallest card.*/
	@Override
	public Card SmartSelection(Hand hand, CardsInformation.Suit lead,
							   CardsInformation.Suit trumps, boolean hasTrumpOnBoard,
							   Card winningCard, Card largestTrump){
		Card trumpCard = null;
		Card maxNormalCard = null;
		Card minNormalCard = null;
		Card smallestCard = null;
		//When it is the first to play
		if(lead == null){
			for(int i = 0; i < hand.getNumberOfCards(); i++){
				if(hand.get(i).getSuit() == trumps){
					if(trumpCard == null || GameInformation.rankGreater(hand.get(i),trumpCard)){
						trumpCard = hand.get(i);
					}
				}
				if(maxNormalCard == null || GameInformation.rankGreater(hand.get(i), maxNormalCard)){
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
					if(trumpCard == null || GameInformation.rankGreater(hand.get(i),trumpCard)){
						trumpCard = hand.get(i);
					}
				}
				if(hand.get(i).getSuit() == lead){
					if(maxNormalCard == null || GameInformation.rankGreater(hand.get(i),maxNormalCard)){
						maxNormalCard = hand.get(i);
					}
					if(minNormalCard == null || GameInformation.rankGreater(minNormalCard, hand.get(i))){
						minNormalCard = hand.get(i);
					}
				}
				if(smallestCard == null || GameInformation.rankGreater(smallestCard,hand.get(i))){
					smallestCard = hand.get(i);
				}
			}
			//If it has leading suit cards in hand.
			if(maxNormalCard != null){
				if(hasTrumpOnBoard){
					return minNormalCard;
				}
				if(GameInformation.rankGreater(maxNormalCard,winningCard)){
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
						Card curTrumpOnBoard = largestTrump;
						if (GameInformation.rankGreater(trumpCard,curTrumpOnBoard)){
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



}
