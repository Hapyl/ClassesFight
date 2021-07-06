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

import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Draw;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.utils.GameUtils;

public class WindStrider extends Ability {
	private final double distance = 3.0d;

	public WindStrider() {
		// Instantly create swirl of wind that pulls nearby enemies to it but pushes you up in the sky and grants you slow falling for a short duration.
		super("Wind Strider", "Instantly create swirl of wind at your current location. Use the gust of it to pull enemies and fly high up.");
		this.setItem(Material.FEATHER);
		this.setCooldownSec(8);
	}

	@Override
	public Response useAbility(Player player) {

		final Location location = player.getLocation();

		// fx
		PlayerLib.playSound(Sound.ITEM_TRIDENT_RIPTIDE_1, 0.75f);
		Geometry.drawCircle(location, distance, Quality.HIGH, new Draw(Particle.BLOCK_CRACK) {
			@Override
			public void draw(Location location) {
				final World world = location.getWorld();
				if (world == null) {
					return;
				}
				world.spawnParticle(this.getParticle(), location, 1, 0, 0, 0, 0, Material.FLOWERING_AZALEA_LEAVES.createBlockData());
			}
		});

		PlayerLib.addEffect(player, PotionEffectType.SLOW_FALLING, 40, 1);
		GameUtils.getPlayerInRange(location, distance).forEach(target -> {
			if (target == player) {
				target.setVelocity(target.getVelocity().setY(1.25f));
			}
			else {
				target.setVelocity(location.toVector().subtract(target.getLocation().toVector()).multiply(0.6d));
			}
		});

		return Response.OK;
	}
}
