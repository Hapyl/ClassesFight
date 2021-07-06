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

package ru.hapyl.classesfight.cosmetics.contrail;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.BlockContrail;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class BedRockingContrail extends BlockContrail {

	public BedRockingContrail() {
		super("Bed Rockin'", "The strongest of it's kind.", NOT_AVAILABLE_IN_SHOP, Material.BEDROCK, ShopItemRarity.LEGENDARY, Material.BEDROCK);
	}

	@Override
	public void additionalEffect(Player player, Location location) {
		player.spawnParticle(Particle.ASH, location.add(0.0d, 0.25d, 0.0d), 3, 0.2d, 0.0d, 0.2d, 0);
	}
}
