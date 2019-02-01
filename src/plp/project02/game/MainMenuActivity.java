package plp.project02.game;

import plp.project02.R;
import plp.project02.game.users.Player;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainMenuActivity extends Activity implements OnClickListener {
	
	public static int numOfPlayers = 1;
	public static int minBet = 100;
	public static int maxBet = 1000;
	public static int numDecks = 1;
	
	private static final int DIALOG_NUM_PLAYERS = 1;
	private static final int DIALOG_MIN_BET = 2;
	private static final int DIALOG_MAX_BET = 3;
	private static final int DIALOG_NUM_DECKS = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
	}

	public void onClick(View arg0) {
		Button button = (Button) findViewById(R.id.buttonOK);
		EditText editName = (EditText) findViewById(R.id.playersNameEdit);
		
		if (arg0 == button) {
			try {
				EditText numPlayersEdit = (EditText) findViewById(R.id.numOfPlayersEdit);
				MainMenuActivity.numOfPlayers = Integer.parseInt(numPlayersEdit.getText().toString());
				
				if (MainMenuActivity.numOfPlayers < 1 || MainMenuActivity.numOfPlayers > 5) {
					throw new NumberFormatException();
				}
			}
			catch (NumberFormatException e) {
				showDialog(DIALOG_NUM_PLAYERS);
				return;
			}
			
			try {
				EditText minBetEdit = (EditText) findViewById(R.id.minBetEdit);
				MainMenuActivity.minBet = Integer.parseInt(minBetEdit.getText().toString());
				
				if (MainMenuActivity.minBet > 10000) {
					throw new NumberFormatException();
				}
			}
			catch (NumberFormatException e) {
				showDialog(DIALOG_MIN_BET);
				return;
			}
			
			try {
				EditText maxBetEdit = (EditText) findViewById(R.id.maxBetEdit);
				MainMenuActivity.maxBet = Integer.parseInt(maxBetEdit.getText().toString());
				
				if (MainMenuActivity.maxBet <= MainMenuActivity.minBet) {
					throw new NumberFormatException();
				}
			}
			catch (NumberFormatException e) {
				showDialog(DIALOG_MAX_BET);
				return;
			}
			
			try {
				EditText numDecksEdit = (EditText) findViewById(R.id.numDecksEdit);
				MainMenuActivity.numDecks = Integer.parseInt(numDecksEdit.getText().toString());
				
				if (MainMenuActivity.numDecks < 0) {
					throw new NumberFormatException();
				}
			}
			catch (NumberFormatException e) {
				showDialog(DIALOG_NUM_DECKS);
				return;
			}
			
			// TODO: add more players
			Round.addPlayer(new Player(editName.getText().toString(), 10000));
			Round.numDecks = MainMenuActivity.numDecks;
			startActivity(new Intent(this, Main.class));
		}
		
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		EditText numPlayersEdit = (EditText) findViewById(R.id.numOfPlayersEdit);
		Integer numPlayers = MainMenuActivity.numOfPlayers;
		numPlayersEdit.setText(numPlayers.toString());
		
		EditText minBetEdit = (EditText) findViewById(R.id.minBetEdit);
		Integer minBetInteger = MainMenuActivity.minBet;
		minBetEdit.setText(minBetInteger.toString());
		
		EditText maxBetEdit = (EditText) findViewById(R.id.maxBetEdit);
		Integer maxBetInteger = MainMenuActivity.maxBet;
		maxBetEdit.setText(maxBetInteger.toString());
		
		EditText numDecksEdit = (EditText) findViewById(R.id.numDecksEdit);
		Integer numDecksInteger = MainMenuActivity.numDecks;
		numDecksEdit.setText(numDecksInteger.toString());
		
		Button button = (Button) findViewById(R.id.buttonOK);
		button.setOnClickListener(MainMenuActivity.this);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch(id) {
		case DIALOG_NUM_PLAYERS:
			builder.setMessage("The number of players is invalid. Please enter a number from 1 to 5.")
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			           }
			       });
			break;
		case DIALOG_MIN_BET:
			builder.setMessage("The value for the minimum bet is invalid. Please enter a number from $1 to $10,000.")
			       .setCancelable(false)
			       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			           }
			       });
			break;
		case DIALOG_MAX_BET:
			builder.setMessage("The value for the maximum bet is invalid. Please enter a number from $1 to $10,000. The value must be higher than the minimum bet.")
		       .setCancelable(false)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           }
		       });
			break;
		case DIALOG_NUM_DECKS:
			builder.setMessage("The value for the number of decks is invalid. Please enter a number more than 1.")
					.setCancelable(false)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					break;
		}
		
		dialog = builder.create();

		return dialog;
	}
	
	
	
}