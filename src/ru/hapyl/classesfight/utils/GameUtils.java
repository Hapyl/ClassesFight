package ru.hapyl.classesfight.utils;

import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.util.Action;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.cooldown.Cooldown;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GameUtils {

	public static Entity prepareEntity(LivingEntity entity) {
		entity.setInvulnerable(true);
		entity.setAI(false);
		entity.addScoreboardTag(EnumEffect.COSMETIC_ID);
		return entity;
	}

	public static String roundTick(int tick) {
		return tick % 20 == 0 ? "" + (tick / 20) : BukkitUtils.decimalFormat((tick / 20f));
	}

	@Nullable
	public static Entity findNearestEntity(Location fromWhere, double radius) {
		return findNearestEntity(fromWhere, radius, null, null);
	}

	public static void setHeldItem(Player player, @Nullable ItemStack stack) {
		if (stack == null) {
			// TRAP fix
			stack = new ItemStack(Material.AIR);
		}
		player.getInventory().setItemInMainHand(stack);
	}

	public static String formatDate(long l) {
		final Date date = new Date(l);
		return new SimpleDateFormat("MMM dd yyyy HH:mm:ss").format(date);
	}

	public static void lockArmorStandSlots(ArmorStand stand) {
		for (final EquipmentSlot value : EquipmentSlot.values()) {
			stand.addEquipmentLock(value, ArmorStand.LockType.REMOVING_OR_CHANGING);
		}
	}

	public static Player getNearestPlayer(Location location, double radius, Player exclude) {
		return (Player)getNearestEntity(location, radius, entity -> entity instanceof Player && entity != exclude && !Spectator.isSpectator((Player)entity));
	}

	public static Entity getNearestEntity(Location fromWhere, double radius, Predicate<Entity> predicate) {
		if (fromWhere.getWorld() == null) {
			throw new NullPointerException("Cannot find entity in null world!");
		}
		final List<Entity> list = fromWhere.getWorld().getNearbyEntities(fromWhere, radius, radius, radius)
				.stream()
				.filter(predicate)
				.collect(Collectors.toList());
		Entity nearest = null;
		double dist = -1;
		for (Entity entity : list) {
			final double distance = entity.getLocation().distance(fromWhere);
			// init
			if (nearest == null) {
				nearest = entity;
				dist = distance;
			}
			else {
				if (distance <= dist) {
					nearest = entity;
					dist = distance;
				}
			}
		}
		return nearest;
	}

	public static void setCollision(Entity entity, boolean flag) {
		GameTask.runLater(() -> Bukkit.getOnlinePlayers().forEach(player -> {
			final Team team = getTeam(player.getScoreboard());
			team.setOption(Team.Option.COLLISION_RULE, flag ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
			team.addEntry(entity.getName());
		}), 1);
	}

	public static void setNameTagVisibility(Player player, boolean flag) {
		GameTask.runLater(() -> Bukkit.getOnlinePlayers().forEach(online -> {
			final Team team = getTeam(online.getScoreboard());
			team.setOption(Team.Option.NAME_TAG_VISIBILITY, flag ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
			team.addEntry(player.getName());
		}), 1);
	}

	public static void setNameTagVisibility(boolean flag) {
		for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			setNameTagVisibility(onlinePlayer, flag);
		}
	}

	public static void setCollision(boolean flag) {
		for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			setCollision(onlinePlayer, flag);
		}
	}

	private static Player[] insurePlayers(Player[] players) {
		if (players == null || players.length == 0) {
			return Bukkit.getOnlinePlayers().toArray(new Player[]{});
		}
		return players;
	}

	private static Team getTeam(Scoreboard score) {
		Team team = score.getTeam("game_team");
		if (team == null) {
			team = score.registerNewTeam("game_team");
			team.setAllowFriendlyFire(true);
			team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
			team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
			return team;
		}
		return team;
	}


	public static String getTodayDate() {
		final Calendar calendar = Calendar.getInstance();
		final String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
		return ChatColor.DARK_GRAY + String.format("%1$s %2$s%4$s %3$s",
				// why the hell first month value is 0?
				month,
				// but for the day it's 1, ok!
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.YEAR),
				getNumberPronoun(calendar.get(Calendar.DAY_OF_MONTH)));
	}

	public static String getNumberPronoun(int number) {
		// 11, 12, 13
		if (number >= 11 && number <= 13) {
			return "th";
		}
		switch (number % 10) {
			case 1:
				return "st";
			case 2:
				return "nd";
			case 3:
				return "rd";
			default:
				return "th";
		}
	}

	public static void tracePath(Location start, Location end, double step, double entityDistance, Player executor, Consumer<Player> touchAction, Consumer<Location> tickAction) {
		final double maxDist = start.distance(end);
		final Vector vector = end.toVector().subtract(start.toVector()).normalize().multiply(step);

		new GameTask() {

			private double tick = 0;

			@Override
			public void run() {

				if (tick >= maxDist) {
					this.cancel();
					return;
				}

				start.add(vector);

				if (start.getWorld() == null) {
					return; // should never happens
				}

				final Collection<Entity> entities = start.getWorld().getNearbyEntities(start, entityDistance, entityDistance, entityDistance);

				if (!entities.isEmpty()) {
					for (Entity entity : entities) {
						if (entity == executor) {
							continue;
						}
						if (entity instanceof Player) {
							if (Spectator.isSpectator((Player)entity)) {
								continue;
							}
							touchAction.accept((Player)entity);
							this.cancel();
							return;
						}
					}
				}

				tickAction.accept(start);
				tick += step;

			}
		}.runTaskTimer(0, 1);
	}

	@Nullable
	public static <T extends EntityType> Entity findNearestEntity(Location fromWhere, double radius, Entity exclude, T entityType) {
		if (fromWhere.getWorld() == null) {
			throw new NullPointerException("Cannot find entity in null world!");
		}
		final List<Entity> list = fromWhere.getWorld().getNearbyEntities(fromWhere, radius, radius, radius)
				.stream()
				.filter(entity -> exclude == null || entity != exclude)
				.filter(entity -> entityType == null || entity.getType() == entityType)
				.collect(Collectors.toList());

		Entity nearest = null;
		double dist = -1;
		for (Entity entity : list) {
			if (entity instanceof Player && Spectator.isSpectator((Player)entity)) {
				continue;
			}
			// init
			final double distance = entity.getLocation().distance(fromWhere);
			if (nearest == null) {
				nearest = entity;
				dist = distance;
			}
			else {
				if (distance <= dist) {
					nearest = entity;
					dist = distance;
				}
			}
		}
		return nearest;
	}

	public static class Traceable {

		private final Location location;
		private final Vector vector;
		private final double limit, shift;
		private final int delay;
		private double current;
		private boolean stop;

		public Traceable(Location location, double limit, double shift, int delay) {
			this.location = location.clone();
			this.vector = this.location.getDirection();
			this.limit = limit;
			this.shift = shift;
			this.delay = delay;
			this.current = 0.0d;
		}

		public Location getLocation() {
			return location;
		}

		public Block getBlock() {
			return this.location.getBlock();
		}

		public void stopIfNotPassable() {
			if (this.getBlock().getType().isOccluding()) {
				this.stop();
			}
		}

		private void stop() {
			this.stop = true;
		}

		public double getTick() {
			return current;
		}

		private void start(Action<Traceable> action, Action<Player> action0) {

			new GameTask() {
				@Override
				public void run() {
					if ((current += shift) >= limit) {
						this.cancel();
						return;
					}

					final double x = vector.getX() * current;
					final double y = vector.getY() * current;
					final double z = vector.getZ() * current;

					location.add(x, y, z);
					if (action != null) {
						action.use(Traceable.this);
					}
					if (stop) {
						this.cancel();
						return;
					}

					if (action0 != null) {
						GameUtils.getPlayerInRange(location, 0.5d).forEach(action0::use);
					}

					location.subtract(x, y, z);

				}
			}.runTaskTimer(0, delay);


		}

	}

	public static void rayTraceLine(Player player, double limitDist, double shift, int delay, @Nullable Action<Traceable> onMove, @Nullable Action<Player> onHit) {
		new Traceable(player.getEyeLocation(), limitDist, shift, delay).start(onMove, onHit);
	}

	public static void rayTraceLine(Location startLocation, double limitDist, double shift, int delay, @Nullable Action<Traceable> onMove, @Nullable Action<Player> onHit) {
		startLocation.getDirection().setY(0.0d);
		new Traceable(startLocation, limitDist, shift, delay).start(onMove, onHit);
	}

	public static void rayTraceLine(Player shooter, double maxDistance, double shift, double damage, EnumDamageCause cause, @NULLABLE Consumer<Location> onMove, @NULLABLE Consumer<Player> onHit) {
		final Location location = shooter.getLocation().add(0, 1.5, 0);
		final Vector vector = location.getDirection().normalize();

		main:
		for (double i = 0; i < maxDistance; i += shift) {

			double x = vector.getX() * i;
			double y = vector.getY() * i;
			double z = vector.getZ() * i;
			location.add(x, y, z);

			// check for the hitting a block and an entity
			if (location.getBlock().getType().isOccluding()) {
				break;
			}

			final Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5);
			if (!entities.isEmpty()) {
				for (Entity entity : entities) {
					if (entity == shooter)
						continue;
					if (entity instanceof Player) {
						if (!Spectator.isSpectator((Player)entity)) {
							DamageFeature.damage((Player)entity, shooter, damage, cause);
							if (onHit != null) {
								onHit.accept((Player)entity);
							}
							break main;
						}
					}
				}
			}

			if (i > 1.0) {
				if (onMove != null) {
					onMove.accept(location);
				}
			}
			location.subtract(x, y, z);

		}
	}

	public static boolean isLookingAt(Player player, Entity who, double maxDist) {
		Vector v = who.getLocation().toVector().subtract(player.getLocation().toVector());
		return player.getLocation().getDirection().normalize().crossProduct(v).lengthSquared() < maxDist && v.normalize()
				.dot(player.getLocation().getDirection().normalize()) >= 0;
	}

	public static void createFakeExplosion(Location loc, double radius, double damage, @Nullable Player exclude, EnumDamageCause cause) {
		final Set<Player> players = GameUtils.getPlayerInRange(loc, radius);

		for (Player player : players) {
			if (exclude != null && exclude == player) {
				continue;
			}
			DamageFeature.damage(player, exclude, damage, cause);
		}

		double g = radius - 2;

		loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc.add(0, 0.5, 0), 3, g, 0, g, 0.1);
		loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc.add(0, 0.5, 0), 5, g, 0, g, 0.3);

		SoundLib.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.8f);
		GeometryLib.drawCircle(loc, 30, radius, new GeometryLib.TinyParticle(Particle.CRIT));
	}

	public static void villagerNo(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.AMBIENT, 1, 1);
	}

	public static void removeHeldItem(Player player, int i) {
		player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - i);
	}

	public static void villagerYes(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, SoundCategory.AMBIENT, 1, 1);
	}

	public static Location clone(Location location) {
		return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public static Entity getTargetEntity(Player player, double maxDist, double shift, Predicate<Entity> predicate) {
		final Location location = player.getLocation().add(0, 1.5, 0);
		final Vector vector = location.getDirection().normalize();
		final float radius = 1.25f;

		for (double i = 0; i < maxDist; i += shift) {

			double x = vector.getX() * i;
			double y = vector.getY() * i;
			double z = vector.getZ() * i;
			location.add(x, y, z);

			final Collection<Entity> entities = location.getWorld().getNearbyEntities(location, radius, radius, radius);
			if (!entities.isEmpty()) {
				for (Entity target : entities) {
					if (target == player) {
						continue;
					}
					if (player.hasLineOfSight(target) && (predicate != null && predicate.test(target))) {
						return target;
					}
					else {
						return null;
					}
				}
			}

			location.subtract(x, y, z);
		}

		return null;
	}

	public static Player getTargetPlayer(Player player, double maxDist, double shift) {
		return (Player)getTargetEntity(player, maxDist, shift, target -> target instanceof Player && !Spectator.isSpectator((Player)target));
	}

	public static void resetPlayer(Player player) {

		// reset cooldown
		for (Material value : Material.values()) {
			if (player.hasCooldown(value))
				player.setCooldown(value, 0);
		}

		// Update level
		Experience.getInstance().updatePlayerProgress(player);

		Cooldown.getCooldowns(player).forEach(Cooldown::stopCooldown);

		player.setMaxHealth(20.0d);
		player.setHealth(20.0d);

		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.setFoodLevel(20);
		player.setFlySpeed(0.1f);
		player.setWalkSpeed(0.2f);
		player.setFireTicks(0);
		player.setInvulnerable(false);
		player.setAllowFlight(false);
		player.resetPlayerTime();
		player.setArrowsInBody(0);

	}

	public static Set<Player> getRealPlayers() {
		final Set<Player> players = new HashSet<>();
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (!onlinePlayer.getName().contains("[NPC]")) {
				players.add(onlinePlayer);
			}
		}
		return players;
	}

	public static void hideBut(Player player) {
		GameUtils.getRealPlayers().forEach(p -> player.hidePlayer(ClassesFight.getPlugin(), p));
	}

	public static void showBut(Player player) {
		GameUtils.getRealPlayers().forEach(p -> player.showPlayer(ClassesFight.getPlugin(), p));
	}

	public static void hidePlayer(Player player) {
		GameUtils.getRealPlayers().iterator().forEachRemaining(i -> i.hidePlayer(ClassesFight.getPlugin(), player));
	}

	public static void showPlayer(Player player) {
		GameUtils.getRealPlayers().iterator().forEachRemaining(i -> i.showPlayer(ClassesFight.getPlugin(), player));
	}

	public static Set<LivingEntity> getLivingEntitiesInRange(Location location, double d3d) {
		final Set<LivingEntity> players = new HashSet<>();
		if (location.getWorld() == null) {
			return players;
		}
		location.getWorld().getNearbyEntities(location, d3d, d3d, d3d).forEach(player -> {
			if (!(player instanceof LivingEntity)) {
				return;
			}
			if (player instanceof Player) {
				if (!Spectator.isSpectator((Player)player)) {
					players.add((LivingEntity)player);
				}
			}
			players.add((LivingEntity)player);
		});
		return players;
	}

	public static Set<Entity> getEntitiesInRange(Location location, double d3drange) {
		final Set<Entity> players = new HashSet<>();
		if (location.getWorld() == null) {
			return players;
		}
		location.getWorld().getNearbyEntities(location, d3drange, d3drange, d3drange).forEach(player -> {
			if (player instanceof Player) {
				if (!Spectator.isSpectator((Player)player)) {
					players.add(player);
				}
			}
			players.add(player);
		});
		return players;
	}

	public static Set<Player> getPlayerInRange(Location location, double d3drange) {
		final Set<Player> players = new HashSet<>();
		if (location.getWorld() == null) {
			return players;
		}
		location.getWorld()
				.getNearbyEntities(location, d3drange, d3drange, d3drange)
				.stream()
				.filter(entity -> entity instanceof Player)
				.forEach(player -> {
					if (!Spectator.isSpectator((Player)player)) {
						players.add((Player)player);
					}
				});
		return players;
	}

	public static boolean isValidPlayer(Player player) {
		return GameManager.current().isGameInProgress() && !Spectator.isSpectator(player);
	}

	public static boolean isValidPlayer(Player player, ClassManager clazz) {
		return GameManager.current().isGameInProgress() && !Spectator.isSpectator(player) && ClassManager.getClass(player) == clazz;
	}

	public static void removeItem(Player player, int slot, int amount) {
		final PlayerInventory inventory = player.getInventory();
		final ItemStack item = inventory.getItem(slot);
		if (item != null) {
			item.setAmount(item.getAmount() - amount);
		}
	}
}
