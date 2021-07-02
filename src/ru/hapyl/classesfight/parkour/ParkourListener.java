package ru.hapyl.classesfight.parkour;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class ParkourListener implements Listener {

    private final ParkourManager manager = ClassesFight.getSingleton().getParkourManager();

    @EventHandler()
    public void handleTpEvent(PlayerTeleportEvent ev) {
        final Player player = ev.getPlayer();
        if (manager.hasParkour(player)) {
            if (ev.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                // stack overflow
                manager.playerParkour.remove(player);
                manager.failParkour(player, FailReason.TELEPORT);
            }
        }
    }

    @EventHandler()
    public void handleChangeGameMode(PlayerGameModeChangeEvent ev) {
        final Player player = ev.getPlayer();
        if (manager.hasParkour(player)) {
            manager.playerParkour.remove(player);
            manager.failParkour(player, FailReason.GAMEMODE_CHANGE);
        }
    }

    @EventHandler()
    public void handleFlyEvent(PlayerToggleFlightEvent ev) {
        final Player player = ev.getPlayer();
        if (manager.hasParkour(player)) {
            manager.playerParkour.remove(player);
            manager.failParkour(player, FailReason.FLIGHT);
            OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
        }
    }

    @EventHandler()
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        if (ev.getClickedBlock() != null) {

            if (ev.getAction() != Action.PHYSICAL) {
                return;
            }

            final Material blockType = ev.getClickedBlock().getType();
            final Location location = ev.getClickedBlock().getLocation();
            final Player player = ev.getPlayer();
            final int[] blockLocation = new int[]{location.getBlockX(), location.getBlockY(), location.getBlockZ()};
            final Parkour parkour = ParkourCourse.getInstanceOf(blockLocation);

            if (parkour == null) {
                return;
            }

            final ParkourData data = manager.getData(player);

            /** Start/Finish*/
            if (blockType == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                ev.setUseInteractedBlock(Event.Result.DENY);

                if (player.hasCooldown(Material.BEDROCK)) {
                    return;
                }

                // Start
                if (parkour.getStart().compare(blockLocation)) {
                    parkour.printDebugData(player);
                    if (parkour.getStart().compare(blockLocation)) {
                        manager.startParkour(player, parkour);
                    }
                }

                // Finish
                else if (parkour.getFinish().compare(blockLocation)) {
                    if (manager.hasParkour(player)) {
                        // check for checkpoint completion
                        if (!data.getCheckPoints().isEmpty()) {
                            manager.tpToCheckpoint(player);
                            ParkourManager.parkourMessage(player, "&cYou must pass all the checkpoints to finish parkour! You missed %s.",
                                    data.getParkour().getCheckpoints().size() - data.passedCheckpointCount());
                        }
                        else {
                            manager.finishParkour(player, parkour);
                        }
                    }
                    else {
                        ParkourManager.parkourMessage(player, "&cYou must first start this parkour!");
                        OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
                    }

                }
                else {
                    Chat.sendMessage(player, "&4An error occurred whilst trying to perform parkour action! Please report this issue!");
                    Chat.sendMessage(player, ChatColor.GOLD + parkour.printDebugData(player));
                }

                player.setCooldown(Material.BEDROCK, 30);
            }
            /**  Checkpoints */
            else if (blockType == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                ev.setUseInteractedBlock(Event.Result.DENY);

                if (!manager.hasParkour(player) || player.hasCooldown(Material.HONEY_BOTTLE)) {
                    return;
                }

                player.setCooldown(Material.HONEY_BOTTLE, 15);

                if (data.hasNextCheckpoint()
                        && data.getNextCheckpoint().compare(blockLocation)) {
                    data.nextCheckpoint(true);
                }
                else if (data.prevCheckpoint(blockLocation)) {
                    // do nothing
                }
                else {
                    OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
                    ParkourManager.parkourMessage(player, "&cYou missed a checkpoint!");
                }

            }
        }
    }

}
