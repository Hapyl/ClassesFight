package ru.hapyl.classesfight.gameeffect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public interface GameEffect {

    /**
     * Executes whenever effect first applied.
     *
     * @param player - Receiver.
     */
    void onEffectStart(Player player, Object... extra);

    /**
     * Executes whenever effect stops.
     *
     * @param player - Receiver.
     */
    void onEffectStop(Player player);

    /**
     * Returns a string name of effect.
     */
    String getEffectName();

    /**
     * If value greater than 0L then 'affectTask' will
     * be played after delay until effect will stop.
     */
    default long getTaskDelay() {
        return 0L;
    }

    /**
     * Executes if 'getTaskDelay' greater than 0.
     *
     * @param player - Receiver.
     */
    default void affectTask(Player player) {

    }

    /**
     * Will be displayed in TAB after the time, can be used to show charged of the shield, etc.
     */
    default String getExtraInfo(Player player) {
        return "";
    }

    static void displayParticleForBut(Player player, Particle particle, Location loc, int amount, double oX, double oY, double oZ, float speed) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online == player) {
                continue;
            }
            online.spawnParticle(particle, loc, amount, oX, oY, oZ, speed);
        }
    }


}
