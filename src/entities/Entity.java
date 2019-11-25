package entities;

import java.awt.Graphics2D;

import input.Input;

public abstract class Entity {

	protected double	x;
	protected double	y;

	public Entity(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public abstract void update(Input input);

	public abstract void render(Graphics2D g);

}
