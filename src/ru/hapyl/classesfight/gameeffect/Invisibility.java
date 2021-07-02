package ru.hapyl.classesfight.gameeffect;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.utils.GameUtils;

public class Invisibility implements GameEffect {

    @Override
    public void onEffectStart(Player player, Object... extra) {
        GameUtils.hidePlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
    }

    @Override
    public void onEffectStop(Player player) {
        GameUtils.showPlayer(player);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    @Override
    public String getEffectName() {
        return "Invisibility";
    }

}
