package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class JumpBoostBlessing implements Blessing {

    private static final PotionEffect EFFECT = new PotionEffect(PotionEffectType.JUMP, 20 * 20, 2);


    @Override
    public void bless(Player player) {
        player.addPotionEffect(EFFECT);
    }

    @Override
    public String message() {
        return "You will jump higher.";
    }

    @Override
    public void playSound(Player player) {
        OldPlayerLib.playSound(player, Sound.ENTITY_RABBIT_DEATH, 0.75f);
    }

}
