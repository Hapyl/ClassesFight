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
import kz.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.concurrent.ThreadLocalRandom;

public class BatsPack extends Ability {

	public BatsPack() {
		super("Bats Pack", "Summons a pack of loyal bats in front of you that flies forward, damaging and blinding everyone who got caught.");
		this.setItem(Material.FLINT);
		this.setCooldownSec(10);
	}

	@Override
	public Response useAbility(Player player) {
		final Bat[] bats = new Bat[10];

		for (int i = 0; i < bats.length; i++) {
			bats[i] = spawnBat(player);
		}

		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_BAT_AMBIENT, 1.0f);
		GameTask.runTaskTimerTimes((task, tick) -> {

			for (Bat bat : bats) {
				bat.teleport(bat.getLocation().add(bat.getLocation().getDirection().multiply(0.5d)));
				if (bat.getLocation().getBlock().getType().isOccluding() || bat.isDead()) {
					bat.remove();
					continue;
				}
				GameUtils.getPlayerInRange(bat.getLocation(), 1.0d).forEach(target -> {
					if (target == player) {
						return;
					}
					DamageFeature.damageTick(target, player, 0.1, EnumDamageCause.ENTITY_ATTACK, 5);
					PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 10, 1);
					bat.remove();
				});
			}

			if (tick == 0) {
				for (Bat bat : bats) {
					bat.remove();
				}
			}


		}, 1, 60).addCancelEvent(() -> {
			for (Bat bat : bats) {
				bat.remove();
			}
		});

		return Response.OK;
	}

	private Bat spawnBat(Player player) {
		final Location location = player.getLocation().clone();
		final Location finalLocation = location.add(location.getDirection().setY(0.0d).multiply(2.0d));

		// randomize
		double nextX = ThreadRandom.nextDouble();
		double nextY = ThreadRandom.nextDouble();
		double nextZ = ThreadRandom.nextDouble();

		nextX = ThreadLocalRandom.current().nextBoolean() ? -nextX : nextX;
		nextY = ThreadLocalRandom.current().nextBoolean() ? -nextY : nextY;
		nextZ = ThreadLocalRandom.current().nextBoolean() ? -nextZ : nextZ;

		finalLocation.add(nextX, nextY, nextZ);
		return GarbageCollector.spawnEntity(finalLocation, Bat.class, me -> {
			me.setInvulnerable(true);
			me.setSilent(true);
			me.setAI(false);
			me.setGravity(false);
			me.setAwake(true);
		});
	}

}
