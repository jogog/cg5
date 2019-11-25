package game;

import java.awt.Graphics2D;
import java.util.ArrayList;

import display.Display;
import entities.Bullet;
import entities.Enemy;
import entities.Player;
import input.Input;
import utils.Time;

public class Game implements Runnable {

	public static final int			WIDTH				= 800;
	public static final int			HEIGHT				= 600;
	public static final String		TITLE				= "TDS Game";
	public static final int			CLEAR_COLOR			= 0xff000000;
	public static final int			NUM_BUFFERS			= 3;

	public static final float		UPDATE_RATE			= 60.0f;
	public static final float		UPDATE_INTERVAL		= Time.SECOND / UPDATE_RATE;
	public static final long		IDLE_TIME			= 1;

	private final int				PlAYER_SPEED		= 4;
	private final int				PLAYER_R			= 7;
	private final int				LIVES				= 3;

	private boolean					running;
	private Thread					gameThread;
	private Input					input;

	public Graphics2D				g;

	public Player					player				= new Player(WIDTH / 2, 500, PLAYER_R, PlAYER_SPEED, LIVES);
	public static ArrayList<Bullet>	bullets				= new ArrayList<Bullet>();
	public ArrayList<Enemy>			enemies				= new ArrayList<Enemy>();

	private boolean					waveStart			= true;
	public long						waveStartTimer		= 0;
	public long						waveStartTimerDiff	= 0;
	public int						waveNumber			= 0;
	public int						waveDelay			= 2000;

	Display							display				= new Display();

	public Game() {
		running = false;
		display.create(WIDTH, HEIGHT, TITLE, CLEAR_COLOR, NUM_BUFFERS);
		g = display.getGraphics();
		input = new Input();
		display.addInputListener(input);
	}

	public synchronized void start() {
		if (running)
			return;

		running = true;
		gameThread = new Thread(this);
		gameThread.start();
	}

	public synchronized void stop() {
		if (!running)
			return;

		running = false;

//		drawing final screen
		display.drawGameOver(g, player.getScore());

		try {
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		display.destroy();
	}

	public void createEnemies() {
		enemies.clear();
		switch (waveNumber) {
		case 1:
			for (int i = 0; i < 4; i++) {
				enemies.add(new Enemy(1, 1, 1, 1));
			}
			break;

		case 2:
			for (int i = 0; i < 8; i++) {
				enemies.add(new Enemy(1, 1, 1, 1));
			}
			break;

		case 3:
			for (int i = 0; i < 4; i++) {
				enemies.add(new Enemy(1, 1, 1, 3));
			}
			for (int i = 0; i < 9; i++) {
				enemies.add(new Enemy(1, 1, 1, 2));
			}
			break;

		case 4:
			for (int i = 0; i < 5; i++) {
				enemies.add(new Enemy(1, 1, 2, 1));
			}
			for (int i = 0; i < 2; i++) {
				enemies.add(new Enemy(1, 1, 2, 2));
			}
			break;

		case 5:
			for (int i = 0; i < 15; i++) {
				enemies.add(new Enemy(1, 1, 1, 2));
			}
			for (int i = 0; i < 10; i++) {
				enemies.add(new Enemy(1, 1, 2, 1));
			}
			for (int i = 0; i < 5; i++) {
				enemies.add(new Enemy(1, 1, 2, 2));
			}
		}
	}

	public void update() {

		// starting waves
		if (waveStartTimer == 0 && enemies.size() == 0) {
			waveNumber++;
			waveStart = false;
			waveStartTimer = System.nanoTime();
		} else {
			waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
			if (waveStartTimerDiff > waveDelay) {
				waveStart = true;
				waveStartTimer = 0;
				waveStartTimerDiff = 0;
			}
		}

//		creating enemies on wave
		if (waveStart && enemies.size() == 0) {
			createEnemies();
		}

//		player update
		player.update(input);

//		bullets update
		for (int i = 0; i < bullets.size(); i++) {
			boolean remove = bullets.get(i).update();
			if (remove) {
				bullets.remove(i);
				i--;
			}
		}

//		enemies update
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).update(input);
		}

//		checking hits on enemies
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			double bx = b.getX();
			double by = b.getY();
			double br = b.getR();

			for (int j = 0; j < enemies.size(); j++) {
				Enemy e = enemies.get(j);
				double ex = e.getX();
				double ey = e.getY();
				double er = e.getR();

				double dist = Math.sqrt((bx - ex) * (bx - ex) + (by - ey) * (by - ey));

				if (dist <= br + er) {
					e.hit();
					bullets.remove(i);
					i--;
					break;
				}
			}
		}

//		removing dead enemies
		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i).isDead()) {
				Enemy e = enemies.get(i);
				player.addScore(e.getType() + e.getRank());
				enemies.remove(i);
				i--;
			}
		}

//		checking player death
		if (player.isDead()) {
			stop();
		}

//		checking player hitting
		if (!player.isRecover()) {
			double px = player.getX();
			double py = player.getY();
			double pr = player.getR();

			for (int j = 0; j < enemies.size(); j++) {
				Enemy e = enemies.get(j);
				double ex = e.getX();
				double ey = e.getY();
				double er = e.getR();

				double dist = Math.sqrt((px - ex) * (px - ex) + (py - ey) * (py - ey));

				if (dist <= pr + er) {
					player.losingLife();
				}
			}

		}
	}

	public void render() {

		display.clear();

//	drawing player
		player.render(g);

//	drawing bullets
		if (!bullets.isEmpty()) {
			for (int i = 0; i < bullets.size(); i++) {
				bullets.get(i).render(g);
			}
		}

//	drawing enemies
		if (!enemies.isEmpty()) {
			for (int i = 0; i < enemies.size(); i++) {
				enemies.get(i).render(g);
			}
		}

//	drawing wave numbers
		if (waveStartTimer != 0) {
			display.drawWave(g, waveNumber, waveStartTimerDiff, waveDelay);
		}

//	drawing player lives and score
		display.drawInfo(g, player);

		display.swapBuffers();

	}

//	main thread
	public void run() {
		float delta = 0;
		long lastTime = Time.get();

		while (running) {

			long now = Time.get();
			long elapsedTime = now - lastTime;
			lastTime = now;

			boolean render = false;
			delta += (elapsedTime / UPDATE_INTERVAL);
			while (delta >= 1) {
				update();
				delta--;
				render = true;
			}

			if (render) {
				render();
			} else {
				try {
					Thread.sleep(IDLE_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
