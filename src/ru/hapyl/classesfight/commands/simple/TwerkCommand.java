package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.command.SimpleCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.List;

public class TwerkCommand extends SimpleCommand {

    public TwerkCommand(String str) {
        super(str);
    }

    private GameTask task;

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (true) {
            sender.sendMessage(ChatColor.RED + "This command is currently disabled.");
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }

}