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

package ru.hapyl.classesfight.ability.cooldown;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class Cooldown {

	/**
	 * Simple class for creating cooldowns.
	 */

	public static final Map<String, Cooldown> cooldowns = new HashMap<>();
	private static final String idPath = "%s:%s";

	private final Player player;
	private final UUID uuid;
	private final String cooldownName;
	private final String id;
	private final long startedAt;
	private final int millis;
	private boolean done;

	public Cooldown(Player player, String id, String name, int tick) {
		this.player = player;
		this.uuid = player.getUniqueId();
		this.id = id.toLowerCase(Locale.ROOT);
		this.cooldownName = name;
		this.millis = tick * 50;
		this.startedAt = System.currentTimeMillis();
		cooldowns.put(String.format(idPath, this.uuid, this.id), this);
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	@Deprecated
	public Cooldown(UUID uuid, String id, int timeSec) {
		this(Objects.requireNonNull(Bukkit.getPlayer(uuid)), id, "unnamed cooldown", timeSec);
	}

	public void onStop() {

	}

	public String getId() {
		return this.id;
	}

	public boolean stopCooldown() {
		cooldowns.remove(String.format(idPath, this.uuid, this.id));
		return false;
	}

	public String getTimeLeftSec() {
		return new DecimalFormat("00,00").format(this.getTimeLeft() / 10);
	}

	public int getTimeLeft() {
		return (int)(this.millis - ((System.currentTimeMillis() - this.startedAt)));
	}

	// Static Members
	public static List<Cooldown> getCooldowns(Player player) {
		final List<Cooldown> list = new ArrayList<>();
		if (cooldowns.isEmpty()) {
			return list;
		}
		for (final Cooldown value : cooldowns.values()) {
			if (value.player == player) {
				list.add(value);
			}
		}
		return list;
	}

	public static void resetAll() {
		cooldowns.values().forEach(Cooldown::stopCooldown);
		cooldowns.clear();
	}

	@Nullable
	public static Cooldown getCooldown(UUID uuid, String id) {
		return cooldowns.getOrDefault(String.format(idPath, uuid, id), null);
	}

	public static Cooldown getCooldown(Player player, String id) {
		return getCooldown(player.getUniqueId(), id);
	}

	public static boolean isOnCooldown(Player player, String id) {
		return isOnCooldown(player.getUniqueId(), id);
	}

	public static boolean isOnCooldown(UUID uuid, String id) {
		final Cooldown cd = getCooldown(uuid, id);
		if (cd == null) {
			return false; // if not cooldown
		}
		if (cd.getTimeLeft() >= 0) {
			return true; // if not done yet
		}
		return cd.stopCooldown(); // stop
	}

	public String getName() {
		return cooldownName;
	}
}
 