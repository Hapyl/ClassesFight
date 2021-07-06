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

package ru.hapyl.classesfight.gameeffect;

import org.bukkit.entity.Player;

// Should probably use T but...
public class Shield extends VarArgsGameEffect<Integer> {

	@Override
	protected Integer defaultValue() {
		return 0;
	}

	@Override
	public void onEffectStarts(Player player, Integer integer) {
		setValue(player, integer);
	}

	@Override
	public void onEffectStop(Player player) {
		removeValue(player);
	}

	@Override
	public String getExtraInfo(Player player) {
		final int charges = getValue(player);
		return (charges == -1 ? "∞" : charges) + "";
	}

	@Override
	public String getEffectName() {
		return "Shielded";
	}
}
