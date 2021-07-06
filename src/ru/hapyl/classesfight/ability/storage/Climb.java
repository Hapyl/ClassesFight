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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.utils.Supplier;

public class Climb extends Ability {
	public Climb() {
		super("Climb", "Use the wall you hug to climb it and perform back-flip, gaining speed boost.");
		this.setItem(Material.LEATHER_BOOTS);
		this.setCooldownSec(8);
	}

	@Override
	public Response useAbility(Player player) {

		final Location playerLocation = player.getLocation();
		final Location location = playerLocation.add(playerLocation.getDirection().multiply(1).setY(0.0d));
		if (location.getBlock().getType().isAir()) {
			return Response.error("Not hugging wall.");
		}

		// Flip
		player.teleport(new Supplier<>(player.getLocation()).supply(loc -> loc.setYaw(loc.getYaw() + 180)));

		player.setVelocity(playerLocation.getDirection().multiply(-1.35).setY(0.75d));
		PlayerLib.addEffect(player, PotionEffectType.SPEED, 60, 2);
		PlayerLib.playSound(playerLocation, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.0f);

		return Response.OK;
	}

}
