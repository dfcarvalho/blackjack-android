package plp.project02.engine;

import java.util.ArrayList;
import java.util.List;

import plp.project02.util.InstancePool;
import plp.project02.util.InstancePool.PoolObjectFactory;
import android.view.MotionEvent;
import android.view.View;

public class InputManager implements View.OnKeyListener, View.OnTouchListener, Object {
	
	/**
	 * Singleton object
	 */
	private static InputManager singleton = null;
	
	/**
	 * Reference to Engine
	 */
	private Engine engine;
	
	/**
	 * Class that stores key press and release events
	 */
	public class KeyEvent {
		// constants - avoid using enums on Android
		public static final int KEY_DOWN = 0;
		public static final int KEY_UP = 1;
		
		/**
		 * type of the key event (KEY_DOWN or KEY_UP)
		 */
		public int type;
		
		/**
		 * Code of the key being pressed or released
		 */
		public int keyCode;
		
		/**
		 * Unicode character of the key
		 */
		public char keyChar;
	}
	
	/**
	 * Class that stores touch events
	 */
	public class TouchEvent {
		// constants - avoid using enum on Android
		public static final int TOUCH_DOWN = 0;
		public static final int TOUCH_UP = 1;
		public static final int TOUCH_DRAG = 2;
		
		/**
		 * Type of touch event
		 */
		public int type;
		
		/**
		 * Coordinates of the touch event
		 */
		public int x, y;
		
		/**
		 * The number of fingers touching the screen (this version does not support multi-touch) 
		 */
		public int pointer;
	}
	
	/**
	 * Array storing the current state of each key
	 */
	private boolean[] keysState = new boolean[128];
	
	/**
	 * Pool to recycle KeyEvent objects
	 */
	private InstancePool<KeyEvent> keyEventPool;
	
	/**
	 * List of key events received by the Android UI Thread
	 */
	private List<KeyEvent> keyEventsBuffer = new ArrayList<KeyEvent>();
	
	/**
	 * List of key events to be sent to the main thread to be processed by the app
	 */
	private List<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
	
	/**
	 * Whether the screen is being touched at the moment
	 */
	private boolean isTouched;
	/**
	 * Coordinates of the last touch
	 */
	private int touchX;
	private int touchY;
	
	/**
	 * Pool to recycle TouchEvent objects
	 */
	private InstancePool<TouchEvent> touchEventPool;
	
	/**
	 * List of touch events received by the Android UI Thread
	 */
	private List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
	
	/**
	 * List of touch events to be sent to the main thread to be processed by the app
	 */
	private List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	
	/**
	 * Used transform the coordinates from the size of the screen
	 */
	private float scaleX;
	private float scaleY;
	
	/**
	 * Private constructor.
	 * @param engine - handle to Engine.
	 */
	private InputManager(Engine engine) {
		this.engine = engine;
	}

	/**
	 * Returns the singleton instance of the class. 
	 * @param engine - handle to Engine.
	 * @return - singleton instance of the class.
	 */
	public static InputManager getInstance(Engine engine) {
		if (singleton == null) {
			singleton = new InputManager(engine);
		}
		return singleton;
	}
	
	/**
	 * Initializes the InputManager
	 */
	public void init() {
		// set scale values
		scaleX = engine.videoManager.getScaleX();
		scaleY = engine.videoManager.getScaleY();
		
		// create KeyEvent object pool
		PoolObjectFactory<KeyEvent> factoryKeyEvents = new PoolObjectFactory<KeyEvent>() {
			public KeyEvent createObject() {
				return new KeyEvent();
			}
		};
		
		keyEventPool = new InstancePool<KeyEvent>(factoryKeyEvents, 50);
		engine.videoManager.setFocusableInTouchMode(true);
		engine.videoManager.requestFocus();
		engine.videoManager.setOnKeyListener(this);
		
		// create TouchEvent object pool
		PoolObjectFactory<TouchEvent> factoryTouchEvents = new PoolObjectFactory<TouchEvent>() {
			public TouchEvent createObject() {
				return new TouchEvent();
			}
		};
		
		touchEventPool = new InstancePool<TouchEvent>(factoryTouchEvents, 100);
		engine.videoManager.setOnTouchListener(this);
	}
	
	/**
	 * Releases the InputManager from the memory
	 */
	public boolean release() {
		keyEventPool.release();
		keyEventPool = null;
		
		keyEventsBuffer.clear();
		keyEventsBuffer = null;
		
		keyEvents.clear();
		keyEvents = null;
		
		touchEventPool.release();
		touchEventPool = null;
		
		touchEventsBuffer.clear();
		touchEventsBuffer = null;
		
		touchEvents.clear();
		touchEvents = null;
		
		engine = null;
		InputManager.singleton = null;
		
		return true;
	}
	
	/**
	 * Check if a key is currently pressed
	 * @param keyCode - code of key to be checked
	 * @return - true if key is pressed, false if key is not pressed
	 */
	public boolean isKeyPressed(int keyCode) {
		if (keyCode < 0 || keyCode > 127) {
			return false;
		}
		return keysState[keyCode];
	}
	
	/**
	 * Returns the list of key events to be processed by the app.
	 * @return - List<KeyEvent> with all the KeyEvents to be processed.
	 */
	public List<KeyEvent> getKeyEvents() {
		synchronized (this) {
			for (int i = 0; i < keyEvents.size(); i++) {
				keyEventPool.free(keyEvents.get(i));
			}
			keyEvents.clear();
			keyEvents.addAll(keyEventsBuffer);
			keyEventsBuffer.clear();
			return keyEvents;
		}
	}
	
	public boolean isTouchDown(int pointer) {
		synchronized (this) {
			if (pointer == 0) {
				return isTouched;
			}
			else {
				return false;
			}
		}
	}
	
	public int getTouchX(int pointer) {
		synchronized (this) {
			return touchX;
		}
	}
	
	public int getTouchY(int pointer) {
		synchronized (this) {
			return touchY;
		}
	}
	
	/**
	 * Returns the list of touch events to be processed by the app.
	 * @return - List<TouchEvent> with all the TouchEvents to be processed.
	 */
	public List<TouchEvent> getTouchEvents() {
		synchronized (this) {
			for (int i = 0; i < touchEvents.size(); i++) {
				touchEventPool.free(touchEvents.get(i));
			}
			touchEvents.clear();
			touchEvents.addAll(touchEventsBuffer);
			touchEventsBuffer.clear();
			return touchEvents;
		}
	}
	
	/**
	 * Called when a key event occurs in the Android UI Thread
	 */
	public boolean onKey(View v, int keyCode, android.view.KeyEvent event) {
		if (event.getAction() == android.view.KeyEvent.ACTION_MULTIPLE) {
			return false;
		}
		
		synchronized (this) {
			KeyEvent keyEvent = keyEventPool.newObject();
			keyEvent.keyCode = keyCode;
			keyEvent.keyChar = (char) event.getUnicodeChar();
			
			if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
				keyEvent.type = KeyEvent.KEY_DOWN;
				if (keyCode > 0 && keyCode < 128) {
					keysState[keyCode] = false;
				}
			}
			if (event.getAction() == android.view.KeyEvent.ACTION_UP) {
				keyEvent.type = KeyEvent.KEY_UP;
				if (keyCode > 0 && keyCode < 128) {
					keysState[keyCode] = true;
				}
			}
			keyEventsBuffer.add(keyEvent);
		}
		
		return false;
	}

	/**
	 * Called when a touch event occurs in the Android UI Thread
	 */
	public boolean onTouch(View v, MotionEvent event) {
		synchronized (this) {
			TouchEvent touchEvent = touchEventPool.newObject();
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchEvent.type = TouchEvent.TOUCH_DOWN;
				isTouched = true;
				break;
			case MotionEvent.ACTION_MOVE:
				touchEvent.type = TouchEvent.TOUCH_DRAG;
				isTouched = true;
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				touchEvent.type = TouchEvent.TOUCH_UP;
				isTouched = false;
				break;
			}
			
			touchEvent.x = touchX = (int) (event.getX() * scaleX);
			touchEvent.y = touchX = (int) (event.getY() * scaleY);
			touchEventsBuffer.add(touchEvent);
			
			return true;
		}
	}
}
