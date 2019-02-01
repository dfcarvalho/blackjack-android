package plp.project02.engine;

public class GameThread implements Object, Runnable {
	
	/**
	 * Reference to the main game Engine
	 */
	private Engine engine;
	
	/**
	 * Reference to the game thread
	 */
	Thread gameThread;
	
	/**
	 * Indicates whether the thread should run or not
	 */
	private volatile boolean running;
	
	/**
	 * Construtor
	 * @param engine - reference to Engine
	 */
	public GameThread(Engine engine) {
		this.engine = engine;
	}
	
	public boolean release() {
		return false;
	}
	
	/**
	 * Resumes/Starts the thread
	 */
	public void resume() {
		running = true;
		
		gameThread = new Thread(this);
		gameThread.setName("GameThread");
		gameThread.start();
	}

	/**
	 * Game Loop
	 */
	public void run() {
		while (running) {
			// TODO: update soundManager
			
			Scene scene = engine.getCurrentScene();
			
			if (scene != null) {
				scene.exec();
			}
		}
	}
	
	/**
	 * Pauses the game thread
	 */
	public void pause() {
		running = false;
		
		try {
			gameThread.join();
		}
		catch (InterruptedException e){
			// do nothing
		}
	}
}
