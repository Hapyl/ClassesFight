package ru.hapyl.classesfight.utils;

import org.bukkit.Bukkit;

public class sou {

    public static void t(Object s) {
        s = s == null ? "null" : s.toString();
        System.out.println(s.toString());
        Bukkit.broadcastMessage(s.toString());
    }

}
