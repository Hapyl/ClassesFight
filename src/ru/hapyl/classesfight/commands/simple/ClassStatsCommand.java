package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.command.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.menu.ClassStatsMenu;

import java.util.List;

public class ClassStatsCommand extends SimpleCommand {

    public ClassStatsCommand(String str) {
        super(str);
        this.setAllowOnlyPlayer(true);
        this.setAliases("cs");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        final Player player = (Player)sender;

        if (args.length == 0) {
            new ClassStatsMenu(player);
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }

}