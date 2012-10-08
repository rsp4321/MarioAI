package pet.Exceptions;

/**
 * Esta classe definirá a exceção de haver um tubo sob a área do objeto a ser construído.
 * Isso resolve problemas envolvendo tubos e plataformas em cima de tubos.
 * 
 * @author rodrigo
 *
 */
public class OverTubeException extends Exception {

	/**
	 * Gerado pelo compilador.
	 */
	private static final long serialVersionUID = 4196821594591801116L;

	private int offset[];

	public OverTubeException(int offset[]) {

		super("A tube was found over the area of the object. Offset [x,y]: ["
				+ offset[0] + "," + offset[1] + "]");

		this.offset = offset;
	}

}
