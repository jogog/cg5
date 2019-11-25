package display;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import javax.swing.JFrame;

import entities.Player;
import game.Game;
import input.Input;

public class Display {

	private boolean					created	= false;
	private JFrame					window;
	private Canvas					content;

	private BufferedImage			buffer;
	private int[]					bufferData;
	private Graphics				bufferGraph;
	private int						clearColor;

	private static BufferStrategy	bufferStrat;

	public void create(int width, int height, String title, int _clearColor, int numBuffers) {
		if (this.created)
			return;

		this.window = new JFrame(title);
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.content = new Canvas();

		Dimension size = new Dimension(width, height);
		this.content.setPreferredSize(size);

		this.window.setResizable(false);
		this.window.getContentPane().add(this.content);
		this.window.pack();
		this.window.setLocationRelativeTo(null);
		this.window.setVisible(true);

		this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.bufferData = ((DataBufferInt) this.buffer.getRaster().getDataBuffer()).getData();
		this.bufferGraph = this.buffer.getGraphics();
		((Graphics2D) this.bufferGraph).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		this.clearColor = _clearColor;

		this.content.createBufferStrategy(numBuffers);
		bufferStrat = this.content.getBufferStrategy();

		this.created = true;
	}

	public void clear() {
		Arrays.fill(bufferData, clearColor);
	}

	public void swapBuffers() {
		Graphics g = bufferStrat.getDrawGraphics();
		g.drawImage(buffer, 0, 0, null);
		bufferStrat.show();
	}

	public Graphics2D getGraphics() {
		return (Graphics2D) bufferGraph;
	}

	public void destroy() {
		if (!created)
			return;

		window.dispose();
	}

	public void setTitle(String title) {
		window.setTitle(title);
	}

	public void addInputListener(Input inputListener) {
		window.add(inputListener);
	}

	public void drawWave(Graphics2D g, int waveNumber, long waveStartTimerDiff, int waveDelay) {
		g.setFont(new Font("Century Gothic", Font.PLAIN, 25));
		String s = String.format("WAVE %d", waveNumber);
		int length = (int) (g.getFontMetrics().getStringBounds(s, g).getWidth());
		int alpha = (int) (255 * (Math.sin(3.14 * waveStartTimerDiff / waveDelay)));
		if (alpha > 255) {
			alpha = 255;
		}
		g.setColor(new Color(255, 255, 255, alpha));
		g.drawString(s, Game.WIDTH / 2 - length / 2, Game.HEIGHT / 2);
	}

	public void drawGameOver(Graphics2D g, int score) {
		clear();

		g.setFont(new Font("Century Gothic", Font.PLAIN, 25));
		String s = "GaME OVER :(";
		int length = (int) (g.getFontMetrics().getStringBounds(s, g).getWidth());
		g.setColor(new Color(255, 255, 255));
		g.drawString(s, Game.WIDTH / 2 - length / 2, Game.HEIGHT / 2);
		s = String.format("Score: %d", score);
		length = (int) (g.getFontMetrics().getStringBounds(s, g).getWidth());
		g.drawString(s, Game.WIDTH / 2 - length / 2, Game.HEIGHT / 2 + 30);

		swapBuffers();
	}
	
	public void drawInfo(Graphics2D g, Player player) {
		for (int i = 0; i < player.getLives(); i++) {
			g.setColor(Color.WHITE);
			g.fillOval(20 + (20 * i), 20, player.getR() * 2, player.getR() * 2);

			g.setStroke(new BasicStroke(3));
			g.setColor(Color.WHITE.darker());
			g.fillOval(20 + (20 * i), 20, player.getR() * 2, player.getR() * 2);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
			g.drawString(String.format("Score: %d", player.getScore()), Game.WIDTH - 100, 30);
		}
	}
	
}
