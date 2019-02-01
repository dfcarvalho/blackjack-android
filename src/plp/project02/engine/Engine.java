package plp.project02.engine;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.util.ArrayList;
import plp.project02.engine.exceptions.ComponentInitException;

public class Engine implements Object {
	
	/**
	 * Reference to singleton object
	 */
	private static Engine singleton = null;
	
	/**
	 * Indicates whether it was released the previous activity exit
	 */
	private static boolean ended;
	
	/**
	 * Reference to the main game thread (game loop)
	 */
	private GameThread gameThread;
	
	/**
	 * Object used to make the singleton thread-safe
	 */
	private static java.lang.Object syncObject = new java.lang.Object();
	
	/**
	 * Reference to the main Android Activity
	 * public - to avoid get method's overhead 
	 */
	public final Activity mainActivity;
	
	/**
	 * Reference to VideoManager
	 * public - to avoid get method's overhead
	 */
	public VideoManager videoManager = null;
	
	// TODO: Reference to AudioManager
	
	/**
	 * Reference to InputManager
	 * public - to avoid get method's overhead
	 */
	public InputManager inputManager;
	
	/**
	 * Reference to the assets (for file access - Android) 
	 */
	private AssetManager assetsManager;
	
	/**
	 * List of scenes
	 */
	private ArrayList<Scene> scenes;
	
	/**
	 * Reference to current scene
	 */
	private Scene currentScene = null;
	
	/**
	 * Construtor
	 * @param mainActivity - reference to the Android Activity the game is running in
	 */
	private Engine(Context mainActivity) {
		this.mainActivity = (Activity) mainActivity;
		Engine.ended = true;
	}
	
	/**
	 * Returns the singleton instance to this class 
	 * @param mainActivity - reference to the Android Activity the game is running in
	 */
	public static Engine getInstance(Context mainActivity) {
		if (singleton == null) {
			synchronized (syncObject) {
				singleton = new Engine(mainActivity);
			}
		}
		return singleton;
	}
	
	/**
	 * Initializes engine components (Video, Audio, Input, etc...)
	 */
	public void init() {
		try {
			ended = false;
			
			gameThread = new GameThread(this);
			
			assetsManager = mainActivity.getAssets();
			
			// initialize video
			videoManager = VideoManager.getInstance(this);
			videoManager.init();
			
			// TODO: initialize audio
			
			// initialize input
			inputManager = InputManager.getInstance(this);
			inputManager.init();
		
			scenes = new ArrayList<Scene>();
		}
		catch (ComponentInitException e) {
			Log.d("ComponentInitException", e.getErrorMsg());
		}
	}
	
	/**
	 * Free everything the game loaded 
	 */
	public boolean release() {
		gameThread.pause();
		gameThread = null;
		
		if (currentScene != null) {
			currentScene.release();
			currentScene = null;
		}
		
		videoManager.release();
		videoManager = null;
		
		inputManager.release();
		inputManager = null;
		
		// TODO: release AudioManager
		
		Engine.singleton = null;
		Engine.ended = true;
			
		return true;	
	}
	
	/**
	 * Resumes the paused app. Also called at the start of the app.
	 */
	public void resume() {
		gameThread.resume();
		videoManager.resume();
	}
    
	/**
	 * Pauses the app.
	 */
	public void pause() {
		gameThread.pause();
		videoManager.pause();
		
		// the input thread is paused by Android
    }
	
	/**
	 * Add a new scene to the list
	 * @param newScene
	 */
	public void addScene(Scene newScene) {
		scenes.add(newScene);
	}
	
	/**
	 * Loads a scene from the list and starts it
	 * @param id - scene id
	 */
	public void setCurrentScene(int id) {
		if (id < scenes.size()) {
			Scene scene = scenes.get(id);
			
			if (currentScene != null) {
				currentScene.release();
				currentScene = null;
			}
			
			currentScene = scene;
			
			currentScene.init();
			
			// TODO: reset timeManager
		}
	}
	
	/**
	 * Returns the scene that's currently running
	 */
	public Scene getCurrentScene() {
		return currentScene;
	}
    
	/**
	 * Returns reference to assets 
	 */
    public AssetManager getAssetManager() {
    	return this.assetsManager;
    }
    
    public static boolean getEnded() {
    	return ended;
    }
}