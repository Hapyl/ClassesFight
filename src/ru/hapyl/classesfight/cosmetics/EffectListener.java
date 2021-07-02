package ru.hapyl.classesfight.cosmetics;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.CosmeticEntry;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffect;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.utils.VanillaCooldown;

public class EffectListener implements Listener {

    private final boolean debugMode = false;

    @EventHandler
    public void handleEffectListener(PlayerMoveEvent ev) {

        if (!GameManager.current().isGameInProgress() && !debugMode) {
            return;
        }

        final Player player = ev.getPlayer();
        final Location from = ev.getFrom();
        final Location to = ev.getTo();

        if (Spectator.isSpectator(player)
                || player.isInvisible()
                || GameEffectManager.playerHasEffect(player, GameEffectType.INVISIBILITY)
                || VanillaCooldown.hasCooldown(player, VanillaCooldown.CONTRAIL_ENTRY)
                || !GameManager.current().arePlayersRevealed()) {
            return;
        }

        if (to == null) {
            return;
        }

        if (to.distance(from) >= 0.2d && to.getY() == from.getY()) {

            if (debugMode) {
                Chat.sendActionbar(player, "&aDisplaying contrail in debug mode");
            }

            final Database database = Database.getDatabase(player);
            final CosmeticEntry cosmetics = database.getCosmetics();
            final EnumEffect contrail = cosmetics.getCurrentEffect(EnumEffect.Type.CONTRAIL);

            if (contrail == null || !contrail.isEnabled(player)) {
                return;
            }

            VanillaCooldown.setCooldown(player, VanillaCooldown.CONTRAIL_ENTRY);
            contrail.display(player);

        }

    }

}