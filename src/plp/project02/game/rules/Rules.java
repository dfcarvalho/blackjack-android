package plp.project02.game.rules;

import plp.project02.game.Card;

public class Rules {

	private static int minBetValue = 10;
	private static int maxBetValue = 100;

	public Rules() {
		
	}

	public static int setCardValue(int rank) {
		if (rank <= Card.TEN) { // Specific blackjack rule
			return rank;
		} else if (rank <= Card.KING) {
			return 10; // Face cards
		} else {
			return 0; // TODO throw exception
		}
	}

	public static int calcHandValue(int totalHandValue, int numberOfAces) {
		if (numberOfAces > 0) { // will never be less than zero. but is
			if (totalHandValue <= 11) {
				totalHandValue += 10; // one of the aces will have a value of 11
			}
		}
		return totalHandValue;
	}

	public static void setMinBetValue(int bet) {
		Rules.minBetValue = bet;
	}

	public static int getMinBetValue() {
		return minBetValue;
	}

	public static void setMaxBetValue(int bet) {
		Rules.maxBetValue = bet;
	}

	public static int getMaxBetValue() {
		return maxBetValue;
	}

	public static boolean isValidBetValue(float f) {
		if (f >= minBetValue && f <= maxBetValue) {
			return true;
		} else {
			return false;
		}
	}

}
