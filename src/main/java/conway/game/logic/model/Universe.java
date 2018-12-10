package conway.game.logic.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Universe {
	private Logger logger = LogManager.getLogger(Universe.class);

	private Cell[][] universeCells;

	public Universe(int xRng, int yRng) throws Exception {
		if (xRng > 0 && yRng > 0) {
			universeCells = new Cell[yRng][xRng];
			logger.info("Created universe with[x:{}], [y:{}]", xRng, yRng);
		} else {
			throw new Exception("Unable to create universe with [xRng:" + xRng + "], [yRng:" + yRng
					+ "]; Ensure xRng and yRng > 0");
		}
	}

	public int getX() {
		return universeCells[0].length;
	}

	public int getY() {
		return universeCells.length;
	}

	public Cell getCell(int x, int y) {
		if (x >= 0 && y >= 0 && x < getX() && y < getY()) {
			return universeCells[y][x];
		}
		return null;
	}

	public boolean setCell(Cell cell) {
		if (cell.getX() >= 0 && cell.getY() >= 0 && cell.getX() < getX() && cell.getY() < getY()
				&& universeCells[cell.getY()][cell.getX()] == null) {
			universeCells[cell.getY()][cell.getX()] = cell;
			return true;
		}
		return false;
	}

	public void deleteCell(int x, int y) {
		if (x >= 0 && y >= 0 && x < getX() && y < getY()) {
			universeCells[y][x] = null;
		}
	}

	public boolean isCellExist(int x, int y) {
		if (x >= 0 && y >= 0 && x < getX() && y < getY()) {
			return universeCells[y][x] != null;
		}

		return false;
	}
}
