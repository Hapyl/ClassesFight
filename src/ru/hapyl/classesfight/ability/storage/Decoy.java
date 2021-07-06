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
import kz.hapyl.spigotutils.module.reflect.npc.ClickType;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;

public class Decoy extends Ability {
	public Decoy() {
		super("Decoy", "Create a decoy of yourself that moves forward. Enemies who hit it will be damaged and paralyzed for a short duration.");
		this.setItem(Material.WHEAT);
		this.setCooldownSec(20);
	}

	private void onHit(Player owner, Player player) {
		DamageFeature.damage(player, owner, 5.0d, EnumDamageCause.ENTITY_ATTACK);
		PlayerLib.playSound(player, Sound.ENCHANT_THORNS_HIT, 1.0f);
		PlayerLib.playSound(player, Sound.ENTITY_WITCH_CELEBRATE, 2.0f);
		PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 40, 1);
		PlayerLib.addEffect(player, PotionEffectType.SLOW, 40, 4);
	}

	@Override
	public Response useAbility(Player player) {
		final Location location = player.getLocation();

		final HumanNPC npc = new HumanNPC(location, "", player.getName()) {
			@Override
			public void onClick(Player tag, HumanNPC npc, ClickType clickType) {
				if (player == tag) {
					return;
				}
				if (clickType == ClickType.ATTACK) {
					onHit(player, tag);
					npc.remove();
				}
			}
		};

		npc.showAll();
		npc.setEquipment(player.getEquipment());

		new GameTask() {
			private int tick = 0;
			private boolean hitWall;

			@Override
			public void run() {
				if (tick++ >= (200) || !npc.isAlive()) {
					if (npc.isAlive()) {
						npc.remove();
					}
					this.cancel();
					return;
				}

				if (hitWall) {
					return;
				}

				location.add(location.getDirection().setY(0.0d).multiply(0.2d));
				if (!location.getBlock().isPassable()) {
					this.hitWall = true;
					return;
				}
				npc.setLocation(location);


			}
		}.runTaskTimer(0, 1);

		return Response.OK;
	}
}
