package plp.project02.engine;

import android.util.Log;
import plp.project02.engine.exceptions.DrawingException;
import plp.project02.engine.exceptions.ImageNotLoadedException;

public class Sprite implements Object {
	
	/**
	 * Reference to sprite sheet image
	 */
	private Image image;
	
	/**
	 * size of the sprite frame
	 */
	private Frame frame;
	
	/**
	 * Flag to indicate if the sprite should be drawn on the screen
	 */
	private boolean visible;
	
	/**
	 * Reference to the scene to which this sprite belongs
	 */
	private Scene scene;
	
	/**
	 * Frame currently shown in the sprite's animation sequence
	 */
	private int currentImageFrame;
	
	/**
	 * Constructor
	 * @param scene - reference to the scene to which this sprite belongs
	 */
	public Sprite(Scene scene) {
		image = null;
		frame = null;
		visible = true;
		this.scene = scene; 
	}
	
	/**
	 * Releases sprite from memory (along with its image)
	 */
	public boolean release() {
		try {
			scene.engine.videoManager.releaseImage(this.image);
			this.image = null;
			this.frame = null;
			scene = null;
			return true;
		}
		catch (NullPointerException e) {
			Log.d("NullPointerException", e.getMessage());
			return false;
		}
	}
	
	/**
	 * Create a new sprite
	 * @param imageFilePath - path to the sprite's image file
	 * @param iFrame - index to the first animation frame 
	 * @param frameSize - size of the sprite's image frames
	 * @return - true if success, false if failed
	 */
	public boolean create(String imageFilePath, int iFrame, Frame frameSize) {
		try {
			image = scene.engine.videoManager.loadImage(imageFilePath, frameSize);
			currentImageFrame = iFrame;
			setPos(new Frame(0, 0, 0, 0));
		}
		catch (ImageNotLoadedException e) {
			Log.d("ImageNotLoadedException", e.getErrorMsg());
			return false;
		}
		catch (NullPointerException e) {
			Log.d("NullPointerException", e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Draws the sprite on the back buffer
	 */
	public void draw() {
		if (visible) {
			try {
				scene.engine.videoManager.drawImageFrame(image, currentImageFrame, frame);
			}
			catch (DrawingException e) {
				Log.d("DrawingException", e.getErrorMsg());
			}
		}
	}
	
	public void setPos(Frame newPos) {
		frame = newPos;
		
		try {
			frame.right = frame.left + image.getFrameWidth();
			frame.bottom = frame.top + image.getFrameHeight();
		}
		catch (NullPointerException e) {
			Log.d("NullPointerException", e.getMessage());
		}
	}
	
	public Frame getFrame() {
		return this.frame;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean getVisible() {
		return this.visible;
	}
	
	/**
	 * Creates a clone of the sprite
	 * @return - the created sprite clone
	 */
	public Sprite getClone() {
		Sprite sprClone = new Sprite(this.scene);
		sprClone.create(this.image.getFilePath(), this.currentImageFrame, this.image.getFrameSize());
		return sprClone;
	}
}
