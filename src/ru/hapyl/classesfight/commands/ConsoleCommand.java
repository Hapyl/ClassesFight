package ru.hapyl.classesfight.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConsoleCommand implements CommandExecutor {

    private final List<UUID> allowedUUID = new ArrayList<>();

    {
        // hapyl
        allowedUUID.add(UUID.fromString("b58e578c-8e36-4789-af50-1ee7400307c0"));
        allowedUUID.add(UUID.fromString("cf3fbee2-431b-33d9-851d-dae239a1936a"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("console")) {

            if (sender instanceof Player) {

                Player player = (Player) sender;

                if (args.length >= 1) {

                    if (allowedUUID.contains(player.getUniqueId())) {

                        StringBuilder all = new StringBuilder();

                        for (String arg : args) {
                            all.append(arg.replace('&', '§')).append(" ");
                        }

                        player.sendMessage(ChatColor.GREEN + "Made console perform command: " + all);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), all.toString());

                    } else {
                        player.sendMessage(ChatColor.RED + "You are not allowed to use this command!");
                    }

                } else {
                    player.sendMessage(ChatColor.RED + "Not enough arguments. Usage: " + cmd.getUsage());
                }

            }
        }

        return true;
    }


}
