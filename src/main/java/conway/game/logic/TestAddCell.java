package conway.game.logic;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import conway.game.logic.model.Cell;

@Component
public class TestAddCell implements Runnable {

	Random rand = new Random();

	@Autowired
	UniverseProcessor processor;

	@PostConstruct
	public void init() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		ArrayList<Cell> cellList = new ArrayList<>();
		
		System.out.println("Start tester");
		while (true) {
			if (rand.nextInt() % 5 == 0) {
				for (int numCell = rand.nextInt(10); numCell >= 0; numCell--) {
					cellList.add(new Cell(rand.nextInt(20), rand.nextInt(20), 0, 0, 0));
				}
				processor.updateUniverse(cellList);
			}

			try {
				Thread.sleep(100 + rand.nextInt(10000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
