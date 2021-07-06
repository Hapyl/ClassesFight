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

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.ExperienceEntry;
import ru.hapyl.classesfight.experience.ExpGUI;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.menu.clazz.ClassRoleSelectGUI;
import ru.hapyl.classesfight.menu.minigame.MinigameMenu;

public class GameMenuGUI extends PlayerGUI {

	private final Database database;

	public GameMenuGUI(Player player) {
		super(player, "Game Menu", 5);
		this.database = Database.getDatabase(getPlayer());
		this.updateMenu();
	}

	private void updateMenu() {

		final ItemStack blackBar = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&0").toItemStack();
		this.fillItem(0, 8, blackBar);
		this.fillItem(this.getSize() - 9, this.getSize() - 1, blackBar);
		this.setCloseMenuItem(this.getSize() - 5);

		// Map Selector
		this.setItem(11, new ItemBuilder(Material.CLOCK).setName("&aSelect Map").addLore().addLore("&7Current Map").addLore(" &a" + GameMap.getCurrentMap().getMapName()).addLore().addSmartLore("Select a map you wish to play on. Try them all and find your favorite!").toItemStack());
		this.setClick(11, MapSelectMenu::new);

		// Class Select
		this.setItem(13, new ItemBuilder(Material.TOTEM_OF_UNDYING).setName("&aSelect Class").addLore().addLore("&7Selected Class").addLore(" &a" + ClassManager.getClass(getPlayer()).getDisplayName()).addLore().addSmartLore("Select a class you want to play! Some classes must be unlocked from Levelling.").toItemStack());
		this.setClick(13, ClassRoleSelectGUI::new);

		// Levelling
		final ExperienceEntry exp = database.getExperienceEntry();
		final Experience experience = Experience.getInstance();

		final long totalExpForNextLvlScaled = experience.getExpNeededFor(exp.getLvl() + 1) - experience.getExpNeededFor(exp.getLvl());
		final long currentExpScaled = experience.getExp(getPlayer()) - experience.getExpNeededFor(exp.getLvl());

		final double percentDoneBars = (double)(currentExpScaled * 20 / totalExpForNextLvlScaled);
		final double percentDone = (double)(currentExpScaled * 100 / totalExpForNextLvlScaled);

		this.setItem(15, new ItemBuilder(Material.EMERALD)
				.setName("&aLevelling")
				.addLore()
				.addLore("&7Your Level")
				.addLore("&a " + exp.getLvl() + "&7, progress to next level:")
				.addLore("" + buildBars(percentDoneBars) + " &e " + String.format("%.1f", percentDone) + "%")
				.addLore()
				.addSmartLore("Play to level up your skill and unlock awesome rewards!")
				.toItemStack());

		this.setClick(15, ExpGUI::new);

		// Info
		this.setItem(30, new ItemBuilder(Material.BOOK).setName("&aStats").addSmartLore("View your stats here!").toItemStack());
		this.setClick(30, InfoMenu::new);

		// Settings
		this.setItem(32, new ItemBuilder(Material.COMPARATOR).setName("&aSettings").addSmartLore("Tune client side settings to your likings.").toItemStack());
		this.setClick(32, SettingsMenu::new);

		// Minigames
		this.setItem(28, new ItemBuilder(Material.JUKEBOX)
				.setName("&aMini Games")
				.setSmartLore("Play minigames in between games to earn cosmetic rewards!")
				.toItemStack(), MinigameMenu::new);

		// Archive
		this.setItem(34, new ItemBuilder(Material.FLOWER_BANNER_PATTERN)
				.setName("&aArchive")
				.setSmartLore("Everything about everything! This archive contains info about all existing things in Classes Fight!")
				.toItemStack(), this::soundPop);

		// Global Stats Info
		this.setItem(21, new ItemBuilder(Material.PAPER)
				.setName("&aGlobal Class Stats")
				.setSmartLore("All statistics that is collected by the server about every class!")
				.toItemStack(), ClassStatsMenu::new);

		// Codes
		this.setItem(23, new ItemBuilder(Material.DARK_OAK_SIGN).setName("&aRedeem Code").setSmartLore("Redeem a code to get ingame currency and other loot!").toItemStack());
		this.setClick(23, this::soundPop);

		this.openInventory();
	}

	private void soundPop(Player player) {
		PlayerLib.lavaPop(player);
	}

	public String buildBars(double percent) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			if ((int)percent > i) {
				builder.append(ChatColor.GREEN);
			}
			else {
				builder.append(ChatColor.RED);
			}
			builder.append('■');
		}
		return builder.toString();
	}

}
