package ru.hapyl.classesfight.feature;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.HashSet;
import java.util.Set;

public class Spectator implements Listener {

    public static final Set<Player> spectators = new HashSet<>();

    public static void add(Player player) {

        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setInvulnerable(true);
        spectators.add(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200000, 20));
        player.getInventory().clear();
        GameUtils.hidePlayer(player);

        // update health idk here for now
        new GameTask() {
            @Override
            public void run() {

            }
        }.runTaskTimer(0, 10);

    }

    public static void remove(Player player) {
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setInvulnerable(false);
        spectators.remove(player);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        GameUtils.showPlayer(player);
    }

    public static void wipe() {
        final Set<Player> hash = new HashSet<>(spectators);
        for (Player g : hash) {
            remove(g);
        }
    }

    public static Set<Player> getSpectators() {
        return spectators;
    }

    public static boolean isSpectator(Player player) {
        return spectators.contains(player);
    }

}
