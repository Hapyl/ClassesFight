package ru.hapyl.classesfight.classes.iclass;

import com.google.common.collect.Sets;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.math.Numbers;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.iclass.extra.PrincessCrown;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.entity.Entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Princess extends IClass implements PrincessCrown {

	private final int chargeDelay = 15;
	private final int ultimateTime = 13 * 20;
	private final Map<Player, Integer> stacks = new HashMap<>();
	private final Material[] validBlocks = {Material.GOLD_BLOCK, Material.RAW_GOLD_BLOCK, Material.YELLOW_GLAZED_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.YELLOW_WOOL};

	public Princess() {
		super("Helene", Material.GOLDEN_HELMET);
		this.setInfo("A young princess, who thinks she battle on her own. She isn't wrong...__", Abilities.PRINCESS_FLASH.getAbout() + Abilities.PATH_WRITTER
				.getAbout(), "Starfall!", "Toss your crown into the air to summon the &eStarfall &eSword &7that charges over &b" + BukkitUtils
				.roundTick(ultimateTime) + "&bs&7. While charging, your hits will generate &eStarfall &eCharges once every &b" + BukkitUtils.roundTick(chargeDelay) + "s&7 (Max 13 Charges). After, explodes and deals damage to enemies. Radius and damage of the explosion is scaled with &eStarfall &eCharges&7.", 7);
		this.setRole(ClassRole.ASSASSIN);
		this.setUltimateSound(Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.0f);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJiMGI3MTI0ZTFkZjJiOWExZDBhOTk2OWE5OTQ1ZjUxNDgxMTU4MGRjZWViZTg2ZTZlODNiMmU1YjA0NzJmIn19fQ==");
		eq.setChestplate(Material.GOLDEN_CHESTPLATE);
		eq.setLeggings(Color.ORANGE);
		eq.setBoots(Color.YELLOW);

		eq.addItem(new CFItemBuilder(Material.IRON_SWORD, "festering_desire").setName("&aFestering Desire &6&lCLICK")
				.addClickEvent(this::useFesteringDesire)
				.withCooldown(10 * 20)
				.setPureDamage(3.5d)
				.applyDefaultSettings()
				.build());

		Abilities.PRINCESS_FLASH.addItemIfExists(eq);
		Abilities.PATH_WRITTER.addItemIfExists(eq);

	}

	private void useFesteringDesire(Player player) {
		final int tickBetween = 2;
		final Location eyeLocation = player.getEyeLocation();
		final Location location = eyeLocation.add(eyeLocation.getDirection().multiply(1.5).setY(0.0d));
		GameTask.runTaskTimerTimes((task, tick) -> {
			PlayerLib.playSound(location, Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.5f);
			PlayerLib.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.2f);
			PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 1, 0.2, 0.2, 0.2, 0);
			GameUtils.getPlayerInRange(location, 1).forEach(target -> {
				if (player == target) {
					return;
				}
				DamageFeature.setLastDamager(target, player);
				DamageFeature.damageTick(target, null, 2.0d, EnumDamageCause.ENTITY_ATTACK, tickBetween);
			});
		}, tickBetween, 5);
	}

	@Override
	public void useUltimate(Player player) {
		this.setUsingUltimate(player, true, ultimateTime + 35);
		playCrownAnimation(player, 35, me -> createSpirit(player));
		this.stacks.put(player, 0);
	}

	@Override
	public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
		if (isUsingUltimate(player) && !player.hasCooldown(Material.IRON_HOE)) {
			if (player == event.getWhoGotDamaged()) {
				return;
			}
			final int next = Numbers.clamp(stacks.getOrDefault(player, 0) + 1, 0, 13);
			stacks.put(player, next);
			player.setCooldown(Material.IRON_HOE, chargeDelay);
			Chat.sendTitle(player, "", "&e🗡 &6&l" + (next == 13 ? "MAX" : next), 0, 25, 5);
		}
	}

	@Override
	public void onStopOnce() {
		stacks.clear();
	}

	private void createSpirit(Player player) {
		final Location spawnLocation = player.getEyeLocation();
		Objects.requireNonNull(player.getLocation().getWorld()).strikeLightningEffect(spawnLocation);
		final ArmorStand entity = (ArmorStand)Entities.ARMOR_STAND.spawn(spawnLocation, me -> {
			me.setVisible(false);
			me.setMarker(true);
			me.setInvulnerable(true);
			me.setRightArmPose(new EulerAngle(Math.toRadians(80.0d), 0.0d, 0.0d));
			me.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
		});

		GameTask.runTaskTimerTimes((task, tick) -> {
			if (Spectator.isSpectator(player)) {
				entity.remove();
				task.cancel();
				return;
			}

			if (tick == 0) {
				final Location location = entity.getLocation().add(0.0d, 0.5d, 0.0d);
				final int stack = stacks.getOrDefault(player, 1);
				final double radius = (stack / 3.0d + (1.2d));
				final double damage = (stack + (2.5d));

				entity.remove();
				PlayerLib.spawnParticle(location, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0);
				PlayerLib.playSound(Sound.ENTITY_GENERIC_EXPLODE, 1.5f);
				GameUtils.createFakeExplosion(location, radius, damage, player, EnumDamageCause.STARFALL);
				return;
			}

			//entity.getEquipment()
			//		.setItemInMainHand(new ItemStack((tick <= 60 && (tick % 10) != 0) ? Material.GOLDEN_SWORD : Material.DIAMOND_SWORD));

			final Location location = entity.getLocation();
			final Location eyeLocation = player.getEyeLocation();
			location.setYaw(location.getYaw() + 10f);
			eyeLocation.setYaw(location.getYaw());

			entity.teleport(eyeLocation);
		}, 1, ultimateTime);
	}

	private void createUltimateZone(final Player player, final Location location, final ArmorStand stand) {
		location.subtract(0, 1, 0);
		final Set<Block> affectedBlocks = Sets.newHashSet();

		final int maxRadius = 40;
		final int waitTime = 3;
		final int tickPerExplosion = 5;
		final int waitTimeBeforeExplosion = ((maxRadius * waitTime) / 2);
		this.setUsingUltimate(player, true);

		// Spawn Animation
		new GameTask() {
			private int radius = 0;

			@Override
			public void run() {
				if ((radius += 2) >= maxRadius) {
					this.cancel();
					return;
				}

				for (int x = -radius; x < radius; x++) {
					for (int z = -radius; z < radius; z++) {
						if (Math.pow(x, 2) + Math.pow(z, 2) > radius) {
							continue;
						}

						//affect here
						if (location.getWorld() == null) {
							break;
						}
						location.add(x, 0, z);

						// Get the highest block
						int y = 0;
						Block testBlock = location.getBlock().getRelative(BlockFace.UP);
						while (!testBlock.getType().isAir() && y++ < 20) {
							testBlock = testBlock.getRelative(BlockFace.UP);
						}

						final Block block = location.add(0, y, 0).getBlock();
						location.subtract(x, y, z);
						if (affectedBlocks.contains(block)) {
							continue;
						}
						affectedBlocks.add(block);
						sendChange(block, CollectionUtils.randomElement(validBlocks, validBlocks[0]));
					}
				}

			}
		}.addCancelEvent(() -> this.clearBlocks(affectedBlocks)).runTaskTimer(0, waitTime);

		// Affection
		new GameTask() {
			private int currentTick = ultimateTime;

			@Override
			public void run() {
				if ((currentTick -= tickPerExplosion) <= 0) {
					clearBlocks(affectedBlocks);
					setUsingUltimate(player, false);
					this.cancel();
					return;
				}

				final Block block = CollectionUtils.randomElement(affectedBlocks);
				if (block == null) {
					return; // ?
				}
				final Location blockLocation = block.getLocation().add(0, 1, 0);
				PlayerLib.spawnParticle(blockLocation, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0);
				PlayerLib.playSound(blockLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.75f);
				GameUtils.getPlayerInRange(blockLocation, 2)
						.forEach(target -> DamageFeature.damage(target, player, 5.0d, EnumDamageCause.ENTITY_ATTACK));
				blockLocation.subtract(0, 1, 0);
			}
		}.runTaskTimer(waitTimeBeforeExplosion, tickPerExplosion);

	}

	private void sendChange(Block block, Material material) {
		final BlockData data = material.createBlockData();
		for (final Player player : Bukkit.getOnlinePlayers()) {
			player.sendBlockChange(block.getLocation(), data);
		}
	}

	private void clearBlocks(Set<Block> set) {
		for (final Block block : set) {
			block.getState().update(false, false);
		}
		set.clear();
	}

}
