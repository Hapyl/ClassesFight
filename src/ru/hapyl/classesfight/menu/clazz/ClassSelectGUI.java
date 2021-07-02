package ru.hapyl.classesfight.menu.clazz;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.gui.GUI;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.IClass;
import ru.hapyl.classesfight.menu.GameMenuGUI;

import java.util.Comparator;
import java.util.List;

public class ClassSelectGUI extends PlayerGUI {

    private final List<ClassManager> classes;

    public ClassSelectGUI(Player player, IClass.ClassRole role) {
        super(player, "Class Select ▶ " + role.getName(), 5);
        this.classes = ClassManager.getByRole(role);
        this.classes.sort(Comparator.comparingInt(a -> (a.isUnlocked(getPlayer()) ? 0 : 1)));
        this.setArrowBack("Main Menu", GameMenuGUI::new);
        this.updateMenu();
    }

    private void updateMenu() {

        int slot = 10;
        for (ClassManager clazz : this.classes) {
            final boolean isUnlocked = clazz.isUnlocked(getPlayer());
            this.setItem(slot, clazz.getCachedInfo().getItem(getPlayer()));

            if (isUnlocked) {
                this.setClick(slot, player -> ClassManager.setClass(player, clazz, true));
            }
            else {
                this.setClick(slot, player -> {
                    Chat.sendMessage(player, "&cThis class is not yet unlocked!");
                    kz.hapyl.spigotutils.module.player.PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
                });
            }

            slot += slot % 9 == 7 ? 3 : 1;
        }

        this.openInventory();

    }


}
