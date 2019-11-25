package entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import game.Game;
import input.Input;

public class Player extends Entity {

	private int		r;
	private int		speed;
	private int		lives;
	private int		score;

	private long	firiingTimer;
	private long	firingDelay	= 200;

	private boolean	recovery;
	private long	recoveryTime;

	private Color	color1		= Color.WHITE;
	private Color	color2		= Color.RED;

	public Player(double x, double y, int r, int speed, int lives) {
		super(x, y);
		this.r = r;
		this.speed = speed;
		this.lives = lives;
		firiingTimer = System.nanoTime();
		recovery = false;
		recoveryTime = 0;
		score = 0;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getR() {
		return r;
	}

	public int getLives() {
		return lives;
	}

	public boolean isDead() {
		return lives <= 0;
	}

	public boolean isRecover() {
		return recovery;
	}

	public int getScore() {
		return score;
	}

	public void losingLife() {
		lives--;
		recovery = true;
		recoveryTime = System.nanoTime();
	}

	public void addScore(int sc) {
		score += sc;
	}

	@Override
	public void update(Input input) {
		boolean firing = false;
		double newX = x;
		double newY = y;

		if (input.getKey(KeyEvent.VK_UP)) {
			newY -= speed;
		}
		if (input.getKey(KeyEvent.VK_RIGHT)) {
			newX += speed;
		}
		if (input.getKey(KeyEvent.VK_DOWN)) {
			newY += speed;
		}
		if (input.getKey(KeyEvent.VK_LEFT)) {
			newX -= speed;
		}
		if (input.getKey(KeyEvent.VK_Z)) {
			firing = true;
		}

		if (newX < r) {
			newX = r;
		} else if (newX >= Game.WIDTH - r) {
			newX = Game.WIDTH - r;
		}

		if (newY < r) {
			newY = r;
		} else if (newY >= Game.HEIGHT - r) {
			newY = Game.HEIGHT - r;
		}

		x = newX;
		y = newY;

		if (firing) {
			long elapsed = (System.nanoTime() - firiingTimer) / 1000000;
			if (elapsed > firingDelay) {
				Game.bullets.add(new Bullet(x, y, 270));
				firiingTimer = System.nanoTime();
			}
		}

		long elapsed = (System.nanoTime() - recoveryTime) / 1000000;
		if (elapsed > 2000) {
			recovery = false;
			recoveryTime = 0;
		}
	}

	@Override
	public void render(Graphics2D g) {
		if (recovery) {
			g.setColor(color2);
			g.fillOval((int) x - r, (int) y - r, r * 2, r * 2);

			g.setStroke(new BasicStroke(3));
			g.setColor(color2.darker());
			g.fillOval((int) x - r, (int) y - r, r * 2, r * 2);
		} else {
			g.setColor(color1);
			g.fillOval((int) x - r, (int) y - r, r * 2, r * 2);

			g.setStroke(new BasicStroke(3));
			g.setColor(color1.darker());
			g.fillOval((int) x - r, (int) y - r, r * 2, r * 2);
		}

	}
}
