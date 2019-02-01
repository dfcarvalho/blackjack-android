package plp.project02.game;

import java.util.ArrayList;
import plp.project02.game.users.*;

// TODO CHANGE: changed the name from GameEngine to Round
public class Round {
	
	private static Person dealer = new Person();
	private static ArrayList<Player> gamePlayers = new ArrayList<Player>();
	public static int numDecks = 1;
	
	private TablePile pile;
	private int numOfPlayers;
	private int currentPlayer;
	private boolean enableInsurance;
	private boolean paidInsurance;
	
	// TODO DEBUG: experimenting with enums on Android
	public enum RoundState {
		RS_ROUND_START,
		RS_BET,
		RS_PLAYERS_TURN,
		RS_DEALER_TURN,
		RS_ROUND_END,
		RS_GAMEOVER
	};
	private RoundState roundState;
	
	public enum RoundResult {
		RR_PLAYER_WIN,
		RR_PLAYER_BLACKJACK,
		RR_PLAYER_LOSE,
		RR_DRAW,
		RR_NOT_FINISHED
	};
	private RoundResult roundResult;
	
	public Round() {
		pile = new TablePile(this.numDecks);
		this.numOfPlayers = gamePlayers.size();
	}
	
	public void init() {
		roundState = RoundState.RS_ROUND_START;
		roundResult = RoundResult.RR_NOT_FINISHED;
		currentPlayer = 0;
		enableInsurance = false;
		paidInsurance = false;
		
		// deal cards
		dealInitialCards();
	}
	
	public static void addPlayer(Player p) {
		gamePlayers.add(p);
	}
	
	// TODO CHANGE: uses new TablePile.dealCard()
	public void dealInitialCards() {
		clearHands();
		
		// dealer
		dealer.hit(pile.dealCard());
		dealer.hit(pile.dealCard());
		
		// players
		for (int i = 0; i < gamePlayers.size(); i++) {
			// deals two cards to each player
			gamePlayers.get(i).hit(pile.dealCard());
			gamePlayers.get(i).hit(pile.dealCard());
			gamePlayers.get(i).checkIfSplitable();
		}
		
		for (int i = 0; i < gamePlayers.size(); i++) {
			if (gamePlayers.get(i).getHand().calcHandValue() == 21) {
				roundResult = RoundResult.RR_PLAYER_BLACKJACK;
				roundState = RoundState.RS_ROUND_END;
			}
		}
		
		if (dealer.getHand().getCard(0).getRank() == Card.ACE) {
			enableInsurance = true;
		}
		
	}
	
	public void goToPlayersTurn() {
		roundState = RoundState.RS_PLAYERS_TURN;
	}
	
	public void goToBetting() {
		roundState = RoundState.RS_BET;
	}
	
	public void goToEndRound() {
		roundState = RoundState.RS_ROUND_END;
	}
	
	public RoundState getRoundState() {
		return roundState;
	}
	
	public void standCurrentPlayer() {
		if (getCurrentPlayer().isHandSplit() && getCurrentPlayer().getCurrentHand() == 0) {
			getCurrentPlayer().nextHand();
		}
		else {
			if ( (currentPlayer+1) >= numOfPlayers ) {
				roundState = RoundState.RS_DEALER_TURN;
				currentPlayer = 0;
			}
			else {
				currentPlayer++;
			}
		}
	}
	
	public Card hitCurrentPlayer() {
		Card card = pile.dealCard();
		getCurrentPlayer().hit(card);
		return card;
	}
	
	public void execDealersTurn() {
		while (dealer.getHand().calcHandValue() < 17) {
			dealer.hit(pile.dealCard());
		}
	}
	
	public Player getCurrentPlayer() {
		return (Player) gamePlayers.get(currentPlayer);
	}
	
	public Person getDealer() {
		return dealer;
	}
	
	public Player getPlayer(int iIndex) {
		return (Player) gamePlayers.get(iIndex);
	}
	
	public void clearHands() {
		dealer.clearHand();
		
		for (int i = 0; i < gamePlayers.size(); i++) {
			gamePlayers.get(i).clearHand();
		}
	}
	
	public void endRound() {		
		// check who won
		for (int i = 0; i < numOfPlayers; i++) {
			
			Player player = (Player) gamePlayers.get(i);

			int playerHandValue = player.getHand().calcHandValue();
			int dealerHandValue = dealer.getHand().calcHandValue();
			
			if (roundResult == RoundResult.RR_PLAYER_BLACKJACK) {
				player.addCash(player.getLastBetValue() * 3/2);
				return;
			}
			
			if (playerHandValue <= 21) {
				// player's hand is valid
				if (dealerHandValue <= 21) {
					
					if (player.isInsuranced()) {
						if (dealerHandValue == 21 && dealer.getHand().getNumCards() == 2) {
							player.addCash(player.getLastBetValue());
							paidInsurance = true;
						}
					}
					
					// dealer's hand is valid
					if (playerHandValue > dealer.getHand().calcHandValue()) {
						roundResult = RoundResult.RR_PLAYER_WIN;
						player.addCash(player.getLastBetValue());
					}
					if (playerHandValue == dealer.getHand().calcHandValue()) {
						roundResult = RoundResult.RR_DRAW;
					}
					if (playerHandValue < dealer.getHand().calcHandValue()) {
						roundResult = RoundResult.RR_PLAYER_LOSE;
						player.spendCash(player.getLastBetValue());
					}
				}
				else {
					roundResult = RoundResult.RR_PLAYER_WIN;
					player.addCash(player.getLastBetValue());
				}
			}
			else {
				// player's hand more than 21
				roundResult = RoundResult.RR_PLAYER_LOSE;
				player.spendCash(player.getLastBetValue());
			}
			
			if (player.isHandSplit()) {
				player.nextHand();
				
				playerHandValue = player.getHand().calcHandValue();
				
				if (playerHandValue <= 21) {
					// player's hand is valid
					if (dealerHandValue <= 21) {
						
						if (player.isInsuranced()) {
							if (dealerHandValue == 21 && dealer.getHand().getNumCards() == 2) {
								player.addCash(player.getLastBetValue());
								paidInsurance = true;
							}
						}
						
						// dealer's hand is valid
						if (playerHandValue > dealer.getHand().calcHandValue()) {
							roundResult = RoundResult.RR_PLAYER_WIN;
							player.addCash(player.getLastBetValue());
						}
						if (playerHandValue == dealer.getHand().calcHandValue()) {
							roundResult = RoundResult.RR_DRAW;
						}
						if (playerHandValue < dealer.getHand().calcHandValue()) {
							roundResult = RoundResult.RR_PLAYER_LOSE;
							player.spendCash(player.getLastBetValue());
						}
					}
					else {
						roundResult = RoundResult.RR_PLAYER_WIN;
						player.addCash(player.getLastBetValue());
					}
				}
				else {
					// player's hand more than 21
					roundResult = RoundResult.RR_PLAYER_LOSE;
					player.spendCash(player.getLastBetValue());
				}
			}
		}
	}

	/**
	 * @return the numOfPlayers
	 */
	public int getNumOfPlayers() {
		return numOfPlayers;
	}
	
	public int getNumOfDecks() {
		return pile.getNumberOfDecks(); 
	}
	
	public boolean getEnableInsurance() {
		return this.enableInsurance;
	}
	
	public void resetInsurance() {
		enableInsurance = false;
	}
	
	public RoundResult getRoundResult() {
		return this.roundResult;
	}
	
	public boolean wasInsurancePaid() {
		return paidInsurance;
	}
	
	public boolean release() {
		pile = null;
		clearHands();
		
		gamePlayers.clear();
		dealer = null;
		
		return true;
	}
	
}