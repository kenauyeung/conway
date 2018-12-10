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

/**
 * This class responsible for handling the universe.
 * 
 * @author Ken
 *
 */
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

	private UniverseTransition universeTransition;

	@PostConstruct
	public void init() {
		if (pulse < 1) {
			throw new IllegalArgumentException("Pulse must be greater than 0");
		}

		universeViewMessage.setXRng(universeView.getX());
		universeViewMessage.setYRng(universeView.getY());

		universeTransition = new UniverseTransition(messageId, universeView);

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

			broadcastMessage(universeTransition.nextGenerationTransition(universeViewMessage));
		}
	}

	public Message getUniverseView() {
		return universeViewMessage;
	}

	private void broadcastMessage(Message msg) {
		if (msg != null) {
			broadcastTemplate.convertAndSend(TOPIC_CELLS, msg);
		}
	}

	public void updateUniverseCells(List<Cell> cells) {
		if (cells != null && !cells.isEmpty()) {
			synchronized (universeView) {
				List<Cell> update = cells.stream().filter(universeView::setCell).collect(Collectors.toList());
				broadcastMessage(new Message(messageId.incrementAndGet(), update, null));
			}
		}
	}
}
