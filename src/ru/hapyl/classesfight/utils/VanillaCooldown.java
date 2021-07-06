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

package ru.hapyl.classesfight.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class VanillaCooldown {

	public static final VanillaCooldown CONTRAIL_ENTRY = new VanillaCooldown(Material.MUSIC_DISC_CAT, 2);

	private final Material material;
	private final int cooldown;

	public VanillaCooldown(Material material, int cooldown) {
		this.material = material;
		this.cooldown = cooldown;
	}

	public static boolean hasCooldown(Player player, Material material) {
		return player.hasCooldown(material);
	}

	public static void setCooldown(Player player, Material material, int cooldown) {
		player.setCooldown(material, cooldown);
	}

	public static void setCooldown(Player player, VanillaCooldown cooldown) {
		setCooldown(player, cooldown.getMaterial(), cooldown.getCooldown());
	}

	public static boolean hasCooldown(Player player, VanillaCooldown cooldown) {
		return hasCooldown(player, cooldown.getMaterial());
	}

	public Material getMaterial() {
		return material;
	}

	public int getCooldown() {
		return cooldown;
	}
}
