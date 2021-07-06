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

package ru.hapyl.classesfight.cosmetics.kill;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

import java.util.concurrent.ThreadLocalRandom;

public class CookiesMadness extends AbstractEffect {

	public CookiesMadness() {
		super("Cookie Madness", "More cookies! Mo-o-ore!", 375, Material.COOKIE, ShopItemRarity.RARE);
	}

	@Override
	public void display(Player player) {
		final Location location = player.getLocation();
		for (int i = 0; i < 10; i++) {
			final Item item = location.getWorld().dropItemNaturally(location.clone().subtract(0.5d, -1.0d, 0.5d),
					new ItemBuilder(Material.COOKIE).setName(ThreadLocalRandom.current().nextInt(100, 5000) + "").toItemStack());
			item.setPickupDelay(5000);
			item.setTicksLived(5940);
		}
	}
}
