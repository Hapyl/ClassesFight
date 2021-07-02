package ru.hapyl.classesfight.gameeffect;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Strength implements GameEffect {
    @Override
    public void onEffectStart(Player player, Object... extra) {

    }

    @Override
    public void onEffectStop(Player player) {

    }

    @Override
    public long getTaskDelay() {
        return 10;
    }

    @Override
    public void affectTask(Player player) {
        PlayerLib.spawnParticle(player.getLocation(), Particle.CRIMSON_SPORE, 15, 0, 0.5, 0, 0);
    }

    @Override
    public String getEffectName() {
        return "Strength";
    }
}
