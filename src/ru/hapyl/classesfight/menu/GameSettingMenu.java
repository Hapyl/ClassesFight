package ru.hapyl.classesfight.menu;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.feature.GameSettings;

public class GameSettingMenu extends PlayerGUI {

	private final GameSettings settings;

	public GameSettingMenu(Player player) {
		super(player, "Game Settings", 3);
		this.settings = GameManager.current().getSettings();
		this.updateMenu();
	}

	private void updateMenu() {
		this.setCloseMenuItem(this.getSize() - 5);

		this.setItem(11, new ItemBuilder(Material.SKELETON_SKULL).setName("&aDisasters")
				.setSmartLore("Disasters can make your game more fun and hardcode!")
				.addLore()
				.addLore("&eClick to manage")
				.toItemStack());
		this.setClick(11, DisasterMenu::new);

		this.openInventory();
	}
}
