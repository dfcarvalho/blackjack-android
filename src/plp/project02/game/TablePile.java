package plp.project02.game;

import java.util.ArrayList;
import java.util.Random;

public class TablePile {

	private int numberOfDecks;

	private ArrayList<Deck> pile = new ArrayList<Deck>();

	public TablePile(int numberOfDecks) {
		this.numberOfDecks = numberOfDecks;
		
		for (int i = 0; i < this.numberOfDecks; i++) {
			Deck d = new Deck();
			pile.add(d);
		}
	}
	
	public Card dealCard() {
		int r = new Random().nextInt(numberOfDecks);
		Card card = pile.get(r).pickCard();
		
		return card;
	}
	
	public int getNumberOfDecks() {
		return numberOfDecks;
	}
}