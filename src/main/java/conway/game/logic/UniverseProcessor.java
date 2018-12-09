package conway.game.logic;

import static conway.config.WebSocketPath.TOPIC_CELLS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import conway.game.logic.model.Cell;
import conway.game.logic.model.Message;
import conway.game.logic.model.Universe;

@Component
public class UniverseProcessor implements Runnable {

	private Logger logger = LogManager.getLogger(UniverseProcessor.class);

	@Autowired
	private Universe universeView;

	@Autowired
	private SimpMessagingTemplate broadcastTemplate;

	@Value("${universe.processor.pulse}")
	private long pulse;

	private AtomicLong messageId = new AtomicLong();

	private Message universeViewMessage = new Message();

	@PostConstruct
	public void init() {
		if (pulse < 1) {
			throw new IllegalArgumentException("Pulse must be greater than 0");
		}

		universeViewMessage.setXRng(universeView.getX());
		universeViewMessage.setYRng(universeView.getY());

		new Thread(this).start();
	}

	@Override
	public void run() {

		logger.info("Starting UniverseProcessor with pulse[{}ms]", pulse);

		while (true) {
			try {
				Thread.sleep(pulse);
			} catch (InterruptedException e) {
				logger.error("error during pulse", pulse, e);
			}

			calculateNextGeneration();
		}
	}

	public Message getUniverseView() {
		return universeViewMessage;
	}

	private void broadcastMessage(Message msg) {
		broadcastTemplate.convertAndSend(TOPIC_CELLS, msg);
	}

	public void updateUniverse(List<Cell> cells) {
		if (cells != null && !cells.isEmpty()) {
			synchronized (universeView) {
				List<Cell> update = cells.stream().filter(universeView::setCell).collect(Collectors.toList());
				broadcastMessage(new Message(messageId.incrementAndGet(), null, update, null));
			}
		}
	}

	private void calculateNextGeneration() {

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

			deleteList.forEach(cell -> universeView.deleteCell(cell.getXLocation(), cell.getYLocation()));
			createList.forEach(universeView::setCell);

			universeViewMessage.setId(messageId.incrementAndGet());
			universeViewMessage.setMessageData(viewList, null, null);

			if (!createList.isEmpty() || !deleteList.isEmpty()) {
				broadcastMessage(new Message(messageId.incrementAndGet(), null, createList, deleteList));
			}
		}
	}

	private List<Cell> getNeighbours(int x, int y) {
		List<int[]> neighbourHoods = Arrays.asList(new int[] { getWrapX(x - 1), getWrapY(y - 1) },
				new int[] { x, getWrapY(y - 1) }, new int[] { getWrapX(x + 1), getWrapY(y - 1) },
				new int[] { getWrapX(x - 1), y }, new int[] { getWrapX(x + 1), y },
				new int[] { getWrapX(x - 1), getWrapY(y + 1) }, new int[] { x, getWrapY(y + 1) },
				new int[] { getWrapX(x + 1), getWrapY(y + 1) });

		return neighbourHoods.stream().filter(cell -> universeView.isCellExist(cell[0], cell[1]))
				.map(cell -> universeView.getCell(cell[0], cell[1])).collect(Collectors.toList());
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
