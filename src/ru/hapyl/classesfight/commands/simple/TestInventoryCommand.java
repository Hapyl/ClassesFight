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
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TestInventoryCommand extends SimpleCommand {

    public TestInventoryCommand(String str) {
        super(str);
        this.setAllowOnlyOp(true);
        this.setAllowOnlyPlayer(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player)sender;

        if (args.length == 1) {
            final int integer = Math.min(Math.max(1, Validate.getInt(args[0])), 20);
            Chat.sendMessage(player, "&aCreating test GUI with %s items...", integer);
            final PlayerGUI gui = new PlayerGUI(player, "Test GUI™ - " + integer, 2 + ((int)Math.ceil((float)integer / 5)));
            final List<ItemStack> temp = new ArrayList<>();
            for (int i = 0; i < integer; i++) {
                temp.add(new ItemBuilder(Material.EMERALD).setAmount(i + 1).toItemStack());
            }
            gui.openInventory();
        }


    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }

}