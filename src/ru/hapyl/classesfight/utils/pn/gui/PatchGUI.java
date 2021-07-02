package ru.hapyl.classesfight.utils.pn.gui;

import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.utils.pn.PatchNotes;

public class PatchGUI extends PlayerGUI {


	protected final PatchNotes patch;

	public PatchGUI(Player player, PatchNotes patch, String suffix, int size) {
		super(player, "Patch Notes " + patch.getVersion() + (suffix.isEmpty() ? "" : " " + suffix), size);
		this.patch = patch;
		this.updateMenu();
		this.openInventory();
	}

	protected void updateMenu() {

	}

	protected static final Icon ICONS = new Icon() {
		@Override
		public String getChangeIcon() {
			return "&f&l♻ &7";
		}

		@Override
		public String getBuffIcon() {
			return "&a&l↑ &7";
		}

		@Override
		public String getNerfIcon() {
			return "&c&l↓ &7";
		}

		@Override
		public String getBugFixIcon() {
			return "&2&l⛏ &7";
		}
	};

	protected interface Icon {
		String getChangeIcon();

		String getBuffIcon();

		String getNerfIcon();

		String getBugFixIcon();
	}

}
