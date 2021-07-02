package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class SpeedBlessing implements Blessing {

    private static final PotionEffect EFFECT = new PotionEffect(PotionEffectType.SPEED, 20 * 20, 3);

    @Override
    public void bless(Player player) {
        player.addPotionEffect(EFFECT);
    }

    @Override
    public String message() {
        return "You will move faster!";
    }

    @Override
    public void playSound(Player player) {
        OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);
    }
}
