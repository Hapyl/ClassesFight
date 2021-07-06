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
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

public class LaserEye extends Ability {
	public LaserEye() {
		super("Laser Eye", "Activate laser for &b3s&7 that rapidly damages enemies.");
		this.setItem(Material.ENDER_EYE);
		this.setCooldownSec(15);
	}

	@Override
	public Response useAbility(Player player) {
		GameTask.runTaskTimerTimes((task) -> GameUtils.rayTraceLine(player.getEyeLocation(), 50, 0.5d, 0, move -> {
			move.stopIfNotPassable();
			final Location location = move.getLocation();
			if (location.getWorld() == null) {
				return;
			}
			if (move.isStopped()) {
				PlayerLib.spawnParticle(location, Particle.LAVA, 2, 0, 0, 0, 0);
			}
			else {
				location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
			}
		}, hit -> {
			if (hit == player) {
				return;
			}
			DamageFeature.damageTick(hit, player, 0.25d, EnumDamageCause.ENTITY_ATTACK, 10);
		}), 1, 60);
		return Response.OK;
	}
}
