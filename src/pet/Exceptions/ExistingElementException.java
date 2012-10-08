package pet.Exceptions;

//import dk.itu.mario.level.Level;

/**
 * Esta classe definirá a exceção de ter um objeto no caminho. Ex.: Um bloco no
 * meio da área para construir um tubo.
 * 
 * @author rodrigo
 * 
 */
public class ExistingElementException extends Exception {

	/**
	 * Gerado pelo compilador. Depois irei pesquisar sobre isso :^D
	 */
	private static final long serialVersionUID = -7329118557233698024L;

	private byte element;
	private int offset[];

	public ExistingElementException(byte element, int offset[]) {
		
		super("An object was found in the area of object. Position: ["
				+ offset[0] + "," + offset[1] + "]");
		this.element = element;
		this.offset = offset;
	}
}
