package pet.Exceptions;

/**
 * Exceção para tratar a situação de não haver um chão sob a área onde o
 * elemento será posto.
 * 
 * @author rodrigo
 * 
 */
public class WithoutFloorException extends Exception {

	/**
	 * Gerado pelo compilador.
	 */
	private static final long serialVersionUID = 3622530121507522389L;

	private int offset[];
	private int width;

	public WithoutFloorException(int offset[], int width) {

		super("A gap was found under the area of object. Offset: [" + offset[0]
				+ "," + offset[1] + "]; Width: " + width);
		this.width = width;
		this.offset = offset;
	}
}
