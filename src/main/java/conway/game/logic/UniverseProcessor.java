package conway.game.logic;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import conway.game.logic.model.Cell;
import conway.game.logic.model.Universe;

@Component
public class UniverseProcessor implements Runnable {

	private Logger logger = LogManager.getLogger(UniverseProcessor.class);

	@Autowired
	private Universe universeView;

	private long pulse = 500;

	private List<int[]> deleteList = new ArrayList<>();
	private List<Cell> createList = new ArrayList<>();

	public UniverseProcessor() throws IllegalArgumentException {
		if (pulse < 1) {
			throw new IllegalArgumentException("Pulse must be greater than 0");
		}
	}

	@PostConstruct
	public void init() {

		universeView.setCell(new Cell(2, 2, 0, 0, 0));
		universeView.setCell(new Cell(3, 3, 0, 0, 0));
		universeView.setCell(new Cell(3, 4, 0, 0, 0));
		universeView.setCell(new Cell(2, 4, 0, 0, 0));
		universeView.setCell(new Cell(1, 4, 0, 0, 0));

		new Thread(this).start();
	}

	@Override
	public void run() {

		logger.info("Starting UniverseProcessor with pulse[{}ms]", pulse);

		display(-1);

		while (true) {
			try {
				Thread.sleep(pulse);
			} catch (InterruptedException e) {
				logger.error("error during pulse", pulse, e);
			}

			long stime = System.currentTimeMillis();
			calculateNextGeneration();

			display(System.currentTimeMillis() - stime);
		}
	}

	private void display(long run) {
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String s = "Run : " + run + "ms\n";
		for (int y = 0, by = universeView.getY(); y < by; y++) {
			for (int x = 0, bx = universeView.getX(); x < bx; x++) {
				s += universeView.isCellExist(x, y) ? "*" : ".";
			}
			s += "\n";
		}
		System.out.print(s);
	}

	public void updateUniverse(List<Cell> cells) {
		if (cells != null && !cells.isEmpty()) {
			synchronized (universeView) {
				cells.forEach(universeView::setCell);
			}
		}
	}

	private void calculateNextGeneration() {

		synchronized (universeView) {

			/*
			 * 1) Any live cell with fewer than two live neighbors dies, as if by
			 * underpopulation.
			 * 
			 * 2) Any live cell with two or three live neighbors lives on to the next
			 * generation.
			 * 
			 * 3) Any live cell with more than three live neighbors dies, as if by
			 * overpopulation.
			 * 
			 * 4) Any dead cell with exactly three live neighbors becomes a live cell, as if
			 * by reproduction.
			 */
			for (int y = 0, boundY = universeView.getY(); y < boundY; y++) {
				for (int x = 0, boundX = universeView.getX(); x < boundX; x++) {

					int numNeighbour = getNumberOfNeighbor(x, y);
					if (universeView.isCellExist(x, y)) {
						if (numNeighbour < 2 || numNeighbour > 3) {
							deleteList.add(new int[] { x, y });
						}
					} else if (numNeighbour == 3) {
						createList.add(new Cell(x, y, 0, 0, 0));
					}
				}
			}

			deleteList.forEach(i -> universeView.deleteCell(i[0], i[1]));
			createList.forEach(universeView::setCell);
			deleteList.clear();
			createList.clear();
		}
	}

	private int getNumberOfNeighbor(int x, int y) {
		int neighbour = 0;

		// top right
		if (universeView.isCellExist(getWrapX(x - 1), getWrapY(y - 1))) {
			neighbour++;
		}

		// top center
		if (universeView.isCellExist(x, getWrapY(y - 1))) {
			neighbour++;
		}

		// top left
		if (universeView.isCellExist(getWrapX(x + 1), getWrapY(y - 1))) {
			neighbour++;
		}

		// middle right
		if (universeView.isCellExist(getWrapX(x - 1), y)) {
			neighbour++;
		}

		// middle left
		if (universeView.isCellExist(getWrapX(x + 1), y)) {
			neighbour++;
		}

		// bottom right
		if (universeView.isCellExist(getWrapX(x - 1), getWrapY(y + 1))) {
			neighbour++;
		}

		// bottom center
		if (universeView.isCellExist(x, getWrapY(y + 1))) {
			neighbour++;
		}

		// bottom left
		if (universeView.isCellExist(getWrapX(x + 1), getWrapY(y + 1))) {
			neighbour++;
		}

		return neighbour;
	}

	// wrap around x when out of bound
	private int getWrapX(int x) {
		return x < 0 ? x + universeView.getX() : x % universeView.getX();
	}

	// wrap around y when out of bound
	private int getWrapY(int y) {
		return y < 0 ? y + universeView.getY() : y % universeView.getY();
	}
}
