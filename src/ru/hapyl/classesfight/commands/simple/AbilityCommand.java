package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimpleAdminCommand;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Abilities;

import java.util.List;

public class AbilityCommand extends SimpleAdminCommand {

    public AbilityCommand(String str) {
        super(str);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        final Player player = (Player)sender;
        final GameManager current = GameManager.current();

        if (current.isGameInProgress() && !current.isDebugMode()) {
            Chat.sendMessage(player, "&cYou can only use this in debug mode or in lobby!");
            return;
        }

        if (args.length != 1) {
            Chat.sendMessage(player, "&cNot enough arguments!");
            return;
        }

        final Abilities ability = Validate.getEnumValue(Abilities.class, args[0]);

        if (ability == null) {
            Chat.sendMessage(player, "&cInvalid ability, use tab-completer!");
            return;
        }

        Chat.sendMessage(player, "&aUsed %s! (Ignoring Cooldown)", ability.getName());
        ability.useAbility(player);

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return super.completerSort(super.arrayToList(Abilities.values()), args);
        }
        return null;
    }
}