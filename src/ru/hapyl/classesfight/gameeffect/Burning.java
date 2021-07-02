package ru.hapyl.classesfight.gameeffect;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;

public class Burning implements GameEffect {
    @Override
    public void onEffectStart(Player player, Object... extra) {

    }

    @Override
    public void onEffectStop(Player player) {

    }

    @Override
    public String getEffectName() {
        return "Burning";
    }

    @Override
    public long getTaskDelay() {
        return 10;
    }

    @Override
    public void affectTask(Player player) {
        DamageFeature.damage(player, player, 1.0d, EnumDamageCause.FIRE, true);
        GameEffect.displayParticleForBut(player, Particle.FLAME, player.getLocation(), 10, 0.25d, 0.5d, 0.25d, 0.01f);
    }
}
