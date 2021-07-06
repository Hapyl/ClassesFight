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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;

public class GiantSword extends AbstractEffect {

	public GiantSword() {
		super("Giant's Sword", "A big RIP for this warrior.", 2000, Material.IRON_SWORD, ShopItemRarity.LEGENDARY);
	}

	@Override
	public void display(Player player) {
		player.getLocation().getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.RECORDS, 50, 0.5f);
		final Location location = player.getLocation().clone().subtract(1.6d, 0.6d, 4.3d);
		location.setYaw(0.0f);
		location.setPitch(0.0f);
		final Giant giant = player.getWorld().spawn(location, Giant.class, me -> {
			me.setInvisible(true);
			me.setInvulnerable(true);
			me.setCustomNameVisible(false);
			me.setCustomName("Dinnerbone"); // flip em
			me.setGravity(false);
			me.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
		});
		GarbageCollector.add(giant);
		new GameTask() {
			@Override
			public void run() {
				giant.remove();
			}
		}.runTaskLater(60);
	}
}
