package plp.project02.game;

import plp.project02.game.rules.Rules;

public class Card {
	private int suit;
	private int rank;
	private int value;

	public final static int CLUBS = 1;
	public final static int DIAMONDS = 2;
	public final static int HEARTS = 3;
	public final static int SPADES = 4;

	public final static int ACE = 1;
	public final static int DEUCE = 2;
	public final static int THREE = 3;
	public final static int FOUR = 4;
	public final static int FIVE = 5;
	public final static int SIX = 6;
	public final static int SEVEN = 7;
	public final static int EIGHT = 8;
	public final static int NINE = 9;
	public final static int TEN = 10;
	public final static int JACK = 11;
	public final static int QUEEN = 12;
	public final static int KING = 13;

	public Card(int rank, int suit) {
		this.rank = rank;
		this.suit = suit;
		this.value = Rules.setCardValue(rank);
	}

	public static boolean isValidRank(int rank) {
		return (rank >= Card.ACE) && (rank <= Card.KING);
	}

	public static boolean isValidSuit(int suit) {
		return (suit >= Card.CLUBS) && (suit <= Card.SPADES);
	}

	public int getRank() {
		return rank;
	}

	public int getSuit() {
		return suit;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public String toString() {
		String auxrank = "";
		String auxsuit = "";

		switch (rank) {
		case 1:
			auxrank = "ACE";
			break;
		case 2:
			auxrank = "DEUCE";
			break;
		case 3:
			auxrank = "THREE";
			break;
		case 4:
			auxrank = "FOUR";
			break;
		case 5:
			auxrank = "FIVE";
			break;
		case 6:
			auxrank = "SIX";
			break;
		case 7:
			auxrank = "SEVEN";
			break;
		case 8:
			auxrank = "EIGHT";
			break;
		case 9:
			auxrank = "NINE";
			break;
		case 10:
			auxrank = "TEN";
			break;
		case 11:
			auxrank = "JACK";
			break;
		case 12:
			auxrank = "QUEEN";
			break;
		case 13:
			auxrank = "KING";
			break;
		}

		switch (suit) {
		case 1:
			auxsuit = "CLUBS";
			break;
		case 2:
			auxsuit = "DIAMONDS";
			break;
		case 3:
			auxsuit = "HEARTS";
			break;
		case 4:
			auxsuit = "SPADES";
			break;
		}

		return "Rank = " + auxrank + "\t" + "Suit = " + auxsuit;
	}
}
