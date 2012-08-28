package muller;

import java.util.Random;
import java.util.ArrayList;
import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.engine.sprites.Enemy;
import dk.itu.mario.level.Level;

public class MartinMuellerLevel extends Level implements LevelInterface {

	// class holding the zones
	Zones zones;
	Zone zone;
	Random random;
	int width;
	int height;
	ArrayList<Integer> zstring;

	private static final int ODDS_STRAIGHT = 0;
	private static final int ODDS_HILL_STRAIGHT = 1;
	private static final int ODDS_TUBES = 2;
	private static final int ODDS_JUMP = 3;
	private static final int ODDS_CANNONS = 4;
	private static final int JumpingThreshold = 3;

	private int[] odds = new int[5];
	private int totalOdds;

	private int difficulty;
	private int type;
	private int gaps;
	private int turtles;
	private int coins;

	// Contadores dos bichos
	private int goombas[] = { 10, 10 };
	private int green_koopas[] = { 10, 10 };
	private int red_koopas[] = { 10, 10 };
	private int plants = 5;
	private int spikis = 5;

	private GamePlay playerM;

	public MartinMuellerLevel(int width, int height, long seed, int difficulty,
			int type, GamePlay playerMetrics) {
		super(width, height);
		this.playerM = playerMetrics;
		zones = new Zones();
		zone = new Zone();
		// zones.loadFromDir("src//dk//itu//mario//level//generator//martinmueller//zones//5//");
		// zones.loadFromDir("src//dk//itu//mario//level//generator//martinmueller//zones//10//");
		// zones.loadFromDir("./zones/15/");
		// zones.loadFromDir("./zones/20/");
		// zones.loadFromDir("./zones/25/");
		// zones.loadFromDir("./zones/30/");

		zones.loadFromDir("./zones/rsp/");
		random = new Random(seed);
		this.width = width;
		this.height = height;
		ArrayList<ArrayList> zstrings[] = new ArrayList[30];
		int lGap[] = new int[30];
		int lPlat[] = new int[30];
		int lAll[] = new int[30];
		int lQuest[] = new int[30];
		int lCoin[] = new int[30];
		int lTubes[] = new int[30];
		int lInt[] = new int[30];
		for (int i = 0; i < 30; i++) {
			lGap[i] = 0;
			lPlat[i] = 0;
			lAll[i] = 0;
			lQuest[i] = 0;
			lCoin[i] = 0;
			lTubes[i] = 0;
			lInt[i] = 0;
		}
		zstring = new ArrayList<Integer>();
		difficulty = 50;

		// build 30 randomly created levels from the zones we have in our
		// database
		for (int i = 0; i < 30; i++) {
			int offset = 8;
			// create some random zones
			while (offset < width - 30) {
				int tmp = random.nextInt(zones.getNumZones());
				zstring.add(tmp);
				offset += zones.get(tmp).getWidth();
				lGap[i] += zones.get(tmp).getNoGaps();
				lPlat[i] += zones.get(tmp).getNoPlattforms();
				lQuest[i] += zones.get(tmp).getNoPowerups();
				lCoin[i] += zones.get(tmp).getNoCoins();
				lTubes[i] += zones.get(tmp).getNoTubes();
				lAll[i] += zones.get(tmp).getNofuncs();
				lInt[i] = (lGap[i] * 2) + lPlat[i] + lTubes[i];
			}
			System.out.println(zstring.toString());
			zstrings[i] = new ArrayList();
			zstrings[i].add((ArrayList<Integer>) zstring.clone());

			zstring.clear();

		}

		// ok, we have 30 randomly created levels know.
		// now, let's choose the most suitable one for our contestant.'

		// for the moment, we choose the one which the highest number of
		// objects, however,
		// other methods could be used here, like statistical or ai methods.
		int actual = 0;
		int level = 1;
		for (int i = 0; i < 30; i++) {
			if (lInt[i] > actual) {
				actual = lInt[i];
				level = i;
			}
		}
		creat(seed, difficulty, type, zstrings[level]);
	}

	public void creat(long seed, int difficulty, int type, ArrayList zones) {
		this.type = type;
		this.difficulty = 100;
		odds[ODDS_STRAIGHT] = 30;
		odds[ODDS_HILL_STRAIGHT] = 20;
		odds[ODDS_TUBES] = 2 + 2 * difficulty;
		int jumpDifficulty = 1;
		// adapt the game so that it has a number of gaps proportional to the
		// number of jumps the player made in the test level. The more the
		// jumps,
		// the more the gaps.
		if (playerM.jumpsNumber > JumpingThreshold)
			jumpDifficulty = 2;
		odds[ODDS_JUMP] = jumpDifficulty;
		odds[ODDS_CANNONS] = -10 + 5 * difficulty;

		random = new Random(seed);

		int pups = 100 - difficulty;

		// set the end piece
		int floor = height - 1 - random.nextInt(4);

		// create the exit
		xExit = width - 8;
		yExit = height - 1;

		// create a floor
		for (int x = 0; x < width; x++) {
			setBlock(x, height - 1, Level.HILL_TOP);
			// setBlock(x, height-1, Level.HILL_FILL);
		}

		int offset = 8;
		// create some random zones

		// zn=zones.get(0);
		zones = (ArrayList) zones.get(0);
		for (int i = 0; i < zones.size(); i++) {
			int tmp = Integer.parseInt(zones.get(i).toString());
			if (random.nextInt(1000) > 50)
				addEnemyLine(offset, offset + 8, random.nextInt(30) + 1);
			decorate(offset, 30, 100);

			offset = buildZone(tmp, offset);
		}

		return;
	}

	public int buildZone(int zid, int offset) {
		Zone zone = new Zone();
		zone = zones.get(zid);
		difficulty = 20;

		double p_no_pups = (difficulty + 100)
				* Math.exp(-0.001 * difficulty * difficulty);
		int i_no_pups = (int) p_no_pups;

		for (int i = 0; i < zone.getWidth(); i++) {
			for (int j = 0; j < zone.getHeight(); j++) {
				// depending on difficulty, put powerups in the questionmark
				// blocks
				byte element;

				// g(x) = (x + 100) e^(-(0.001) xÂ²)

				if ((((byte) zone.getElement(j, i) == Level.BLOCK_EMPTY) || ((byte) zone
						.getElement(j, i) == (byte) 20))
						&& random.nextInt(100) < i_no_pups) {
					element = Level.BLOCK_POWERUP;
				} else
					element = (byte) zone.getElement(j, i);

				setBlock(i + offset, j + 1, element);

			}
		}
		return offset + zone.getWidth();
	}

	private void addEnemyLine(int x0, int x1, int y) {
		for (int x = x0; x < x1; x++) {
			if (random.nextInt(50) < 25) {
				int type = random.nextInt(4);

				type = random.nextInt(3);
				if (turtles < Constraints.turtels) {
					if (type == Enemy.ENEMY_GREEN_KOOPA
							|| type == Enemy.ENEMY_RED_KOOPA) {
						turtles++;
						setSpriteTemplate(x, y,
								new SpriteTemplate(type,
										random.nextInt(35) < difficulty));
					} else {
						setSpriteTemplate(x, y,
								new SpriteTemplate(type,
										random.nextInt(35) < difficulty));
					}
				} else {
					setSpriteTemplate(
							x,
							y,
							new SpriteTemplate(Enemy.ENEMY_GOOMBA, random
									.nextInt(35) < difficulty));
				}
			}
		}

		// for (int x=x0;x<x1;x++)
		// {
		// if (x % 2 == 0)
		// setSpriteTemplate (x,y,new
		// SpriteTemplate(Enemy.ENEMY_GREEN_KOOPA,true));
		// else
		// setSpriteTemplate (x,y,new
		// SpriteTemplate(Enemy.ENEMY_RED_KOOPA,false));
		// }

	}

	private void decorate(int xStart, int xLength, int floor) {
		// if its at the very top, just return
		if (floor < 1)
			return;
		boolean rocks = true;

		// add an enemy line above the box
		addEnemyLine(xStart + 1, xLength - 1, floor - 1);

		int s = random.nextInt(4);
		int e = random.nextInt(4);

		if (floor - 2 > 0) {
			if ((xLength - 1 - e) - (xStart + 1 + s) > 1) {
				for (int x = xStart + 1 + s; x < xLength - 1 - e; x++) {
					setBlock(x, floor - 2, (byte) (2 + 2 * 16));
				}
			}
		}

		s = random.nextInt(4);
		e = random.nextInt(4);

		if (floor - 4 > 0) {
			if ((xLength - 1 - e) - (xStart + 1 + s) > 2) {
				for (int x = xStart + 1 + s; x < xLength - 1 - e; x++) {
					if (rocks) {
						if (x != xStart + 1 && x != xLength - 2
								&& random.nextInt(2) == 0) {
							if (random.nextInt(2) == 0) {
								setBlock(x, floor - 4, BLOCK_POWERUP);
							} else {
								if (coins < Constraints.coinBlocks) {
									coins++;
									setBlock(x, floor - 4, BLOCK_COIN);
								} else {
									setBlock(x, floor - 4, BLOCK_EMPTY);
								}
							}
						} else if (random.nextInt(4) == 0) {
							if (random.nextInt(4) == 0) {
								setBlock(x, floor - 4, (byte) (2 + 1 * 16));
							} else {
								setBlock(x, floor - 4, (byte) (1 + 1 * 16));
							}
						} else {
							setBlock(x, floor - 4, BLOCK_EMPTY);
						}
					}
				}
			}
		}
	}

	private void fixWalls() {
		boolean[][] blockMap = new boolean[width + 1][height + 1];

		for (int x = 0; x < width + 1; x++) {
			for (int y = 0; y < height + 1; y++) {
				int blocks = 0;
				for (int xx = x - 1; xx < x + 1; xx++) {
					for (int yy = y - 1; yy < y + 1; yy++) {
						if (getBlockCapped(xx, yy) == GROUND) {
							blocks++;
						}
					}
				}
				blockMap[x][y] = blocks == 4;
			}
		}
		blockify(this, blockMap, width + 1, height + 1);
	}

	private void blockify(Level level, boolean[][] blocks, int width, int height) {
		int to = 0;
		if (type == LevelInterface.TYPE_CASTLE) {
			to = 4 * 2;
		} else if (type == LevelInterface.TYPE_UNDERGROUND) {
			to = 4 * 3;
		}

		boolean[][] b = new boolean[2][2];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int xx = x; xx <= x + 1; xx++) {
					for (int yy = y; yy <= y + 1; yy++) {
						int _xx = xx;
						int _yy = yy;
						if (_xx < 0)
							_xx = 0;
						if (_yy < 0)
							_yy = 0;
						if (_xx > width - 1)
							_xx = width - 1;
						if (_yy > height - 1)
							_yy = height - 1;
						b[xx - x][yy - y] = blocks[_xx][_yy];
					}
				}

				if (b[0][0] == b[1][0] && b[0][1] == b[1][1]) {
					if (b[0][0] == b[0][1]) {
						if (b[0][0]) {
							level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
						} else {
							// KEEP OLD BLOCK!
						}
					} else {
						if (b[0][0]) {
							// down grass top?
							level.setBlock(x, y, (byte) (1 + 10 * 16 + to));
						} else {
							// up grass top
							level.setBlock(x, y, (byte) (1 + 8 * 16 + to));
						}
					}
				} else if (b[0][0] == b[0][1] && b[1][0] == b[1][1]) {
					if (b[0][0]) {
						// right grass top
						level.setBlock(x, y, (byte) (2 + 9 * 16 + to));
					} else {
						// left grass top
						level.setBlock(x, y, (byte) (0 + 9 * 16 + to));
					}
				} else if (b[0][0] == b[1][1] && b[0][1] == b[1][0]) {
					level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
				} else if (b[0][0] == b[1][0]) {
					if (b[0][0]) {
						if (b[0][1]) {
							level.setBlock(x, y, (byte) (3 + 10 * 16 + to));
						} else {
							level.setBlock(x, y, (byte) (3 + 11 * 16 + to));
						}
					} else {
						if (b[0][1]) {
							// right up grass top
							level.setBlock(x, y, (byte) (2 + 8 * 16 + to));
						} else {
							// left up grass top
							level.setBlock(x, y, (byte) (0 + 8 * 16 + to));
						}
					}
				} else if (b[0][1] == b[1][1]) {
					if (b[0][1]) {
						if (b[0][0]) {
							// left pocket grass
							level.setBlock(x, y, (byte) (3 + 9 * 16 + to));
						} else {
							// right pocket grass
							level.setBlock(x, y, (byte) (3 + 8 * 16 + to));
						}
					} else {
						if (b[0][0]) {
							level.setBlock(x, y, (byte) (2 + 10 * 16 + to));
						} else {
							level.setBlock(x, y, (byte) (0 + 10 * 16 + to));
						}
					}
				} else {
					level.setBlock(x, y, (byte) (0 + 1 * 16 + to));
				}
			}
		}
	}

	/**
	 * Função para criar os inimigos aleatóriamente no mapa.
	 */
	public void addEnemies() {
		/*
		 * Criando contadores para os inimigos já colocados no mapa
		 */
		int add_gkoopas[] = { 0, 0 };
		int add_rkoopas[] = { 0, 0 };
		int add_goombas[] = { 0, 0 };
		int add_plants = 0;
		int add_spikis = 0;

		boolean completo = false;
		int tipo;
		int x;
		int y;
		boolean asas;

		/*
		 * Utilizando a classe Random para gerar números aleatórios
		 */
		while (!completo) {
			/*
			 * Definindo aleatóriamente o tipo e as coordenadas
			 */
			tipo = random.nextInt(5);
			x = random.nextInt(super.width);
			y = random.nextInt(super.height - 1);

			/*
			 * Verificando se não existe um inimigo nessa coordenada
			 */
			SpriteTemplate aux = getSpriteTemplate(x, y);
			if (aux == null) {
				switch (tipo) {
				/*
				 * Nos inimigos com asas, verificamos se há um bloco de chão.
				 * Se houver, ele será instanciado no mapa :^D
				 */

				/*
				 * Green Koopa
				 */
				case 0:
					asas = random.nextBoolean();

					if (!asas) {
						if (add_gkoopas[0] != green_koopas[0]) {

							/*
							 * Verificando se o bloco não é uma parte de um
							 * cano
							 */
							boolean cano = (getBlock(x, y) == Level.TUBE_SIDE_LEFT)
									|| (getBlock(x, y) == Level.TUBE_SIDE_RIGHT)
									|| (getBlock(x, y) == Level.TUBE_TOP_LEFT)
									|| (getBlock(x, y) == Level.TUBE_TOP_RIGHT);

							if (getBlock(x, y + 1) == Level.HILL_TOP && !cano) {
								add_gkoopas[0]++;
								setSpriteTemplate(x, y, new SpriteTemplate(
										Enemy.ENEMY_GREEN_KOOPA, asas));
							}
						}
					} else {
						if (add_gkoopas[1] != green_koopas[1]) {
							boolean plataforma = false;
							int i_y;

							for (i_y = y + 1; i_y < super.height - 1; i_y++) {
								plataforma = getBlock(x, i_y) == Level.HILL_TOP;

								if (plataforma)
									break;
							}

							for (i_y = 0; i_y < super.height - 1; i_y++)
								plataforma = plataforma
										&& (getSpriteTemplate(x, i_y) == null);

							if (plataforma) {
								add_gkoopas[1]++;
								setSpriteTemplate(x, y, new SpriteTemplate(
										Enemy.ENEMY_GREEN_KOOPA, asas));
							}
						}
					}
					break;

				/*
				 * Red Koopa
				 */
				case 1:
					asas = random.nextBoolean();

					if (!asas) {
						if (add_rkoopas[0] != red_koopas[0]) {
							boolean cano = (getBlock(x, y) == Level.TUBE_SIDE_LEFT)
									|| (getBlock(x, y) == Level.TUBE_SIDE_RIGHT)
									|| (getBlock(x, y) == Level.TUBE_TOP_LEFT)
									|| (getBlock(x, y) == Level.TUBE_TOP_RIGHT);

							if ((getBlock(x, y + 1) == Level.HILL_TOP) && !cano) {
								add_rkoopas[0]++;
								setSpriteTemplate(x, y, new SpriteTemplate(
										Enemy.ENEMY_RED_KOOPA, asas));
							}
						}
					} else {
						if (add_rkoopas[1] != red_koopas[1]) {
							boolean plataforma = false;
							int i_y;

							for (i_y = y + 1; i_y < super.height - 1; i_y++) {
								plataforma = (getBlock(x, i_y) == Level.HILL_TOP);

								if (plataforma)
									break;
							}

							for (i_y = 0; i_y < super.height - 1; i_y++)
								plataforma = plataforma
										&& (getSpriteTemplate(x, i_y) == null);

							if (plataforma) {
								add_rkoopas[1]++;
								setSpriteTemplate(x, y, new SpriteTemplate(
										Enemy.ENEMY_RED_KOOPA, asas));
							}

						}
					}
					break;

				/*
				 * Goomba
				 */
				case 2:
					asas = random.nextBoolean();

					if (!asas) {
						if (add_goombas[0] != goombas[0]) {
							boolean cano = (getBlock(x, y) == Level.TUBE_SIDE_LEFT)
									|| (getBlock(x, y) == Level.TUBE_SIDE_RIGHT)
									|| (getBlock(x, y) == Level.TUBE_TOP_LEFT)
									|| (getBlock(x, y) == Level.TUBE_TOP_RIGHT);

							if (getBlock(x, y + 1) == Level.HILL_TOP && !cano) {
								add_goombas[0]++;
								setSpriteTemplate(x, y, new SpriteTemplate(
										Enemy.ENEMY_GOOMBA, asas));
							}
						}
					} else {
						if (add_goombas[1] != goombas[1]) {
							boolean plataforma = false;
							int i_y;

							for (i_y = y + 1; i_y < super.height - 1; i_y++) {
								plataforma = (getBlock(x, i_y) == Level.HILL_TOP);

								if (plataforma)
									break;
							}

							for (i_y = 0; i_y < super.height - 1; i_y++)
								plataforma = plataforma
										&& (getSpriteTemplate(x, i_y) == null);

							if (plataforma) {
								add_goombas[1]++;
								setSpriteTemplate(x, y, new SpriteTemplate(
										Enemy.ENEMY_GOOMBA, asas));
							}
						}
					}

					break;

				/*
				 * Plant
				 */
				case 3:
					if (add_plants != plants) {
						boolean tubo = getBlock(x, y + 1) == Level.TUBE_TOP_LEFT
								|| getBlock(x, y + 1) == Level.TUBE_TOP_RIGHT;

						if (tubo) {
							add_plants++;
							setSpriteTemplate(x, y, new SpriteTemplate(
									Enemy.ENEMY_FLOWER, false));
						}
					}
					break;

				/*
				 * Spiky
				 */
				case 4:
					if (add_spikis != spikis) {
						boolean cano = (getBlock(x, y) == Level.TUBE_SIDE_LEFT)
								|| (getBlock(x, y) == Level.TUBE_SIDE_RIGHT)
								|| (getBlock(x, y) == Level.TUBE_TOP_LEFT)
								|| (getBlock(x, y) == Level.TUBE_TOP_RIGHT);

						if (getBlock(x, y + 1) == Level.HILL_TOP && !cano) {
							add_spikis++;
							setSpriteTemplate(x, y, new SpriteTemplate(
									Enemy.ENEMY_SPIKY, false));
						}
					}
					break;
				}
			}

			completo = (add_gkoopas[0] == green_koopas[0])
					&& (add_gkoopas[1] == green_koopas[1])
					&& (add_rkoopas[0] == red_koopas[0])
					&& (add_rkoopas[1] == red_koopas[1])
					&& (add_goombas[0] == goombas[0])
					&& (add_goombas[1] == goombas[1]) && (add_plants == plants)
					&& (add_spikis == spikis);
		}
	}

	/**
	 * Overload do construtor definindo a quantidade de bichos.
	 * 
	 * @param width
	 * @param height
	 * @param seed
	 * @param difficulty
	 * @param type
	 * @param playerMetrics
	 * @param cont_rkoopas
	 *            Quantidade de Koopas vermelhas
	 * @param cont_gkoopas
	 *            Quantidade de Koopas verdes
	 * @param cont_goombas
	 *            Quantidade de Goombas (bolinha marrom)
	 * @param cont_plants
	 *            Quantidade de Plantas
	 * @param cont_spikis
	 *            Quantidade de Spikis (porco-espinho)
	 */
	public MartinMuellerLevel(int width, int height, long seed, int difficulty,
			int type, GamePlay playerMetrics, int cont_rkoopas[],
			int cont_gkoopas[], int cont_goombas[], int cont_plants,
			int cont_spikis) {
		super(width, height);
		this.playerM = playerMetrics;
		zones = new Zones();
		zone = new Zone();
		// zones.loadFromDir("src//dk//itu//mario//level//generator//martinmueller//zones//5//");
		// zones.loadFromDir("src//dk//itu//mario//level//generator//martinmueller//zones//10//");
		// zones.loadFromDir("./zones/15/");
		// zones.loadFromDir("./zones/20/");
		// zones.loadFromDir("./zones/25/");
		// zones.loadFromDir("./zones/30/");

		zones.loadFromDir("./zones/rsp/");

		random = new Random(seed);
		this.width = width;
		this.height = height;
		ArrayList<ArrayList> zstrings[] = new ArrayList[30];
		int lGap[] = new int[30];
		int lPlat[] = new int[30];
		int lAll[] = new int[30];
		int lQuest[] = new int[30];
		int lCoin[] = new int[30];
		int lTubes[] = new int[30];
		int lInt[] = new int[30];
		for (int i = 0; i < 30; i++) {
			lGap[i] = 0;
			lPlat[i] = 0;
			lAll[i] = 0;
			lQuest[i] = 0;
			lCoin[i] = 0;
			lTubes[i] = 0;
			lInt[i] = 0;
		}
		zstring = new ArrayList<Integer>();
		difficulty = 50;

		// build 30 randomly created levels from the zones we have in our
		// database
		for (int i = 0; i < 30; i++) {
			int offset = 8;
			// create some random zones
			while (offset < width - 30) {
				int tmp = random.nextInt(zones.getNumZones());
				zstring.add(tmp);
				offset += zones.get(tmp).getWidth();
				lGap[i] += zones.get(tmp).getNoGaps();
				lPlat[i] += zones.get(tmp).getNoPlattforms();
				lQuest[i] += zones.get(tmp).getNoPowerups();
				lCoin[i] += zones.get(tmp).getNoCoins();
				lTubes[i] += zones.get(tmp).getNoTubes();
				lAll[i] += zones.get(tmp).getNofuncs();
				lInt[i] = (lGap[i] * 2) + lPlat[i] + lTubes[i];
			}
			System.out.println(zstring.toString());
			zstrings[i] = new ArrayList();
			zstrings[i].add((ArrayList<Integer>) zstring.clone());

			zstring.clear();

		}

		// ok, we have 30 randomly created levels know.
		// now, let's choose the most suitable one for our contestant.'

		// for the moment, we choose the one which the highest number of
		// objects, however,
		// other methods could be used here, like statistical or ai methods.
		int actual = 0;
		int level = 1;
		for (int i = 0; i < 30; i++) {
			if (lInt[i] > actual) {
				actual = lInt[i];
				level = i;
			}
		}

		// Definindo a quantidade dos bichos
		this.red_koopas = cont_rkoopas;
		this.green_koopas = cont_gkoopas;
		this.goombas = cont_goombas;
		this.plants = cont_plants;
		this.spikis = cont_spikis;

		// creat(seed, difficulty, type, zstrings[level]);

		/*
		 * Chamando a minha função creat()
		 */
		creat(difficulty, type, zstrings[level]);
	}

	/**
	 * Overload para criar os inimigos usando a função addEnemies().
	 * 
	 * @param difficulty
	 * @param type
	 * @param zones
	 */
	@SuppressWarnings("rawtypes")
	public void creat(/* long seed, */int difficulty, int type, ArrayList zones) {
		this.type = type;
		this.difficulty = 100;
		odds[ODDS_STRAIGHT] = 30;
		odds[ODDS_HILL_STRAIGHT] = 20;
		odds[ODDS_TUBES] = 2 + 2 * difficulty;
		int jumpDifficulty = 1;
		// adapt the game so that it has a number of gaps proportional to the
		// number of jumps the player made in the test level. The more the
		// jumps,
		// the more the gaps.
		if (playerM.jumpsNumber > JumpingThreshold)
			jumpDifficulty = 2;
		odds[ODDS_JUMP] = jumpDifficulty;
		odds[ODDS_CANNONS] = -10 + 5 * difficulty;

		random = new Random(30);

		// create the exit
		xExit = width - 8;
		yExit = height - 1;

		// create a floor
		for (int x = 0; x < width; x++) {
			setBlock(x, height - 1, Level.HILL_TOP);
			// setBlock(x, height-1, Level.HILL_FILL);
		}

		int offset = 8;
		// create some random zones

		// zn=zones.get(0);
		zones = (ArrayList) zones.get(0);
		for (int i = 0; i < zones.size(); i++) {
			int tmp = Integer.parseInt(zones.get(i).toString());
			// if (random.nextInt(1000) > 50)
			// addEnemyLine(offset, offset + 8, random.nextInt(30) + 1);
			// decorate(offset, 30, 100);

			offset = buildZone(tmp, offset);
		}

		/*
		 * Criando os inimigos
		 */
		addEnemies();

		return;
	}
}
