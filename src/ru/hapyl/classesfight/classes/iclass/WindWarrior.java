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

package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.Weapon;

public class WindWarrior extends IClass {

	public WindWarrior() {
		super("Wind Warrior", Material.ELYTRA);
		this.setRole(ClassRole.ASSASSIN);
		this.setInfo("Light as a leaf and fast as a samurai. Uses his wind abilities to gain control over his enemies.",
				Abilities.WIND_STRIDER.getAbout(), "Wind Fury", "Instantly slash with your sword to create tornado and launch it forward. Enemies who get close to it will be pulled in and rapidly damaged.", 8);

		this.setUltimateSound(Sound.ITEM_TRIDENT_RIPTIDE_3, 0.0f);

		final ClassEquipment eq = this.getClassEquipment();

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJiYmNjMzc4Njc3NzIzMDYzMWExNjA3NTYyYjc4YTkwYTQ5OGYwZTI0ZTQzZmEwNmQyNzc3NWRjYTE1ZDc3ZCJ9fX0=");
		eq.setChestplate(179, 113, 0);
		eq.setLeggings(230, 145, 0);
		eq.setBoots(255, 190, 77);

		eq.addItem(new Weapon(Material.LILY_PAD).withName("Clover").withDamage(3.0d).build());
		Abilities.WIND_STRIDER.addItemIfExists(eq);
	}

	@Override
	public void useUltimate(Player player) {
		final Location startLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2.5d).setY(0.0d));

		GameUtils.rayTraceLine(startLocation, 30, 0.25, 1, move -> {
			final Location location = move.getLocation();
			final double tT = move.getTick() * 2.0d;
			for (double y = 0.0d; y < 5.0; y += 0.5d) {
				double yM = y / 2.5d;
				for (double t = 0.0d; t < Math.PI * (yM + 0.5d); t += Math.PI / 8) {
					final double x = yM * Math.sin(t + tT);
					final double z = yM * Math.cos(t + tT);
					location.add(x, y, z);
					PlayerLib.spawnParticle(location, Particle.CRIT, 1, 0, 0, 0, 0);
					location.subtract(x, y, z);
				}
			}
			GameUtils.getPlayerInRange(location, 2.0d).forEach(target -> {
				if (target == player) {
					return;
				}
				target.setVelocity(location.toVector().subtract(target.getLocation().toVector()).multiply(0.45d));
				if (move.getTick() % 2.5d == 0) {
					DamageFeature.damage(target, player, 4.5d, EnumDamageCause.TORNADO);
				}
			});
		}, null);

	}

}
