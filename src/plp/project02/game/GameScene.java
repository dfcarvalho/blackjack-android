package plp.project02.game;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import plp.project02.engine.Engine;
import plp.project02.engine.Frame;
import plp.project02.engine.Image;
import plp.project02.engine.Scene;
import plp.project02.engine.Sprite;
import plp.project02.engine.InputManager.TouchEvent;
import plp.project02.engine.exceptions.DrawingException;
import plp.project02.engine.exceptions.OverBetException;
import plp.project02.game.rules.Rules;
import plp.project02.game.users.Player;

public class GameScene extends Scene {
	/**
	 * Reference to the current game round
	 */
	private Round currentRound;
	
	/**
	 * List of the dealer's cards sprites
	 */
	private ArrayList<Sprite> sprListDealersHand;
	// TODO DEBUG: only one player for now
	/**
	 * List of the player's cards sprites
	 */
	private ArrayList<Sprite> sprListPlayersHand;
	
	/**
	 * List of the player's split hand's cards sprites
	 */
	private ArrayList<Sprite> sprListPlayersSplitHand;
	
	/**
	 * List of touch events on this game frame
	 */
	private List<TouchEvent> touchEvents;
	
	/**
	 * Sprites of the buttons
	 */
	private Sprite sprButtonHit;
	private Sprite sprButtonStand;
	private Sprite sprButtonBetInc;
	private Sprite sprButtonBetDec;
	private Sprite sprButtonBetPlace;
	private Sprite sprButtonInsurance;
	private Sprite sprButtonSplit;
	
	private int currentBetValue;
	
	/**
	 * Background image
	 */
	private Image bgTable;
	
	/**
	 * Indicates whether to show the win or lose message
	 */
	private boolean showWinOrLose;
	
	/**
	 * Indicates whether to show the players hand value
	 */
	private boolean showPlayersHandValue;
	
	private boolean lostFirstHand;
	
	/**
	 * Is the player out of cash?
	 */
	private boolean gameOver;
	
	/**
	 * Coordinates of UI elements 
	 */
	private final int CARD_WIDTH = 	64;
	private final int CARD_HEIGHT = 93;
	private final int POS_X_PLAYER_CARD = 590;
	private final int POS_Y_PLAYER_CARD = 500;
	private final int POS_X_PLAYER_CARD2 = 800;
	private final int POS_Y_PLAYER_CARD2 = 500;
	private final int POS_X_PLAYER_NAME = 50; 
	private final int POS_Y_PLAYER_NAME = 80;
	private final int POS_X_PLAYER_HAND_VALUE = 610;
	private final int POS_Y_PLAYER_HAND_VALUE = 630;
	private final int POS_X_DEALER_CARD = 590;
	private final int POS_Y_DEALER_CARD = 280;
	private final int POS_X_DEALER_HAND_VALUE = 600;
	private final int POS_Y_DEALER_HAND_VALUE = 400;
	private final int POS_X_BET = 600;
	private final int POS_Y_BET = 450;
	private final int POS_X_MONEY = 1100;
	private final int POS_Y_MONEY = 80;
	private final int POS_X_INSURANCE = 900;
	private final int POS_Y_INSURANCE = 300;
	private final int POS_X_WIN_OR_LOSE = 200;
	private final int POS_Y_WIN_OR_LOSE = 380;
	private final int POS_X_GAMEOVER = 50;
	private final int POS_Y_GAMEOVER = 650;
	
	/**
	 * Constructor
	 * @param engine
	 */
	public GameScene(Engine engine) {
		super(engine);
	}

	@Override
	public void init() {
		Rules.setMinBetValue(MainMenuActivity.minBet);
		Rules.setMaxBetValue(MainMenuActivity.maxBet);
		
		currentBetValue = Rules.getMinBetValue();
		
		sprListDealersHand = new ArrayList<Sprite>();
		sprListPlayersHand = new ArrayList<Sprite>();
		sprListPlayersSplitHand = null;
		
		// create button sprites
		sprButtonHit = createSprite("images/button_hit.png", 0, null);
		if (sprButtonHit != null) {
			sprButtonHit.setPos(new Frame(40, 640, 0, 0));
			sprButtonHit.setVisible(false);
		}
		sprButtonStand = createSprite("images/button_stand.png", 0, null);
		if (sprButtonStand != null) {
			sprButtonStand.setPos(new Frame(230, 640, 0, 0));
			sprButtonStand.setVisible(false);
		}
		sprButtonBetInc = createSprite("images/button_bet_inc.png", 0, null);
		if (sprButtonBetInc != null) {
			sprButtonBetInc.setPos(new Frame(420, 640, 0, 0));
			sprButtonBetInc.setVisible(false);
		}
		sprButtonBetDec = createSprite("images/button_bet_dec.png", 0, null);
		if (sprButtonBetDec != null) {
			sprButtonBetDec.setPos(new Frame(610, 640, 0, 0));
			sprButtonBetDec.setVisible(false);
		}
		sprButtonBetPlace = createSprite("images/button_bet_place.png", 0, null);
		if (sprButtonBetPlace != null) {
			sprButtonBetPlace.setPos(new Frame(800, 640, 0, 0));
			sprButtonBetPlace.setVisible(false);
		}
		sprButtonInsurance = createSprite("images/button_insurance.png", 0, null);
		if (sprButtonInsurance != null) {
			sprButtonInsurance.setPos(new Frame(800, 650, 0, 0));
			sprButtonInsurance.setVisible(false);
		}
		sprButtonSplit = createSprite("images/button_split.png", 0, null);
		if (sprButtonSplit != null) {
			sprButtonSplit.setPos(new Frame(990, 650, 0, 0));
			sprButtonSplit.setVisible(false);
		}
		
		// Background Table image
		bgTable = addImage("images/bg_image_2.png", null);
		
		// TODO DEBUG: hardcode 1 deck
		currentRound = new Round();
		currentRound.init();
		
		showWinOrLose = false;
		showPlayersHandValue = false;
	}
	
	@Override
	public void exec() {
		touchEvents = engine.inputManager.getTouchEvents();
		
		switch(currentRound.getRoundState()) {
		case RS_ROUND_START:
			onRoundStartExec();
			break;
		case RS_BET:
			onBetExec();
			break;
		case RS_PLAYERS_TURN:
			onPlayersTurnExec();
			break;
		case RS_DEALER_TURN:
			onRoundDealerTurnExec();
			break;
		case RS_ROUND_END:
			onRoundEndExec();
			break;
		}
	}
	
	private void onRoundStartExec() {
		showPlayersHandValue = false;
		lostFirstHand = false;
		
		createInitialCardsSprites();
		
		currentRound.getCurrentPlayer().getHand().calcHandValue();
		
		// animations for start of round:
		// shuffle cards
		// deal cards
		
		sprButtonBetDec.setVisible(true);
		sprButtonBetInc.setVisible(true);
		sprButtonBetPlace.setVisible(true);
		currentRound.goToBetting();
	}
	
	private void onBetExec() {
		int playerCash = currentRound.getCurrentPlayer().getCash();
		int maxBet = Rules.getMaxBetValue();
		int minBet = Rules.getMinBetValue();
		
		if (currentBetValue > playerCash) {
			currentBetValue = playerCash;
		}
		
		for (int i = 0; i < touchEvents.size(); i++) {
			TouchEvent event = touchEvents.get(i);
			Frame pos = new Frame(event.x, event.y, event.x+5, event.y+5);
			
			if (event.type == TouchEvent.TOUCH_DOWN) {
				
				// touch on BET+ button
				if (sprButtonBetInc.getFrame().collisionTest(pos)) {
					try {
						currentBetValue += minBet;
						
						if (currentBetValue > playerCash) {
							throw new OverBetException(OverBetException.BET_MORE_THAN_CASH);
						}
						if (currentBetValue > maxBet) {
							throw new OverBetException(OverBetException.BET_MORE_THAN_MAX_BET);
						}
						
					}
					catch (OverBetException e) {
						switch(e.getErrorCode()) {
						case OverBetException.BET_MORE_THAN_CASH:
							currentBetValue = playerCash;
							break;
						case OverBetException.BET_MORE_THAN_MAX_BET:
							currentBetValue = maxBet;
						}
					}
					
				} // end of BET+
				
				// touch on BET- button
				if (sprButtonBetDec.getFrame().collisionTest(pos)) {
					if (currentBetValue - minBet > minBet) {
						currentBetValue -= minBet;
					}
					else {
						currentBetValue = minBet;
					}
				} // end of BET-
				
				// touch on PLACE button
				if (sprButtonBetPlace.getFrame().collisionTest(pos)) {
					currentRound.getCurrentPlayer().setLastBetValue(currentBetValue);
					
					sprListDealersHand.get(0).setVisible(true);
					sprListPlayersHand.get(0).setVisible(true);
					sprListPlayersHand.get(1).setVisible(true);
					
					sprButtonBetDec.setVisible(false);
					sprButtonBetInc.setVisible(false);
					sprButtonBetPlace.setVisible(false);
					sprButtonHit.setVisible(true);
					sprButtonStand.setVisible(true);
					showPlayersHandValue = true;

					currentRound.goToPlayersTurn();
				}
			} // end of touch down event
		} // end of events loop
	}
	
	private void onPlayersTurnExec() {
		// check if insurance should be enabled
		if (currentRound.getEnableInsurance()) {
			sprButtonInsurance.setVisible(true);
		}
		
		if (currentRound.getCurrentPlayer().isHandSplitable()) {
			sprButtonSplit.setVisible(true);
		}
		else {
			sprButtonSplit.setVisible(false);
		}
		
		for (int i = 0; i < touchEvents.size(); i++) {
			TouchEvent event = touchEvents.get(i);
			Frame pos = new Frame(event.x, event.y, event.x+5, event.y+5);
			
			if (event.type == TouchEvent.TOUCH_DOWN) {
				
				// touch on HIT button
				if (sprButtonHit.getFrame().collisionTest(pos)) {
						Card card = currentRound.hitCurrentPlayer();
						int cardNum = currentRound.getCurrentPlayer().getHand().getNumCards() - 1;
						Sprite cardSprite = createCardSprite(card);
						
						int playersCurrentHand = currentRound.getCurrentPlayer().getCurrentHand();
						
						if (playersCurrentHand == 0) {
							cardSprite.setPos(new Frame(POS_X_PLAYER_CARD+CARD_WIDTH/2*cardNum, POS_Y_PLAYER_CARD, 0, 0));
						}
						if (playersCurrentHand == 1) {
							cardSprite.setPos(new Frame(POS_X_PLAYER_CARD2+CARD_WIDTH/2*cardNum, POS_Y_PLAYER_CARD2, 0, 0));
						}
						
					synchronized (this) {
						sprListPlayersHand.add(cardSprite);
					}
						
					Hand playerHand = currentRound.getCurrentPlayer().getHand();
					
					// check if more than 21
					if (playerHand.calcHandValue() >= 21) {
						if (currentRound.getCurrentPlayer().isHandSplit()) {
							if (playersCurrentHand == 0) {
								currentRound.getCurrentPlayer().nextHand();
								
								if (playerHand.calcHandValue() > 21) {
									lostFirstHand = true;
									
									for (int j = 0; j < sprListPlayersHand.size(); j++) {
										sprListPlayersHand.get(j).setVisible(false);
									}
								}
							}
							if (playersCurrentHand == 1) {
								synchronized (currentRound) {
									currentRound.goToEndRound();
									sprButtonHit.setVisible(false);
									sprButtonStand.setVisible(false);
								}
							}
						}
						else {
							synchronized (currentRound) {
								currentRound.goToEndRound();
								sprButtonHit.setVisible(false);
								sprButtonStand.setVisible(false);
							}
						}
					}
					
					sprButtonInsurance.setVisible(false);
					sprButtonSplit.setVisible(false);
				
				} // end of buttonHit test
				
				// touch on STAND button
				if (sprButtonStand.getFrame().collisionTest(pos)) {
					synchronized(currentRound) {
						int playersCurrentHand = currentRound.getCurrentPlayer().getCurrentHand();
						
						currentRound.standCurrentPlayer();
						
						if (currentRound.getCurrentPlayer().isHandSplit()) {
							if (playersCurrentHand == 1) {
								sprButtonHit.setVisible(false);
								sprButtonStand.setVisible(false);
							}
						}
						else {
							sprButtonHit.setVisible(false);
							sprButtonStand.setVisible(false);
						}
					}
					
					sprButtonInsurance.setVisible(false);
					sprButtonSplit.setVisible(false);
					currentRound.resetInsurance();
					currentRound.getCurrentPlayer().resetSplit();
				}
				
				// touch on SPLIT button
				if (sprButtonSplit.getVisible() && sprButtonSplit.getFrame().collisionTest(pos)) {
					currentRound.getCurrentPlayer().split();
					splitHand();
				}
				
				// touch on insurance button
				if (sprButtonInsurance.getVisible() && sprButtonInsurance.getFrame().collisionTest(pos)) {
					currentRound.getCurrentPlayer().insurance();
					sprButtonInsurance.setVisible(false);
					currentRound.resetInsurance();
				}
			} // end of touch down event
		} // end of events loop
	}
	
	private void onRoundDealerTurnExec() {
		currentRound.execDealersTurn();
		
		Hand dealerHand = currentRound.getDealer().getHand();
		
		// create sprites for dealers cards
		for (int i = 2; i < dealerHand.getNumCards(); i++) {
			Sprite spr = createCardSprite(dealerHand.getCard(i));
			spr.setPos(new Frame(POS_X_DEALER_CARD+CARD_WIDTH/2*i, POS_Y_DEALER_CARD, 0, 0));
			sprListDealersHand.add(spr);
		}
		
		sprListDealersHand.get(1).setVisible(true);
		
		currentRound.goToEndRound();
	}
	
	private void onRoundEndExec() {
		if (!showWinOrLose) {
			currentRound.endRound();
		}
		
		if (currentRound.getCurrentPlayer().getCash() <= 0) {
			gameOver = true;
		}
		
		showWinOrLose = true;
			
		for (int i = 0; i < touchEvents.size(); i++) {
			TouchEvent event = touchEvents.get(i);
			
			if (event.type == TouchEvent.TOUCH_DOWN) {
				showWinOrLose = false;
			}
		}
		
		if (showWinOrLose) return;
		
		if (gameOver) {
			engine.pause();
			engine.mainActivity.startActivity(new Intent(engine.mainActivity, MainMenuActivity.class));
			engine.mainActivity.finish();
		}
		
		synchronized (this) {
			// delete dealers cards sprites
			for (int i = 0; i < sprListDealersHand.size(); i++) {
				deleteSprite(sprListDealersHand.get(i));
				sprListDealersHand.get(i).release();
			}
			sprListDealersHand.clear();
			
			// delete players cards sprites
			for (int i = 0; i < sprListPlayersHand.size(); i++) {
				deleteSprite(sprListPlayersHand.get(i));
				sprListPlayersHand.get(i).release();
			}
			sprListPlayersHand.clear();
			
			currentRound = new Round();
			currentRound.init();
		}
	}
	
	private void createInitialCardsSprites() {
		// create sprites for dealer's initial hand
		Hand dealersHand = currentRound.getDealer().getHand();
		for (int i = 0; i < 2; i++) {
			Sprite cardSprite = createCardSprite(dealersHand.getCard(i));
			cardSprite.setPos(new Frame(POS_X_DEALER_CARD+CARD_WIDTH/2*i, POS_Y_DEALER_CARD, 0, 0));
			cardSprite.setVisible(false);
			
			synchronized (this) {
				sprListDealersHand.add(cardSprite);
			}
		}
		
		// create sprites for players' initial hands
		// TODO DEBUG: only one player for now
		Hand playersHand = currentRound.getCurrentPlayer().getHand();
		for (int i = 0; i < 2; i++) {
			Sprite cardSprite = createCardSprite(playersHand.getCard(i));
			cardSprite.setPos(new Frame(POS_X_PLAYER_CARD+CARD_WIDTH/2*i, POS_Y_PLAYER_CARD, 0, 0));
			cardSprite.setVisible(false);
			synchronized (this) {
				sprListPlayersHand.add(cardSprite);
			}
		}
	}
	
	private void splitHand() {
		sprListPlayersSplitHand = new ArrayList<Sprite>();
		Sprite spr = sprListPlayersHand.get(1);
		spr.setPos(new Frame(POS_X_PLAYER_CARD2, POS_Y_PLAYER_CARD2, 0, 0));
		sprListPlayersSplitHand.add(spr);
		sprListPlayersSplitHand.remove(spr);
	}
	
	private Sprite createCardSprite(Card card) {
		int rank = card.getRank() - 1;
		int suit = card.getSuit() - 1;
		Sprite spr;
		
		synchronized (this) {
			spr = createSprite("images/cards_spritesheet.png", suit*13 + rank, new Frame(0, 0, CARD_WIDTH, CARD_HEIGHT));
		}
		
		return spr;
	}
	
	@Override
	public void draw() {
		// draw background
		try {
			engine.videoManager.drawImage(bgTable, new Frame(0, 0, 0, 0));
		}
		catch (DrawingException e) {
			Log.d("DrawingException", e.getErrorMsg());
		}
		catch (NullPointerException e) {
			Log.d("NullPointerException", e.getMessage());
		}
		
		synchronized (this) {
			// draw scene sprites
			super.draw(); 
			
			Player player = currentRound.getCurrentPlayer();
			Integer playerHandValue = player.getHand().calcHandValue();
			Integer bet = currentBetValue;
			Integer money = player.getCash();
			
			try {
				if (lostFirstHand) {
					engine.videoManager.drawText("You lost the first hand", 30, Color.WHITE, new Frame(POS_X_PLAYER_CARD-200, POS_Y_PLAYER_CARD, 0, 0));
				}
				// draw players name
				engine.videoManager.drawText(player.getName(), 30, Color.WHITE, new Frame(POS_X_PLAYER_NAME, POS_Y_PLAYER_NAME, 0, 0));
				
				if (showPlayersHandValue) {
					// draw player's hand value
					engine.videoManager.drawText(playerHandValue.toString(), 30, Color.WHITE, new Frame(POS_X_PLAYER_HAND_VALUE, POS_Y_PLAYER_HAND_VALUE, 0, 0));
				}
				// draw player's bet
				engine.videoManager.drawText("$" + bet.toString(), 30, Color.WHITE, new Frame(POS_X_BET, POS_Y_BET, 0, 0));
				// draw player's money
				engine.videoManager.drawText("$" + money.toString(), 30, Color.WHITE, new Frame(POS_X_MONEY, POS_Y_MONEY, 0, 0));
				// draw insurance value
				engine.videoManager.drawText("Insurance: $" + player.getInsuranceValue(), 30, Color.WHITE, new Frame(POS_X_INSURANCE, POS_Y_INSURANCE, 0, 0));
			}
			catch (DrawingException e) {
				Log.d("DrawingException", e.getErrorMsg());
			}
			catch (NullPointerException e) {
				Log.d("NullPointerException", e.getMessage());
			}
		
			// draw win or lose message and dealers cards
			try {
				if (showWinOrLose) {
					
					// draw dealers other cards
					for (int i = 1; i < sprListDealersHand.size(); i++) {
						sprListDealersHand.get(i).setVisible(true);
						sprListDealersHand.get(i).draw();
					}
					
					// TODO: draw if insurance was paid
					
					Integer dealerHandValue = currentRound.getDealer().getHand().calcHandValue();
					engine.videoManager.drawText(dealerHandValue.toString(), 30, Color.RED, new Frame(POS_X_DEALER_HAND_VALUE, POS_Y_DEALER_HAND_VALUE, 0, 0));
					
					switch(currentRound.getRoundResult()) {
					case RR_PLAYER_WIN:
						engine.videoManager.drawText("WIN", 60, Color.BLUE, new Frame(POS_X_WIN_OR_LOSE, POS_Y_WIN_OR_LOSE, 0, 0));
						break;
					case RR_PLAYER_LOSE:
						engine.videoManager.drawText("LOSE", 60, Color.BLUE, new Frame(POS_X_WIN_OR_LOSE, POS_Y_WIN_OR_LOSE, 0, 0));
						break;
					case RR_DRAW:
						engine.videoManager.drawText("DRAW", 60, Color.BLUE, new Frame(POS_X_WIN_OR_LOSE, POS_Y_WIN_OR_LOSE, 0, 0));
						break;
					case RR_PLAYER_BLACKJACK:
						showPlayersHandValue = true;
						sprListDealersHand.get(0).setVisible(true);
						sprListPlayersHand.get(0).setVisible(true);
						sprListPlayersHand.get(1).setVisible(true);
						engine.videoManager.drawText("BLACKJACK", 60, Color.BLUE, new Frame(POS_X_WIN_OR_LOSE, POS_Y_WIN_OR_LOSE, 0, 0));
						break;
					case RR_NOT_FINISHED:
						// do nothing
						break;
					}
					
					if (gameOver) {
						engine.videoManager.drawText("You're out of cash. Game Over!", 60, Color.RED, new Frame(POS_X_GAMEOVER, POS_Y_GAMEOVER, 0, 0));
					}
				}
			}
			catch (DrawingException e){
				Log.d("DrawingException", e.getErrorMsg());
			}
		}
	}

	@Override
	public boolean release() {
		currentRound.release();
		currentRound = null;
		
		bgTable = null;
		
		// delete dealers cards sprites
		for (int i = 0; i < sprListDealersHand.size(); i++) {
			deleteSprite(sprListDealersHand.get(i));
			sprListDealersHand.get(i).release();
		}
		sprListDealersHand.clear();
		
		// delete players cards sprites
		for (int i = 0; i < sprListPlayersHand.size(); i++) {
			deleteSprite(sprListPlayersHand.get(i));
			sprListPlayersHand.get(i).release();
		}
		sprListPlayersHand.clear();
		
		if (sprListPlayersSplitHand != null) {
			for (int i = 0; i < sprListPlayersSplitHand.size(); i++) {
				deleteSprite(sprListPlayersSplitHand.get(i));
				sprListPlayersSplitHand.get(i).release();
			}
			sprListPlayersSplitHand.clear();
		}
		
		deleteSprite(sprButtonHit);
		sprButtonHit.release();
		deleteSprite(sprButtonStand);
		sprButtonStand.release();
		deleteSprite(sprButtonBetDec);
		sprButtonBetDec.release();
		deleteSprite(sprButtonBetInc);
		sprButtonBetInc.release();
		deleteSprite(sprButtonBetPlace);
		sprButtonBetPlace.release();
		deleteSprite(sprButtonSplit);
		sprButtonSplit.release();
		
		return super.release();
	}
}