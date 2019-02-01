package plp.project02.engine;

import java.util.ArrayList;

import android.util.Log;

import plp.project02.engine.exceptions.ImageNotLoadedException;

public abstract class Scene implements Object {
	/**
	 * Handle to Engine
	 */
	protected Engine engine;
	
	/**
	 * Images used on this scene
	 */
	protected ArrayList<Image> images;
	
	/**
	 * Sprites on this scene
	 */
	protected static ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	
	/**
	 * Standard constructor
	 */
	public Scene(Engine engine) {
		this.engine = engine;
		images = new ArrayList<Image>();
	}
	
	/**
	 * Releases scene from memory (along with loaded images and sounds)
	 */
	public boolean release() {
		// release images
		for (int i = images.size() - 1; i >= 0; i--) {
			engine.videoManager.releaseImage(images.get(i));
		}
		images.clear();
		
		// release sprites
		for (int i = sprites.size() - 1; i >= 0; i--) {
			sprites.get(i).release();
		}
		sprites.clear();
		
		return true;
	}
	
	/**
	 * Initializes scene
	 */
	public abstract void init();
	
	/**
	 * Executes scene
	 */
	public abstract void exec();
	
	/**
	 * Draws scene
	 */
	public void draw() {
		// draw scene
		for (int i = 0; i < sprites.size(); i++) {
			sprites.get(i).draw();
		}
	}
	
	protected Sprite createSprite(String imageFilePath, int iFrame, Frame frameSize) {
		Sprite newSprite = new Sprite(this); 
		newSprite.create(imageFilePath, iFrame, frameSize);
		sprites.add(newSprite);
		
		return newSprite;
	}
	
	protected void deleteSprite(Sprite sprite) {
		for (int i = 0; i < sprites.size(); i++) {
			if (sprite == sprites.get(i)) {
				sprites.remove(i); 
			}
		}
	}
	
	/**
	 * Adds image to scene
	 */
	protected Image addImage(String filePath, Frame frameSize) {
		Image newImage;
		
		try {
			newImage = engine.videoManager.loadImage(filePath, frameSize);	
		}
		catch (ImageNotLoadedException e) {
			Log.d("ImageNotLoadedException", e.getErrorMsg());
			return null;
		}
		
		images.add(newImage);
		return newImage;
	}
}
