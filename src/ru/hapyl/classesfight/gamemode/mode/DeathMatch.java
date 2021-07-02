package ru.hapyl.classesfight.gamemode.mode;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.gamemode.CFGameMode;
import ru.hapyl.classesfight.runnable.GameTask;

public class DeathMatch extends CFGameMode {

    protected DeathMatch() {
        super("Death Match FFA", 300);
    }

    @Override
    public void processBeforeGameStart() {

    }

    @Override
    public void processPlayerBeforeGameStart(Player player) {

    }

    @Override
    public void processAfterGameEnd() {

    }

    @Override
    public void processPlayerAfterGameEnd(Player player) {

    }

    private final int respawnTimeSec = 5;

    @Override
    public void processPlayerDeath(Player player, Player killer) {
        new GameTask() {

            int respawnAt = respawnTimeSec + 1;

            @Override
            public void run() {

                // respawn
                if (++respawnAt <= 0) {
                    Chat.sendTitle(player, "&aRespawned!", "", 0, 10, 5);
                }
                else {
                    Chat.sendTitle(player, "&aYou will respawn", String.format("&ain %s...", respawnAt), 0, 40, 0);
                }


            }
        }.runTaskTimer(0, 20);
    }

    public void spawnPlayer(Player player) {

    }

}
