package ru.hapyl.classesfight.gameeffect;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;

public class Drowning implements GameEffect {

    @Override
    public void onEffectStart(Player player, Object... extra) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 3));
    }

    @Override
    public void onEffectStop(Player player) {
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }

    @Override
    public String getEffectName() {
        return "Drowning";
    }

    @Override
    public long getTaskDelay() {
        return 10;
    }

    @Override
    public void affectTask(Player player) {
        DamageFeature.damage(player, player, 0.5d, EnumDamageCause.ENTITY_ATTACK, true);
        GameEffect.displayParticleForBut(player, Particle.BUBBLE_POP, player.getLocation(), 10, 0.25d, 0.5d, 0.25d, 0.1f);
    }

}
