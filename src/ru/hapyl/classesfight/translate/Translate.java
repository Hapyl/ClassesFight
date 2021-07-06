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

package ru.hapyl.classesfight.translate;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

public enum Translate {

	// Common
	NO_PERMISSION("&cYou don't have enough permissions!"),

	TEST_MESSAGE("This is a test locale message for you, {}!"),

	// Scoreboards
	SCORE_GAME("Game"),
	SCORE_MODE("Mode"),
	SCORE_GAME_STATE("State"),
	SCORE_PLAYERS("Players"),
	SCORE_YOU("You, {}"),
	SCORE_PLAYER_CLASS("Class"),
	SCORE_PLAYER_COINS("Coins"),
	SCORE_PLAYER_RUBIES("Rubies");

	private final String key;
	private final String def;

	Translate(String def) {
		// NO_PERMISSION
		// no-permission
		this.key = this.name().toLowerCase().replace("_", "-");
		this.def = def;
	}

	public String getString(Locale locale) {
		return TranslateManager.current().getLocale(locale).getByKeyOr(this.key, this.def);
	}

	public String getString() {
		return this.def;
	}

	public void sendMessage(Player player, Object... replacements) {
		final String format = Chat.bformat(getString(), replacements);
		Chat.sendMessage(player, format);
	}

}
