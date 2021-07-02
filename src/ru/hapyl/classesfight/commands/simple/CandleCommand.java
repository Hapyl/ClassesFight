package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.command.SimpleAdminCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.utils.candle.CandleUI;

import java.util.List;

public class CandleCommand extends SimpleAdminCommand {

	public CandleCommand(String str) {
		super(str);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		new CandleUI((Player) sender);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}