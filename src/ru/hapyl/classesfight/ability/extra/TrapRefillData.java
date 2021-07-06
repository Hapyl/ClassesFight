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

package ru.hapyl.classesfight.ability.extra;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.PlayerData;

import java.util.Random;

public class TrapRefillData extends PlayerData {

	private final ArmorStand entity;
	private int uses = 4;
	private TrapType nextTrap;

	public TrapRefillData(Player player, ArmorStand entity) {
		super(player);
		this.entity = entity;
		this.nextTrap = TrapType.WALL_TRAP;
	}

	public double getDistance() {
		return distance;
	}

	private double distance = 0.75f;
	private GameTask task;
	private final Sound[] sounds = {Sound.ITEM_AXE_WAX_OFF, Sound.ENTITY_IRON_GOLEM_REPAIR, Sound.ITEM_SHIELD_BLOCK};

	public void startTask() {
		final Player player = getPlayer();
		final Location location = player.getLocation();
		this.stopTask();
		this.task = new GameTask() {

			private final int maxTick = 160;
			private int tick = 0;

			@Override
			public void run() {
				if (!player.isSneaking() || player.getLocation().distance(location) >= distance || entity.isDead()) {
					stopTask();
					Chat.sendTitle(player, "&4&l⛏", "&cInterrupted", 0, 10, 5);
					return;
				}

				if (tick % 40 == 0) {
					PlayerLib.playSound(location, CollectionUtils.randomElement(sounds), (float)(Math.min(new Random().nextFloat() + 1.0f, 2.0)));
				}

				final StringBuilder builder = new StringBuilder();
				final int percent = tick * 10 / maxTick;
				for (int i = 0; i < 10; i++) {
					builder.append(i < percent ? "&6" : "&7").append("⛏");
				}
				Chat.sendTitle(player, builder.toString(), String.format("&eCrafting %s...", nextTrap.getTrapName()), 0, 10, 0);

				if (tick++ >= maxTick) {
					final TrapType trap = getTrapAndNext();
					player.getInventory().addItem(trap.getAbility().getItem().getItem());
					Chat.sendTitle(player, "&2&l⛏", String.format("&aCrafted %s!", trap.getTrapName()), 10, 30, 10);
					PlayerLib.playSound(location, Sound.BLOCK_ANVIL_USE, 2.0f);
					stopTask();
					if (--uses <= 0) {
						stopTask();
						Chat.sendMessage(player, "&aYour Toolbox run out of materials and disappeared.");
						PlayerLib.playSound(entity.getLocation(), Sound.BLOCK_ANVIL_BREAK, 0.75f);
						PlayerLib.spawnParticle(entity.getLocation().add(0.0d, 1.4d, 0.0d), Particle.EXPLOSION_NORMAL, 5, 0.1d, 0.05d, 0.1d, 0.025f);
						entity.remove();
					}
					else {
						Chat.sendMessage(player, "&aToolbox materials %s%%", BukkitUtils.decimalFormat(uses * 100 / 4));
					}
				}

			}
		}.runTaskTimer(1, 1);
	}

	public void stopTask() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
	}

	public ArmorStand getEntity() {
		return entity;
	}

	public TrapType getTrapAndNext() {
		this.nextTrap = this.nextTrap.next();
		return nextTrap;
	}

	public enum TrapType {
		FLOOR_TRAP(Abilities.TRAP_CAGE.getAbility()),
		WALL_TRAP(Abilities.TRAP_TRIPWIRE.getAbility());

		private final Ability ability;

		TrapType(Ability ability) {
			this.ability = ability;
		}

		public String getTrapName() {
			return ability.getName();
		}

		public Ability getAbility() {
			return ability;
		}

		public TrapType next() {
			return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
		}
	}


}
