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

package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.CosmeticEntry;
import ru.hapyl.classesfight.utils.Validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CosmeticCommand extends SimpleCommand {

	public CosmeticCommand(String str) {
		super(str);
		this.setAllowOnlyPlayer(true);
		this.setAllowOnlyOp(true);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player)sender;

		// cosmetic grant/revoke PLAYER cosmetic
		if (args.length == 3) {

			final Player onlinePlayer = Bukkit.getPlayer(args[1]);
			final EnumEffect enumEffect = Validator.getEnumValue(EnumEffect.class, args[2]);

			boolean everything = false;

			if (onlinePlayer == null) {
				player.sendMessage(ChatColor.RED + "This player is not online!");
				return;
			}

			if (enumEffect == null) {
				if (args[2].equalsIgnoreCase("*")) {
					everything = true;
				}
				else {
					player.sendMessage("Effect not found!");
					return;
				}
			}

			final Database database = Database.getDatabase(onlinePlayer);
			final CosmeticEntry cosmetics = database.getCosmetics();
			final String cosmeticName = enumEffect == null ? "Everything" : enumEffect.getClazz().getName();

			if (args[0].equalsIgnoreCase("grant")) {

				if (everything) {
					boolean addedNew = false;
					for (EnumEffect value : EnumEffect.values()) {
						if (!cosmetics.hasCosmetic(value)) {
							cosmetics.grantCosmetic(value);
							addedNew = true;
						}
					}
					if (addedNew) {
						Chat.sendMessage(player, "&aGrant all cosmetics to %s.", onlinePlayer.getName());
					}
					else {
						Chat.sendMessage(player, "&c%s already owns every cosmetic. Wow!", onlinePlayer.getName());
					}
					return;
				}

				if (cosmetics.getOwnedCosmetics().contains(enumEffect)) {
					Chat.sendMessage(player, "&c%s already owns this cosmetic!", onlinePlayer.getName());
				}
				else {
					cosmetics.grantCosmetic(enumEffect);
					Chat.sendMessage(player, "&aGranted %s cosmetic to %s.", cosmeticName, onlinePlayer.getName());
					Chat.sendMessage(onlinePlayer, "&6You just receiver %s cosmetic from an admin!", cosmeticName);
				}

			}
			else if (args[0].equalsIgnoreCase("revoke")) {

				if (everything) {
					boolean removeOld = false;
					for (EnumEffect value : EnumEffect.values()) {
						if (cosmetics.hasCosmetic(value)) {
							cosmetics.revokeCosmetic(value);
							removeOld = true;
						}
					}
					if (removeOld) {
						Chat.sendMessage(player, "&aRevoked all cosmetics from %s.", onlinePlayer.getName());
					}
					else {
						Chat.sendMessage(player, "&c%s doesn't own any cosmetics. Lame...", onlinePlayer.getName());
					}
					return;
				}

				if (!cosmetics.getOwnedCosmetics().contains(enumEffect)) {
					Chat.sendMessage(player, "&c%s does not own this cosmetic!", onlinePlayer.getName());
				}
				else {
					cosmetics.revokeCosmetic(enumEffect);
					Chat.sendMessage(player, "&aRevoked %s cosmetic from %s.", cosmeticName);
					Chat.sendMessage(onlinePlayer, "&6An admin revoked your %s cosmetic!", cosmeticName);
				}

			}
			else {
				player.sendMessage("Invalid usage!");
			}

		}
		else {
			player.sendMessage("Not enough arguments!");
		}

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		switch (args.length) {
			case 1: {
				return super.completerSort(Arrays.asList("grant", "revoke"), args);
			}
			case 2: {
				// null -> player list
				return null;
			}

			case 3: {
				final List<String> list = super.arrayToList(EnumEffect.values());
				list.add("*");
				return super.completerSort(list, args);
			}
		}
		return Collections.emptyList();
	}

}