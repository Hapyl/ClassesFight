package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class HealingBlessing implements Blessing {

    public static final double HEALING_AMOUNT = 6.0d;

    @Override
    public void bless(Player player) {
        DamageFeature.addHealth(player, HEALING_AMOUNT);
    }

    @Override
    public String message() {
        return "You will be blessed with life!";
    }

    @Override
    public void playSound(Player player) {
        OldPlayerLib.playSound(player, Sound.ENTITY_VILLAGER_YES, 0.75f);
    }
}