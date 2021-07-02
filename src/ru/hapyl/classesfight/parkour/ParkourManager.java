package ru.hapyl.classesfight.parkour;

import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.feature.GameItem;
import ru.hapyl.classesfight.feature.record.Record;
import ru.hapyl.classesfight.stats.StatsContainer;
import ru.hapyl.classesfight.stats.StatsHologramManager;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.CenterChat;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParkourManager implements Runnable {

	private final GameItem gameItems = ClassesFight.getSingleton().getGameItems();

	public ParkourManager() {

	}

	public final Map<Player, ParkourData> playerParkour = new ConcurrentHashMap<>();

	private final ItemStack TP_TO_CHECKPOINT_ITEM =
			new CFItemBuilder(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "parkour_tp").setName("&aTeleport to Checkpoint")
					.addClickEvent(this::tpToCheckpoint)
					.withCooldown(20)
					.build();

	private final ItemStack RESET_PARKOUR_ITEM = new CFItemBuilder(Material.REDSTONE, "parkour_reset").setName("&aReset Parkour")
			.addClickEvent(this::resetParkour)
			.withCooldown(20)
			.build();

	private final ItemStack LEAVE_PARKOUR_ITEM = new CFItemBuilder(Material.REDSTONE_BLOCK, "parkour_quit").setName("&cQuit Parkour")
			.addClickEvent(this::quitParkour)
			.withCooldown(20)
			.build();

	private void quitParkour(Player player) {

		tempRecord.remove(player);

		if (this.hasParkour(player)) {
			final ParkourData data = this.getData(player);
			playerParkour.remove(player);
			if (data == null) {
				return;
			}
			final Parkour parkour = data.getParkour();
			if (parkour == null) {
				return;
			}
			player.teleport(parkour.getQuitPlace().toLocation(true), PlayerTeleportEvent.TeleportCause.UNKNOWN);
			parkourMessage(player, "Quit %s!", parkour.getName());
			player.getInventory().clear();
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			gameItems.giveAll(player);
		}
	}

	public void tpToCheckpoint(Player player) {
		if (this.hasParkour(player)) {
			final ParkourData data = this.getData(player);
			if (data.hasLastCheckpoint()) {
				player.teleport(data.getLastCheckpoint().toLocation(true), PlayerTeleportEvent.TeleportCause.UNKNOWN);
				OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);
				data.incrementCheckpointsTeleports();
			}
			else {
				parkourMessage(player, "&cYou haven't passed any checkpoints yet!");
				OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
			}
		}
	}

	@NULLABLE
	public ParkourData getPlayerData(Player player) {
		return playerParkour.getOrDefault(player, null);
	}

	public void resetAll() {
		playerParkour.forEach((player, parkour) -> quitParkour(player));
	}

	public void reset(Player player) {
		quitParkour(player);
	}

	public void failParkour(Player player, FailReason reason) {
		parkourMessage(player, "&cParkour failed, " + reason.getReason());
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		player.getInventory().clear();
		gameItems.giveAll(player);
	}

	public boolean hasParkour(Player player) {
		return playerParkour.containsKey(player);
	}

	private void resetParkour(Player player) {
		final ParkourData data = this.getData(player);
		if (data != null) {
			this.startParkour(player, data.getParkour());
			player.teleport(data.getParkour().getQuitPlace().toLocation(true), PlayerTeleportEvent.TeleportCause.UNKNOWN);
		}
	}

	private final Map<Player, Record> tempRecord = new HashMap<>();

	public void startParkour(Player player, Parkour parkour) {

		final Record oldRecord = tempRecord.get(player);
		if (oldRecord != null) {
			oldRecord.forceStop();
		}
		tempRecord.put(player, new Record(player, false));

		// reset time
		PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 1.25f);
		if (hasParkour(player) && getData(player).getParkour().equals(parkour)) {
			parkourMessage(player, "Reset time for %s!", parkour.toString());
			getData(player).resetTime();
			getData(player).resetCheckpoints();
		}
		else {
			// start parkour
			player.setGameMode(GameMode.ADVENTURE);
			player.removePotionEffect(PotionEffectType.SPEED);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false));
			final PlayerInventory inventory = player.getInventory();
			inventory.clear();
			inventory.setItem(3, TP_TO_CHECKPOINT_ITEM);
			inventory.setItem(5, RESET_PARKOUR_ITEM);
			inventory.setItem(7, LEAVE_PARKOUR_ITEM);
			inventory.setHeldItemSlot(4);

			parkourMessage(player, "Started %s!", parkour.toString());
			playerParkour.put(player, new ParkourData(player, parkour));
		}
	}

	public ParkourData getData(Player player) {
		return playerParkour.get(player);
	}

	public void finishParkour(Player player, Parkour parkour) {
		final ParkourData data = getData(player);
		data.setFinished();
		final long finishedAt = data.getFinishedAt();
		final String format = new SimpleDateFormat("mm:ss.SS").format(finishedAt);

		// FIXME: 006. 06/06/2021 - Maybe change to be consistent

		final Record record = tempRecord.get(player);
		if (record != null) {
			record.setFinished(true);
			record.save();
			record.sendRecordMessage("&aFinished recording replay! &e/parkour replay &ato start/stop playing it.");
			tempRecord.remove(player);
		}

		final String line = "&6&l&m                                                                 ";
		final boolean isPersonalBest = StatsContainer.current().setParkourStatIfBetter(parkour, data);
		final boolean isNewWorldRecord = StatsContainer.current().isNewTopOne(parkour, finishedAt);

		// Message
		Chat.sendMessage(player, line);
		CenterChat.sendCenteredMessage(player, "");
		CenterChat.sendCenteredMessage(player, "&e&l%s COMPLETE", parkour.getName().toUpperCase(Locale.ROOT));
		CenterChat.sendCenteredMessage(player, "&7%s%s", format, (isNewWorldRecord ?
				" &c&lNEW WORLD RECORD!" :
				isPersonalBest ? " &a&lPERSONAL BEST!" : ""));

		if (isNewWorldRecord) {
			PlayerLib.playSound(Sound.ENTITY_ENDER_DRAGON_DEATH, 1.75f);
		}

		Bukkit.getOnlinePlayers().forEach(online -> {
			if (online == player) {
				return;
			}
			if (isNewWorldRecord) {
				parkourBroadcast("&c&lNEW RECORD! &a%s completed %s in %s!", player.getName(), parkour.getName(), format);
			}
			else {
				parkourBroadcast("%s completed %s in %s!", player.getName(), parkour.getName(), format);
			}
		});

		Chat.sendMessage(player, "");
		CenterChat.sendCenteredMessage(player, "&7Jumps &f" + data.getJumps());
		CenterChat.sendCenteredMessage(player, "&7Checkpoints Used &f" + data.getCheckpointTeleports());
		CenterChat.sendCenteredMessage(player, "");
		Chat.sendMessage(player, line);

		final StatsHologramManager stats = StatsHologramManager.current();
		final ParkourCourse byParkour = ParkourCourse.getByParkour(parkour);

		if (byParkour == null) {
			Chat.broadcast("&4Could not find parkour course for %s. Report this!", parkour.getName());
		}
		else {
			stats.updateParkourLeaderboard(byParkour);
			stats.updateViewers();
		}

		OldPlayerLib.playSound(player, Sound.ENTITY_VILLAGER_YES, 1.15f);
		OldPlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.25f);

		if (!parkour.hasCompleteBefore(player)) {
			if (parkour.hasCoinsReward()) {
				Database.getDatabase(player).getCoinsEntry().grantCoins(parkour.getCoinsReward(), false, parkour.getName() + " Completed");
				parkour.setCompletion(player, true);
			}
		}
		player.getInventory().clear();
		gameItems.giveAll(player);
		playerParkour.remove(player);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

	private void parkourBroadcast(String str, Object... var1) {
		final String bcMessage = Chat.format("&a&lPARKOUR! &a" + str, var1);
		Bukkit.broadcastMessage(bcMessage);
	}

	public static void parkourMessage(Player player, String msg, Object... replacements) {
		Chat.sendMessage(player, "&a&lPARKOUR! &7" + msg, replacements);
	}

	@Override
	public void run() {
		if (playerParkour.isEmpty()) {
			return;
		}

		playerParkour.forEach((player, data) -> Chat.sendActionbar(player, data.getTimePassedFormatted()));
	}

}
