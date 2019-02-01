package plp.project02.game.users;

import plp.project02.game.Card;
import plp.project02.game.Hand;

public class Player extends Person {
	
	private String name;

	private Account wallet;
	
	private Hand splitHand;

	private int lastBetValue;
	
	private boolean insuranced;
	
	private int insuranceValue;
	
	private boolean splitableHand;
	
	private int currentHand;

	public Player(String name, int initialCash) {
		this.name = name;
		this.wallet = new Account(initialCash);
		splitHand = null;
		insuranced = false;
		insuranceValue = 0;
		splitableHand = false;
		currentHand = 0;
	}

	public int getCash() {
		return wallet.getCash();
	}

	public void addCash(int cash) {
		wallet.addCash(cash);
	}

	public void spendCash(int cash) {
		wallet.removeCash(cash);
	}
	
	@Override
	public void hit(Card c) {
		if (currentHand == 0) {
			h.addCard(c);
		}
		if (currentHand == 1) {
			splitHand.addCard(c);
		}
	}

	public void split() {
		if (splitableHand) {
			splitHand = new Hand();
			
			Card card1 = h.getCard(0);
			Card card2 = h.getCard(1);
			
			h.clear();
			
			h.addCard(card1);
			splitHand.addCard(card2);
			
			splitableHand = false;
		}
	}

	public void insurance() {
		insuranced = true;
		insuranceValue = lastBetValue/2;
		spendCash(insuranceValue);
	}
	
	@Override
	public Hand getHand() {
		if (currentHand == 0) {
			return this.h;
		}
		if (currentHand == 1) {
			return this.splitHand;
		}
		
		return null;
	}
	
	@Override
	public void clearHand() {
		super.clearHand();
		
		if (splitHand != null) {
			splitHand.clear();
			splitHand = null;
			currentHand = 0;
		}
		
		insuranced = false;
		insuranceValue = 0;
	}

	public void setLastBetValue(int betValue) {
		this.lastBetValue = betValue;
	}

	public int getLastBetValue() {
		return lastBetValue;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public int getMoneyLeft() {
		return wallet.getCash();
	}
	
	public boolean isInsuranced() {
		return this.insuranced;
	}
	
	public int getInsuranceValue() {
		return this.insuranceValue;
	}
	
	public boolean isHandSplitable() {
		return this.splitableHand;
	}
	
	public int getCurrentHand() {
		return this.currentHand;
	}
	
	public boolean isHandSplit() {
		return splitHand != null;
	}
	
	public void nextHand() {
		if (splitHand != null) {
			currentHand = 1;
		}
	}
	
	public void checkIfSplitable() {
		Card card1 = h.getCard(0);
		Card card2 = h.getCard(1);
		
		if (card1.getRank() == card2.getRank()) {
			splitableHand = true;
		}
		else {
			splitableHand = false;
		}
	}
	
	public void resetSplit() {
		splitableHand = false;
	}

}
