package plp.project02.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;
import plp.project02.engine.Engine;

public class Main extends Activity {
	
	private static Engine engine;
	private PowerManager.WakeLock wakeLock;
	
	private static final int DIALOG_ID_EXIT = 1;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        // stop screen from dimming down
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wakeLock.acquire();
        
        // remove title and notifications bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        engine = Engine.getInstance(this);
        engine.init();
        
        engine.addScene(new GameScene(engine));
		engine.setCurrentScene(0);
        
        setContentView(engine.videoManager);
    }
    
    /**
     * Called when activity is paused and when it is stopped
     */
    public void onPause() {
    	engine.pause();
    	wakeLock.release();
    	super.onPause();
    	
    	if (isFinishing()) {
    		engine.release();
    	}
    }
    
    /**
     * Called when activity is resumed and when it is created
     */
    public void onResume() {
    	super.onResume();
    	
    	wakeLock.acquire();
    	
    	engine = Engine.getInstance(this);
    	if (Engine.getEnded()) {
    		engine.init();
    	}
    	engine.resume();
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		showDialog(DIALOG_ID_EXIT);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch(id) {
		case DIALOG_ID_EXIT:
			builder.setMessage("Are you sure you want to exit?")
			       .setCancelable(false)
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			        	   Main.this.finish();
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.dismiss();
			           }
			       });
			break;
		}
		
		dialog = builder.create();

		return dialog;
	}
}
