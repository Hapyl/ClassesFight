/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight.menu.clazz;

import kz.hapyl.spigotutils.module.chat.Chat;
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
