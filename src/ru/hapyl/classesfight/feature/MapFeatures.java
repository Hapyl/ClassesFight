package ru.hapyl.classesfight.feature;

import io.netty.util.internal.ConcurrentSet;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.entity.Rope;
import kz.hapyl.spigotutils.module.math.Numbers;
import kz.hapyl.spigotutils.module.particle.ParticleBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.feature.library.VoidCat;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameElement;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.OldPlayerLib;
import ru.hapyl.classesfight.utils.PlayerDirection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapFeatures implements Listener, Runnable, GameElement {

	private int currentTick = 0;
	private final double healingRadius = 6.5;

	private final Set<Rope> ropes;
	private final Set<Entity> boosterStorage;
	private final Set<Player> onTheWay;
	private final Set<PiggyBooster> initBoosters;
	private final Map<Player, Float> coldMap;
	private final Map<Player, Integer> voidMap;
	private VoidCat voidCat;

	private final LibraryKeyport keyports;
	private final char[] chars = {'ᛈ', 'ᚢ', 'ᛋ', 'ᛏ', 'ᛟ', 'ᛏ', 'ᚨ'};

	public MapFeatures(JavaPlugin plugin) {
		this.boosterStorage = new ConcurrentSet<>();
		this.onTheWay = new HashSet<>();
		this.ropes = new HashSet<>();
		this.initBoosters = new HashSet<>();
		this.coldMap = new HashMap<>();
		this.voidMap = new HashMap<>();
		this.keyports = new LibraryKeyport();
		this.initBoosters();
	}

	@Override
	public void onStart() {
		this.spawnJapanBoosterInfo(Bukkit.getWorlds().get(0));
		this.spawnRopes();
		this.startLibraryTick();
	}

	@Override
	public void onStop() {
		boosterStorage.clear();
		for (Player player : onTheWay) {
			player.eject();
		}
		voidMap.clear();
		onTheWay.clear();
		coldMap.clear();
		if (voidCat != null) {
			voidCat.remove();
		}
		this.removeRopes();
		this.resetDoor();
	}

	public void resetDoor() {
		BukkitUtils.defLocation(90.5, 18.0, 171.5).getBlock().setType(Material.CLAY, true);
		final Location start = BukkitUtils.defLocation(86.5, 20.0, 109.5);
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 8; j++) {
				start.add(0, j, i);
				if (j == 0) {
					start.getBlock().setType(Material.LOOM, false);
				}
				else {
					start.getBlock().setType((j == 7 && (i == 0 || i == 4)) ? Material.SPRUCE_PLANKS : Material.BOOKSHELF, false);
				}
				start.subtract(0, j, i);
			}
		}
	}

	private void startLibraryTick() {
		if (GameMap.getCurrentMap() != GameMap.LIBRARY) {
			return;
		}
		this.voidCat = new VoidCat();
		new GameTask() {
			private int tick = 0;

			@Override
			public void run() {

				// Teleports
				for (final BlockLocation bLoc : keyports.getEntrances()) {
					final Location location = bLoc.toLocation();
					location.add(0, 1, 0);
					PlayerLib.spawnParticle(location, Particle.PORTAL, 20, 0.1d, 0.5d, 0.1d, 1.0f);
					PlayerLib.spawnParticle(location, Particle.ENCHANTMENT_TABLE, 10, 0.1d, 0.5d, 0.1d, 1.0f);
				}

				voidCat.tick();

				if ((tick += 5) >= 200) {
					tick = 0;
					for (final Player player : GameManager.current().getPlayers()) {
						removeVoidValue(player);
					}
				}

			}
		}.runTaskTimer(5, 5);
	}

	public boolean checkVoidCatAndTeleport(Entity entity) {
		if (!(entity instanceof Cat)) {
			return false;
		}
		if (this.voidCat.getCat() == entity) {
			this.voidCat.teleportNext();
			return true;
		}
		return false;
	}

	private void removeVoidValue(Player player) {
		final int var = Numbers.clamp(this.voidMap.getOrDefault(player, 0) - 1, 0, 7);
		this.voidMap.put(player, var);
		displayVoidValues(player, var);
	}

	private void removeRopes() {
		this.ropes.forEach(Rope::remove);
		this.ropes.clear();
	}

	private void initBoosters() {
		registerCloudBooster(new PiggyBooster(1031, 23, 989, -1.1, 1, -1.1));
		registerCloudBooster(new PiggyBooster(1012, 27, 975, -1.8, 1.0, 0.5));
		registerCloudBooster(new PiggyBooster(988, 19, 982, 1.2, 1.0, 1.4));
		registerCloudBooster(new PiggyBooster(1005, 20, 1000, 2.0, 1.0, 0.0));
		registerCloudBooster(new PiggyBooster(1024, 22, 1013, -1.5, 1.2, 2.2));
		registerCloudBooster(new PiggyBooster(1010, 18, 1040, 2.7, 1.7, 0.0));
		registerCloudBooster(new PiggyBooster(1051, 24, 1052, 0.0, 1.0, 2.0));
		registerCloudBooster(new PiggyBooster(1059, 27, 1073, 0.25, 1.5, -2.5));
		registerCloudBooster(new PiggyBooster(1069, 23, 1042, 1.2, 1.5, 2.5));
		registerCloudBooster(new PiggyBooster(1087, 23, 1069, 1.65, 1.25, -3.0));
		registerCloudBooster(new PiggyBooster(1104, 27, 1035, -0.2, 1.0, 3.0));
		registerCloudBooster(new PiggyBooster(1070, 25, 1019, 2.0, 1.5, -0.5));
		registerCloudBooster(new PiggyBooster(1091, 30, 1009, -2.0, 1.0, 0.5));
		registerCloudBooster(new PiggyBooster(1048, 23, 976, 0.5, 1.8, -2.0));
		registerCloudBooster(new PiggyBooster(1051, 36, 962, -0.75, 0.2, 1.5));
		registerCloudBooster(new PiggyBooster(1090, 36, 960, 1.0, 1.5, 3.0));
		registerCloudBooster(new PiggyBooster(1081, 18, 974, -2.0, 1.5, 1.5));
		registerCloudBooster(new PiggyBooster(1080, 18, 974, -2.0, 1.4, 1.5));
		registerCloudBooster(new PiggyBooster(1064, 34, 958, 1.0, 0.0, 1.0));

		// Viking Map
		registerCloudBooster(new PiggyBooster(GameMap.VIKING_SHIP, -203, 18, 156, -1.8, 0.8, 1.8));
		registerCloudBooster(new PiggyBooster(GameMap.VIKING_SHIP, -219, 18, 172, 1.8, 0.8, -1.8));

	}

	public void registerCloudBooster(PiggyBooster booster) {
		this.initBoosters.add(booster);
	}

	@EventHandler()
	public void handleDismount0(VehicleExitEvent ev) {
		ev.setCancelled(cancelDismount(ev.getExited(), ev.getVehicle()));
	}

	@EventHandler
	public void handleDismount1(EntityDismountEvent ev) {
		ev.setCancelled(cancelDismount(ev.getEntity(), ev.getDismounted()));
	}

	private boolean cancelDismount(Entity entity, Entity mount) {
		if (entity instanceof Player && onTheWay.contains(entity)) {
			Chat.sendTitle((Player)entity, "", "&cCannot dismount booster, wait for it to land.", 2, 8, 2);
			mount.addPassenger(entity);
			return true;
		}
		return false;
	}

	@EventHandler()
	public void handleMovement(PlayerMoveEvent ev) {
		if (GameMap.getCurrentMap() != GameMap.LIBRARY || !GameManager.current().isGameInProgress()) {
			return;
		}

		final Player player = ev.getPlayer();
		final boolean bool = this.keyports.testPlayer(player);
		if (bool) {
			CollectionUtils.addMapValue(this.voidMap, player, 1);
			CollectionUtils.clampMapValue(this.voidMap, player, 0, 7);
			final int current = this.voidMap.getOrDefault(player, 0);
			displayVoidValues(player, current);
		}

	}

	private void displayVoidValues(Player player, int current) {
		if (current <= 0) {
			return;
		}
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < chars.length; i++) {
			builder.append(i < current ? "&d&n" : "&8").append(chars[i]);
		}
		String subtitle = "";
		switch (current) {
			case 5 -> subtitle = "Void is Watching...";
			case 6 -> subtitle = "Vulnerable to Void";
			case 7 -> {
				subtitle = "Void Consuming You";
				DamageFeature.damage(player, player, 14.0d, EnumDamageCause.VOID);
			}
		}
		Chat.sendTitle(player, builder.toString(), "&6" + subtitle, 0, 20, 5);
	}

	@EventHandler
	public void handleInteract(PlayerInteractEvent ev) {
		final GameMap currentMap = GameMap.getCurrentMap();
		final Player player = ev.getPlayer();
		final Action action = ev.getAction();

		if (GameManager.current().isGameInProgress() && action == Action.PHYSICAL && ev.getClickedBlock() != null) {
			final Block clickedBlock = ev.getClickedBlock();

			// Piggy Boosters
			if (clickedBlock.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
				switch (currentMap) {
					case SKY, VIKING_SHIP -> {
						final PiggyBooster booster = byLocation(new BlockLocation(ev.getClickedBlock().getLocation()));
						if (booster != null) {
							this.boosterStorage.add(booster.spawnAndRide(player, false));
							this.onTheWay.add(player);
						}
						return;
					}
				}
			}

			// Japan Jump Pad
			if (currentMap == GameMap.JAPAN && clickedBlock.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
				final PlayerDirection direction = PlayerDirection.getDirection(player);

				final Vector vector = new Vector(0.0d, 2.0d, 0.0d);
				final double trueDoubleValue = 1.15d;

				switch (direction) {
					case NORTH -> vector.setZ(-trueDoubleValue);
					case SOUTH -> vector.setZ(trueDoubleValue);
					case WEST -> vector.setX(-trueDoubleValue);
					case EAST -> vector.setX(trueDoubleValue);
				}

				player.setVelocity(vector);
				GameEffectManager.applyEffect(player, GameEffectType.FALL_DAMAGE_RESISTANCE, 63);
				OldPlayerLib.playSound(player, Sound.ENTITY_WITHER_SHOOT, 0.75f);

			}

		}

	}

	public void spawnJapanBoosterInfo(World world) {
		new GameTask() {
			private final Location location = new Location(world, 106.5, 16.25, -139.5);
			private double theta = 0;

			@Override
			public void run() {

				double x = 0.8d * Math.sin(theta);
				double z = 0.8d * Math.cos(theta);

				location.add(x, 0, z);
				world.spawnParticle(Particle.FIREWORKS_SPARK, location, 1, 0, 0, 0, 0);
				world.spawnParticle(Particle.FLAME, location, 1, 0, 0, 0, 0);
				location.subtract(x, 0, z);

				theta += Math.PI / 8;
				if (theta > Math.PI * 2) {
					theta = 0;
				}
			}
		}.runTaskTimer(0, 2);
	}

	public PiggyBooster byLocation(BlockLocation loc) {
		for (PiggyBooster booster : initBoosters) {
			if (booster.getLocation().compare(loc)) {
				return booster;
			}
		}
		return null;
	}

	private void spawnRopes() {
		spawnRope(-213, 22, 173, -224, 37, 173);
		spawnRope(-224, 33, 178, -225, 37, 174);
		spawnRope(-224, 33, 168, -225, 37, 172);
		spawnRope(-226, 38, 173, -242, 21, 173);
	}

	private void spawnRope(double x, double y, double z, double x1, double y1, double z1) {
		final Rope rope = new Rope(x, y, z, x1, y1, z1);
		this.ropes.add(rope);
		rope.spawn();
	}

	private void checkHeightLimit(Player player) {
		if (player.getLocation().getY() <= 0) {
			GameManager.current().getPlayers().remove(player);
			DamageFeature.fakeDeath(player);
			DamageFeature.handleKillAndMessage(player, null, EnumDamageCause.VOID);
			player.teleport(player.getLocation().add(0, 25, 0));
			player.teleport(GameManager.current().getRandomInGamePlayer());
		}
	}

	private void setSheerColdValue(Player player, float value) {
		this.coldMap.put(player, Math.max(0, Math.min(100, value)));
	}

	private float getSheerColdValue(Player player) {
		return this.coldMap.getOrDefault(player, 0f);
	}

	private void addSheerColdValue(Player player, float toAdd) {
		this.setSheerColdValue(player, this.getSheerColdValue(player) + toAdd);
	}

	private boolean isInWater(Player player) {
		return player.getLocation().getBlock().getType() == Material.WATER || player.getLocation()
				.getBlock()
				.getRelative(BlockFace.DOWN)
				.getType() == Material.WATER;
	}

	@Override
	public void run() {
		if (!GameManager.current().isGameInProgress()) {
			return;
		}

		if (currentTick++ >= 20) {
			currentTick = 0;
		}

		if (GameMap.getCurrentMap() == GameMap.VIKING_SHIP) {
			GameManager.current().getPlayers().forEach(player -> {
				if (isInWater(player)) {
					addSheerColdValue(player, 0.5f);
				}
				else {
					addSheerColdValue(player, -0.25f);
				}

				final float sheerCold = getSheerColdValue(player);
				player.setFreezeTicks((int)sheerCold);
				if (sheerCold > 0) {
					final StringBuilder builder = new StringBuilder();
					final int tick = (int)(sheerCold * 20 / 100);
					for (int i = 0; i < 20; i++) {
						builder.append(i <= tick ? "&b-" : "&8-");
					}
					Chat.sendActionbar(player, builder.toString());
				}

				if (sheerCold >= 30 && (currentTick == 20)) {
					PlayerLib.playSound(player, Sound.BLOCK_GLASS_BREAK, 1.0f - Math.max(0.0f, sheerCold / 100));
					DamageFeature.damage(player, null, 2.0d, EnumDamageCause.ENTITY_ATTACK);
				}

				if (sheerCold >= 60) {
					Chat.sendTitle(player, "&c&l⚠", "", 0, 10, 5);
				}

				if (sheerCold >= 100) {
					DamageFeature.damage(player, player, DamageFeature.getHealth(player) + 1.0d, EnumDamageCause.ENTITY_ATTACK);
				}

			});
		}

		this.boosterStorage.forEach(entity -> {
			if (entity.isOnGround() || entity.isDead()) {
				entity.getPassengers().forEach(passenger -> {
					if (!(passenger instanceof Player)) {
						return;
					}
					onTheWay.remove((Player)passenger);
					passenger.eject();
				});
				entity.remove();
				this.boosterStorage.remove(entity);
			}
		});

		if (currentTick == 20) {
			final GameMap currentMap = GameMap.getCurrentMap();

			switch (currentMap) {
				case JAPAN -> {
					drawSakuraParticles(75.5, 12, -126.5);
					drawSakuraParticles(137.5, 12, -126.5);
				}
				case VIKING_SHIP -> {
					for (final PiggyBooster booster : initBoosters) {
						if (booster.getMap() == GameMap.VIKING_SHIP) {
							PlayerLib.spawnParticle(booster.getLocation().toLocation(), Particle.CRIT_MAGIC, 5, 0.25d, 0.1d, 0.25d, 0.1f);
						}
					}
				}
				case SKY -> {
					GameManager.current().forEachInGamePlayer(this::checkHeightLimit);
				}
			}

		}

	}

	private void healPlayers(Location loc) {
		GameUtils.getPlayerInRange(loc, healingRadius).forEach(player -> {
			Chat.sendActionbar(player, "&5&l✿ &dYou feel sakura's petals on your head &5&l✾");
			DamageFeature.addHealth(player, 0.5f);
			PlayerLib.spawnParticle(player.getEyeLocation().add(0.0d, 0.5d, 0.0d), Particle.HEART, 1, 0, 0, 0, 0);
		});
	}

	private void drawSakuraParticles(double x, double y, double z) {

		Location location = new Location(Bukkit.getWorlds().get(0), x, y, z);
		healPlayers(location);

		for (float theta = 0.0f; theta < Math.PI * 2; theta += Math.PI / 20) {
			final double xFinal = healingRadius * Math.sin(theta);
			final double zFinal = healingRadius * Math.cos(theta);
			location.add(xFinal, 0, zFinal);
			ParticleBuilder.redstoneDust(Color.fromRGB(255, 183, 197)).setAmount(2).display(location);
			location.subtract(xFinal, 0, zFinal);
		}

	}

}