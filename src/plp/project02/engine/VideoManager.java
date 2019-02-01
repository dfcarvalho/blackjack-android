package plp.project02.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import plp.project02.engine.exceptions.*;

/**
 * Class that manages video and images. Uses a Singleton pattern.
 */
public class VideoManager extends SurfaceView implements Object, Runnable {

	/**
	 * Reference to Engine
	 */
	private Engine engine;
	
	/**
	 * Reference to singleton object
	 */
	private static VideoManager singleton = null;
	
	/**
	 * Object to make the singleton thread-safe
	 */
	private static java.lang.Object syncObject = new java.lang.Object();
	
	/**
	 * Thread that renders the video
	 */
	private Thread renderThread;
	
	/**
	 * Surface holder used by SurfaceView to draw on the renderThread 
	 */
	private SurfaceHolder holder;
	
	/**
	 * Canvas instance used to draw on backbuffer 
	 */
	private Canvas canvas = null;
	
	/**
	 * Bitmap for Backbuffer
	 */
	private Bitmap backbuffer;
	
	/**
	 * Flag to indicate if renderThread should run or stop
	 */
	private volatile boolean running = false;
	
	/**
	 * Frame containing the dimensions of the screen.
	 */
	private Frame screenSize;
	
	/**
	 * Used to scale the images to the size of the screen
	 */
	private float scaleX;
	private float scaleY;
	
	/**
	 * List of images currently loaded in memory.
	 */
	private ArrayList<Image> images;
	
	private long lastTime = 0;
	
	/**
	 * Constructor with parameters. It's private because of the singleton pattern used.
	 */
	private VideoManager(Engine engine) {
		super(engine.mainActivity);
		// get assets handle
		this.engine = engine;
	}

	/**
	 * Returns an instance of this class. Singleton Pattern is used.
	 * @param context
	 * @return CVideoManager singleton object
	 */
	public static VideoManager getInstance(Engine engine) {
		if (singleton == null)
		{
			synchronized (syncObject) {
				singleton = new VideoManager(engine);
			}
		}
		return singleton;
	}
	
	
	/**
	 * Initializes Video component
	 * @throws ComponentInitException - check String returned by getErroMsg()
	 */
	public void init() throws ComponentInitException {
		int screenX = engine.mainActivity.getWindowManager().getDefaultDisplay().getWidth();
		int screenY = engine.mainActivity.getWindowManager().getDefaultDisplay().getHeight();
		
		screenSize = new Frame(0, 0, screenX, screenY);
		
		scaleX = (float) 1280 / screenX;
		scaleY = (float) 800 / screenY;
		
		// get surface holder
		holder = getHolder();

		// create images list
		images = new ArrayList<Image>();
		
		backbuffer = Bitmap.createBitmap(1280, 800, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(backbuffer);
		
		if (images == null || backbuffer == null || canvas == null) {
			throw new ComponentInitException(ComponentInitException.ERROR_VIDEO_INIT);
		}
		
		running = true;
	}

	/**
	 * Releases the class and all images that were loaded by the app.
	 */
	public boolean release() {
		running = false;
		// Free all images from memory
		for (int i = images.size()-1; i >= 0; i--) {
			Image image = images.get(i);
			do {
				image.release();
			}
			while (image.getNumRef() > 0);
			
			images.remove(i);
		}
		
		engine = null;
		// deletes reference and let GC do its job
		VideoManager.singleton = null;
		
		return true;
	}

	/**
	 * Resumes rendering
	 */
	public void resume() {
		running = true;
		renderThread = new Thread(this);
		renderThread.setName("VideoManager");
		renderThread.start();
	}
	
	/**
	 * Run render thread
	 */
	public void run() {
		while(running) {
			// get time in milisecons
			long startTime =  System.nanoTime() / 1000000L;
			long deltaTime = startTime - lastTime;
			
			while (deltaTime < 16) {
				deltaTime = startTime - lastTime;
			}
			
			// check if surface is drawable at the moment
			if (holder.getSurface().isValid() == false) {
				continue;
			}
			
			try {
				engine.getCurrentScene().draw();
			}
			catch (NullPointerException e) {
				// scene not loaded yet
				return;
			}
			
			// start drawing
			Canvas surfaceCanvas = holder.lockCanvas();
			
			Rect rectDest = surfaceCanvas.getClipBounds();
			
			// draw scene
			
			surfaceCanvas.drawBitmap(backbuffer, null, rectDest, null);
			
			// stop drawing and show images on screen
			holder.unlockCanvasAndPost(surfaceCanvas);
		}
		
		lastTime = System.nanoTime() / 1000000L;
	}
	
	/**
	 * Pauses the render thread
	 */
	public void pause() {
		running = false;
		
		try {
			renderThread.join();
		}
		catch (InterruptedException e) {
			// not a problem
		}
	}
	
	/**
	 * Loads an image.
	 * @param imageFilePath - image file path (relative to assets folder)
	 * @return reference to the image loaded, or null if unable to load
	 * @throws ImageNotLoadedException - check String returned by getErrorMSg() for details 
	 */
	public Image loadImage(String imageFilePath) throws ImageNotLoadedException {
		// stores reference to image
		Image image;
		
		// check if image is already loaded
		for (int i = 0; i < images.size(); i++) {
			image = images.get(i);
			
			if (imageFilePath == image.getFilePath()) {
				// if image is found, add numRef and return reference to it
				image.addNumRef();
				return image;
			}
		}
		
		// if image is not yet loaded...
		image = new Image();
		InputStream imageStream;
		
		image.setFilePath(imageFilePath);
		
		try {
			imageStream = engine.getAssetManager().open(imageFilePath);
			image.setBitmap(BitmapFactory.decodeStream(imageStream));
			imageStream.close();
		}
		catch (IOException e) {
			throw new ImageNotLoadedException(imageFilePath, ImageNotLoadedException.ERROR_FILE_NOT_FOUND);
		}
		
		if (image.getBitmap() == null) {
			image.release();
			throw new ImageNotLoadedException(imageFilePath, ImageNotLoadedException.ERROR_BITMAP_NOT_CRATED);
		}

		image.setImageSize(new Frame(0, 0, image.getBitmap().getWidth(), image.getBitmap().getHeight()));
		image.addNumRef();
		
		// add image to images list
		images.add(image);
		
		return image;
	}
	
	/**
	 * Loads image and calculates the frames based on given frame size.
	 * @param imageFilePath - image file path (relative to assets folder). 
	 * @param frameSize - dimensions of the image's frames.
	 * @return - reference to the loaded image.
	 * @throws ImageNotLoadedException - check String returned by getErrorMSg() for details
	 */
	public Image loadImage(String imageFilePath, Frame frameSize) throws ImageNotLoadedException {
		Image image = loadImage(imageFilePath);
		
		if (frameSize != null) {
			image.setFrameSize(frameSize);
		}
		else {
			image.setFrameSize(image.getImageSize());
		}
		return image;
	}
	
	/**
	 * Releases image from memory if not being used anymore. 
	 * @param image - reference to image to be released.
	 */
	public void releaseImage(Image image) {
		// search for image in list
		for (int i = 0; i < images.size(); i++) {
			if (images.get(i) == image) {
				// if image is released...
				if (image.release()) {
					//... remove from list
					images.remove(i);
				}
			}
		}
	}
	/**
	 * Draws and image on the given position
	 * @param image - image to be drawn
	 * @param pos - Frame instance containing the position on screen where the image should be drawn
	 * @throws DrawingException - check String returned by getErrorMSg() for details
	 */
	public void drawImage(Image image, Frame pos) throws DrawingException {
		if (canvas != null && image != null) {
			canvas.drawBitmap(image.getBitmap(), pos.left, pos.top, null);
		}
		else {
			throw new DrawingException(DrawingException.ERROR_IMAGE_NULL);
		}
	}
	
	/**
	 * Draws a frame of an image on the screen.
	 * @param image - image to be drawn
	 * @param iFrame - index of the frame to be drawn
	 * @param pos - Frame instance containing the position on screen where the image should be drawn
	 * @throws DrawingException - check String returned by getErrorMSg() for details
	 */
	public void drawImageFrame(Image image, int iFrame, Frame pos) throws DrawingException {
		Frame frame = image.getFrame(iFrame);
		
		if (frame == null) {
			throw new DrawingException(DrawingException.ERROR_IMAGE_FRAME);
		}
		
		Bitmap b = Bitmap.createBitmap(image.getBitmap(), 
				frame.left, frame.top, frame.getWidth(), frame.getHeight());
		
		if (b == null) {
			throw new DrawingException(DrawingException.ERROR_FRAME_BITMAP);
		}
		
		if (canvas == null) {
			throw new DrawingException(DrawingException.ERROR_CANVAS);
		}
		
		canvas.drawBitmap(b, pos.left, pos.top, null);
	}
	
	public void drawText(String text, float size, int color, Frame pos) throws DrawingException {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setTextSize(size);
		
		try {
			canvas.drawText(text, pos.left, pos.top, paint);
		}
		catch (NullPointerException e) {
			throw new DrawingException(DrawingException.ERROR_CANVAS);
		}
	}

	/**
	 * @return the screenSize
	 */
	public Frame getScreenSize() {
		return screenSize;
	}

	/**
	 * @return the scaleX
	 */
	public float getScaleX() {
		return scaleX;
	}

	/**
	 * @return the scaleY
	 */
	public float getScaleY() {
		return scaleY;
	}
}
