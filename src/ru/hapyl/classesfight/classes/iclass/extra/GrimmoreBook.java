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

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.utils.RomanNumber;

public enum GrimmoreBook {

	NORMAL(1, Material.BOOK),
	KNOWLEDGE(2, Material.KNOWLEDGE_BOOK),
	SIGNED(3, Material.WRITTEN_BOOK),
	ENCHANTED(4, Material.ENCHANTED_BOOK);

	private final int bookLevel;
	private final Material book;
	private final ItemStack stack;

	GrimmoreBook(int bookLevel, Material book) {
		this.bookLevel = bookLevel;
		this.book = book;
		this.stack = new ItemBuilder(this.book).setName("&aGrimoire &8&l" + RomanNumber.toRoman(this.bookLevel)).build();
	}

	public ItemStack getStack() {
		return stack;
	}

	public Material getBook() {
		return book;
	}

	public int getBookLevel() {
		return bookLevel;
	}

	public static boolean hasCooldown(Player player) {
		return player.hasCooldown(NORMAL.getBook());
	}

	public static void applyCooldown(Player player, int cd) {
		for (final GrimmoreBook value : values()) {
			player.setCooldown(value.getBook(), cd);
		}
	}

	public static boolean isGrimmoreItem(ItemStack stack) {
		if (stack == null) {
			return false;
		}

		final Material type = stack.getType();
		return type == NORMAL.getBook() || type == KNOWLEDGE.getBook() || type == SIGNED.getBook() || type == ENCHANTED.getBook();
	}

	public boolean isMaxed() {
		return this.getBookLevel() >= 4;
	}
}
