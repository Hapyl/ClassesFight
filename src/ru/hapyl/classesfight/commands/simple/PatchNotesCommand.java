package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimplePlayerCommand;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.utils.pn.PatchNotes;
import ru.hapyl.classesfight.utils.pn.gui.PatchNotesGUI;

import java.util.ArrayList;
import java.util.List;

public class PatchNotesCommand extends SimplePlayerCommand {

	public PatchNotesCommand(String str) {
		super(str);
	}

	@Override
	protected void execute(Player player, String[] strings) {
		if (strings.length == 0) {
			Chat.sendMessage(player, "&cProvide version to see patch notes.");
			return;
		}

		final double version = Validate.getDouble(strings[0]);
		final PatchNotes patchNotes = PatchNotes.getByVersion(version);

		if (patchNotes == null) {
			Chat.sendMessage(player, "&cCouldn't find patch notes for version \"%s\"!", version);
			return;
		}

		new PatchNotesGUI(player, patchNotes);

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		final List<String> strings = new ArrayList<>();
		for (final Double ver : PatchNotes.byVersion.keySet()) {
			strings.add(ver + "");
		}
		return super.completerSort(strings, args);
	}

}