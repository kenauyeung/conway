package conway.game.logic.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Universe {
	private Logger logger = LogManager.getLogger(Universe.class);

	private Cell[][] worldCells;

	public Universe(int xRng, int yRng) throws Exception {
		if (xRng > 0 && yRng > 0) {
			worldCells = new Cell[yRng][xRng];
			logger.info("Created world with[x:{}], [y:{}]", xRng, yRng);
		} else {
			throw new Exception(
					"Unable to create world with [xRng:" + xRng + "], [yRng:" + yRng + "]; Ensure xRng and yRng > 0");
		}
	}

	public int getX() {
		return worldCells[0].length;
	}

	public int getY() {
		return worldCells.length;
	}

	public Cell getCell(int x, int y) {
		if (x >= 0 && y >= 0 && x < getX() && y < getY()) {
			return worldCells[y][x];
		}
		return null;
	}

	public void setCell(Cell cell) {
		if (cell.getXLocation() >= 0 && cell.getYLocation() >= 0 && cell.getXLocation() < getX()
				&& cell.getYLocation() < getY()) {
			worldCells[cell.getYLocation()][cell.getXLocation()] = cell;
		}
	}

	public void deleteCell(int x, int y) {
		if (x >= 0 && y >= 0 && x < getX() && y < getY()) {
			worldCells[y][x] = null;
		}
	}

	public boolean isCellExist(int x, int y) {
		if (x >= 0 && y >= 0 && x < getX() && y < getY()) {
			return worldCells[y][x] != null;
		}

		return false;
	}
}
