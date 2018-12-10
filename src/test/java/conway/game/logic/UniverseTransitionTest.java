package conway.game.logic;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import conway.game.logic.model.Cell;
import conway.game.logic.model.Universe;

import java.util.stream.Stream;

class UniverseTransitionTest {

	private Universe universeView;

	private UniverseTransition transition;

	private AtomicLong messageId;

	private int universeSize = 5;

	@BeforeEach
	void setup() throws Exception {
		messageId = new AtomicLong();
		universeView = new Universe(universeSize, universeSize);
		transition = new UniverseTransition(messageId, universeView);
	}

	@Test
	void shouldThrowExceptionWithNullIdCounter() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new UniverseTransition(null, universeView));
	}

	@Test
	void shouldThrowExceptionWithNullUniverseView() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new UniverseTransition(messageId, null));
	}

	@Test
	void shouldBeEmptyUniverse() {
		universeView.setCell(new Cell(0, 0, 0, 0, 0));
		universeView.setCell(new Cell(1, 0, 0, 0, 0));

		transition.nextGenerationTransition(null);
		Assertions.assertEquals(0, getNumberOfLiveCells());
	}

	@Test
	void shouldRetainLastUniverseView() {
		universeView.setCell(new Cell(0, 0, 0, 0, 0));
		universeView.setCell(new Cell(1, 0, 0, 0, 0));
		universeView.setCell(new Cell(0, 1, 0, 0, 0));
		universeView.setCell(new Cell(1, 1, 0, 0, 0));

		transition.nextGenerationTransition(null);
		Assertions.assertEquals(4, getNumberOfLiveCells());
		Assertions.assertTrue(universeView.isCellExist(0, 0));
		Assertions.assertTrue(universeView.isCellExist(1, 0));
		Assertions.assertTrue(universeView.isCellExist(0, 1));
		Assertions.assertTrue(universeView.isCellExist(1, 1));
	}

	@Test
	void gliderShouldWrapAroundUniverse() {
		int[][] initialGliderLocation = { { 1, 0 }, { 2, 1 }, { 0, 2 }, { 1, 2 }, { 2, 2 } };
		for (int i = 0; i < initialGliderLocation.length; i++) {
			universeView.setCell(new Cell(initialGliderLocation[i][0], initialGliderLocation[i][1], 0, 0, 0));
		}

		ArrayList<int[][]> gliderPath = new ArrayList<>();
		gliderPath.add(new int[][] { { 0, 1 }, { 2, 1 }, { 1, 2 }, { 2, 2 }, { 1, 3 } });
		gliderPath.add(new int[][] { { 2, 1 }, { 0, 2 }, { 2, 2 }, { 1, 3 }, { 2, 3 } });
		gliderPath.add(new int[][] { { 1, 1 }, { 2, 2 }, { 3, 2 }, { 1, 3 }, { 2, 3 } });
		gliderPath.add(new int[][] { { 2, 1 }, { 3, 2 }, { 1, 3 }, { 2, 3 }, { 3, 3 } });
		gliderPath.add(new int[][] { { 1, 2 }, { 3, 2 }, { 2, 3 }, { 3, 3 }, { 2, 4 } });
		gliderPath.add(new int[][] { { 3, 2 }, { 1, 3 }, { 3, 3 }, { 2, 4 }, { 3, 4 } });
		gliderPath.add(new int[][] { { 2, 2 }, { 3, 3 }, { 4, 3 }, { 2, 4 }, { 3, 4 } });
		gliderPath.add(new int[][] { { 3, 2 }, { 4, 3 }, { 2, 4 }, { 3, 4 }, { 4, 4 } });
		gliderPath.add(new int[][] { { 3, 0 }, { 2, 3 }, { 4, 3 }, { 3, 4 }, { 4, 4 } });
		gliderPath.add(new int[][] { { 3, 0 }, { 4, 0 }, { 4, 3 }, { 2, 4 }, { 4, 4 } });
		gliderPath.add(new int[][] { { 3, 0 }, { 4, 0 }, { 3, 3 }, { 0, 4 }, { 4, 4 } });
		gliderPath.add(new int[][] { { 0, 0 }, { 3, 0 }, { 4, 0 }, { 4, 3 }, { 0, 4 } });
		gliderPath.add(new int[][] { { 0, 0 }, { 4, 0 }, { 4, 1 }, { 0, 4 }, { 3, 4 } });
		gliderPath.add(new int[][] { { 0, 0 }, { 3, 0 }, { 0, 1 }, { 4, 1 }, { 0, 4 } });
		gliderPath.add(new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 4, 1 }, { 4, 4 } });
		gliderPath.add(new int[][] { { 1, 0 }, { 0, 1 }, { 1, 1 }, { 4, 1 }, { 0, 4 } });
		gliderPath.add(new int[][] { { 1, 0 }, { 4, 0 }, { 0, 1 }, { 1, 1 }, { 0, 2 } });
		gliderPath.add(new int[][] { { 1, 0 }, { 1, 1 }, { 4, 1 }, { 0, 2 }, { 1, 2 } });
		gliderPath.add(new int[][] { { 0, 0 }, { 1, 1 }, { 2, 1 }, { 0, 2 }, { 1, 2 } });
		gliderPath.add(new int[][] { { 1, 0 }, { 2, 1 }, { 0, 2 }, { 1, 2 }, { 2, 2 } });
		gliderPath.add(new int[][] { { 0, 1 }, { 2, 1 }, { 1, 2 }, { 2, 2 }, { 1, 3 } });

		gliderPath.stream().forEach(path -> {
			transition.nextGenerationTransition(null);
			Assertions.assertEquals(5, getNumberOfLiveCells());
			Stream.of(path).forEach(point -> Assertions.assertTrue(universeView.isCellExist(point[0], point[1])));
		});
	}

	private int getNumberOfLiveCells() {
		int numOfLiveCells = 0;
		for (int x = 0; x < universeSize; x++) {
			for (int y = 0; y < universeSize; y++) {
				if (universeView.isCellExist(x, y)) {
					numOfLiveCells++;
				}
			}
		}
		return numOfLiveCells;
	}
}
