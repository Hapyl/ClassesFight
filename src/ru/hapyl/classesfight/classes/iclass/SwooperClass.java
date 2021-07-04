package ru.hapyl.classesfight.classes.iclass;

import com.google.common.collect.Lists;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.Numbers;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.particle.AbstractParticleBuilder;
import kz.hapyl.spigotutils.module.particle.ParticleBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwooperClass extends IClass implements Listener {

	private final int ultimateTime = 200;
	private final int rifleCooldown = 45;
	private final int satchelCooldown = 100;

	private final ItemStack rocketLauncher = new ItemBuilder(Material.GOLDEN_HORSE_ARMOR, "rocket_launcher").setName("&aShowstopper &6&lCLICK")
			.addClickEvent(this::shootRocketLauncher)
			.build();

	private final Map<Player, Entity> satchelMap;
	private final double[] armorStandOffsets = {0.5d, -1.17d, 0.5d};

	public SwooperClass() {
		super("Swooper", Material.SUGAR);

		this.setRole(ClassRole.RANGE);
		this.setInfo("A sniper with slow firing rifle, but fast ways to move around the battlefield.__",
				"&e○ Blast Pack__&b1) &7Throw the blast pack!__&b2) &7Click again to explode!__&b3) &7???__&b4) &7Fly!____&e○ Sniper Zoom &e&lSNEAK &7Zoom your rifle to increase range and damage.",
				"Show Stopper",
				"Equip a rocket launcher for &b" + BukkitUtils.roundTick(ultimateTime) + "s&7. &6&lCLICK &7to launch explosive in front of you. On impact, explodes in a massive radius damaging everyone.", 8);

		this.setRating(ClassRating.NOT_YET);
		this.setLvlRequired(1);

		this.satchelMap = new HashMap<>();
		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjE4MWM4MTFhZDM3NDY3NTUwZDdjMDFjYWMyZTUyMjNjNGU5OWZhNzkwNjM0OGY5NDBjOTQ1NmQ4YWEwY2QxYiJ9fX0=");
		eq.setChestplate(25, 53, 82);
		eq.setLeggings(25, 53, 92);
		eq.setBoots(25, 53, 102);

		eq.createAndAddItem(Material.WOODEN_HOE, "swoop_range", this::shootRifle, "Rifle");
		eq.createAndAddItem(Material.DETECTOR_RAIL, "swoop_trap", this::throwTrap, "Blast Pack");

	}

	@Override
	public void onStopOnce() {
		satchelMap.values().forEach(Entity::remove);
		satchelMap.clear();
	}

	@Override
	public void onDeath(Player player) {
		final Entity entity = satchelMap.get(player);
		if (entity != null) {
			entity.remove();
			satchelMap.remove(player);
		}
	}

	@EventHandler()
	public void handleSneaking(PlayerToggleSneakEvent ev) {
		final Player player = ev.getPlayer();

		if (!GameUtils.isValidPlayer(player, ClassManager.SWOOPER) || player.getInventory().getHeldItemSlot() != 0) {
			return;
		}

		if (ev.isSneaking()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000, 6));
		}
		else {
			player.removePotionEffect(PotionEffectType.SLOW);
		}

	}

	private void shootRifle(Player player) {
		if (player.hasCooldown(Material.WOODEN_HOE)) {
			return;
		}
		player.setCooldown(Material.WOODEN_HOE, isUsingUltimate(player) ? rifleCooldown / 2 : rifleCooldown);
		PlayerLib.playSound(Sound.ENTITY_GENERIC_EXPLODE, 2.0f);
		GameUtils.rayTraceLine(player, (player.isSneaking() ? 50 : 25), 0.5d, (player.isSneaking() ? 8.0 : 4.0), EnumDamageCause.RIFLE,
				move -> PlayerLib.spawnParticle(move, Particle.FIREWORKS_SPARK, 1, 0, 0, 0, 0), null);
	}

	private void throwTrap(Player player) {
		if (player.hasCooldown(Material.DETECTOR_RAIL)) {
			return;
		}

		// blow
		if (hasSatchel(player)) {
			final ItemStack item = getSatchel(player);
			if (item != null) {
				item.setType(Material.DETECTOR_RAIL);
			}
			player.setCooldown(Material.DETECTOR_RAIL, isUsingUltimate(player) ? satchelCooldown / 2 : satchelCooldown);
			blowSatchel(player);
			return;
		}

		final Vector direction = player.getEyeLocation().getDirection().normalize();
		final Vector vector = new Vector(direction.getX(), direction.getY(), direction.getZ());

		final Item item = player.getLocation().getWorld().dropItem(player.getEyeLocation(), new ItemStack(this.getClassEquipment().getItem(1)));
		this.satchelMap.put(player, item);

		item.setPickupDelay(Integer.MAX_VALUE);
		item.setVelocity(vector);
		item.setThrower(player.getUniqueId());

		final ItemStack handItem = getSatchel(player);

		if (handItem != null) {
			handItem.setType(Material.ACTIVATOR_RAIL);
			CFItemBuilder.setName(handItem, "&aBlast Pack &e&lCLICK TO DETONATE");
		}

		new GameTask() {
			private int wait = 100;

			@Override
			public void run() {

				if (satchelMap.get(player) != item) {
					this.cancel();
					return;
				}

				// error!
				if (wait-- <= 0) {
					player.setCooldown(Material.DETECTOR_RAIL, 0);
					item.remove();
					this.cancel();
					return;
				}

				if (item.isOnGround()) {
					SwooperClass.this.stickBlastPack(player, item);
					this.cancel();
				}

			}
		}.runTaskTimer(0, 1);

	}

	private ItemStack getSatchel(Player player) {
		return player.getInventory().getItem(1);
	}

	private void blowSatchel(Player player) {
		final Entity satchel = this.satchelMap.get(player);
		this.satchelMap.remove(player);
		satchel.remove();

		final ItemStack handItem = getSatchel(player);
		if (handItem != null) {
			CFItemBuilder.setName(handItem, "&aBlast Pack");
		}

		final double blowDist = 4;
		final Location location = satchel.getLocation();

		if (satchel instanceof ArmorStand) {
			location.subtract(armorStandOffsets[0], armorStandOffsets[1], armorStandOffsets[2]);
		}

		if (location.getWorld() == null) {
			return;
		}

		location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 15, 1.75f);
		Geometry.drawSphere(location, ((blowDist * 2) + 1), blowDist, new WorldParticle(Particle.FIREWORKS_SPARK));

		// push
		final Collection<Entity> players = location.getWorld()
				.getNearbyEntities(location, blowDist, blowDist * 0.5f, blowDist, entity -> entity instanceof Player);
		players.forEach(target -> {
			if (target != player) {
				DamageFeature.damage((Player)target, player, 2.0d, EnumDamageCause.SATCHEL);
			}
			else {
				GameEffectManager.applyEffect(player, GameEffectType.FALL_DAMAGE_RESISTANCE, 40, true);
			}

			final Vector normalVector = target.getLocation().toVector().subtract(location.toVector()).normalize();
			target.setVelocity(normalVector.multiply(target == player ? 1.35d : 0.35d));
		});

	}

	private void stickBlastPack(Player player, Item item) {
		final Location location = item.getLocation().clone();
		item.remove();

		final Location spawnLocation = location.clone().add(armorStandOffsets[0], armorStandOffsets[1], armorStandOffsets[2]);
		spawnLocation.setYaw(0.0f);
		spawnLocation.setPitch(0.0f);

		final ArmorStand entity =
				GarbageCollector.spawnEntity(spawnLocation, ArmorStand.class, me -> {
					me.setSilent(true);
					me.setVisible(false);
					me.setMarker(true);
					me.setGravity(false);
					me.setInvulnerable(true);
					me.getEquipment().setHelmet(new ItemStack(Material.DETECTOR_RAIL));
					me.setHeadPose(new EulerAngle(Math.toRadians(270), Math.toRadians(315), 0));
				});

		this.satchelMap.put(player, entity);
		PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
		PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.75f);
		PlayerLib.spawnParticle(location, Particle.VILLAGER_HAPPY, 1, 0.1f, 0.1f, 0.1f, 0.0025f);
	}

	private boolean hasSatchel(Player player) {
		return this.satchelMap.containsKey(player);
	}

	private void shootRocketLauncher(Player player) {
		player.getInventory().getItemInMainHand().setAmount(0);
		final Location location = player.getEyeLocation().clone();
		final Vector vector = location.getDirection();
		new GameTask() {

			private double distance = 0.5d;
			private float pitch = 0.45f;

			private void explode() {
				PlayerLib.spawnParticle(location, Particle.EXPLOSION_HUGE, 1, 0, 0, 0, 0);
				PlayerLib.spawnParticle(location, Particle.LAVA, 20, 1, 1, 1, 0);
				PlayerLib.spawnParticle(location, Particle.FLAME, 15, 1, 1, 1, 0.75f);
				PlayerLib.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.0f);
				PlayerLib.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.75f);
				GameUtils.getPlayerInRange(location, 6.0d).forEach(entity -> {
					// / (entity == player ? 2 : 1)
					final double damage = (20.0d - entity.getLocation().distance(location));
					DamageFeature.damage(entity, player, damage, EnumDamageCause.ENTITY_ATTACK);
				});
				this.cancel();
			}

			@Override
			public void run() {

				if (((distance += 0.5d) >= 40.0d) || (!location.getBlock().getType().isAir())) {
					explode();
					return;
				}

				double x = distance * vector.getX();
				double y = distance * vector.getY();
				double z = distance * vector.getZ();

				location.add(x, y, z);
				pitch = Numbers.clamp(pitch + 0.025f, 0.0f, 2.0f);
				PlayerLib.spawnParticle(location, Particle.LAVA, 4, 0.01, 00.1, 00.1, 0);
				PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, pitch);

			}
		}.runTaskTimer(0, 1);
	}

	@Override
	public void useUltimate(Player player) {
		setUsingUltimate(player, true);
		final PlayerInventory inventory = player.getInventory();
		inventory.setItem(4, rocketLauncher);
		inventory.setHeldItemSlot(4);

		if (true) {
			new GameTask() {

				private int tick = ultimateTime;

				private void removeRocketLauncher() {
					setUsingUltimate(player, false);
					player.getInventory().setItem(4, new ItemStack(Material.AIR));
					this.cancel();
				}

				@Override
				public void run() {

					if (tick-- <= 0 || player.getInventory().getItem(4) == null) {
						this.removeRocketLauncher();
						return;
					}

					final int tick20 = tick * 20 / ultimateTime;
					final StringBuilder builder = new StringBuilder();
					for (int i = 0; i < 20; i++) {
						builder.append(i <= tick20 ? ChatColor.GOLD : ChatColor.DARK_GRAY).append("-");
					}
					Chat.sendTitle(player, "&eRocket Fuse", builder.toString(), 0, 5, 2);

				}
			}.runTaskTimer(0, 1);
			return;
		}

		// swooping ultimate
		this.setUsingUltimate(player, true);
		final List<Location> list = Lists.newArrayList();
		final AbstractParticleBuilder particle = new ParticleBuilder(Particle.ENCHANTMENT_TABLE).setAmount(5)
				.setOffX(0.2d)
				.setOffZ(0.2d)
				.setSpeed(0.1f);

		new GameTask() {
			private int currentTick = 0;

			@Override
			public void run() {
				if (Spectator.isSpectator(player)) {
					Chat.sendMessage(player, "&aYou died, Swooper is gone!");
					PlayerLib.playSound(player, Sound.BLOCK_GLASS_BREAK, 0.33f);
					this.cancel();
					return;
				}

				if (currentTick++ >= ultimateTime) {
					SwooperClass.this.swoopBack(player, list);
					this.cancel();
					return;
				}

				if (currentTick % 5 == 0) {
					particle.display(player.getLocation());
				}

				int tick = ((currentTick * 20) / ultimateTime);
				Chat.sendTitle(player, "", buildBars(tick, ChatColor.GREEN), 0, 10, 0);

				list.add(player.getLocation());

			}
		}.runTaskTimer(0, 1);

	}

	private String buildBars(int tick, ChatColor a) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			builder.append(tick > i ? a : ChatColor.DARK_GRAY).append("|");
		}
		return builder.toString();
	}

	private void swoopBack(Player player, List<Location> list) {
		GameEffectManager.applyEffect(player, GameEffectType.INVISIBILITY, list.size());

		boolean allowedToFly = player.getAllowFlight();
		boolean isFlying = player.isFlying();
		float oldSpeed = player.getFlySpeed();

		player.setAllowFlight(true);
		player.setFlying(true);
		player.setFlySpeed(0.0f);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5000, 3, true, false));

		new GameTask() {
			private int index = list.size() - 1;

			@Override
			public void run() {
				if (Spectator.isSpectator(player)) {
					Chat.sendMessage(player, "&aYou died, Swooper is gone!");
					list.clear();
					this.cancel();
					return;
				}

				if (index > 0) {
					final Location location = list.get(index--);
					int tick = ((index * 20) / list.size());
					Chat.sendTitle(player, "", buildBars(tick, ChatColor.YELLOW), 0, 10, 0);
					player.teleport(location);
					return;
				}

				player.removePotionEffect(PotionEffectType.SLOW);
				player.setFlying(isFlying);
				player.setAllowFlight(allowedToFly);
				player.setFlySpeed(oldSpeed);
				setUsingUltimate(player, false);
				list.clear();
				this.cancel();

			}
		}.runTaskTimer(0, 1);

	}

}
