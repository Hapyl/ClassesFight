package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.ClassUltimate;

public class UltimateBlessing implements Blessing {

    @Override
    public void bless(Player player) {
        ClassUltimate.chargeUltimate(player);
    }

    @Override
    public String message() {
        return "Your ultimate will be charged!";
    }

    @Override
    public void playSound(Player player) {

    }

}
