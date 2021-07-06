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
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SpellMode {

	private SpellButton firstButton;
	private SpellButton secondButton;
	private long lastUsed;

	public SpellMode() {
		this.firstButton = null;
		this.secondButton = null;
		this.lastUsed = System.currentTimeMillis();
	}

	public SpellButton[] addAndCheck(SpellButton spell) {

		this.lastUsed = System.currentTimeMillis();

		if (this.firstButton == null) {
			this.firstButton = spell;
			return null;
		}

		this.secondButton = spell;
		return new SpellButton[]{firstButton, secondButton};

	}

	public void clearButtons() {
		this.firstButton = null;
		this.secondButton = null;
	}

	public SpellMode titleCurrent(Player player) {
		Chat.sendTitle(player, " ", String.format("%s&r %s", notNullButton(firstButton), notNullButton(secondButton)), 0, 40, 5);
		//Chat.sendActionbar(player, String.format("%s&r %s", notNullButton(firstButton), notNullButton(secondButton)));
		if (firstButton == null) {
			PlayerLib.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f);
		} else {
			PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 1.0f);
		}
		return this;
	}

	private String notNullButton(SpellButton button) {
		return button == null ? "&8_" : button.toString();
	}

	public boolean isTimedOut() {
		return (System.currentTimeMillis() - this.lastUsed) >= 2000L;
	}

	public enum SpellButton {
		LEFT, RIGHT;

		@Override
		public String toString() {
			return "&a&n" + (this == LEFT ? "L" : "R");
		}
	}

}
