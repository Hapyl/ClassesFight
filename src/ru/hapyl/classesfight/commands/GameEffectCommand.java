package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.utils.Validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameEffectCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("gameeffect")) {

                if (!GameManager.current().isGameInProgress()) {
                    Chat.sendMessage(player, "&cYou can only this in game.");
                    return true;
                }

                // gameeffect EFFECT ticks

                if (args.length == 2) {

                    final GameEffectType gameEffect = Validator.getEnumValue(GameEffectType.class, args[0]);

                    if (gameEffect == null) {
                        Chat.sendMessage(player, "&cInvalid GameEffect! Try these:\n ", Arrays.toString(GameEffectType.values()));
                        return true;
                    }

                    final int ticks = NumberConversions.toInt(args[1]);

                    if (ticks <= 0) {
                        GameEffectManager.removeEffect(player, gameEffect);
                        Chat.sendMessage(player, "&aStopped %s.", gameEffect.getEffect().getEffectName());
                    }
                    else {
                        GameEffectManager.applyEffect(player, gameEffect, ticks);
                        Chat.sendMessage(player, "&aApplied %s for %s ticks.", gameEffect.getEffect().getEffectName(), ticks);
                    }
                }
                else {
                    Chat.sendMessage(player, "&cInvalid usage! /gameffect (GameEffectType) (long)");
                }

            }
        }
        else {
            sender.sendMessage("You must be a player to use this.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("gameeffect")) {
            if (args.length == 1) {
                return Chat.tabCompleterSort(Chat.arrayToList(GameEffectType.values()), args);
            }
            return Collections.emptyList();
        }
        return null;
    }
}