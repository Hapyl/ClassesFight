package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
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