package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class FeedbackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("feedback")) {
                Chat.sendMessage(player, "&cThis command is temporary disabled, sorry!");
                Chat.sendMessage(player, "&cIf you wish to report bugs just tell one of the admins.");
            }
        }
        else {
            sender.sendMessage("You must be a player to use this.");
        }
        return true;
    }

}