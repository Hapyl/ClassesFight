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

import kz.hapyl.spigotutils.module.hologram.Hologram;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.entity.Player;

public class BodyData {

	private final Player player;
	private final HumanNPC npc;
	private final Hologram hologram;
	private int health;
	private long lastDamage;

	public BodyData(Player player, HumanNPC npc) {
		this.player = player;
		this.npc = npc;
		this.health = 14;
		this.hologram = new Hologram().addLine(player.getName()).addLine(String.format("&a❤ %s", this.health)).create(npc.getLocation()).showAll();
		this.lastDamage = 0L;
	}

	public int getHealth() {
		return health;
	}

	public void damageIfCan() {
		if (System.currentTimeMillis() - this.lastDamage >= 500 && health > 0) {
			--this.health;
			this.lastDamage = System.currentTimeMillis();
			this.hologram.setLine(1, "&a❤ " + this.health).showAll();
		}
	}

	public Player getPlayer() {
		return player;
	}

	public HumanNPC getNpc() {
		return npc;
	}

	public void remove() {
		this.npc.remove();
		this.hologram.destroy();
	}

}