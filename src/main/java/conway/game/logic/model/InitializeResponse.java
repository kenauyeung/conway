package conway.game.logic.model;

public class InitializeResponse {
	private int[] color;
	private Message initialMessage;

	public int[] getColor() {
		return color;
	}

	public void setColor(int[] color) {
		this.color = color;
	}

	public Message getInitialMessage() {
		return initialMessage;
	}

	public void setInitialMessage(Message initialMessage) {
		this.initialMessage = initialMessage;
	}
}
