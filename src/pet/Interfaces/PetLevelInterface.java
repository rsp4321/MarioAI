/**
 * 
 */
package pet.Interfaces;

import pet.Exceptions.AnotherGapException;
import pet.Exceptions.ExistingElementException;
import pet.Exceptions.OverTubeException;
import pet.Exceptions.WithoutFloorException;
import dk.itu.mario.MarioInterface.LevelInterface;

/**
 * @author rodrigo
 */
public interface PetLevelInterface extends LevelInterface {

	static final int PLATFORM = 0;
	static final int FLOOR = 1;
	static final int TUBE = 2;
	static final int GAP = 3;

	/**
	 * Rotina para criar o mapa inteiro em geral.
	 */
	public void CreateMap();

	/**
	 * Rotina para criar os blocos no mapa.
	 */
	public void CreateBlockMap();

	/**
	 * Rotina para criar as moedas no mapa.
	 */
	public void CreateCoinMap();

	/**
	 * Ela criará uma montanha (plataforma) no cenário.
	 * 
	 * @param offset
	 *            Posição {x,y} do bloco inferior esquerdo do elemento.
	 * 
	 * @param width
	 *            Largura do elemento.
	 * 
	 * @param height
	 *            Altura do elemento.
	 * 
	 * @throws ExistingElementException
	 *             Ela é disparada quando aparece um objeto no meio da área
	 *             delimitada (um bloco, por exemplo).
	 * 
	 * @throws OverTubeException
	 *             Ela é disparada quando há um tubo sob a área da plataforma.
	 */
	public void CreatePlatform(int offset[], int width, int height)
			throws ExistingElementException, WithoutFloorException,
			OverTubeException;

	/**
	 * Essa rotina criará um buraco no chão.
	 * 
	 * @param offset
	 *            Posição {x} do início do chão.
	 * 
	 * @param width
	 *            Largura do elemento.
	 * @throws AnotherGapException
	 */
	public void CreateGap(int offset, int width) throws AnotherGapException;

	/**
	 * Rotina para criar um tubo no cenário. Ela deverá verificar se há um
	 * elemento no caminho.
	 * 
	 * @param offset
	 *            Posição {x,y} do bloco inferior esquerdo do elemento.
	 * 
	 * @param height
	 *            Altura do elemento.
	 * 
	 * @throws ExistingElementException
	 *             Ela é disparada quando aparece um objeto no meio da área
	 *             delimitada (um bloco, por exemplo).
	 * 
	 * @throws OverTubeException
	 *             Ela é disparada quando há um tubo sob a área delimitada.
	 */
	public void CreateTube(int offset[], int height)
			throws ExistingElementException, WithoutFloorException,
			OverTubeException;

	/**
	 * Rotina para criar um chão no cenário. Após criado um chão, podemos
	 * definir nossos buracos e outros elementos.
	 */
	public void CreateFloor();

	/**
	 * Rotina para verificar a existência de um objeto no meio da área do
	 * elemento a ser criado. Ex.: Um bloco na área definida para construir um
	 * tubo.
	 * 
	 * @param offset
	 *            Posição {x,y} do bloco inferior esquerdo do elemento.
	 * 
	 * @param width
	 *            Largura do elemento.
	 * 
	 * @param height
	 *            Altura do elemento.
	 * 
	 * @throws ExistingElementException
	 *             A exceção correspondente ao encontro.
	 */
	public void SearchExistingElements(int offset[], int width, int height)
			throws ExistingElementException;

	/**
	 * Rotina para checar se não há nenhum buraco (gap) embaixo da área definida
	 * para o elemento.
	 * 
	 * @param offset
	 *            Posição {x} da base do elemento.
	 * 
	 * @param width
	 *            Largura do elemento.
	 * 
	 * @throws WithoutFloorException
	 *             Exceção que tratará o problema.
	 */
	public void SearchGapsUnderElements(int offset[], int width)
			throws WithoutFloorException;
}
