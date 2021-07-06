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

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.cosmetics.Prefix;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class CustomChat implements Listener {

	private static final String FORMAT = "&e(Lv$LVL$) $DEAD$__$CLASS$ $PREFIX$$NAME_COLOR$__$NAME$: ";

	@EventHandler(ignoreCancelled = true)
	public void handleChat(AsyncPlayerChatEvent ev) {

		ev.setCancelled(true);
		final Player player = ev.getPlayer();
		String message = ev.getMessage().replace("%", "%%");

		if (player.isOp()) {
			message = ChatColor.translateAlternateColorCodes('&', message);
		}

		final Database database = Database.getDatabase(player);
		final EnumEffect prefix = database.getCosmetics().getCurrentEffect(EnumEffect.Type.PREFIX);

		final String finalString = new Placeholder(FORMAT.replace("__", ""))
				.replace("LVL", database.getExperienceEntry().getLvl())
				.replace("DEAD", Spectator.isSpectator(player) ? "&4☠☠☠ " : "")
				.replace("CLASS", ClassManager.getClass(player).getDisplayName())
				.replace("NAME_COLOR", player.isOp() ? ChatColor.RED : ChatColor.YELLOW)
				.replace("PREFIX", prefix == null ? "" : (prefix.isEnabled(player) ? ((Prefix)prefix.getClazz()).getPrefix() + " " : ""))
				.replace("NAME", player.getName())
				.buildString();

		String finalMessage1 = message;
		Bukkit.getOnlinePlayers().forEach(s -> {
			String finalMessage = finalMessage1;
			final String playerName = "@" + s.getName();
			if (finalMessage.contains(playerName)) {
				finalMessage = finalMessage.replace(playerName, ChatColor.YELLOW + playerName + ChatColor.RESET);
				OldPlayerLib.playSound(s, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f);
			}
			s.sendMessage(Chat.format(finalString) + ChatColor.RESET + finalMessage);
		});

	}

	private static class Placeholder {

		private static final char PLACE_HOLDER_CHAR = '$';
		private String input;

		protected Placeholder(String input) {
			this.input = input;
		}

		protected Placeholder replace(String placeholderText, Object replacement) {
			placeholderText = placeholderText.replace("$", "");
			input = input.replace(PLACE_HOLDER_CHAR + placeholderText + PLACE_HOLDER_CHAR, replacement.toString());
			return this;
		}

		protected String buildString() {
			return this.input.replace("$", "");
		}

	}


}
