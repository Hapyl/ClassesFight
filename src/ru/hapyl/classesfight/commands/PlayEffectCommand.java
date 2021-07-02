package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.utils.Validator;

import java.util.ArrayList;
import java.util.List;

public class PlayEffectCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("play")) {
                if (args.length >= 1) {
                    EnumEffect effect = Validator.getEnumValue(EnumEffect.class, args[0]);
                    if (effect == null) {
                        Chat.sendMessage(player, "&cInvalid effect.");
                        return true;
                    }
                    effect.display(player);
                    Chat.sendMessage(player, "&aPlaying %s effect for you.", Chat.capitalize(effect));
                }
            }
        }
        else {
            sender.sendMessage("You must be a player to use this.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("play")) {
            if (args.length == 1) {
                final List<String> all = new ArrayList<>(Chat.arrayToList(EnumEffect.values()));
                return Chat.tabCompleterSort(all, args);
            }
        }
        return null;
    }

}