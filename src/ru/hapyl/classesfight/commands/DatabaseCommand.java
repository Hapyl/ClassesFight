package ru.hapyl.classesfight.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class DatabaseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("database")) {
                if (player.isOp()) {
                    if (true) {
                        sender.sendMessage(ChatColor.RED + "This command is temporary disabled, sorry!");
                        sender.sendMessage(ChatColor.RED + "This is intentional, do not report this.");
                    }
                    else {
                        onCommand(sender, cmd, args);
                    }
                }
            }
        }
        else {
            sender.sendMessage("You must be a player to use this.");
        }
        return true;
    }

    public void onCommand(CommandSender sender, Command command, String[] args) {

    }

}