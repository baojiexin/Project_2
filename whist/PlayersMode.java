import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardGame;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

public class PlayersMode extends CardGame{
    /** New Function used for normal NPC to follow the rules*/
    public static Card NPCSelection(Hand hand, CardsInformation.Suit lead, CardsInformation.Suit trumps){
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
    public static Card SmartSelection(Hand hand, CardsInformation.Suit lead, CardsInformation.Suit trumps, boolean hasTrumpOnBoard, Card winningCard, Card largestTrump){
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



}
