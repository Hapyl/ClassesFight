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

package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.utils.Validator;

public class DamageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("damage")) {
                // damage ATTACK DAMAGE DEFENSE HEALTH

                if (args.length == 3) {

                    EnumInfo attack = Validator.getEnumValue(EnumInfo.class, args[0]);
                    double damage = Validator.getDouble(args[1]);
                    EnumInfo defense = Validator.getEnumValue(EnumInfo.class, args[2]);

                    if (attack == null || defense == null || damage == -999d) {
                        player.sendMessage("Invalid Parameter!");
                        return true;
                    }

                    final double finalDamage = DamageFeature.calculateDamage(attack, damage, defense);
                    Chat.sendMessage(player, "&aYou would damage for %s with %s Attack VS %s Defense.", finalDamage, attack.getValue(), defense.getValue());

                } else player.sendMessage("Invalid Usage! Format: /damage (Attack) (damage) (Defense) (Health)");

            }
        } else {
            sender.sendMessage("You must be a player to use this.");
        }
        return true;
    }

}