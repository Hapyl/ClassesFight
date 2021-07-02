package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.PlayerManager;

public class BuildCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if (command.getName().equalsIgnoreCase("build")) {
                if (commandSender.isOp()) {

                    final PlayerManager manager = PlayerManager.current();

                    final boolean hasBuildMode = manager.hasBuildMode(player);
                    manager.setBuildMode(player, !hasBuildMode);

                    if (manager.hasBuildMode(player)) {
                        Chat.sendMessage(player, "&6Build mode enabled, you may now build.");
                        if (player.getGameMode() != GameMode.CREATIVE) {
                            Chat.sendMessage(player, "&6Changed your gamemode to creative.");
                            player.setGameMode(GameMode.CREATIVE);
                        }
                    }
                    else {
                        Chat.sendMessage(player, "&6Build mode disabled, you can no longer build.");
                    }

                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, SoundCategory.RECORDS, 5, 1.25f);

                }
                else {
                    commandSender.sendMessage(ChatColor.RED + "You don't have permissions to do this.");
                }
            }

            return true;
        }
        return true;
    }
}
