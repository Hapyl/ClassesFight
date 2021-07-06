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
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

public class BlindnessPotion extends Ability {

	private final int auraDuration = 200;

	public BlindnessPotion() {
		super("Dr. Ed's Amnesia Extract Serum", "Swiftly throw a potion in the air that explodes and creates an aura for &b10s&7. Enemies that step inside it will be affected by Amnesia; This effect will persist for additional &b1s &7after player leaves the aura. Dr. Ed is immune to this effect.");
		this.setItem(Material.POTION);
		this.setCooldownSec(30);
	}

	@Override
	public Response useAbility(Player player) {

		final int timeBeforeExplode = 20;
		final Location location = player.getLocation();
		final ArmorStand entity = GarbageCollector.spawnEntity(location.clone().add(0.0d, 1.0d, 0.0d), ArmorStand.class, me -> {
			me.setSilent(true);
			me.setMarker(true);
			me.setVisible(false);
			me.getEquipment().setHelmet(new ItemStack(Material.POTION));
		});

		PlayerLib.playSound(location, Sound.ENTITY_CHICKEN_EGG, 0.0f);

		new GameTask() {
			private int tick = 0;

			@Override
			public void run() {

				final Location location = entity.getLocation();

				if (tick++ >= timeBeforeExplode) {

					PlayerLib.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.75f);
					PlayerLib.spawnParticle(location, Particle.CLOUD, 5, 0.1, 0.05, 0.1, 0.02f);

					entity.remove();
					this.cancel();
					return;
				}

				entity.teleport(location.clone().add(0.0d, (0.18d / (tick / Math.PI)), 0.0d));
				entity.setHeadPose(entity.getHeadPose().add(0.15d, 0.0d, 0.0d));

			}
		}.runTaskTimer(0, 1);

		new GameTask() {
			private int tick = 0;

			@Override
			public void run() {

				if (tick++ > auraDuration) {
					this.cancel();
					return;
				}

				Geometry.drawCircle(location, 3.5d, Quality.LOW, new WorldParticle(Particle.END_ROD, 0.0d, 0.0d, 0.0d, 0.01f));
				GameUtils.getPlayerInRange(location, 3.5d).forEach(target -> {

					if (player == target) {
						return;
					}

					GameEffectManager.applyEffect(target, GameEffectType.AMNESIA, 20, true);

				});

			}
		}.runTaskTimer(timeBeforeExplode, 1);

		return Response.OK;
	}
}
