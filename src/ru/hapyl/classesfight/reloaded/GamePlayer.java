package ru.hapyl.classesfight.reloaded;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.cooldown.Cooldown;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.feature.AbilitiesCooldown;
import ru.hapyl.classesfight.feature.EnumDamageCause;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayer {

    public final static Map<UUID, GamePlayer> gamePlayers = new HashMap<>();

    private final Player player;

    private GameManager playerClass;
    private double maxHealth;
    private double health;
    private EnumDamageCause lastDamageCause;
    private Entity lastDamager;

    public GamePlayer(Player player) {
        this.player = player;
        gamePlayers.put(player.getUniqueId(), this);
    }

    public static GamePlayer getGamePlayer(Player player) {
        final GamePlayer game = gamePlayers.get(player.getUniqueId());
        if (game == null) {
            return new GamePlayer(player);
        }
        return game;
    }

    public void reset() {

        // Reset Cooldowns
        for (Material value : Material.values()) {
            if (player.hasCooldown(value)) player.setCooldown(value, 0);
        }

        // Update level
        Experience.getInstance().updatePlayerProgress(player);

        Cooldown.getCooldowns(player).forEach(Cooldown::stopCooldown);

        player.setHealth(20.0d);
        player.setMaxHealth(20.0d);

        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFlySpeed(0.1f);
        player.setWalkSpeed(0.2f);
        player.setFireTicks(0);
        player.setInvulnerable(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.resetPlayerTime();
        player.setArrowsInBody(0);
    }

    public void heal(double amount) {
        this.health = Math.min(amount, this.maxHealth);
    }

    public void damage(double amount) {
        this.health = Math.max(0.0d, amount);
    }

    public Player getPlayer() {
        return player;
    }

    public GameManager getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(GameManager playerClass) {
        this.playerClass = playerClass;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public EnumDamageCause getLastDamageCause() {
        return lastDamageCause;
    }

    public void setLastDamageCause(EnumDamageCause lastDamageCause) {
        this.lastDamageCause = lastDamageCause;
    }

    public Entity getLastDamager() {
        return lastDamager;
    }

    public void setLastDamager(Entity lastDamager) {
        this.lastDamager = lastDamager;
    }
}
