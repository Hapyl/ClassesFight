package ru.hapyl.classesfight.utils.pn.gui;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.utils.pn.PatchNotes;

public class PatchNotesGUI extends PatchGUI {

	public PatchNotesGUI(Player player, PatchNotes patch) {
		super(player, patch, "", 3);
	}

	@Override
	protected void updateMenu() {
		final boolean anyClassChanges = !patch.getClassPatches().isEmpty();

		this.setItem(11, new ItemBuilder(Material.BOOK).setName("&aGeneral Changes").addLore("&7Coming Soon™!").toItemStack());
		this.setItem(13, new ItemBuilder(Material.REPEATING_COMMAND_BLOCK).setName("&aSystem Changes").addLore("&7Coming Soon™!").toItemStack());

		this.setItem(15, new ItemBuilder(Material.TOTEM_OF_UNDYING).setName("&aClass Changes")
				.addSmartLore(anyClassChanges
						? String.format("&8There is &l%s &8class changes.", patch.getClassPatches().values().size())
						: "&8There was no class changes.")
				.addLore("")
				.addLore("&7About Icons:")
				.addLore(" " + ICONS.getChangeIcon() + "- General Change")
				.addLore(" " + ICONS.getBuffIcon() + "- Buff")
				.addLore(" " + ICONS.getNerfIcon() + "- Nerf")
				.addLore(" " + ICONS.getBugFixIcon() + "- Bug Fix")
				.addLore().addLore("&eClick to see class changes")
				.toItemStack(), (player) -> new ClassChangesGUI(player, patch));
	}

}
