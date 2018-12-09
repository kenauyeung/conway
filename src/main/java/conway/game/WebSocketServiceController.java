package conway.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static conway.config.WebSocketPath.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import conway.game.logic.UniverseProcessor;
import conway.game.logic.model.Cell;
import conway.game.logic.model.InitializeResponse;
import conway.game.logic.model.Universe;

@Controller
public class WebSocketServiceController {

	private Random random = new Random();

	@Autowired
	private Universe universeView;

	@Autowired
	UniverseProcessor processor;

	@MessageMapping("/addcells")
	public void addCells(@Payload List<Cell> cells, SimpMessageHeaderAccessor headerAccessor) {
		if (cells != null && !cells.isEmpty()) {
			int[] color = (int[]) headerAccessor.getSessionAttributes().get("color");
			if (color != null) {
				cells.stream().forEach(cell -> cell.setColor(color[0], color[1], color[2]));
				processor.updateUniverse(cells);
			}
		}
	}

	@MessageMapping("/initialize")
	@SendToUser(USER_INITIALIZE)
	public InitializeResponse initializeUser(SimpMessageHeaderAccessor headerAccessor) {
		int[] color = { random.nextInt(256), random.nextInt(256), random.nextInt(256) };
		headerAccessor.getSessionAttributes().put("color", color);

		InitializeResponse res = new InitializeResponse();
		res.setColor(color);
		res.setInitialMessage(processor.getUniverseView());

		return res;
	}
}
