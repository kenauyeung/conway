package conway.game.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import conway.game.logic.model.Cell;
import conway.game.logic.model.Message;
import conway.game.logic.model.Universe;

/**
 * This class responsible for handling universe transition calculation.
 * @author Ken
 *
 */
public class UniverseTransition {

	private Universe universeView;
	private AtomicLong messageId;

	public UniverseTransition(AtomicLong messageId, Universe universeView) {
		this.messageId = messageId;
		this.universeView = universeView;
	}

	public Message nextGenerationTransition(Message universeViewMessage) {

		synchronized (universeView) {
			List<Cell> viewList = new ArrayList<>();
			List<Cell> createList = new ArrayList<>();
			List<Cell> deleteList = new ArrayList<>();

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

					List<Cell> neighbours = getNeighbours(x, y);
					Cell currentCell = universeView.getCell(x, y);
					if (currentCell != null) {
						if (neighbours.size() < 2 || neighbours.size() > 3) {
							deleteList.add(currentCell);
							currentCell = null;
						}
					} else if (neighbours.size() == 3) {
						int red = neighbours.stream().mapToInt(Cell::getRed).sum();
						int green = neighbours.stream().mapToInt(Cell::getGreen).sum();
						int blue = neighbours.stream().mapToInt(Cell::getBlue).sum();
						currentCell = new Cell(x, y, red / 3, green / 3, blue / 3);
						createList.add(currentCell);
					}

					if (currentCell != null) {
						viewList.add(currentCell);
					}
				}
			}

			deleteList.forEach(cell -> universeView.deleteCell(cell.getX(), cell.getY()));
			createList.forEach(universeView::setCell);

			universeViewMessage.setId(messageId.incrementAndGet());
			universeViewMessage.setMessageData(viewList, null);

			if (!createList.isEmpty() || !deleteList.isEmpty()) {
				return new Message(messageId.incrementAndGet(), createList, deleteList);
			}
		}
		
		return null;
	}

	/**
	 * Get the 8 neighbour cells around the cell at x,y
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private List<Cell> getNeighbours(int x, int y) {
		List<int[]> neighbourHoods = Arrays.asList(new int[] { getWrapX(x - 1), getWrapY(y - 1) },
				new int[] { x, getWrapY(y - 1) }, new int[] { getWrapX(x + 1), getWrapY(y - 1) },
				new int[] { getWrapX(x - 1), y }, new int[] { getWrapX(x + 1), y },
				new int[] { getWrapX(x - 1), getWrapY(y + 1) }, new int[] { x, getWrapY(y + 1) },
				new int[] { getWrapX(x + 1), getWrapY(y + 1) });

		return neighbourHoods.stream().filter(cell -> universeView.isCellExist(cell[0], cell[1]))
				.map(cell -> universeView.getCell(cell[0], cell[1])).collect(Collectors.toList());
	}

	/**
	 * wrap around x when out of bound
	 * 
	 * @param x
	 * @return
	 */
	private int getWrapX(int x) {
		return x < 0 ? x + universeView.getX() : x % universeView.getX();
	}

	/**
	 * wrap around y when out of bound
	 * 
	 * @param y
	 * @return
	 */
	private int getWrapY(int y) {
		return y < 0 ? y + universeView.getY() : y % universeView.getY();
	}
}
