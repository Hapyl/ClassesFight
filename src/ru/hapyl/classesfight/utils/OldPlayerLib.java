package ru.hapyl.classesfight.utils;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public interface OldPlayerLib {

    static void playSound(Player player, Sound sound, float pitch) {
        player.playSound(player.getLocation(), sound, SoundCategory.RECORDS, 5, pitch);
    }

    static void addPotionEffect(Player player, PotionEffectType type, int duration, int amplifier) {
        player.addPotionEffect(new PotionEffect(type, duration, amplifier));
    }

    static void removePotionEffect(Player player, PotionEffectType type) {
        player.removePotionEffect(type);
    }

}
