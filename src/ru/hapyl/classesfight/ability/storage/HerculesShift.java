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

package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.runnable.GameTask;

public class HerculesShift extends Ability {
	public HerculesShift() {
		super("Dash", "");
		this.setItem(Material.SPECTRAL_ARROW);
		this.setRemoveItemOnUse(true);
	}

	@Override
	public Response useAbility(Player player) {
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.25f);
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.85f);
		player.setVelocity(player.getLocation().getDirection().normalize().multiply(1.8d));
		grantBack(player, 8 * 20);
		return Response.OK;
	}

	public void grantBack(Player player, int tick) {
		new GameTask() {
			@Override
			public void run() {
				PlayerLib.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1.0f);
				player.getInventory().addItem(getItem().getItem());
			}
		}.runTaskLater(tick);
	}

}
