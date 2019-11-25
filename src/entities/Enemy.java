package entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import game.Game;
import input.Input;

public class Enemy extends Entity {

	private int		r;
	private double	rad;
	private double	speed;
	private double	dx;
	private double	dy;

	private int		health;
	private int		rank;
	private int		type;

	private boolean	ready;
	private boolean	dead;

	private Color	color1;

	public Enemy(double x, double y, int type, int rank) {
		super(x, y);

		this.type = type;
		this.rank = rank;

//		types of enemies
		switch (type) {
		case 1:
			color1 = Color.BLUE;
			speed = 2;
			switch (rank) {
			case 1:
				r = 5;
				health = 1;
				break;
			case 2:
				r = 7;
				health = 3;
				break;
			case 3:
				r = 10;
				health = 5;
				break;
			}
			break;
		case 2:
			color1 = Color.RED;
			speed = 7;
			health = 1;
			switch (rank) {
			case 1:
				r = 7;
				break;
			case 2:
				r = 5;
				break;
			}
			break;
		}

		this.x = Math.random() * Game.WIDTH / 2 + Game.WIDTH / 4;
		this.y = -r;

		double angle = Math.random() * 140 + 20;
		rad = Math.toRadians(angle);

		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;

		ready = false;
		dead = false;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getR() {
		return r;
	}

	public int getType() {
		return type;
	}

	public int getRank() {
		return rank;
	}

	public boolean isDead() {
		return dead;
	}

	public void hit() {
		health--;
		if (health <= 0) {
			dead = true;
		}
	}

	@Override
	public void update(Input input) {
		x += dx;
		y += dy;

		if (!ready) {
			if (x > r && x < Game.WIDTH - r && y > r && y < Game.HEIGHT - r) {
				ready = true;
			}
		}

		if (x < r && dx < 0) {
			dx = -dx;
		}
		if (x > Game.WIDTH - r && dx > 0) {
			dx = -dx;
		}
		if (y < r && dy < 0) {
			dy = -dy;
		}
		if (y > Game.HEIGHT - r && dy > 0) {
			dy = -dy;
		}

	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(color1);
		g.fillOval((int) x - r, (int) y - r, r * 2, r * 2);

		g.setStroke(new BasicStroke(3));
		g.setColor(color1.darker());
		g.drawOval((int) x - r, (int) y - r, r * 2, r * 2);

	}

}
