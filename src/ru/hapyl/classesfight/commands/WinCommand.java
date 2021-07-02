package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GlobalListener;

public class WinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player player) {
			if (cmd.getName().equalsIgnoreCase("testwinfx")) {
				if (player.isOp()) {
					final GameManager current = GameManager.current();
					if (current.isGameInProgress()) {
						current.spawnFireworks(player.getLocation(), false);
					}
					else {
						Chat.sendMessage(player, "&cThe game must be in progress to use this.");
					}
				}
				else {
					Chat.sendMessage(player, "&cYou must be OP to use this.");
				}
			}
		}
		else {
			sender.sendMessage("You must be a player to use this.");
		}
		return true;
	}

}