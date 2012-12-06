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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;

/**
 * @author Rodrigo
 */
public class LevelMap extends Level implements PetLevelInterface {

	// Contador dos elementos do cenário
	private int gaps[] = new int[3];
	private int tubes[] = new int[3];
	private int coins;
	private int quests;
	private int platforms;
	private int mountains;

	// Contadores dos bichos
	private int goombas[] = new int[2];
	private int green_koopas[] = new int[2];
	private int red_koopas[] = new int[2];
	private int plants;
	private int spikis;

	// Nota
	private int result;

	// Quantidades padrão
	private static int default_gaps[] = { 0, 1, 0 };
	private static int default_tubes[] = { 2, 2, 2 };
	private static int default_coins = 30;
	private static int default_quests = 10;
	private static int default_platforms = 5;
	private static int default_mountains = 0;

	private static int default_goombas[] = { 3, 3 };
	private static int default_green_koopas[] = { 3, 3 };
	private static int default_red_koopas[] = { 3, 3 };
	private static int default_plants = 0;
	private static int default_spikis = 3;

	/**
	 * Enumeração dos tipos de manipulação de arquivo.
	 * 
	 * @author rodrigo
	 */
	public static enum FileHandlingType {
		LOAD, SAVE;
	}

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

		this.LoadDefaultQuantities();

		this.CreateMap();
		// this.addEnemies();
	}

	/**
	 * Rotina para construção do mapa a partir de um arquivo texto
	 * pré-existente.
	 * 
	 * @param file
	 *            Objeto Path descrevendo o diretório do arquivo.
	 * 
	 * @param width
	 *            Largura do mapa.
	 * 
	 * @param height
	 *            Altura do mapa.
	 * 
	 * @param type
	 *            Enumeração representando o tipo de manipulação do arquivo.
	 */
	public LevelMap(Path file, int width, int height, FileHandlingType type) {

		super(width, height);

		if (type == FileHandlingType.LOAD)
			this.LoadFromFile(file);
		else
			this.LoadRandomQuantities();

		this.CreateMap();

		if (type == FileHandlingType.SAVE)
			this.SaveLevelOnFile(file);
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

		int i_gaps[] = { 0, 0, 0 };

		for (i = 0; i < 3; i++) {
			while (i_gaps[i] != this.gaps[i]) {

				int offset = n.nextInt(super.width - 10);
				// int width = n.nextInt(5);
				int width = i + 3;

				if (offset > 10) {

					try {
						this.CreateGap(offset, width);
						i_gaps[i]++;

					} catch (AnotherGapException e) {
						System.err.print(e.getMessage());
					}
				}
			}
		}

		i = 0;
		int i_tubes[] = { 0, 0, 0 };

		for (i = 0; i < 3; i++) {
			while (i_tubes[i] != this.tubes[i]) {

				int offset[] = { n.nextInt(super.width - 10),
						n.nextInt(super.height - 1) };
				int height = i + 2;

				if (offset[0] > 8) {
					try {

						this.CreateTube(offset, height);
						i_tubes[i]++;

					} catch (WithoutFloorException | ExistingElementException
							| OverTubeException e) {

						System.err.println(e.getMessage());
					}
				}
			}
		}

		i = 0;

		while (i != this.platforms) {

			int offset[] = { n.nextInt(super.width - 10),
					n.nextInt(super.height - 1) };

			int height = n.nextInt(5);
			int width = n.nextInt(10);

			if (offset[0] > 8) {

				if ((width > 1) && (height > 0)) {

					try {

						this.CreatePlatform(offset, width, height);
						i++;

					} catch (WithoutFloorException | ExistingElementException
							| OverTubeException e) {

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
			throws ExistingElementException, WithoutFloorException,
			OverTubeException {

		this.SearchExistingElements(offset, width, height);
		this.SearchGapsUnderElements(offset, width);
		this.SearchTubesUnderElements(offset);

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

		/*
		 * TODO: Fazer a verificação de buracos vizinhos para evitar bugs
		 * gráficos (chamar a rotina SearchNeighboringGaps()).
		 */

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
					// || (this.getBlock(offset[0], y) == Level.COIN)
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

	/**
	 * Rotina para verificar se não há um tubo debaixo da área a ser desenhada.
	 * 
	 * @param offset
	 *            Posição {x,y} do canto inferior esquerdo do objeto.
	 * 
	 * @throws OverTubeException
	 *             Ela é disparada quando há um tubo embaixo da área.
	 */
	private void SearchTubesUnderElements(int offset[])
			throws OverTubeException {

		if ((this.getBlock(offset[0], offset[1] + 1) == Level.TUBE_TOP_LEFT)
				|| (this.getBlock(offset[0], offset[1] + 1) == Level.TUBE_TOP_RIGHT))
			throw new OverTubeException(offset);
	}

	/**
	 * Rotina para verificar se não há um buraco ocupando toda ou parte da área
	 * reservada para um novo buraco.
	 * 
	 * @param offset
	 *            Posição {x} do canto superior esquerdo do buraco.
	 * 
	 * @param width
	 *            Largura do buraco.
	 * 
	 * @throws AnotherGapException
	 *             Ela é disparado quando há um outro buraco ocupando a área.
	 */
	private void SearchAnotherGap(int offset, int width)
			throws AnotherGapException {

		for (int y = this.height - 1; y < this.height; y++) {

			for (int x = offset; x < offset + width; x++) {

				if ((this.getBlock(x, y) == Level.LEFT_UP_GRASS_EDGE)
						|| (this.getBlock(x, y) == Level.RIGHT_UP_GRASS_EDGE)
						|| (this.getBlock(x, y) == (byte) 0))
					throw new AnotherGapException(offset);
			}
		}
	}

	/**
	 * Rotina para verificar se há um buraco vizinho.
	 * 
	 * @param offset
	 *            Posição {x} do canto esquerdo do buraco.
	 * 
	 * @param width
	 *            Largura do buraco.
	 * 
	 * @return 0: Nenhum vizinho; 1: Um vizinho na direita; 2: Um vizinho na
	 *         esquerda; 3: Dois vizinhos nos dois lados.
	 */
	private int SearchNeighboringGaps(int offset, int width) {
		int res = 0;

		for (int aux = offset - 1; aux != offset + width + 1; aux += width + 1) {
			if (this.getBlock(aux, this.height - 1) == Level.RIGHT_UP_GRASS_EDGE)
				res++;
			else if (this.getBlock(aux, this.height - 1) == Level.LEFT_UP_GRASS_EDGE)
				res += 2;
		}

		return res;
	}

	/**
	 * Rotina para criar uma montanha (plataforma que o Mario não pode passar
	 * por dentro).
	 * 
	 * @param offset
	 *            Posição {x,y} do canto inferior esquerdo.
	 * 
	 * @param width
	 *            Largura do objeto.
	 * 
	 * @param height
	 *            Altura do objeto.
	 * 
	 * @throws WithoutFloorException
	 *             Ela é disparada quando não há um chão para construí-la.
	 * 
	 * @throws OverTubeException
	 *             Ela é disparada quando há um tubo em baixo da área
	 *             delimitada.
	 */
	private void CreateMountain(int offset[], int width, int height)
			throws WithoutFloorException, OverTubeException {

		/*
		 * TODO: Necessita ser feita a verificação de montanhas vizinhas e o
		 * desenho nesse caso.
		 */

		for (int y = offset[1]; y > offset[1] - height; y--) {
			for (int x = offset[0]; x < offset[0] + width; x++) {

				// Plataforma superior
				if (y == offset[1] - height + 1) {

					// Canto superior esquerdo
					if (x == offset[0])
						this.setBlock(x, y, Level.LEFT_UP_GRASS_EDGE);

					// Canto superior direito
					else if (x == offset[0] + height)
						this.setBlock(x, y, Level.RIGHT_UP_GRASS_EDGE);

					// Meio
					else
						this.setBlock(x, y, Level.HILL_TOP);
				}

				// Plataforma inferior
				else if (y == offset[1]) {

					// Canto inferior esquerdo
					if (x == offset[0])
						this.setBlock(x, y, Level.LEFT_POCKET_GRASS);

					// Canto inferior direito
					else if (x == offset[0] + width)
						this.setBlock(x, y, Level.RIGHT_POCKET_GRASS);

					// Meio
					else
						this.setBlock(x, y, Level.HILL_FILL);
				}

				// Plataforma intermediária
				else {
				}
			}
		}
	}

	/**
	 * Rotina para salvar os metadados em um arquivo texto.
	 * 
	 * @param file
	 *            Objeto Path descrevendo o diretório do arquivo.
	 */
	private void SaveLevelOnFile(Path file) {

		// Definindo a codificação do arquivo (padrão da máquina virtual
		Charset cod = Charset.defaultCharset();

		// Criando uma stream ligada ao arquivo
		try (BufferedWriter stream = Files.newBufferedWriter(file, cod,
				StandardOpenOption.CREATE, StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING)) {

			// "Escrevendo" nessa stream
			// stream.write('\n');

			for (int i : this.gaps)
				stream.write(Integer.toString(i) + '\n');

			for (int i : this.tubes)
				stream.write(Integer.toString(i) + '\n');

			stream.write(Integer.toString(this.platforms) + '\n');
			stream.write(Integer.toString(this.mountains) + '\n');
			stream.write(Integer.toString(this.coins) + '\n');
			stream.write(Integer.toString(this.quests) + '\n');

			for (int i : this.red_koopas)
				stream.write(Integer.toString(i) + '\n');

			for (int i : this.green_koopas)
				stream.write(Integer.toString(i) + '\n');

			for (int i : this.goombas)
				stream.write(Integer.toString(i) + '\n');

			stream.write(Integer.toString(this.spikis) + '\n');
			stream.write(Integer.toString(this.plants) + '\n');

		} catch (IOException e) {

			System.err.println(e.getMessage());
		}
	}

	/**
	 * Rotina para carregar os metadados a partir de um arquivo texto.
	 * 
	 * @param file
	 *            Objeto Path descrevendo o diretório do arquivo.
	 */
	 private void LoadFromFile(Path file) {
		Charset cod = Charset.defaultCharset();

		try (BufferedReader stream = Files.newBufferedReader(file, cod)) {

			// Fazendo a leitura propriamente dita
			for (int i = 0; i < 3; i++)
				this.gaps[i] = Integer.parseInt(stream.readLine());

			for (int i = 0; i < 3; i++)
				this.tubes[i] = Integer.parseInt(stream.readLine());

			this.platforms = Integer.parseInt(stream.readLine());
			this.mountains = Integer.parseInt(stream.readLine());
			this.coins = Integer.parseInt(stream.readLine());
			this.quests = Integer.parseInt(stream.readLine());

			for (int i = 0; i < 2; i++)
				this.red_koopas[i] = Integer.parseInt(stream.readLine());

			for (int i = 0; i < 2; i++)
				this.green_koopas[i] = Integer.parseInt(stream.readLine());

			for (int i = 0; i < 2; i++)
				this.goombas[i] = Integer.parseInt(stream.readLine());

			this.spikis = Integer.parseInt(stream.readLine());
			this.plants = Integer.parseInt(stream.readLine());
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.err
					.println("An IOException was ocurred at the reading of the file. Default quantities loaded.");

			this.LoadDefaultQuantities();

		} catch (NullPointerException e) {

			System.err
					.println("The opened file has wrong format. Can't read the quantities. Default quantities loaded.");
			this.LoadDefaultQuantities();
		}
	}

	/**
	 * Rotina para carregar as quantidades padrão dos elementos do cenário.
	 */
	private void LoadDefaultQuantities() {
		this.gaps = LevelMap.default_gaps;
		this.tubes = LevelMap.default_tubes;
		this.platforms = LevelMap.default_platforms;
		this.mountains = LevelMap.default_mountains;
		this.coins = LevelMap.default_coins;
		this.quests = LevelMap.default_quests;

		this.red_koopas = LevelMap.default_red_koopas;
		this.green_koopas = LevelMap.default_green_koopas;
		this.goombas = LevelMap.default_goombas;
		this.plants = LevelMap.default_plants;
		this.spikis = LevelMap.default_spikis;
	}
	
	/**
	 * Rotina para gerar uma quantidade aleatória de elementos do cenário.
	 */
	private void LoadRandomQuantities()
	{
		Random random = new Random();
		
		for (int i=0;i<3;i++)
			this.gaps[i] = random.nextInt(2) + 1;
		
		for (int i=0;i<3;i++)
			this.tubes[i] = random.nextInt(2) + 2;
		
		this.platforms = random.nextInt(3) + 2;
		this.mountains = 0;
		this.coins = random.nextInt(50) + 20;
		this.quests = random.nextInt(10) + 5;
		
		for (int i=0;i<2;i++)
			this.red_koopas[i] = random.nextInt(3) + 2;
		
		for (int i=0;i<2;i++)
			this.green_koopas[i] = random.nextInt(3) + 2;
		
		for (int i=0;i<2;i++)
			this.goombas[i] = random.nextInt(3) + 2;
		
		this.spikis = random.nextInt(5) + 2;
		this.plants = random.nextInt(this.tubes[0] + this.tubes[1] + this.tubes[2]);
			
	}

}
