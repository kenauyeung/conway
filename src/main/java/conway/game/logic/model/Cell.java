package conway.game.logic.model;

public class Cell {

	private int xLocation, yLocation;
	private byte[] rgb;

	public Cell(int xLocation, int yLocation, int red, int green, int blue) {
		this(xLocation, yLocation, (byte) red, (byte) green, (byte) blue);
	}

	public Cell(int xLocation, int yLocation, byte red, byte green, byte blue) {
		this.rgb = new byte[] { red, green, blue };
		this.xLocation = xLocation;
		this.yLocation = yLocation;
	}

	public void setColor(byte red, byte green, byte blue) {
		rgb[0] = red;
		rgb[1] = green;
		rgb[2] = blue;
	}

	public byte getRed() {
		return rgb[0];
	}

	public byte getGreen() {
		return rgb[1];
	}

	public byte getBlue() {
		return rgb[2];
	}

	public int getXLocation() {
		return xLocation;
	}

	public int getYLocation() {
		return yLocation;
	}
}
