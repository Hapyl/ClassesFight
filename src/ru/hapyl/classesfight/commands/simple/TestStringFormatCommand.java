package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimpleAdminCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.test.StringFormatter;

import java.util.List;

public class TestStringFormatCommand extends SimpleAdminCommand {

    public TestStringFormatCommand(String str) {
        super(str);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        final Player player = (Player)sender;

        if (args.length == 0) {
            Chat.sendMessage(player, "&cPlease provide a valid string!");
            return;
        }

        final StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg).append(" ");
        }

        final String stringToFormat = builder.toString().trim();
        Chat.sendMessage(player, "&a&lString Formatter:");
        Chat.sendMessage(player, new StringFormatter(stringToFormat).format());

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }

}