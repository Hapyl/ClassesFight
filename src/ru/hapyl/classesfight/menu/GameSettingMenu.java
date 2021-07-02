package ru.hapyl.classesfight.menu;

import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.feature.GameSettings;

public class GameSettingMenu extends PlayerGUI {

    private final GameSettings settings;

    public GameSettingMenu(Player player) {
        super(player, "Game Settings", 3);
        this.settings = GameManager.current().getSettings();
        this.updateMenu();
        this.setCloseMenuItem();
    }

    private void updateMenu() {
        this.clearEverything();



        this.openInventory();
    }
}
