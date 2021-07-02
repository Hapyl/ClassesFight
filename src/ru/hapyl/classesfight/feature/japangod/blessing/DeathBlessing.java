package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class DeathBlessing implements Blessing {

    @Override
    public void bless(Player player) {
        DamageFeature.fakeDeath(player);
    }

    @Override
    public String message() {
        return "You won't be remembered...";
    }

    @Override
    public void playSound(Player player) {
        OldPlayerLib.playSound(player, Sound.ENTITY_PILLAGER_DEATH, 0.75f);
    }
}
