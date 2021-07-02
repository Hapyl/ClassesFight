package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.command.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RefreshPlayerCommand extends SimpleCommand {

    public RefreshPlayerCommand(String str) {
        super(str);
        this.setAllowOnlyPlayer(true);
        this.setAllowOnlyOp(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player)sender;

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }

}