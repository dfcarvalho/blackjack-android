package plp.project02.game;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
	private ArrayList<Card> newDeck = new ArrayList<Card>();

	// Constructor
	public Deck() {
		for (int suit = Card.CLUBS; suit <= Card.SPADES; suit++) {
			for (int rank = Card.ACE; rank <= Card.KING; rank++) {
				newDeck.add(new Card(rank, suit));
			}
		}
	}

	public int getRemainingCards() {
		return newDeck.size();
	}

	public Card pickCard() {
		if (getRemainingCards() <= 0) {
			return null;
		}
		int r = new Random().nextInt(getRemainingCards());
		Card aux = newDeck.get(r); 
		newDeck.remove(r);
		return aux;
	}
}