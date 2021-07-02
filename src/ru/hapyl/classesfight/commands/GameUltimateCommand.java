package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassUltimate;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class GameUltimateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;

            if (!player.isOp()) return true;

            if (cmd.getName().equalsIgnoreCase("ultimate")) {
                if (GameManager.current().isDebugMode()) {

                    Chat.sendMessage(player, "&aCharged your ultimate!");
                    OldPlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 2.0f);
                    ClassUltimate.setPoints(player, ClassManager.getClass(player).getUltimate().getPoints());

                } else player.sendMessage("You can only use that in debug mode!");
            }
        } else {
            sender.sendMessage("You must be a player to use this.");
        }
        return true;
    }

}