/**
 * 
 */
package pet;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import pet.LevelMap.FileHandlingType;

/**
 * Classe geradora do nova classe de mapa.
 * 
 * @author rodrigo
 * 
 */
public class PetLevelGenerator implements LevelGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dk.itu.mario.MarioInterface.LevelGenerator#generateLevel(dk.itu.mario
	 * .MarioInterface.GamePlay)
	 */
	@Override
	public LevelInterface generateLevel(GamePlay playerMetrics) {

		LevelInterface level = null;

		try {
			Path dir = FileSystems.getDefault().getPath(
					System.getProperty("user.home"), "mario");

			if (Files.notExists(dir))
				Files.createDirectory(dir);

			Path file = Paths.get(dir.toString()).resolve("avaliacao.txt");

			level = (LevelInterface) new LevelMap(file, 320, 15,
					FileHandlingType.SAVE);
		}
		catch (Exception e)
		{
			System.err.print(e.getMessage());
			level = (LevelInterface) new LevelMap (320,15);
		}

		return (LevelInterface) level;
	}
}
