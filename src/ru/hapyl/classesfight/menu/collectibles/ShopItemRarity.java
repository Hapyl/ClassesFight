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

package ru.hapyl.classesfight.menu.collectibles;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum ShopItemRarity {

	/**
	 * Items that are rarer will appear less likely in the shop.
	 *
	 * @apiNote Needs more logic like dupe protection and shop generation
	 */
	COMMON("Common", Material.GREEN_DYE),
	RARE("Rare", Material.BLUE_DYE),
	EPIC("Epic", Material.PURPLE_DYE),
	LEGENDARY("Legendary", Material.ORANGE_DYE),
	CURSED("Cursed", Material.BLACK_DYE);

	private final String name;
	private final Material icon;

	ShopItemRarity(String name, Material dye) {
		this.name = name;
		this.icon = dye;
	}

	public Material getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}

	private static final ChatColor[] colors = {ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.LIGHT_PURPLE, ChatColor.GOLD, ChatColor.RED};

	public String getColor(boolean bold) {
		return colors[this.ordinal()].toString() + (bold ? ChatColor.BOLD : "");
	}
}
