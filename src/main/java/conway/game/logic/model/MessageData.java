package conway.game.logic.model;

import java.util.List;

public class MessageData {

	private List<Cell> view;
	private List<Cell> create;
	private List<Cell> delete;

	public List<Cell> getView() {
		return view;
	}

	public void setView(List<Cell> view) {
		this.view = view;
	}

	public List<Cell> getCreate() {
		return create;
	}

	public void setCreate(List<Cell> create) {
		this.create = create;
	}

	public List<Cell> getDelete() {
		return delete;
	}

	public void setDelete(List<Cell> delete) {
		this.delete = delete;
	}
}