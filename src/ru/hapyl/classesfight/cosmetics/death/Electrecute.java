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

package ru.hapyl.classesfight.cosmetics.death;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;

public class Electrecute extends Box {

	public Electrecute() {
		super(Material.ICE, Sound.BLOCK_GLASS_BREAK, 0.0f, 40, "Electrocute", "Bzz~t", 1000, Material.LIGHT_BLUE_STAINED_GLASS, ShopItemRarity.LEGENDARY);
	}

	@Override
	public void displayExtra(Location location) {
		GameTask.runTaskTimerTimes((task, tick) -> location.getWorld().strikeLightningEffect(location.clone().add(0.0d, 2.0d, 0.0d)), 20, 2, 10);
	}
}
