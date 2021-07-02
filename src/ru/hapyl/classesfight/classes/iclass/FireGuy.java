package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GeometryLib;
import ru.hapyl.classesfight.utils.SoundLib;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FireGuy extends IClass implements Listener {

	public final Set<Player> onReborn = new HashSet<>();

	private final int ULTIMATE_TIME = 200;

	private final int FLASH_COOLDOWN = 600;
	private final int FLASH_TIME = 40;
	private final int FLASH_TIME_WINDUP = 20;

	private final int MOLOTOV_COOLDOWN = 700;
	private final int MOLOTOV_TIME = 100;
	private final double MOLOTOV_HEALING = 0.3465d;
	private final double MOLOTOV_RADIUS = 3.0;

	private final double GUN_PURE_DAMAGE = 3.5d;
	private final int GUN_RELOAD_TIME = 30;

	private final ItemStack FIRE_GUN = new CFItemBuilder(Material.STICK, "fire_gun").setName("&aFire Sprayer &e&lRIGHT CLICK")
			.withCooldown(GUN_RELOAD_TIME)
			.addEnchant(Enchantment.KNOCKBACK, 1)
			.addClickEvent(this::shoot, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)
			.build();
	private final ItemStack FIRE_THROWABLE = new CFItemBuilder(Material.FIRE_CHARGE, "fire_molotov").setName("&aFireball &e&lRIGHT CLICK")
			.withCooldown(MOLOTOV_COOLDOWN)
			.addClickEvent(this::throwMolotov)
			.build();
	private final ItemStack FIRE_FLASH = new CFItemBuilder(Material.WHITE_DYE, "fire_flash").setName("&aFire Flash &e&lRIGHT CLICK")
			.withCooldown(FLASH_COOLDOWN)
			.addClickEvent(this::throwFlash)
			.build();

	private final Map<Entity, Player> molotov = new ConcurrentHashMap<>();
	private final Set<Entity> molotovZones = new HashSet<>();
	private final Map<Player, Entity> ultimateStand = new HashMap<>();

	public FireGuy() {
		super("Spark", Material.BLAZE_POWDER);

		this.setRole(ClassRole.RANGE);
		this.setRating(ClassRating.A);
		this.setInfo("Strikes as &ofire &7with his &ofire &7abilities.____&e○ &eFire Sprayer &7- Long range weapon, shoots fire beams that damages enemies upon hitting them for &c" + GUN_PURE_DAMAGE / 2 + " &7hearts. Does not pierce blocks.____&e○ Fireball &7- Throws a ball of fire into direction you looking that bounces from walls. Upon hitting ground, creates fire in &a" + MOLOTOV_RADIUS + "&7blocks radius. Damage enemies and healing yourself.____&e○ Fire Flash &7- Curves exploding ball of blinding energy up. Exploding after &a" + (FLASH_TIME_WINDUP / 20) + " &7second. Blinding everyone who has line of sight with it.", "Run it Back", "Instantly place a marker at your current location. While active, instead of dying or after &b" + ULTIMATE_TIME / 20 + " &7seconds safely sends you to the marker with health your had before activating it.", 8);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWRlMDk1MzMyNzIwMjE1Y2E5Yjg1ZTdlYWNkMWQwOTJiMTY5N2ZhZDM0ZDY5NmFkZDk0ZDNiNzA5NzY3MDJjIn19fQ==");
		eq.setChestplate(Color.ORANGE);
		eq.setLeggings(Color.RED);
		eq.setBoots(Color.ORANGE);

		eq.addItem(FIRE_GUN);
		eq.addItem(FIRE_THROWABLE);
		eq.addItem(FIRE_FLASH);

	}

	@Override
	public void processDamageEventAsVictim(Player player, PlayerDamageByPlayerEvent event) {
		if (onReborn.contains(player)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onStopOnce() {
		onReborn.clear();
		molotov.keySet().forEach(Entity::remove);
		molotov.clear();
		molotovZones.forEach(Entity::remove);
		molotovZones.clear();
		ultimateStand.forEach((a, b) -> b.remove());
		ultimateStand.clear();
	}

	@Override
	public void onStart(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
	}

	@Override
	public void onStartOnce() {
		new GameTask() {
			@Override
			public void run() {
				molotov.forEach((ent, owner) -> {
					if (ent.isOnGround()) {
						ent.remove();
						spawnMolotov(owner, ent.getLocation());
						molotov.remove(ent);
					}
				});
			}
		}.runTaskTimer(0, 1);
	}

	@Override
	public boolean predicateUltimate(Player player) {
		return getSafeLocation(player) != null;
	}

	@Override
	public String predicateMessage() {
		return "No safe location found!";
	}

	private void throwFlash(Player player) {

		final Location location = player.getLocation().add(0, 1, 0);
		final Item item = location.getWorld().dropItem(location, new ItemStack(Material.WHITE_DYE));

		item.setPickupDelay(50000);
		SoundLib.play(location, Sound.ENTITY_ARROW_SHOOT, 1.5f);

		GarbageCollector.add(item);
		item.setVelocity(location.getDirection().add(new Vector(0, 0.75, 0)));

		new GameTask() {
			int currentTick = 0;

			@Override
			public void run() {

				// Blast
				if (currentTick++ >= FLASH_TIME_WINDUP) {

					if (item.isDead()) {
						this.cancel();
						return;
					}

					SoundLib.play(item.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0);
					item.getWorld().spawnParticle(Particle.FLASH, item.getLocation(), 2, 0, 0, 0, 0);

					GameManager.current().forEachInGamePlayer(gamer -> {
						if (gamer.hasLineOfSight(item)) {
							gamer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, FLASH_TIME, 20));
							SoundLib.playAndCut(gamer, Sound.ITEM_ELYTRA_FLYING, 2, FLASH_TIME);
						}
					});

					item.remove();
					this.cancel();
					return;
				}

				item.getWorld().spawnParticle(Particle.FLAME, item.getLocation(), 1, 0, 0, 0, 0.1);

			}
		}.runTaskTimer(0, 1);

	}

	public void throwMolotov(Player player) {

		final Location location = player.getLocation().add(0, 1, 0);
		final Vector vector = location.getDirection().add(new Vector(0, 0.25, 0));
		final Item item = location.getWorld().dropItem(location, new ItemStack(Material.HONEYCOMB));

		item.setCustomName(player.getUniqueId().toString());
		item.setPickupDelay(50000);
		item.setVelocity(vector.multiply(1.5));
		SoundLib.play(location, Sound.ENTITY_ARROW_SHOOT, 0);
		molotov.put(item, player);

	}

	private void shoot(Player player) {

		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 2.0f);

		double MAX_DISTANCE = 40;
		double SHIFT = 0.5;

		final Location location = player.getLocation().add(0, 1.5, 0);
		final Vector vector = location.getDirection().normalize();

		main:
		for (double i = 0; i < MAX_DISTANCE; i += SHIFT) {

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
					if (entity == player)
						continue;
					if (entity instanceof Player && !Spectator.isSpectator((Player)entity)) {
						DamageFeature.damage((Player)entity, player, GUN_PURE_DAMAGE, EnumDamageCause.FIRE_SPRAY);
						entity.setFireTicks(10);
						spark(entity.getLocation());
						break main;
					}
				}
			}

			if (i > 1.0) {
				location.getWorld().spawnParticle(Particle.FLAME, location, 1, 0., 0, 0, 0.001);
			}
			location.subtract(x, y, z);
		}

	}

	private void spark(Location location) {
		PlayerLib.spawnParticle(location, Particle.LAVA, 3, 0, 0, 0, 0);
	}

	private void spawnMolotov(Player damager, Location loc) {
		new GameTask() {
			private int times = MOLOTOV_TIME / 5;

			@Override
			public void run() {
				if (times-- <= 0) {
					this.cancel();
					return;
				}

				final List<Player> players = GameManager.current().getNearbyPlayers(loc, MOLOTOV_RADIUS, MOLOTOV_RADIUS, MOLOTOV_RADIUS);
				for (Player player : players) {
					if (player == damager) {
						DamageFeature.addHealth(player, Math.min(MOLOTOV_HEALING, DamageFeature.getMaxHealth(player)));
					}
					else {
						DamageFeature.damage(player, damager, 1.0, EnumDamageCause.FIRE_MOLOTOV);
					}
				}
				PlayerLib.playSound(loc, Sound.BLOCK_FIRE_AMBIENT, 2f);
				PlayerLib.spawnParticle(loc, Particle.FLAME, 15, MOLOTOV_RADIUS / 2, 0.1, MOLOTOV_RADIUS / 2, 0.05f);
				GeometryLib.drawCircle(loc, 60, MOLOTOV_RADIUS, new GeometryLib.TinyParticle(Particle.FLAME));

			}
		}.runTaskTimer(0, 5);

	}

	@Override
	public void useUltimate(Player player) {

		final Location safeLocation = getSafeLocation(player);

		if (safeLocation == null) {
			return;
		}

		ArmorStand stand = GarbageCollector.spawnEntity(safeLocation, ArmorStand.class, me -> {
			me.setMaxHealth(DamageFeature.getHealth(player));
			me.setHealth(DamageFeature.getHealth(player));
			me.setGravity(false);
			me.setInvulnerable(true);
			me.setVisible(false);
			me.setFireTicks(400);
		});

		ultimateStand.put(player, stand);

		new GameTask() {
			int currentTick = ULTIMATE_TIME;

			@Override
			public void run() {
				// make sure players ult is still active ; else cancel
				if (ultimateStand.containsKey(player)) {

					// display how much time left
					// symbols => ■□
					StringBuilder current = new StringBuilder();
					for (int i = 0; i < 20; i++) {
						current.append(Chat.format(i >= (currentTick / 10) ? "&c|" : "&a|"));
					}

					// fx
					player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1, 0), 1, 0.5, 0, 0.5, 0.01);

					Chat.sendTitle(player, "", current.toString(), 0, 10, 0);

					PlayerLib.spawnParticle(safeLocation, Particle.LANDING_LAVA, 1, 0.2, 0.2, 0.2, 0.05f);
					PlayerLib.spawnParticle(safeLocation, Particle.DRIP_LAVA, 1, 0.2, 0.2, 0.2, 0.05f);

					if (currentTick-- <= 0) {
						rebornPlayer(player);
						this.cancel();
					}

				}
				else {
					this.cancel();
				}
			}
		}.runTaskTimer(0, 1);

	}

	public boolean isInUltimateForm(Player player) {
		return ultimateStand.containsKey(player);
	}

	public void rebornPlayer(Player player) {

		final Entity stand = ultimateStand.get(player);
		DamageFeature.setHealth(player, ((LivingEntity)stand).getHealth());

		// reborn fx
		final Location location = player.getLocation();
		PlayerLib.spawnParticle(location, Particle.FIREWORKS_SPARK, 50, 0.1d, 0.5d, 0.1d, 0.2f);
		PlayerLib.spawnParticle(location, Particle.LAVA, 10, 0.1d, 0.5d, 0.1d, 0.2f);

		Chat.sendTitle(player, "", "&eRebooting...", 5, 10, 5);
		PlayerLib.playSound(location, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1.5f);
		player.teleport(stand.getLocation().add(0, 1, 0));
		onReborn.add(player);
		stand.remove();
		ultimateStand.remove(player);

		new GameTask() {
			@Override
			public void run() {
				onReborn.remove(player);
			}
		}.runTaskLater(20);

	}

	@EventHandler
	public void handleMove(PlayerMoveEvent ev) {

		final Player player = ev.getPlayer();
		final Location from = ev.getFrom();
		final Location to = ev.getTo();

		if (to == null) {
			return;
		}

		if (GameManager.current().isGameInProgress() && ClassManager.getClass(player) == ClassManager.FIRE_GUY && onReborn.contains(player)) {
			if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
				ev.setCancelled(true);
			}
		}
	}

	private Location getSafeLocation(Player player) {
		final Location location = player.getLocation();
		// start with a bit of Y offset
		location.add(0, 2, 0);
		for (int i = 0; i < 10; i++) {
			location.subtract(0, 1, 0);
			if (!location.getBlock().getType().isAir())
				return location;
		}
		return null;
	}
}
