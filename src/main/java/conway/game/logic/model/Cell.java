package conway.game.logic.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cell {

	@JsonProperty("x")
	private int xLocation;

	@JsonProperty("y")
	private int yLocation;

	private int[] rgb;

	public Cell() {
		this.rgb = new int[] { 0, 0, 0 };
	}

	public Cell(int xLocation, int yLocation, int red, int green, int blue) {
		this.rgb = new int[] { red, green, blue };
		this.xLocation = xLocation;
		this.yLocation = yLocation;
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

	public int getXLocation() {
		return xLocation;
	}

	public int getYLocation() {
		return yLocation;
	}
}
