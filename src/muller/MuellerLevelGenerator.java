package muller;

import java.util.Random;

import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.LevelFactory;
import dk.itu.mario.level.CustomizedLevel;
import dk.itu.mario.level.Level;

public class MuellerLevelGenerator implements LevelGenerator {

	public LevelInterface generateLevel(GamePlay playerMetrics) {
		// LevelInterface level = new CustomizedLevel(320,15,new
		// Random().nextLong(),1,1,playerMetrics);
		// LevelInterface level = new MartinMuellerLevel(320,15,new
		// Random().nextLong(),1,0,playerMetrics);

		/*
		 * Chamando o outro construtor para instanciar a classe
		 * MartinMuellerLevel
		 */
		int gkoopas[] = { 10, 10 };
		int rkoopas[] = { 10, 10 };
		int goombas[] = { 10, 10 };
		int cont_plats = 5;
		int cont_tubes = 5;
		int cont_gaps = 5;
		int cont_coins = 5;
		int cont_quests = 5;

		LevelInterface level = (LevelInterface) new MartinMuellerLevel(320, 15,
				new Random().nextLong(), 1, 0, playerMetrics, rkoopas, gkoopas,
				goombas, 0, 10, cont_plats, cont_gaps, cont_tubes, cont_quests,
				cont_coins);

		LevelFactory.register("MuellerLevel", level);
		return level;
	}

}
