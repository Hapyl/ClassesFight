package ru.hapyl.classesfight.classes.iclass.extra;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.utils.PlayerData;

public class Grimmore extends PlayerData {

	private int usedAtLvl;
	private GrimmoreBook book;

	public Grimmore(Player player) {
		super(player);
		this.book = GrimmoreBook.NORMAL;
		this.usedAtLvl = 1;
	}

	public GrimmoreBook getBook() {
		return book;
	}

	public void setBook(GrimmoreBook book) {
		this.book = book;
	}

	public void nextBook() {
		if (this.book != GrimmoreBook.ENCHANTED) {
			this.book = GrimmoreBook.values()[this.book.ordinal() + 1 >= GrimmoreBook.values().length ? 0 : this.book.ordinal() + 1];
		}
	}

	public void usedNow() {
		this.usedAtLvl = this.book.getBookLevel();
	}

	public int getUsedAtLvl() {
		return usedAtLvl;
	}
}
