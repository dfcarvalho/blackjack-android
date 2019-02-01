package plp.project02.engine;

import java.util.ArrayList;
import android.graphics.Bitmap;

/**
 * Class that represents an image
 */
public class Image implements Object {
	/**
	 * Path to image file (in the Android Assets folder)
	 */
	private String filePath;
	
	/**
	 * Image bitmap
	 */
	private Bitmap bitmap;
	
	/**
	 * Frame containing the dimensions of the image (in pixels)
	 */
	private Frame imageSize;
	
	/**
	 * Frame containing the dimensions of the image's frames (in pixels)
	 */
	private Frame frameSize;
	
	/**
	 * List of frames contained in the image 
	 */
	private ArrayList<Frame> frames;
	
	/**
	 * Number of references pointing to this image 
	 */
	private int numRef;
	
	/**
	 * Constructor
	 */
	public Image() {
		numRef = 0;
		frames = new ArrayList<Frame>();
	}
	
	/**
	 * Calculates the positions of the frames and adds them to the Frames list
	 */
	public void calculateFrames() {
		// clear all frames
		if (frames.size() > 0) {
			frames.clear();
		}
		
		if ( (frameSize != null) && (frameSize.getWidth() != 0) && (frameSize.getHeight() != 0)) {	
			int posX, posY;
			
			int frameWidth = frameSize.getWidth();
			int frameHeight = frameSize.getHeight();
			
			int numFramesX = imageSize.getWidth() / frameWidth;
			int numFramesY = imageSize.getHeight() / frameHeight;
			
			
			for (int j = 0; j < numFramesY; j++) {
				for (int i = 0; i < numFramesX; i++) {
					posX = i*frameWidth;
					posY = j*frameHeight;
					
					Frame frame = new Frame(posX, posY, posX+frameWidth, posY+frameHeight);
					frames.add(frame);
				}
			}
		
		}
		else {
			Frame frame = new Frame(0, 0, imageSize.getWidth(), imageSize.getHeight());
			frames.add(frame);
		}
	}
	
	/**
	 * Frees image from memory (if not needed anymore).
	 * @return true if released, false if not released
	 */
	public boolean release() {
		if (numRef > 1) {
			numRef--;
			return false;
		}
		else {
			bitmap.recycle();
			numRef = 0;
		}
		return true;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String imageFilePath) {
		this.filePath = imageFilePath;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap imageBitmap) {
		this.bitmap = imageBitmap;
	}

	public Frame getImageSize() {
		return imageSize;
	}

	public void setImageSize(Frame imageSize) {
		this.imageSize = imageSize;
	}	

	public void setFrameSize(Frame frameSize) {
		this.frameSize = frameSize;
		calculateFrames();
	}
	
	public int getFrameWidth() {
		return this.frameSize.getWidth();
	}
	
	public int getFrameHeight() {
		return this.frameSize.getHeight();
	}
	
	public Frame getFrame(int iFrame) {
		if (iFrame >= frames.size()) {
			return null;
		}
		
		return frames.get(iFrame);
	}
	
	public Frame getFrameSize() {
		return this.frameSize;
	}

	public int getNumRef() {
		return numRef;
	}
	
	public void addNumRef() {
		numRef++;
	}
}
