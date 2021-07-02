package ru.hapyl.classesfight.menu;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;

public enum MapSize {

    SMALL, MEDIUM, LARGE, MASSIVE;

    public String toString() {
        return ChatColor.DARK_GRAY + Chat.capitalize(this.name()) + " Map";
    }


}
