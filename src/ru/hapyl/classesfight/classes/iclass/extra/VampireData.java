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

package ru.hapyl.classesfight.classes.iclass.extra;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.utils.entity.GarbageEntity;

public class VampireData {

	private final Player player;
	private final GarbageEntity<Bat> spirit;
	private int blood;
	private long fuel;

	public VampireData(Player player) {
		this.player = player;
		this.blood = 0;
		this.fuel = 50;
		this.spirit = new GarbageEntity<>(Bat.class);
	}

	public void spawnBat(String name) {
		this.spirit.spawn(player.getLocation(), me -> {
			me.setMaxHealth(1000);
			me.setHealth(1000);
			me.setAwake(true);
			me.setAI(false);
			me.setSilent(true);
			me.setCustomName(Chat.format(name));
			me.setCustomNameVisible(true);
		});
	}

	public void removeBat() {
		this.spirit.kill();
	}

	public GarbageEntity<Bat> getSpiritGarbage() {
		return this.spirit;
	}

	public long getFuel() {
		return fuel;
	}

	public void setFuel(long fuel) {
		this.fuel = Math.min(Math.max(fuel, 0), 100);
	}

	public int getBlood() {
		return blood;
	}

	public Player getPlayer() {
		return player;
	}

	public Entity getSpirit() {
		return spirit.getEntity();
	}

	public void setBlood(int blood) {
		this.blood = Numbers.clamp(blood, 0, 10);
	}

	public void addBlood(int blood) {
		this.setBlood(this.getBlood() + blood);
	}

	public void removeBlood(int blood) {
		this.setBlood(this.getBlood() - blood);
	}

	public void removeFuel(int fuel) {
		this.setFuel(this.getFuel() - fuel);
	}

	public void addFuel(int fuel) {
		this.setFuel(this.getFuel() + fuel);
	}
}
