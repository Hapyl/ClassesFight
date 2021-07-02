package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.reflect.Reflect;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.ParticlesBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class ShadowWarrior extends IClass implements Listener {

	private final Map<UUID, ArmorStand> playerMarker = new HashMap<>();
	private final Map<UUID, Long> lastDamaged = new HashMap<>();

	private final Material SHADOW_BOMB_MATERIAL = Material.PLAYER_HEAD;

	private final int MAX_TRAVEL_DISTANCE = 100;
	private final int ULTIMATE_LENGTH = 20 * 10;

	private final double ULTIMATE_DAMAGE = 5.0d;
	private final int ULTIMATE_COOLDOWN = 23; // 36

	private final ItemStack LIVID_DAGGER = new CFItemBuilder(Material.IRON_SWORD).setName("&6Livid Dagger")
			.setPureDamage(4.0d)
			.applyDefaultSettings(false)
			.build();
	private final ItemStack SHROUDED_STEP = new CFItemBuilder(Material.NETHERITE_BOOTS, "shrouded_step").setName("&aShrouded Step")
			.addClickEvent(this::deployShroudedStep)
			.build();
	private final ItemStack ORB_TEXTURE = CFItemBuilder.playerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlZDRjZTIzOTMzZTY2ZTA0ZGYxNjA3MDY0NGY3NTk5ZWViNTUzMDdmN2VhZmU4ZDkyZjQwZmIzNTIwODYzYyJ9fX0=")
			.toItemStack();

	public ShadowWarrior() {
		super("Shadow Assassin", Material.ENDERMAN_SPAWN_EGG);

		this.setAttack(EnumInfo.HIGH);
		this.setDefense(EnumInfo.LOW);
		this.setRating(ClassRating.A);
		this.setRole(ClassRole.ASSASSIN);

		this.setInfo("Well trained assassin from dimension of shadows. Has a dagger that knocks enemies from back stab.____&e○ Shadow Prism &7Right-Click to deploy teleport orb which travels in a straight line. Right-Click again to teleport to the orb.____&e○ Dark Cover &7While sneaking you become invisible. While invisible, cannot damage enemies and your footsteps are visible. You cannot enter Dark Cover if you recently took damage.", "Extreme Focus", "Enter Extreme Focus for &b" + ULTIMATE_LENGTH / 20 + "s&7. While active, you will not miss your hits if target is close enough and has no cover.", 9);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI4YWY1MmVmMmY3MmMzYmY1ZWNlNmU3MGE4MmYxMzcxOTU5Y2UzZmNiNzM2YzUwMDMwNWNhZGRjNTA1YzVlMiJ9fX0=");
		eq.setChestplate(Color.BLACK);
		eq.setLeggings(Color.BLACK);
		eq.setBoots(Color.BLACK);

		eq.addItem(LIVID_DAGGER);
		eq.addItem(new CFItemBuilder(SHADOW_BOMB_MATERIAL, "shadow_bomb").setName("&aShadow Prism")
				.setHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlZDRjZTIzOTMzZTY2ZTA0ZGYxNjA3MDY0NGY3NTk5ZWViNTUzMDdmN2VhZmU4ZDkyZjQwZmIzNTIwODYzYyJ9fX0=")
				.addClickEvent((player) -> this.throwShadowBomb(player, player.getLocation(), false), Action.RIGHT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR)
				.build());
		eq.addItem(SHROUDED_STEP);

	}

	@Override
	public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
		if (player.isSneaking()) {
			Chat.sendActionbar(player, "&cCannot deal damage in shadow form!");
			event.setCancelled(true);
			return;
		}

		final Player whoGotDamaged = event.getWhoGotDamaged();
		if (player.getLocation().getDirection().dot(whoGotDamaged.getLocation().getDirection()) > 0) {
			((ShadowWarrior)ClassManager.SHADOW_WARRIOR.getTheClass()).performBackStab(player, whoGotDamaged);
		}

	}

	private void deployShroudedStep(Player player) {
		if (player.hasCooldown(SHROUDED_STEP.getType())) {
			return;
		}
		if (player.isSneaking()) {
			// ticks
			int SHROUDED_STEP_DELAY = 600;
			player.setCooldown(SHROUDED_STEP.getType(), SHROUDED_STEP_DELAY);

			final Location location = player.getLocation();

			final ArmorStand entity = GarbageCollector.spawnEntity(location, ArmorStand.class, me -> {
				me.setVisible(false);
				me.setSilent(true);
				me.setInvulnerable(true);
				me.setSmall(true);
				me.getLocation().setYaw(location.getYaw());
			});

			new GameTask() {
				int currentTick = 0;

				@Override
				public void run() {
					if (entity.isDead() || currentTick++ >= MAX_TRAVEL_DISTANCE) {
						// play fx here so they know it was fake
						if (!entity.isDead()) {
							PlayerLib.spawnParticle(entity.getLocation(), Particle.CRIT_MAGIC, 10, 0, 0, 0, 0.5f);
							entity.remove();
						}
						this.cancel();
						return;
					}

					final Vector direction = entity.getLocation().getDirection();
					entity.setVelocity(new Vector(direction.getX(), -1, direction.getZ()).normalize().multiply(0.15));
					displayFakeFootsteps(entity.getLocation());

				}
			}.runTaskTimer(0, 2);

		}
		else {
			Chat.sendActionbar(player, "&eYou can only use this while in Dark Cover!");
		}
	}

	@Override
	public void useUltimate(Player player) {

		this.setUsingUltimate(player, true, ULTIMATE_LENGTH);
		// reset cooldown
		player.setCooldown(Material.IRON_SWORD, 0);

		final Location location = player.getLocation();
		PlayerLib.playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1.75f);
		PlayerLib.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 1.75f);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ULTIMATE_LENGTH, 0));

		new GameTask() {
			@Override
			public void run() {
				player.playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.RECORDS, 5, 1.85f);

			}
		}.runTaskLater(ULTIMATE_LENGTH);
	}

	@EventHandler(ignoreCancelled = true)
	public void handleUltimateHits0(BlockBreakEvent ev) {
		// Auto-Generated
		final Player player = ev.getPlayer();
		if (this.isUsingUltimate(player)) {
			// don't allow to back stab while in ultimate
			if (player.hasCooldown(Material.IRON_SWORD)) {
				return;
			}
			final Entity nearest = getNearestEntity(player);
			if (nearest != null) {
				// Used entity for testing since im lonely
				final Location location = player.getLocation();
				if (nearest instanceof Player) {
					DamageFeature.damage((Player)nearest, player, ULTIMATE_DAMAGE, EnumDamageCause.ENTITY_ATTACK);
					player.setCooldown(Material.IRON_SWORD, ULTIMATE_COOLDOWN);
				}
				player.playSound(location, Sound.BLOCK_NETHER_ORE_BREAK, SoundCategory.RECORDS, 5, 1.75f);
			}
			else {
				Chat.sendActionbar(player, "&cNo valid enemy found!");
			}
		}
	}

	@EventHandler()
	public void handleBlockPlace(BlockPlaceEvent ev) {
		// Auto-Generated
		final Player player = ev.getPlayer();
		if (CFItemBuilder.itemHasID(player.getInventory().getItemInMainHand(), "shadow_bomb")) {
			ev.setCancelled(true);
			throwShadowBomb(player, ev.getBlock().getLocation(), true);
		}
	}

	@EventHandler()
	public void handleUltimateHits(PlayerInteractEvent ev) {
		// Auto-Generated
		final Player player = ev.getPlayer();
		if (this.isUsingUltimate(player)) {
			// no one loves you off hand!
			if (ev.getHand() == EquipmentSlot.OFF_HAND) {
				return;
			}
			// don't allow to back stab while in ultimate
			if (player.hasCooldown(Material.IRON_SWORD)) {
				return;
			}
			if (ev.getAction() == Action.LEFT_CLICK_BLOCK || ev.getAction() == Action.LEFT_CLICK_AIR) {
				final Entity nearest = getNearestEntity(player);
				if (nearest != null) {
					// Used entity for testing since im lonely
					if (nearest instanceof Player) {
						DamageFeature.damage((Player)nearest, player, ULTIMATE_DAMAGE, EnumDamageCause.ENTITY_ATTACK);
						player.setCooldown(Material.IRON_SWORD, ULTIMATE_COOLDOWN);
					}
					player.playSound(player.getLocation(), Sound.BLOCK_NETHER_ORE_BREAK, SoundCategory.RECORDS, 5, 1.75f);
				}
				else {
					Chat.sendActionbar(player, "&cNo valid enemy found!");
				}
			}
		}
	}

	private Entity getNearestEntity(Player executor) {
		final Location fromWhere = executor.getLocation();
		if (fromWhere.getWorld() == null) {
			throw new NullPointerException("Cannot find entity in null world!");
		}
		final List<Entity> list = fromWhere.getWorld().getNearbyEntities(fromWhere, 10, 5, 10)
				.stream()
				.filter(entity -> entity != executor)
				.filter(entity -> entity.getType() == EntityType.PLAYER)
				.filter(entity -> !Spectator.isSpectator((Player)entity))
				.collect(Collectors.toList());
		Entity nearest = null;
		double dist = -1;
		for (Entity entity : list) {
			if (executor.hasLineOfSight(entity)) {
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
		}
		return nearest;
	}

	@Override
	public void onStopOnce() {
		playerMarker.clear();
	}

	private void throwShadowBomb(Player player, Location startLocation, boolean stay) {
		if (player.hasCooldown(SHADOW_BOMB_MATERIAL)) {
			return;
		}

		final Location location = startLocation.clone().add(0.5, 0, 0.5);

		// Teleport to marker if exists
		if (playerMarker.containsKey(player.getUniqueId())) {

			final ArmorStand marker = playerMarker.get(player.getUniqueId());
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
			final Location markerLocation = marker.getLocation();

			// keep player's yaw and pitch
			markerLocation.setYaw(player.getLocation().getYaw());
			markerLocation.setPitch(player.getLocation().getPitch());

			marker.remove();
			player.teleport(new Location(markerLocation.getWorld(), markerLocation.getBlockX() + 0.5, markerLocation.getY() + 0.15, markerLocation.getBlockZ() + 0.5, player
					.getLocation()
					.getYaw(), player.getLocation().getPitch()));
			playerMarker.remove(player.getUniqueId());
			int SHADOW_BOMB_COOLDOWN = 20 * 20;
			player.setCooldown(SHADOW_BOMB_MATERIAL, SHADOW_BOMB_COOLDOWN);
			// Remove enchant glint
			Chat.sendActionbar(player, "&aTeleported");
			player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, SoundCategory.HOSTILE, 2, 0.75f);
			return;
		}

		// Else spawn one
		ArmorStand entity = GarbageCollector.spawnEntity(location, ArmorStand.class, me -> {
			me.setVisible(false);
			me.setSilent(true);
			me.setInvulnerable(true);
			me.setSmall(true);
			me.getLocation().setYaw(player.getLocation().getYaw());
			me.getEquipment().setHelmet(ORB_TEXTURE);
		});

		// apply cooldown so we can't teleport right away
		int SHADOW_BOMB_COOLDOWN_LAUNCH = 20;
		player.setCooldown(SHADOW_BOMB_MATERIAL, SHADOW_BOMB_COOLDOWN_LAUNCH);

		Chat.sendActionbar(player, "&aPortal Deployed");
		player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_AMBIENT, SoundCategory.HOSTILE, 2, 1.75f);

		playerMarker.put(player.getUniqueId(), entity);
		GarbageCollector.add(entity);
		Reflect.hideEntity(entity, getOnlinePlayerExcept(player));

		if (!stay) {
			new GameTask() {
				int currentTick = 0;

				@Override
				public void run() {

					if (entity.isDead() || currentTick++ >= MAX_TRAVEL_DISTANCE) {
						this.cancel();
						return;
					}

					final Vector direction = entity.getLocation().getDirection();
					entity.setVelocity(new Vector(direction.getX(), -1, direction.getZ()).normalize().multiply(0.2));
					player.spawnParticle(Particle.PORTAL, entity.getLocation().clone().subtract(0, 0.5, 0), 10, 0, 0.5, 0, 0);
				}
			}.runTaskTimer(0, 2);
		}

	}

	public void performBackStab(Player damager, Player whoGotHit) {
		// don't allow damage self
		if (damager == whoGotHit) {
			return;
		}
		// don't back stab if ulting
		if (isUsingUltimate(damager)) {
			return;
		}
		// make sure we hold livid dagger lmao
		if (damager.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD && validate(damager)) {
			if (!damager.hasCooldown(LIVID_DAGGER.getType())) {
				int BACK_STAB_COOLDOWN = 20 * 20;
				damager.setCooldown(LIVID_DAGGER.getType(), BACK_STAB_COOLDOWN);
				damager.getLocation()
						.getWorld()
						.playSound(damager.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.RECORDS, 20, 0.65f);
				final Vector direction = whoGotHit.getLocation().getDirection();
				whoGotHit.getWorld().spawnParticle(Particle.CRIT, whoGotHit.getLocation(), 10, 0.25, 0, 0.25, 0.07f);
				whoGotHit.setVelocity(new Vector(direction.getX(), 0.1, direction.getZ()).multiply(2.13f));
				DamageFeature.damage(whoGotHit, damager, 4.0f, EnumDamageCause.ENTITY_ATTACK);
			}
		}
	}

	@EventHandler()
	public void handleDamage(EntityDamageEvent ev) {

		// Auto-Generated
		if (ev.getEntity() instanceof Player) {
			final Player player = (Player)ev.getEntity();
			if (validate(player)) {
				if (player.isSneaking()) {
					GameUtils.showPlayer(player);
					player.setSneaking(false);
					Chat.sendActionbar(player, "&cYou took damage and lost your invisibility");
					playKickedFx(player.getLocation());
				}
				setLastDamaged(player, System.currentTimeMillis());
			}
		}
	}

	@EventHandler()
	public void handleSneaking(PlayerToggleSneakEvent ev) {

		if (!GameManager.current().isGameInProgress() || !GameManager.current().arePlayersRevealed()) {
			return;
		}

		// Auto-Generated
		final Player player = ev.getPlayer();
		if (validate(player)) {
			final boolean sneaking = ev.getPlayer().isSneaking();
			final Location oneHigher = player.getLocation().clone().add(0, 1, 0);

			// Check if we got hit recently
			final long difference = System.currentTimeMillis() - lastDamaged.getOrDefault(player.getUniqueId(), 0L);
			// in millis
			int SNEAK_DELAY_AFTER_DAMAGE = 3000;
			if (difference <= SNEAK_DELAY_AFTER_DAMAGE) {
				Chat.sendActionbar(player, "&cCannot hide for another %ss", (difference / 1000) + 1);
				return;
			}

			// Enter invisibility form
			if (!sneaking) {
				// Invisibility used to indicate for player
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20000, 2));
				playShadowFormEffect(oneHigher, Type.ENTER);
				Chat.sendActionbar(player, "&aYou are now hidden");
				GameUtils.hidePlayer(player);
			}
			// Exit invisibility form
			else {
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
				playShadowFormEffect(oneHigher, Type.LEAVE);
				Chat.sendActionbar(player, "&cYou are no longer hidden");
				GameUtils.showPlayer(player);
			}
		}
	}

	private void playKickedFx(Location location) {
		PlayerLib.spawnParticle(location, Particle.DRAGON_BREATH, 30, 0, 0, 0, 0.5f);
		PlayerLib.spawnParticle(location, Particle.LAVA, 35, 0, 0, 0, 0);
		PlayerLib.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.75f);
		PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_SCREAM, 1.25f);
	}

	public void setLastDamaged(Player player, long when) {
		lastDamaged.put(player.getUniqueId(), when);
	}

	@Override
	public void onStart(Player player) {
	}

	@Override
	public void onPlayerRevealed(Player player) {
	}

	private List<Player> getOnlinePlayerExcept(Player exceptWho) {
		final List<Player> onlinePlayers = new ArrayList<>();
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer != exceptWho) {
				onlinePlayers.add(onlinePlayer);
			}
		}
		return onlinePlayers;
	}

	@EventHandler()
	public void handleBackStab(EntityDamageByEntityEvent ev) {
		if (ev.getDamager() instanceof Player) {
			// gonna use entity for testing cuz im lonely :(
			final Player damager = (Player)ev.getDamager();

			if (ClassManager.getClass(damager) == ClassManager.SHADOW_WARRIOR) {
				if (damager.isSneaking()) {
					Chat.sendActionbar(damager, "&cCannot deal damage in Dark Cover!");
					ev.setCancelled(true);
					ev.setDamage(0.0d);
				}
			}
		}
	}

	@EventHandler()
	public void handleMovement(PlayerMoveEvent ev) {
		// Auto-Generated
		final Player player = ev.getPlayer();
		final Location from = ev.getFrom();
		if (ev.getTo() == null) {
			return;
		}
		final Location to = ev.getTo();
		if (validate(player)) {
			if ((from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) && player.isSneaking()) {
				displayFakeFootsteps(player.getLocation());
			}
		}
	}

	private void displayFakeFootsteps(Location location) {
		new ParticlesBuilder.BlockDust(location, location.getBlock().getRelative(BlockFace.DOWN).getType(), 0.25, 0, 0.25, 3, 0).show();
	}

	private void playShadowFormEffect(Location where, Type type) {
		if (type == Type.ENTER) {
			PlayerLib.spawnParticle(where, Particle.CRIT, 20, 0, 0.2, 0, 1.0f);
			PlayerLib.spawnParticle(where, Particle.CRIT_MAGIC, 20, 0, 0.2, 0, 0.5f);
			PlayerLib.spawnParticle(where, Particle.WARPED_SPORE, 10, 0, 0.5, 0, 0);
			PlayerLib.playSound(where, Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);
		}
		else {
			PlayerLib.spawnParticle(where, Particle.ENCHANTMENT_TABLE, 10, 0, 0, 0, 2);
			PlayerLib.playSound(where, Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);
		}
	}

	private enum Type {
		ENTER,
		LEAVE
	}

	private boolean validate(Player player) {
		return GameManager.current()
				.isGameInProgress() && ClassManager.getClass(player) == ClassManager.SHADOW_WARRIOR && !Spectator.isSpectator(player);
	}

}
