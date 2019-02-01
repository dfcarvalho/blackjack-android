package plp.project02.engine;

public interface Object {
	/**
	 * Libera objeto da memoria. Deve ser implementada nas classes filhas. 
	 * @return true if succeeded; false if any errors occur.
	 */
	public boolean release();
}
