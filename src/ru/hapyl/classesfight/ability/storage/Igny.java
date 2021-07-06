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
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.utils.GameUtils;

public class Igny extends Ability {

	public Igny() {
		// Damage is scaled of how close enemies are to the center of aura.
		super("Igni", "Fires blazing spirits in front of you that deal damage to enemies.");
		this.setItem(Material.BLAZE_POWDER);
		this.setCooldownSec(10);
	}

	@Override
	public Response useAbility(Player player) {

		final Location location = player.getLocation();
		final Location targetLocation = location.add(player.getLocation().getDirection().multiply(3));

		// fx
		PlayerLib.spawnParticle(targetLocation, Particle.FLAME, 20, 2.0, 0.5, 2.0, 0.01f);
		PlayerLib.playSound(targetLocation, Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);
		PlayerLib.playSound(targetLocation, Sound.ITEM_FIRECHARGE_USE, 0.0f);

		GameUtils.getPlayerInRange(targetLocation, 4d)
				.forEach(target -> {

					if (target == player) {
						return;
					}

					final double distance = targetLocation.distance(target.getLocation());
					double damage = 0.0;

					if (isBetween(distance, 0, 1)) {
						damage = 5.0d;
					}

					else if (isBetween(distance, 1, 2.5)) {
						damage = 3.5d;
					}

					else if (isBetween(distance, 2.5, 4.1d)) {
						damage = 2.0d;
					}

					target.setFireTicks(60);
					DamageFeature.damage(target, player, damage, EnumDamageCause.ENTITY_ATTACK, true);
				});

		return Response.OK;
	}

	private boolean isBetween(double a, double min, double max) {
		return a >= min && a < max;
	}

}
