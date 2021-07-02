package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class LaunchCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("Launch")) {

                if (!player.isOp()) {
                    Chat.sendMessage(player, "&cYou must be OP to use this!");
                    return true;
                }

                // launch (double) (double) (double)

                if (args.length == 3) {

                    double x = NumberConversions.toDouble(args[0]);
                    double y = NumberConversions.toDouble(args[1]);
                    double z = NumberConversions.toDouble(args[2]);

                    player.setVelocity(new Vector(x, y, z));
                    Chat.sendMessage(player, "&aLaunching you with vector of %s, %s, %s.", x, y, z);

                }
                else {
                    Chat.sendMessage(player, "&cInvalid Usage! " + cmd.getUsage());
                }

            }
        }
        else {
            sender.sendMessage("You must be a player to use this.");
        }
        return true;
    }

}