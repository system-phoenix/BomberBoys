package project.bomberboys.game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;

import project.bomberboys.game.blocks.Block;
import project.bomberboys.game.blocks.BonusBlock;
import project.bomberboys.game.blocks.HardBlock;
import project.bomberboys.game.blocks.SoftBlock;
import project.bomberboys.sockets.Client;
import project.bomberboys.sockets.Server;
import project.bomberboys.window.BufferedImageLoader;
import project.bomberboys.window.SpriteSheet;

public class Field {

	private BufferedImageLoader imageLoader = new BufferedImageLoader();
	private BufferedImage terrain;
	private Game game;
	private char[][] gameBoard;
	private GameObject[][] objectBoard;
	private int index;
	private static int width = 5, height = 5;
	private LinkedList<Block> blocks;
	private LinkedList<SpawnPoint> spawnPoints;
	private static BufferedImageLoader loader;
	private static BufferedImage fieldImage;

	public Field(Game game, int index) {
		this.game = game;
		this.index = index;
		this.gameBoard = game.getGameBoard();
		this.objectBoard = game.getObjectBoard();
		this.blocks = new LinkedList<Block>();
		this.spawnPoints = new LinkedList<SpawnPoint>();

		terrain = SpriteSheet.grabImage(imageLoader.load("/img/field/terrain" + index + ".png/"), 1, 1, game.getWidth(), game.getHeight());
		/*if(game.isServer())*/ initField();
	}

	public static Dimension checkField() {
		loader = new BufferedImageLoader();
		fieldImage = loader.load("/img/field/field32.png");
		int i;
		for(i = 0;; i++) {
			int pixel = fieldImage.getRGB(0, i);
			int red = (pixel >> 16) & 0xff;
			int green = (pixel >> 8) & 0xff;
			int blue = (pixel) & 0xff;

			if ((red == 0) && (green == 0) && (blue == 0)) {
				break;
			}
		}

		int j;
		for(j = 0;; j++) {
			int pixel = fieldImage.getRGB(j, 0);
			int red = (pixel >> 16) & 0xff;
			int green = (pixel >> 8) & 0xff;
			int blue = (pixel) & 0xff;

			if ((red == 0) && (green == 0) && (blue == 0)) {
				break;
			}
		}
		height = i;
		width = j;
		return(new Dimension(i, j));
//		initField();
	}

	public void initField() {

		System.out.println("Initializing field...");

		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				int pixel = fieldImage.getRGB(j, i);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
//				if(i == 0 || j == 0 || (j + 1) == width || (i + 1) == height || (i % 2 == 0 && j % 2 == 0)) {
				if(((red == 255) && (green == 255) && (blue == 255)) || (red == 0) && (green == 255) && (blue == 0)) {
					gameBoard[i][j] = 'x';
					HardBlock b = new HardBlock(game, j, i, index, false);
					objectBoard[i][j] = b;
					blocks.add(b);
				} else if ((red == 0) && (green == 0) && (blue == 255)) {
					gameBoard[i][j] = '+';
					SpawnPoint sp = new SpawnPoint(game, j, i);
					objectBoard[i][j] = sp;
					spawnPoints.add(sp);
				} else if ((red == 255) && (green == 0) && (blue == 0)) {
					gameBoard[i][j] = 'v';
				} else {
					gameBoard[i][j] = ' ';
				}
			}
		}

		game.getPlayers()[game.getIndex()].setSpawnPoints(spawnPoints);

		if(game.isServer()) {
		} else {
			((Client) game.getChatSocket()).sendMessage("::Field Created::");
		}
	}

	public void randomizeField() {
		Random rand = new Random();
//		int limit = (height * width) - (height * width) / 4;
		int limit = 500;
		int powerup_type_count = 4;
		float powerup_probability = 0.25f;

		for(int i = 0; i < limit;) {
			int x = rand.nextInt(width);
			int y = rand.nextInt(height);
			int z = rand.nextInt((int)((powerup_type_count-1)/powerup_probability));

			if(gameBoard[y][x] == ' ') {
				gameBoard[y][x] = '#';
				Block b;
				if(z > 3)
					b = new SoftBlock(game, x, y, index, false);
				else
					b = new BonusBlock(game, x, y, index, z, false);
				objectBoard[y][x] = b;
				blocks.add(b);
				i++;
			}
		}

		((Server) game.getChatSocket()).broadcast("::Game Start::", "", game.getIndex());
		game.start();
	}

	public void update() {
		for(int i = 0; i < blocks.size(); i++) {
			blocks.get(i).update();
		}
	}

	public void render(Graphics g) {
//		g.drawImage(terrain, 0, 0, game.getWidth() * game.getScale(), game.getHeight() * game.getScale(), null);
		for(int i = 0; i < blocks.size(); i++) {
			blocks.get(i).render(g);
		}
	}

	public LinkedList<Block> getBlocks() {
		return this.blocks;
	}

	public LinkedList<SpawnPoint> getSpawnPoints() {
		return this.spawnPoints;
	}

	public BufferedImage getTerrain() {
		return this.terrain;
	}

	public int getIndex() {
		return this.index;
	}

}
