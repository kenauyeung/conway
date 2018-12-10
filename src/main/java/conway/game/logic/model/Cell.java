package conway.game.logic.model;

public class Cell {

	private int x;

	private int y;

	private int[] rgb;

	public Cell() {
		this.rgb = new int[] { 0, 0, 0 };
	}

	public Cell(int x, int y, int red, int green, int blue) {
		this.rgb = new int[] { red, green, blue };
		this.x = x;
		this.y = y;
	}

	public int getRed() {
		return rgb[0];
	}

	public int getGreen() {
		return rgb[1];
	}

	public int getBlue() {
		return rgb[2];
	}

	public void setColor(int red, int green, int blue) {
		rgb[0] = red;
		rgb[1] = green;
		rgb[2] = blue;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
