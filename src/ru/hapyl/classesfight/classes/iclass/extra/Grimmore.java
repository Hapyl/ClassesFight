/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

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
