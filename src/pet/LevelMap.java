package pet;

import java.util.Random;

import pet.Exceptions.AnotherGapException;
import pet.Exceptions.ExistingElementException;
import pet.Exceptions.OverTubeException;
import pet.Exceptions.WithoutFloorException;
import pet.Interfaces.PetLevelInterface;

import dk.itu.mario.engine.sprites.Enemy;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.level.Level;

/**
 * @author Rodrigo
 */
public class LevelMap extends Level implements PetLevelInterface {

	// Contador dos elementos do cenário
	private int gaps = 5;
	private int tubes = 10;
	private int coins = 50;
	private int quests = 10;
	private int platforms = 10;

	// Contadores dos bichos
	private int goombas[] = { 5, 5 };
	private int green_koopas[] = { 5, 5 };
	private int red_koopas[] = { 5, 5 };
	private int plants = 0;
	private int spikis = 5;

	/**
	 * Construtor básico do mapa.
	 * 
	 * @param width
	 *            Largura do mapa.
	 * 
	 * @param height
	 *            Altura do mapa.
	 */
	public LevelMap(int width, int height) {
		super(width, height);

		this.CreateMap();
		// this.addEnemies();
	}

	@Override
	public void CreateFloor() {
		for (int x = 0; x < this.width; x++) {
			super.setBlock(x, this.height - 1, Level.HILL_TOP);
		}
	}

	@Override
	public void CreateMap() {

		// Reproduzindo a rotina definida no papel
		this.CreateFloor();

		int i = 0;
		Random n = new Random();

		// Criando o portal da saída
		super.xExit = super.width - 8;
		super.yExit = super.height - 1;

		while (i != this.gaps) {

			int offset = n.nextInt(super.width - 10);
			int width = n.nextInt(5);

			if (offset > 10) {

				if (width >= 3) {

					try {
						this.CreateGap(offset, width);
						i++;

					} catch (AnotherGapException e) {
						System.err.print(e.getMessage());
					}
				}
			}
		}

		i = 0;

		while (i != this.tubes) {

			int offset[] = { n.nextInt(super.width - 10),
					n.nextInt(super.height - 1) };
			int height = n.nextInt(4);

			if (offset[0] > 8) {
				try {

					this.CreateTube(offset, height);
					i++;

				} catch (WithoutFloorException | ExistingElementException
						| OverTubeException e) {

					System.err.println(e.getMessage());
				}
			}
		}

		i = 0;

		while (i != this.platforms) {

			int offset[] = { n.nextInt(super.width - 10),
					n.nextInt(super.height - 1) };

			int height = n.nextInt(4);
			int width = n.nextInt(10);

			if (offset[0] > 8) {

				if (width > 1) {
					
					try {

						this.CreatePlatform(offset, width, height);
						i++;

					} catch (WithoutFloorException | ExistingElementException e) {

						System.err.println(e.getMessage());
					}
				}
			}
		}

		this.CreateBlockMap();
		this.CreateCoinMap();
		
		this.addEnemies();
	}

	@Override
	public void CreatePlatform(int[] offset, int width, int height)
			throws ExistingElementException, WithoutFloorException {

		this.SearchExistingElements(offset, width, height);
		this.SearchGapsUnderElements(offset, width);

		for (int y = offset[1]; y > offset[1] - height; y--) {
			for (int x = offset[0]; x < offset[0] + width; x++) {
				// Plataforma superior
				if (y == offset[1] - height + 1) {
					// Canto superior esquerdo
					if (x == offset[0])
						super.setBlock(x, y, Level.HILL_TOP_LEFT);

					// Canto superior direito
					else if (x == offset[0] + width - 1)
						super.setBlock(x, y, Level.HILL_TOP_RIGHT);

					// Meio
					else
						super.setBlock(x, y, Level.HILL_TOP);
				}

				// Parte de baixo
				else {
					// Borda esquerda
					if (x == offset[0])
						super.setBlock(x, y, Level.HILL_LEFT);

					// Borda direita
					else if (x == offset[0] + width - 1)
						super.setBlock(x, y, Level.HILL_RIGHT);

					// Meio
					else
						super.setBlock(x, y, Level.HILL_FILL);
				}
			}
		}

	}

	@Override
	public void CreateGap(int offset, int width) throws AnotherGapException {

		this.SearchAnotherGap(offset, width);

		for (int y = this.height - 1; y < this.height; y++) {
			for (int x = offset; x < offset + width; x++)
				super.setBlock(x, y, (byte) 0);
		}

		super.setBlock(offset - 1, this.height - 1, Level.RIGHT_UP_GRASS_EDGE);
		super.setBlock(offset + width - 1, this.height - 1,
				Level.LEFT_UP_GRASS_EDGE);
	}

	@Override
	public void CreateTube(int[] offset, int height)
			throws ExistingElementException, WithoutFloorException,
			OverTubeException {

		this.SearchExistingElements(offset, 2, height);
		this.SearchGapsUnderElements(offset, 2);
		this.SearchTubesUnderElements(offset);

		for (int y = offset[1]; y > offset[1] - height; y--) {
			for (int x = offset[0]; x < offset[0] + 2; x++) {

				if (y == offset[1] - height + 1) {
					// Canto superior esquerdo
					if (x == offset[0])
						super.setBlock(x, y, Level.TUBE_TOP_LEFT);

					// Canto superior direito
					else
						super.setBlock(x, y, Level.TUBE_TOP_RIGHT);
				} else {
					// Lateral esquerda
					if (x == offset[0])
						super.setBlock(x, y, Level.TUBE_SIDE_LEFT);

					// Lateral direita
					else
						super.setBlock(x, y, Level.TUBE_SIDE_RIGHT);
				}
			}
		}
	}

	@Override
	public void SearchExistingElements(int[] offset, int width, int height)
			throws ExistingElementException {
		for (int y = offset[1]; y > offset[1] - height; y--) {

			for (int x = offset[0]; x < offset[0] + width; x++) {

				if (super.getBlock(x, y) != (byte) 0)
					/*
					 * && (super.getBlock(x, y) != Level.HILL_TOP) &&
					 * (super.getBlock(x, y) != Level.HILL_TOP_LEFT) &&
					 * (super.getBlock(x, y) != Level.HILL_TOP_RIGHT) &&
					 * (super.getBlock(x, y) != Level.LEFT_UP_GRASS_EDGE) &&
					 * (super.getBlock(x, y) != Level.RIGHT_UP_GRASS_EDGE) &&
					 * (super.getBlock(x, y) != Level.LEFT_POCKET_GRASS) &&
					 * (super.getBlock(x, y) != Level.RIGHT_POCKET_GRASS))
					 */
					throw new ExistingElementException(super.getBlock(x, y),
							offset);
			}
		}

	}

	@Override
	public void CreateBlockMap() {

		Random n = new Random();
		int qtd_quests = 0;

		while (qtd_quests != this.quests) {

			int offset[] = { n.nextInt(this.width - 10), n.nextInt(this.height) };

			int h = this.CalculateHeight(offset);

			if (h == 3) {
				boolean aux = n.nextBoolean();

				if (aux)
					this.setBlock(offset[0], offset[1], BLOCK_POWERUP);
				else
					this.setBlock(offset[0], offset[1], BLOCK_COIN);

				qtd_quests++;
			}
		}
	}

	@Override
	public void CreateCoinMap() {

		Random n = new Random();
		int qtd_coins = 0;

		while (qtd_coins != this.coins) {

			int offset[] = { n.nextInt(this.width - 10), n.nextInt(this.height) };

			int h = this.CalculateHeight(offset);

			if (h == 3) {
				this.setBlock(offset[0], offset[1], Level.COIN);
				qtd_coins++;
			}
		}
	}

	@Override
	public void SearchGapsUnderElements(int offset[], int width)
			throws WithoutFloorException {

		for (int x = offset[0]; x < offset[0] + width; x++) {
			if (super.getBlock(x, offset[1] + 1) == (byte) 0)
				throw new WithoutFloorException(offset, width);
		}
	}

	/**
	 * Rotina para calcular a altura para um elemento de largura 1x1 (blocos,
	 * moedas)
	 * 
	 * @param offset
	 *            Posição {x,y} do elemento.
	 * 
	 * @return A altura do elemento em relação a alguma plataforma.
	 */
	private int CalculateHeight(int offset[]) {
		int h = 0;

		for (int y = offset[1]; y < offset[1] + height; y++) {

			if ((this.getBlock(offset[0], y) == Level.HILL_TOP_LEFT)
					|| (this.getBlock(offset[0], y) == Level.HILL_TOP_RIGHT)
					|| (this.getBlock(offset[0], y) == Level.HILL_TOP)
					|| (this.getBlock(offset[0], y) == Level.GROUND)
					|| (this.getBlock(offset[0], y) == Level.BLOCK_COIN)
					|| (this.getBlock(offset[0], y) == Level.BLOCK_EMPTY)
					|| (this.getBlock(offset[0], y) == Level.BLOCK_POWERUP)
					|| (this.getBlock(offset[0], y) == Level.TUBE_TOP_LEFT)
					|| (this.getBlock(offset[0], y) == Level.TUBE_TOP_RIGHT)
					|| (this.getBlock(offset[0], y) == Level.ROCK)
					|| (this.getBlock(offset[0], y) == Level.COIN)
					|| (this.getBlock(offset[0], y) == Level.HILL_FILL)
					|| (this.getBlock(offset[0], y) == Level.HILL_LEFT)
					|| (this.getBlock(offset[0], y) == Level.HILL_RIGHT)
					|| (this.getBlock(offset[0], y) == Level.TUBE_SIDE_LEFT)
					|| (this.getBlock(offset[0], y) == Level.TUBE_SIDE_RIGHT)
					|| (this.getBlock(offset[0], y) == Level.LEFT_POCKET_GRASS)
					|| (this.getBlock(offset[0], y) == Level.RIGHT_POCKET_GRASS))
				break;

			h++;
		}

		return h;
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
		Random random = new Random();

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
				 * Nos inimigos com asas, verificamos se há um bloco de chão. Se
				 * houver, ele será instanciado no mapa :^D
				 */

				/*
				 * Green Koopa
				 */
				case 0:
					asas = random.nextBoolean();

					if (!asas) {
						if (add_gkoopas[0] != green_koopas[0]) {

							/*
							 * Verificando se o bloco não é uma parte de um cano
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

	private void SearchTubesUnderElements(int offset[])
			throws OverTubeException {

		if ((this.getBlock(offset[0], offset[1] + 1) == Level.TUBE_TOP_LEFT)
				|| (this.getBlock(offset[0], offset[1] + 1) == Level.TUBE_TOP_RIGHT))
			throw new OverTubeException(offset);
	}

	private void SearchAnotherGap(int offset, int width)
			throws AnotherGapException {

		for (int y = this.height - 1; y < this.height; y++) {

			for (int x = offset; x < offset + width; x++) {

				if (this.getBlock(x, y) == Level.LEFT_UP_GRASS_EDGE)
					throw new AnotherGapException(offset);
			}
		}
	}
}
