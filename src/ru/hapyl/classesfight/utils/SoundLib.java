package ru.hapyl.classesfight.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.runnable.GameTask;

public final class SoundLib {

    public static void play(Player listener, Sound sound, float pitch) {
        play(listener, sound, pitch, 20);
    }

    public static void play(Player listener, Sound sound, float pitch, int volume) {
        listener.playSound(listener.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
    }

    public static void play(Sound sound, float pitch, Player... listeners) {
        for (Player listener : listeners) {
            play(listener, sound, pitch);
        }
    }

    public static void play(Location loc, Sound sound, float pitch, int volume) {
        loc.getWorld().playSound(loc, sound, SoundCategory.MASTER, volume, pitch);
    }

    public static void play(Location loc, Sound sound, float pitch) {
        play(loc, sound, pitch, 20);
    }

    public static void playAndCut(Location loc, Sound sound, float pitch, int cutAfter) {
        play(loc, sound, pitch, 20);
        GameTask.runLater(() -> Bukkit.getOnlinePlayers().iterator().forEachRemaining(player -> player.stopSound(sound)), cutAfter);
    }

    public static void playAndCut(Player player, Sound sound, float pitch, int cutAfter) {
        play(player, sound, pitch, 20);
        GameTask.runLater(() -> player.stopSound(sound), cutAfter);
    }


}
