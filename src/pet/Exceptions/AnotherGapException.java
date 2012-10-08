package pet.Exceptions;

/**
 * Esta classe definirá a exceção de haver um outro buraco na área delimitada para um buraco.
 * 
 * @author rodrigo
 *
 */
public class AnotherGapException extends Exception {

	/**
	 * Gerado pelo compilador.
	 */
	private static final long serialVersionUID = -6071630832349914260L;
	
	int offset;
	
	public AnotherGapException(int offset)
	{
		super ("An another gap was found. Offset [x]: [" + offset + "]");
		
		this.offset = offset;
	}

}
