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

package ru.hapyl.classesfight.feature;

public class DeathMessage {

	private static final String PLAYER_PLACEHOLDER = "{player}";

	private final String message;
	private final String damagerSuffix;

	public DeathMessage(String message, String damagerSuffix) {
		this.message = message;
		this.damagerSuffix = message.contains(PLAYER_PLACEHOLDER) ? "" : damagerSuffix;
	}

	public String getMessage() {
		return message;
	}

	public String formatMessage(String player) {
		return getMessage().replace(PLAYER_PLACEHOLDER, player);
	}

	public boolean hasSuffix() {
		return !this.damagerSuffix.isBlank();
	}

	public String getDamagerSuffix() {
		return damagerSuffix;
	}

	public static DeathMessage of(String message, String suffix) {
		return new DeathMessage(message, suffix);
	}

}
