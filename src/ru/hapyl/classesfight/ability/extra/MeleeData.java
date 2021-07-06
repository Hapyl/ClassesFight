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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.storage.MeleeStance;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.PlayerData;

public class MeleeData extends PlayerData {

	private final long enteredAt;
	private final ItemStack rangeItem;
	private final GameTask task;

	public MeleeData(Player player, ItemStack rangeItem) {
		super(player);
		this.enteredAt = System.currentTimeMillis();
		this.rangeItem = rangeItem;
		long maxMeleeStanceTime = 600;
		this.task = new GameTask() {
			@Override
			public void run() {
				((MeleeStance)Abilities.MELEE_STANCE.getAbility()).switchToRange(player);
			}
		}.runTaskLater(maxMeleeStanceTime);
	}

	public void cancelTask() {
		this.task.cancel();
	}

	public long getEnteredAt() {
		return enteredAt;
	}


	public ItemStack getRangeItem() {
		return rangeItem;
	}
}
