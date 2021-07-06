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
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.minigame.Minigame;

import java.util.List;

public class MinigameCommand extends SimpleCommand {

    public MinigameCommand(String str) {
        super(str);
        this.setAllowOnlyPlayer(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player)sender;

        // minigame MINIGAME
        if (args.length >= 1) {

            boolean debug = false;

            if (args.length == 2) {
                debug = args[1].equalsIgnoreCase("-d");
            }

            final Minigame minigame = Validate.getEnumValue(Minigame.class, args[0]);
            if (minigame == null) {
                Chat.sendMessage(player, "&cCould not find minigame %s.", args[0]);
                return;
            }

            Chat.sendMessage(player, "&aStarting %s minigame%s...", minigame.getName(), debug ? " in debug mode" : "");
            minigame.newInstance(player, debug);

        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return super.completerSort(arrayToList(Minigame.values()), args);
        }
        return null;
    }

}