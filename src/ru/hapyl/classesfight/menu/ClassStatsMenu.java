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

package ru.hapyl.classesfight.menu;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.stats.StatsContainer;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ClassStatsMenu extends PlayerGUI {

	private final List<ClassManager> playerSort;
	private Sort currentSort;

	public ClassStatsMenu(Player player) {
		super(player, "Class Stats", 4);
		this.playerSort = new LinkedList<>();
		this.playerSort.addAll(ClassManager.getValidClasses());
		this.currentSort = Sort.DEFAULT;
		this.sortClasses();
		this.updateMenu(0);
	}

	private enum Sort {

		DEFAULT("Default A to Z sort."),

		MOST_KILLS("Whichever has the most kills."),
		LEAST_KILLS("Whichever has the least kills."),

		MOST_DEATHS("Whichever has the most deaths."),
		LEAST_DEATHS("Whichever has the least deaths."),

		MOST_WINS("Whichever has the most wins."),
		LEAST_WINS("Whichever has the least wins."),

		MOST_PLAYED("Whichever is the most played."),
		LEAST_PLAYED("Whichever is the least played"),

		MOST_ULTIMATES_USED("Whichever has used the most ultimates."),
		LEAST_ULTIMATES_USED("Whichever has used the least ultimates.");

		private final String about;

		Sort(String about) {
			this.about = about;
		}

		public String getAbout() {
			return about;
		}

		public Sort next() {
			return values()[(this.ordinal() + 1 >= values().length) ? 0 : this.ordinal() + 1];
		}

		public Sort prev() {
			return values()[(this.ordinal() - 1 < 0) ? (values().length - 1) : this.ordinal() - 1];
		}

	}

	private void updateMenu(int start) {
		this.clearItems();

		int slot = 10;
		for (int i = start; i < start + 14; i++) {
			if (i >= this.playerSort.size()) {
				break;
			}
			final ClassManager clazz = this.playerSort.get(i);
			this.setItem(slot, this.createClassIcon(clazz));
			slot += (slot % 9 == 7) ? 3 : 1;
		}

		// ** Buttons

		// Go Back
		this.setItem(31, new ItemBuilder(Material.SPECTRAL_ARROW).setName("&aGo Back").setLore("&7To Main Menu").toItemStack(), GameMenuGUI::new);

		// Prev Page
		if (start >= 14) {
			this.setItem(30, new ItemBuilder(Material.ARROW).setName("&aPrevious Page").toItemStack());
			this.setClick(30, player -> this.updateMenu(start - 14));
		}

		// Next Page
		if (this.playerSort.size() - (start + 14) >= 1) {
			this.setItem(32, new ItemBuilder(Material.ARROW).setName("&aNext Page").toItemStack());
			this.setClick(32, player -> this.updateMenu(start + 14));
		}

		// Resort
		this.setItem(34, this.createSortItem());

		this.setClick(34, player -> {
			this.currentSort = this.currentSort.next();
			OldPlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 2.0f);
			this.sortClasses();
			this.updateMenu(0);
		}, ClickType.LEFT, ClickType.SHIFT_LEFT);

		this.setClick(34, player -> {
			this.currentSort = this.currentSort.prev();
			OldPlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 2.0f);
			this.sortClasses();
			this.updateMenu(0);
		}, ClickType.RIGHT, ClickType.SHIFT_RIGHT);

		this.openInventory();
	}

	private ItemStack createSortItem() {
		final ItemBuilder builder = new ItemBuilder(Material.NAME_TAG).setName("&aSort").addSmartLore("Sort classes by your wishes.").addLore();
		for (Sort value : Sort.values()) {
			final boolean isCurrentValue = value == this.currentSort;
			builder.addLore((isCurrentValue ? " &b&l" : "&8") + Chat.capitalize(value));
			if (isCurrentValue) {
				builder.addSmartLore(value.getAbout(), "  ");
			}
		}
		builder.addLore();
		builder.addLore("&eLeft-Click to cycle forwards");
		builder.addLore("&eRight-Click to cycle backwards");
		return builder.toItemStack();
	}

	private ItemStack createClassIcon(ClassManager c) {

		final ItemBuilder builder = new ItemBuilder(c.getTheClass().getItem()).setName(c.getDisplayName());

		final long wins = c.getStat(StatsContainer.ClassStat.WIN, 0L);
		final long loses = c.getStat(StatsContainer.ClassStat.LOSE, 0L);
		final long playedTimes = c.getStat(StatsContainer.ClassStat.PLAYED_TIMES, 0L);

		final float winPercent = (wins == 0 || playedTimes == 0) ? (float)0.0 : (((float)wins / playedTimes) * 100f);

		builder.addLore("&7Times Played &b%s", playedTimes);

		builder.addLore("&7Wins &b%s", wins);
		builder.addLore("&7Loses &b%s", loses);

		builder.addLore("&7Kills &b%s", c.getStat(StatsContainer.ClassStat.KILL, 0L));
		builder.addLore("&7Deaths &b%s", c.getStat(StatsContainer.ClassStat.DEATH, 0L));

		builder.addLore("&7Ultimate Used &b%s", c.getStat(StatsContainer.ClassStat.ULT_USE, 0L));
		builder.addLore();
		builder.addLore("&7Win Rate &b%s%%", BukkitUtils.decimalFormat(Float.isNaN(winPercent) ? 0.0f : winPercent));

		return builder.toItemStack();

	}

	private void sortClasses() {

		switch (this.currentSort) {

			case DEFAULT: {
				this.playerSort.sort(Comparator.comparing(a -> (a.getDisplayName().charAt(0) + "")));
				break;
			}

			case MOST_WINS:
			case LEAST_WINS: {
				this.playerSort.sort((a, b) -> {
					final long aWins = a.getStat(StatsContainer.ClassStat.WIN, 0);
					final long bWins = b.getStat(StatsContainer.ClassStat.WIN, 0L);
					return this.currentSort == Sort.LEAST_WINS ? (int)(aWins - bWins) : (int)(bWins - aWins);
				});
				break;
			}

			case MOST_DEATHS:
			case LEAST_DEATHS: {
				this.playerSort.sort((a, b) -> {
					final long aWins = a.getStat(StatsContainer.ClassStat.DEATH, 0);
					final long bWins = b.getStat(StatsContainer.ClassStat.DEATH, 0L);
					return this.currentSort == Sort.LEAST_DEATHS ? (int)(aWins - bWins) : (int)(bWins - aWins);
				});
				break;
			}

			case MOST_KILLS:
			case LEAST_KILLS: {
				this.playerSort.sort((a, b) -> {
					final long aWins = a.getStat(StatsContainer.ClassStat.KILL, 0);
					final long bWins = b.getStat(StatsContainer.ClassStat.KILL, 0L);
					return this.currentSort == Sort.LEAST_KILLS ? (int)(aWins - bWins) : (int)(bWins - aWins);
				});
				break;
			}

			case MOST_PLAYED:
			case LEAST_PLAYED: {
				this.playerSort.sort((a, b) -> {
					final long aWins = a.getStat(StatsContainer.ClassStat.PLAYED_TIMES, 0);
					final long bWins = b.getStat(StatsContainer.ClassStat.PLAYED_TIMES, 0L);
					return this.currentSort == Sort.LEAST_PLAYED ? (int)(aWins - bWins) : (int)(bWins - aWins);
				});
				break;
			}

			case MOST_ULTIMATES_USED:
			case LEAST_ULTIMATES_USED: {
				this.playerSort.sort((a, b) -> {
					final long aWins = a.getStat(StatsContainer.ClassStat.ULT_USE, 0);
					final long bWins = b.getStat(StatsContainer.ClassStat.ULT_USE, 0L);
					return this.currentSort == Sort.LEAST_ULTIMATES_USED ? (int)(aWins - bWins) : (int)(bWins - aWins);
				});
				break;
			}

		}

	}


}
