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
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.utils.GameUtils;

public class Aard extends Ability {

	public Aard() {
		super("Aard", "Creates a small explosion in front of you that pushes enemies away.");
		this.setItem(Material.HEART_OF_THE_SEA);
		this.setCooldownSec(5);
	}

	@Override
	public Response useAbility(Player player) {

		final Vector vector = player.getLocation().getDirection().setY(0.125d).multiply(2.0d);
		final Location inFront = player.getLocation().add(vector);
		final World world = inFront.getWorld();

		if (world != null) {
			PlayerLib.playSound(inFront, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f);
			PlayerLib.spawnParticle(inFront, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0);
			GameUtils.getLivingEntitiesInRange(inFront, 4.0d).forEach(entity -> {
				if (entity == player || Spectator.isSpectator((Player)entity)) {
					return;
				}
				entity.setVelocity(vector);
			});
		}

		return Response.OK;
	}
}
