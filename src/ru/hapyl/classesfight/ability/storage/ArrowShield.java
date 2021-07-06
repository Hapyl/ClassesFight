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

import com.google.common.collect.Maps;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.entity.Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArrowShield extends Ability implements Listener {

	private final Map<Player, List<Arrow>> charges;

	public ArrowShield() {
		super("Arrow Shield", "Create a shield of arrows for &b15s&7, that blocks any damage. Upon blocking damage, an arrow will explode in small AoE.");
		this.setItem(Material.STRING);
		this.setCooldownSec(40);

		this.charges = Maps.newHashMap();

	}

	@EventHandler()
	public void handleProjectileHit(ProjectileHitEvent ev) {
		if (ev.getEntity() instanceof Arrow arrow) {
			if (arrow.getScoreboardTags().contains("FAKE_ARROW")) {
				ev.setCancelled(true);
			}
		}
	}

	public int getCharges(Player player) {
		return charges.getOrDefault(player, new ArrayList<>()).size();
	}

	public void removeCharge(Player player) {
		final List<Arrow> list = this.charges.getOrDefault(player, new ArrayList<>());
		final int sizeMinusOne = list.size() - 1;
		if (sizeMinusOne <= 0) {
			Chat.sendMessage(player, "&a🛡 Your shield has broke!");
			charges.remove(player);
		}
		final Arrow arrow = list.get(sizeMinusOne);
		GameUtils.createFakeExplosion(arrow.getLocation(), 1.0d, 1.0d, player, EnumDamageCause.ENTITY_EXPLOSION);
		arrow.remove();
		list.remove(sizeMinusOne);
	}

	@Override
	public void onStop() {
		charges.keySet().forEach(this::removeArrows);
		charges.clear();
	}

	@Override
	public Response useAbility(Player player) {

		final List<Arrow> list = charges.getOrDefault(player, new ArrayList<>());
		removeArrows(player);

		for (int i = 0; i < 4; i++) {
			list.add((Arrow)Entities.ARROW.spawn(player.getLocation(), me -> {
				me.setSilent(true);
				me.setGravity(false);
				me.setDamage(0.0d);
				me.setCritical(false);
				me.addScoreboardTag("FAKE_ARROW");
			}));
		}

		charges.put(player, list);

		new GameTask() {

			private int tick = 15 * 20;
			private double theta = 0.0d;

			@Override
			public void run() {
				final List<Arrow> arrows = charges.getOrDefault(player, new ArrayList<>());

				if (tick-- <= 0 || Spectator.isSpectator(player)) {
					Chat.sendMessage(player, "&a🛡 Your shield has run out!");
					removeArrows(player);
					this.cancel();
					return;
				}

				final Location location = player.getLocation();
				location.setYaw(0.0f);
				location.setPitch(0.0f);
				final double offset = ((Math.PI * 2) / Math.max(arrows.size(), 1));

				int pos = 1;
				for (final Arrow arrow : arrows) {
					final double x = 1.25d * Math.sin(theta + offset * pos);
					final double z = 1.25d * Math.cos(theta + offset * pos);
					location.add(x, 1, z);
					arrow.teleport(location);
					location.subtract(x, 1, z);
					++pos;
				}

				theta += Math.PI / 20;
				if (theta >= Math.PI * 2) {
					theta = 0;
				}


			}
		}.runTaskTimer(0, 1);

		return Response.OK;
	}

	private List<Arrow> getArrows(Player player) {
		return this.charges.getOrDefault(player, new ArrayList<>());
	}

	private void removeArrows(Player player) {
		final List<Arrow> arrows = getArrows(player);
		arrows.forEach(entity -> {
			PlayerLib.spawnParticle(entity.getLocation(), Particle.EXPLOSION_NORMAL, 3, 0, 0, 0, 0.01f);
			entity.remove();
		});
		arrows.clear();
	}

}
