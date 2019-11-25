package entities;

import java.awt.Color;
import java.awt.Graphics2D;

import game.Game;
import input.Input;

public class Bullet extends Entity {

	private int		r;
	private double	dx;
	private double	dy;
	private double	rad;
	private double	speed	= 7;

	private Color	color1	= Color.YELLOW;

	public Bullet(double x, double y, double angle) {
		super(x, y);
		r = 2;
		rad = Math.toRadians(angle);
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
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

	@Override
	public void update(Input input) {
	}

	public boolean update() {
		x += dx;
		y += dy;
		if (x < -r || x > Game.WIDTH - r || y < -r || y > Game.HEIGHT - r) {
			return true;
		}
		return false;
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(color1);
		g.fillOval((int) x - r, (int) y - r, r * 2, r * 2);
	}

}
