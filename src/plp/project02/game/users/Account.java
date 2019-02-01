package plp.project02.game.users;

public class Account {

	private int cash;

	public Account(int initialCash) {
		this.cash = initialCash;
	}

	public int getCash() {
		return cash;
	}

	public void removeCash(int cash) {
		this.cash -= cash;
	}

	public void addCash(int cash) {
		this.cash += cash;
	}

	public boolean isEmpty() {
		return (cash == 0 ? true : false);
	}
}
