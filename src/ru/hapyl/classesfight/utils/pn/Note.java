package ru.hapyl.classesfight.utils.pn;

public class Note {

	private final String note;
	private final Type type;

	public Note(Type type, String note) {
		this.type = type;
		this.note = note;
	}

	public String getNote() {
		return note;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Note{" + "note='" + note + '\'' + ", type=" + type + '}';
	}

	public enum Type {
		CUSTOM,
		BUG_FIX,
		BUFF,
		NERF,
		CHANGE
	}

}
