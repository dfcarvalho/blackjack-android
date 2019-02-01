package plp.project02.game;

import java.util.ArrayList;
import plp.project02.game.rules.Rules;

public class Hand {

	private int totalHandValue;

	private ArrayList<Card> cards = new ArrayList<Card>();

	public Hand() {
		totalHandValue = 0;
		cards.clear();
	}

	public void addCard(Card c) {
		cards.add(c);
	}

	public void clear() {
		cards.clear();
	}

	public int calcHandValue() {
		int numberOfAces = 0;
		totalHandValue = 0;
		for (int i = 0; i < cards.size(); i++) {
			totalHandValue += cards.get(i).getValue();
			if (cards.get(i).getRank() == Card.ACE) {
				numberOfAces++;
			}
		}
		return Rules.calcHandValue(totalHandValue, numberOfAces);
	}
	
	public int getNumCards() {
		return cards.size();
	}
	
	public Card getCard(int i) {
		return cards.get(i);
	}

}
