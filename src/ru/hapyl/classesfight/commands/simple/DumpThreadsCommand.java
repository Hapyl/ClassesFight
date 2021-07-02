package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DumpThreadsCommand extends SimplePlayerAdminCommand {

	public DumpThreadsCommand(String str) {
		super(str);
	}

	@Override
	protected void execute(Player player, String[] strings) {

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}