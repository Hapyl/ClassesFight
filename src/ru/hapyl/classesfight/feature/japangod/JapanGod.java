package ru.hapyl.classesfight.feature.japangod;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.hapyl.classesfight.feature.BlockLocation;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.feature.japangod.blessing.*;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.OldPlayerLib;
import ru.hapyl.classesfight.utils.entity.GarbageEntity;

import java.util.*;
import java.util.stream.Collectors;

public class JapanGod {

	protected final int relicsNeededForBlessing = 10;
	protected final String relicScoreboardTag = "GodRelic";
	private final int relicSpawnDelay = 200;
	private final Map<ArmorStand, MotionValue> motionValues;
	private final Location godLocation;

	private final ItemStack relicItem = new ItemBuilder(Material.PLAYER_HEAD, "god_relic").setHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2EzNmIzZmIyMzhlOWYyY2Y4MmU5YmExYzMwMzRiOTY5ZmY5NWQzOGQ4ZDE1MTY4ZDQzZDQ2NjQ1MzE2NCJ9fX0=")
			.setName("&c???")
			.addClickEvent(this::prayToGod, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)
			.build(true);

	public JapanGod() {

		this.motionValues = new HashMap<>();
		this.godLocation = new Location(Bukkit.getWorlds().get(0), 106, 18, -144);

		// Relic Spawn Controller
		new GameTask() {
			@Override
			public void run() {
				spawnRelic();
			}
		}.addCancelEvent(motionValues::clear).runTaskTimer(relicSpawnDelay, relicSpawnDelay);

		// Relic Motion Controller
		new GameTask() {
			@Override
			public void run() {
				if (motionValues.isEmpty()) {
					return;
				}
				for (ArmorStand stand : motionValues.keySet()) {

					final MotionValue motionValue = motionValues.get(stand);

					if (motionValue.isUp()) {
						if (motionValue.getValue() >= 15) {
							motionValue.setUp(false);
						}
						else {
							motionValue.setValue(motionValue.getValue() + 1);
						}
					}
					else {
						if (motionValue.getValue() <= -15) {
							motionValue.setUp(true);
						}
						else {
							motionValue.setValue(motionValue.getValue() - 1);
						}
					}

					final Location location = stand.getLocation().clone();
					location.add(0.0d, motionValue.getValue() / 750d, 0.0d);
					location.setYaw(location.getYaw() + 4.2f);
					stand.teleport(location);
					motionValues.put(stand, motionValue);

				}

			}
		}.runTaskTimer(0, 1);

	}

	private boolean isSpawned(Location location) {
		for (ArmorStand armorStand : this.motionValues.keySet()) {
			if (armorStand.getLocation().distance(location) <= 2.5) {
				return true;
			}
		}
		return false;
	}

	private Location randomLocation() {
		Location randomLocation = relicSpawnLocations.get(new Random().nextInt(relicSpawnLocations.size())).toLocation();
		if (isSpawned(randomLocation)) {
			if (motionValues.size() >= relicSpawnLocations.size()) {
				return null;
			}
			return randomLocation();
		}
		return randomLocation;
	}

	private void spawnRelic() {
		final Location location = randomLocation();
		if (location == null) {
			return;
		}
		new GarbageEntity<>(ArmorStand.class).spawn(location, me -> {
			me.setSilent(true);
			me.setSmall(true);
			me.addScoreboardTag(relicScoreboardTag);
			me.setInvisible(true);
			me.setInvulnerable(true);
			me.setMarker(true);
			me.getEquipment().setHelmet(relicItem);
			this.motionValues.put(me, new MotionValue());
		});
	}

	public int getRelicAmount(Player player) {
		final PlayerInventory inventory = player.getInventory();
		final ItemStack item = inventory.getItem(8);
		if (item == null) {
			return 0;
		}
		else {
			return item.getAmount();
		}
	}

	public void addRelic(Player player, int amount) {
		final PlayerInventory inventory = player.getInventory();
		final ItemStack item = inventory.getItem(8);
		if (item == null) {
			inventory.setItem(8, relicItem);
		}
		else {
			final int nextAmount = item.getAmount() + amount;
			item.setAmount(nextAmount);
		}
	}

	public void setRelic(Player player, int amount) {
		final PlayerInventory inventory = player.getInventory();
		final ItemStack item = inventory.getItem(8);
		if (item == null) {
			inventory.setItem(8, relicItem);
			return;
		}
		item.setAmount(amount);
	}

	public void removeByStand(ArmorStand nearestStand) {
		motionValues.remove(nearestStand);
		nearestStand.remove();
	}

	private void prayToGod(Player player) {
		// Are we close enough to use?
		if (player.getLocation().distance(godLocation) <= 7.5) {
			final int relicAmount = this.getRelicAmount(player);

			if (relicAmount >= relicsNeededForBlessing) {
				this.setRelic(player, 0);
				this.bless(player);
			}
			else if (isBetween(relicAmount, 0, 5)) {
				Chat.sendMessage(player, "&cThis seems to do something, but the energy is... weak?");
				OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.6f);
			}
			else if (isBetween(relicAmount, 5, 10)) {
				Chat.sendMessage(player, "&cThis seems to do something, but the energy is... getting stronger?");
				OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f);
			}
		}
		else {
			Chat.sendMessage(player, "&cThis doesn't seems to do anything...");
			OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
		}
	}

	private boolean isBetween(int value, int min, int max) {
		return value >= min && value < max;
	}

	private final List<BlockLocation> relicSpawnLocations = new ArrayList<>(Arrays.asList(
			// outside?
			new BlockLocation(60, 12, -130), new BlockLocation(152, 12, -130), new BlockLocation(94, 16, -152), new BlockLocation(118, 16, -152),
			// the code
			new BlockLocation(109, 9, -151), new BlockLocation(60, 28, -130), new BlockLocation(152, 28, -130),
			// cave
			new BlockLocation(107, 5, -137),
			// island
			new BlockLocation(40, 17, -103),
			// house
			new BlockLocation(88, 12, -177)));

	public void bless(Player player) {
		final String godPrefix = "&6&lGOD!";
		final Blessing blessing = Blessings.random().getBlessing();

		if (blessing == null) {
			Chat.sendMessage(player, "&cBlessing cannot be null!");
			return;
		}

		// pre message
		Chat.sendMessage(player, "%s &dLet me decide your destiny...", godPrefix);
		PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_AMBIENT, 0.35f);
		PlayerLib.playSound(player, Sound.BLOCK_PORTAL_TRIGGER, 2.0f);

		GameTask.runLater(() -> {
			Chat.sendMessage(player, "%s &dI have decided your destiny! %s", godPrefix, blessing.message());
			blessing.playSound(player);
			blessing.bless(player);
			blessing.spawnFx(player.getLocation());
		}, 40);

	}

	private static class MotionValue {

		private boolean up;
		private double value;

		public MotionValue() {
			this.up = true;
			this.value = 0.0d;
		}

		public double getValue() {
			return value;
		}

		public boolean isUp() {
			return up;
		}

		public void setUp(boolean up) {
			this.up = up;
		}

		public void setValue(double value) {
			this.value = value;
		}
	}

}
