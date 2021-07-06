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
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;

public class TripleArrow extends Ability {

	private final double TRIPLE_ARROW_DAMAGE = 1.8d;

	public TripleArrow() {
		super("Multi Shot", "Shoots three arrows into direction you looking.");
		this.setCooldown(75);
		this.setItem(Material.SHEARS);
	}

	@Override
	public Response useAbility(Player player) {

		final Location location = player.getLocation();

		if (location.getWorld() == null) {
			return Response.error("world is null?");
		}

		// fx
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.25f);
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 0.75f);

		final Arrow arrowMiddle = player.launchProjectile(Arrow.class);

		final Arrow arrowLeft = location.getWorld().spawn(player.getLocation().add(player.getLocation().getDirection().add(getVectorLeft(player).multiply(1))).add(0, 1.5, 0), Arrow.class);
		final Arrow arrowRight = location.getWorld().spawn(player.getLocation().add(player.getLocation().getDirection().add(getVectorRight(player).multiply(1))).add(0, 1.5, 0), Arrow.class);

		arrowLeft.setVelocity(arrowMiddle.getVelocity());
		arrowRight.setVelocity(arrowMiddle.getVelocity());

		arrowMiddle.setCritical(true);
		arrowLeft.setCritical(true);
		arrowRight.setCritical(true);

		arrowMiddle.setDamage(TRIPLE_ARROW_DAMAGE);
		arrowLeft.setDamage(TRIPLE_ARROW_DAMAGE);
		arrowRight.setDamage(TRIPLE_ARROW_DAMAGE);

		arrowMiddle.setShooter(player);
		arrowLeft.setShooter(player);
		arrowRight.setShooter(player);

		return Response.OK;
	}

	private Vector getVectorRight(Player player) {
		Vector direction = player.getLocation().getDirection().normalize();
		return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
	}

	private Vector getVectorLeft(Player player) {
		Vector direction = player.getLocation().getDirection().normalize();
		return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
	}

}
