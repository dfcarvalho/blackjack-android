package plp.project02.game.users;

import plp.project02.game.*;

public class Person {
	
	public static final int HIT = 1;
	public static final int STAND = 2;
	
	protected int lastAction;

	protected Hand h;
	
	protected boolean finished;

	public Person() {
		setLastAction(0);
		h = new Hand();
		finished = false;
	}

	public void stand() {
		finished = true;
	}

	public void hit(Card c) {
		h.addCard(c);
	}

	public void clearHand() {
		h.clear();
	}

	public void setLastAction(int lastAction) {
		this.lastAction = lastAction;
	}

	public int getLastAction() {
		return lastAction;
	}
	
	public Hand getHand() {
		return this.h;
	}
	
	public boolean hadFinished() {
		return this.finished;
	}

}
