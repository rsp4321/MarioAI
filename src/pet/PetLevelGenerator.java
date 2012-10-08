/**
 * 
 */
package pet;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;

/**
 * Classe geradora do nova classe de mapa.
 * 
 * @author rodrigo
 *
 */
public class PetLevelGenerator implements LevelGenerator {

	/* (non-Javadoc)
	 * @see dk.itu.mario.MarioInterface.LevelGenerator#generateLevel(dk.itu.mario.MarioInterface.GamePlay)
	 */
	@Override
	public LevelInterface generateLevel(GamePlay playerMetrics) {
		
		LevelInterface level = (LevelInterface) new LevelMap (320,15);
		
		return (LevelInterface) level;
	}

}
