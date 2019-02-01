package plp.project02.engine;

/**
 * Classe que representa um quadro de imagem.
 * @author danilo
 */
public class Frame implements Object {
	/* use private if platform != android
	private int left;
	private int top;
	private int right;
	private int bottom;
	*/
	
	public int left;
	public int top;
	public int right;
	public int bottom;

	/**
	 * Construtor com parametros. Recebe os pontos da posicao do quadro.
	 * @param left - posicao esquerda em X
	 * @param top - posicao superior em Y
	 * @param right - posicao direita em X
	 * @param bottom - posicao inferior em Y
	 */
	public Frame(int left, int top, int right, int bottom) {
		super();
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	public boolean collisionTest(Frame otherFrame) {
		return((left < otherFrame.right)  && (right  > otherFrame.left) && 
				 (top  < otherFrame.bottom) && (bottom > otherFrame.top));
	}
	
	/* Use getters / setters if platform != android
	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}
	*/
	
	public int getWidth() {
		return right - left;
	}
	
	public int getHeight() {
		return bottom - top;
	}

	public boolean release() {
		return true;
	}

}
