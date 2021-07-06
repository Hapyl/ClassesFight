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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TamerData {

	private final Player player;
	private TamerPack pack;

	public TamerData(Player player) {
		this.player = player;
	}

	public void removeMinions() {
		if (this.pack != null) {
			this.pack.recall();
		}
	}

	public void newTamerPack(TamerPacks pack) {
		this.removeMinions();
		this.pack = pack.getPack();
		this.pack.spawn(player);
		Chat.sendMessage(player, "&a☀ You just summoned &e%s&a!", pack.getPack().getName());
	}

	public boolean hasPackAndEntityIsInPack(Entity entity) {
		return this.pack != null && this.pack.isInPack(entity);
	}

	public TamerPack getPack() {
		return pack;
	}

	public Player getPlayer() {
		return player;
	}
}
