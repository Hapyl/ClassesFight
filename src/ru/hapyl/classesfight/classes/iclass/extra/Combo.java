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

import org.bukkit.entity.Player;

public class Combo {

	private final Player player;

	private int comboHit;
	private long lastComboHit;

	public Combo(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public int getComboIfPresent() {
		this.validateCombo();
		return this.comboHit;
	}

	public void reset() {
		this.comboHit = 0;
		this.lastComboHit = 0;
	}

	private void validateCombo() {
		if ((System.currentTimeMillis() - this.lastComboHit) >= 2500) {
			this.comboHit = 0;
		}
	}

	public void incrementCombo(int comboHit) {
		this.comboHit += comboHit;
		this.lastComboHit = System.currentTimeMillis();
	}

}
