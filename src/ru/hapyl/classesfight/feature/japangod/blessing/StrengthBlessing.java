package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class StrengthBlessing implements Blessing {

    private static final PotionEffect EFFECT = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 20, 4);

    @Override
    public void bless(Player player) {
        player.addPotionEffect(EFFECT);
    }

    @Override
    public String message() {
        return "You will become stronger!";
    }

    @Override
    public void playSound(Player player) {
        OldPlayerLib.playSound(player, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.75f);
        OldPlayerLib.playSound(player, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.75f);
    }

}
