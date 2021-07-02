package ru.hapyl.classesfight.gameeffect;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Amnesia implements GameEffect {
    @Override
    public void onEffectStart(Player player, Object... extra) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10000, 3));
    }

    @Override
    public void onEffectStop(Player player) {
        player.removePotionEffect(PotionEffectType.CONFUSION);
    }

    @Override
    public String getEffectName() {
        return "Amnesia";
    }
}
