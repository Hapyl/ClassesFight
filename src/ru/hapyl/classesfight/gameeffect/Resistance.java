package ru.hapyl.classesfight.gameeffect;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Resistance implements GameEffect {

    @Override
    public void onEffectStart(Player player, Object... extra) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 69));
    }

    @Override
    public void onEffectStop(Player player) {
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
    }

    @Override
    public long getTaskDelay() {
        return 4;
    }

    @Override
    public void affectTask(Player player) {
        player.getWorld()
                .spawnParticle(Particle.CRIT_MAGIC, player.getLocation().clone().add(0, 1, 0), 7, 0.2, 0.2, 0.2, 0.5);
    }

    @Override
    public String getEffectName() {
        return "Resistance";
    }
}
