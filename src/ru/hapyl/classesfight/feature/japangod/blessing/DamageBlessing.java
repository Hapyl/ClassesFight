package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class DamageBlessing implements Blessing {

    public static final double DAMAGE_AMOUNT = 6.0d;

    @Override
    public void bless(Player player) {
        DamageFeature.addHealth(player, -DAMAGE_AMOUNT);
    }

    @Override
    public String message() {
        return "You will suffer of pain!";
    }

    @Override
    public void playSound(Player player) {
        OldPlayerLib.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.75f);
    }

}
