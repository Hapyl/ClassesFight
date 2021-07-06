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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.StatType;
import ru.hapyl.classesfight.database.entry.StatisticsEntry;
import ru.hapyl.classesfight.utils.CFItemBuilder;

public class InfoMenu extends PlayerGUI {

	public InfoMenu(Player player) {
		super(player, player.getName() + " ▶ Info", 3);
		this.updateMenu();
	}

	private void updateMenu() {
		this.setItem(11, this.buildClassInfoItem());
		this.setItem(13, this.buildCommonStatsItem());
		this.setItem(15, new ItemBuilder(Material.WRITTEN_BOOK).setName("&aRead Tutorial Book").toItemStack(),
				player -> GameManager.current().displayTutorial(player));
		this.setArrowBack("Main Menu", GameMenuGUI::new);
		this.openInventory();
	}

	private ItemStack buildClassInfoItem() {

		final StatisticsEntry statistics = Database.getDatabase(getPlayer()).getStatistics();
		final CFItemBuilder builder = new CFItemBuilder(Material.IRON_AXE).setName("&aClass Stats")
				.addSmartLore("This will display all stats per class you use. Your currently selected class is highlighted.", 35)
				.hideFlags()
				.addLore("");

		for (ClassManager value : ClassManager.getValidClasses()) {
			builder.addLore(Chat.format("%s &7- &a%s⚔&7, &c%s☠&7, &2%s★",
					ClassManager.getClass(getPlayer()) == value ? value.getDisplayName(ChatColor.YELLOW, ChatColor.BOLD) : value.getDisplayName(),
					statistics.getClassKills(value),
					statistics.getClassDeaths(value),
					statistics.getClassWins(value)));
		}

		return builder.build();

	}

	private ItemStack buildCommonStatsItem() {

		final StatisticsEntry statistics = Database.getDatabase(getPlayer()).getStatistics();
		final CFItemBuilder builder = new CFItemBuilder(Material.HAY_BLOCK).setName("&aCommon Stats")
				.setSmartLore("There are all your common stats here, like kills, deaths, wins etc.")
				.addLore("");

		builder.addLore("&7○ Kills &6" + statistics.getStat(StatType.KILLS));
		builder.addLore("&7○ Deaths &6" + statistics.getStat(StatType.DEATHS));
		builder.addLore("&7○ Wins &6" + statistics.getStat(StatType.WINS));
		builder.addLore("&7○ K/D &6" + statistics.getKda());
		builder.addLore("&7○ Abilities used &6" + statistics.getStat(StatType.ABILITY_USED));
		builder.addLore("&7○ Ultimates used &6" + statistics.getStat(StatType.ULTIMATE_USED));
		builder.addLore("&7○ Total time played &6" + calculateTime(getPlayer()));

		return builder.build();

	}

	private String calculateTime(Player player) {

		final long totalTimeSec = Database.getDatabase(player).getStatistics().getStat(StatType.TOTAL_TIME_PLAYED);

		long hours = totalTimeSec / 3600;
		long minutes = (totalTimeSec % 3600) / 60;
		long seconds = totalTimeSec % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);

	}


}
