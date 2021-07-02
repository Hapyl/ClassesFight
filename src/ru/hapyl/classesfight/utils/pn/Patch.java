package ru.hapyl.classesfight.utils.pn;

import com.google.common.collect.Maps;
import kz.hapyl.spigotutils.module.chat.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Patch {

	private final Type type;
	private final Map<Note.Type, List<Note>> about;

	public Patch(Type type) {
		this.type = type;
		this.about = Maps.newHashMap();
	}

	private Patch addNote(Note.Type type, String str) {
		final List<Note> orDefault = this.about.getOrDefault(type, new ArrayList<>());
		orDefault.add(new Note(type, str));
		this.about.put(type, orDefault);
		return this;
	}

	public Patch addBugFix(String note) {
		return this.addNote(Note.Type.BUG_FIX, note);
	}

	public Patch addNerf(String note, Object... replacements) {
		return this.addNerf(Chat.format(note, replacements));
	}

	public Patch addNerf(String note) {
		return this.addNote(Note.Type.NERF, note);
	}

	public Patch addBuff(String note, Object... dot) {
		return this.addBuff(Chat.format(note, dot));
	}

	public Patch addBuff(String note) {
		return this.addNote(Note.Type.BUFF, note);
	}

	public Patch addChange(String note, Object... dot) {
		return this.addChange(Chat.format(note, dot));
	}

	public Patch addChange(String note) {
		return this.addNote(Note.Type.CHANGE, note);
	}

	public Map<Note.Type, List<Note>> getAbout() {
		return about;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Patch{" + "type=" + type + ", about=" + about + '}';
	}
}
