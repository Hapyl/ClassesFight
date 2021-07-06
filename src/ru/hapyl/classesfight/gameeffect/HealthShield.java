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

import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HealthShield extends VarArgsGameEffect<Double> {

	@Override
	public void onEffectStarts(Player player, Double d) {
		setValue(player, d);
	}

	@Override
	public void onEffectUpdates(Player player, Double aDouble) {
		setValue(player, aDouble);
	}

	@Override
	public void onEffectStop(Player player) {
		removeValue(player);
		PlayerLib.playSound(player, Sound.BLOCK_GLASS_BREAK, 0.0f);
	}

	@Override
	public String getEffectName() {
		return "Shielded";
	}

	@Override
	public String getExtraInfo(Player player) {
		return BukkitUtils.decimalFormat(getValue(player)) + "🛡";
	}

	@Override
	protected Double defaultValue() {
		return 0.0d;
	}
}
