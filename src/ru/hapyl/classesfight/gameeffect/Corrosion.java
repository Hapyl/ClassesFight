package ru.hapyl.classesfight.gameeffect;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Corrosion implements GameEffect {
    @Override
    public void onEffectStart(Player player, Object... extra) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 999999, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 4));
    }

    @Override
    public void onEffectStop(Player player) {
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.POISON);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }

    @Override
    public String getEffectName() {
        return "Corrosion";
    }
}
