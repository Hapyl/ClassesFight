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
import kz.hapyl.spigotutils.module.chat.LazyClickEvent;
import kz.hapyl.spigotutils.module.chat.LazyHoverEvent;
import kz.hapyl.spigotutils.module.command.SimpleAdminCommand;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BlockLocCommand extends SimpleAdminCommand {

	public BlockLocCommand(String str) {
		super(str);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		final Player player = (Player)sender;
		final Block block = player.getTargetBlockExact(20);

		if (block == null) {
			Chat.sendMessage(player, "&cNo valid block in sight!");
			return;
		}

		final Location location = block.getLocation().add(0.5d, 0.0d, 0.5d);
		final String locationString = BukkitUtils.locationToString(location);
		player.spigot()
				.sendMessage(new ComponentBuilder(Chat.format("&aLocation of target block is %s.", locationString))
						.event(LazyHoverEvent.SHOW_TEXT.of(ChatColor.YELLOW + "Click to copy!"))
						.event(LazyClickEvent.SUGGEST_COMMAND.of(locationString))
						.create());


	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}