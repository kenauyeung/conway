package conway.game.logic.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
	private Long id;
	private Integer xRng;
	private Integer yRng;

	@JsonProperty("data")
	private MessageData messageData;

	public Message() {

	}

	public Message(long id, List<Cell> create, List<Cell> delete) {
		this.id = id;
		setMessageData(create, delete);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getXRng() {
		return xRng;
	}

	public void setXRng(Integer xRng) {
		this.xRng = xRng;
	}

	public Integer getYRng() {
		return yRng;
	}

	public void setYRng(Integer yRng) {
		this.yRng = yRng;
	}

	public MessageData getMessageData() {
		return messageData;
	}

	public void setMessageData(MessageData messageData) {
		this.messageData = messageData;
	}

	public void setMessageData(List<Cell> create, List<Cell> delete) {
		if (messageData == null) {
			messageData = new MessageData();
		}
		messageData.setCreate(create);
		messageData.setDelete(delete);
	}
}
