package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Moonwalker extends IClass {

	private final int corrosionTime = 130;
	private final double meteoriteExplosionRadius = 8.5d;

	public Moonwalker() {
		super("Moonwalker", Material.END_STONE);
		this.setAttack(EnumInfo.HIGH);
		this.setRole(ClassRole.RANGE);
		this.setInfo("A traveller from another planet... or, should I say moon? Brings his skills and... planets... with himself!__",
				Abilities.MOONWALKER_WEAPON.getAbout()
						+ Abilities.MOON_PILLAR.getAbout()
						+ "__&e○ Target &e&lSNEAK &7Shows targeting block which is location where abilities are casted to.",
				"Moonteorite", "Summons a meteorite to target location, upon landing, creates huge explosion, damaging players caught in range and applying Corrosion for &b" + (corrosionTime / 20) + "s&7.",
				9);

		this.setRating(ClassRating.S);
		this.setLvlRequired(4);

		final ClassEquipment equipment = this.getClassEquipment(true);

		equipment.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWNmOGZiZDc2NTg2OTIwYzUyNzM1MTk5Mjc4NjJmZGMxMTE3MDVhMTg1MWQ0ZDFhYWM0NTBiY2ZkMmIzYSJ9fX0=");
		equipment.setChestplate(199, 199, 194);
		equipment.setLeggings(145, 145, 136);
		equipment.setBoots(53, 53, 49);

		Abilities.MOONWALKER_WEAPON.addItemIfExists(equipment);
		Abilities.MOON_PILLAR.addItemIfExists(equipment);

	}

	@Override
	public void onStart(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 999999, 2));
	}

	@Override
	public void onStartOnce() {
		final List<Player> moonWalkers = ClassManager.getPlayersInClass(ClassManager.MOONWALKER);
		if (moonWalkers.isEmpty()) {
			return;
		}
		new GameTask() {
			@Override
			public void run() {
				moonWalkers.forEach(player -> {
					if (Spectator.isSpectator(player)) {
						return;
					}
					final Block block = getTargetBlock(player);
					if (block == null || !player.isSneaking()) {
						return;
					}
					final Location location = block.getRelative(BlockFace.UP).getLocation().clone().add(0.5d, 0.0d, 0.5d);
					for (int i = 0; i < 10; i++) {
						player.spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
						location.add(0, 0.15, 0);
					}
				});
			}
		}.runTaskTimer(0, 2);
	}

	@Override
	public void useUltimate(Player player) {
		final int distance = 16;
		final Location playerLocation = getTargetBlock(player).getRelative(BlockFace.UP).getLocation().clone().add(0.5d, 0.0d, 0.5d);
		final Location startLocation = playerLocation.clone().add(distance, distance, distance);

		PlayerLib.playSound(player, Sound.ENTITY_WITHER_DEATH, 0.0f);

		new GameTask() {
			private int tick = 0;

			@Override
			public void run() {

				if (tick++ >= distance + 1) {
					Bukkit.getOnlinePlayers().forEach(player -> player.stopSound(Sound.ENTITY_WITHER_DEATH));
					explode(player, playerLocation);
					this.cancel();
					return;
				}

				GameUtils.getPlayerInRange(playerLocation, 8.5d).forEach(target -> Chat.sendTitle(target, "&4&l⚠", "&cMeteorite Warning!", 0, 5, 5));

				Geometry.drawCircle(playerLocation, 10, Quality.NORMAL, new WorldParticle(Particle.CRIT));
				Geometry.drawCircle(playerLocation, 10.25, Quality.HIGH, new WorldParticle(Particle.SNOW_SHOVEL));

				createBlob(startLocation.clone(), (tick == distance + 1));
				startLocation.subtract(1, 1, 1);

			}
		}.runTaskTimer(5, 2);

	}

	@Override
	public boolean predicateUltimate(Player player) {
		return getTargetBlock(player) != null;
	}

	@Override
	public String predicateMessage() {
		return "No valid block in sight!";
	}

	@Nullable
	private Block getTargetBlock(Player player) {
		return player.getTargetBlockExact(25);
	}

	private void explode(Player executor, Location location) {
		final World world = location.getWorld();
		if (world == null) {
			throw new NullPointerException("world is null");
		}

		// fx
		world.spawnParticle(Particle.EXPLOSION_HUGE, location, 1, 0, 0, 0, 0);
		world.spawnParticle(Particle.EXPLOSION_NORMAL, location, 15, 5, 2, 5, 0);

		world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 50, 0.0f);
		world.playSound(location, Sound.ENTITY_WITHER_HURT, 50, 0.25f);
		world.playSound(location, Sound.ENTITY_ENDER_DRAGON_HURT, 50, 0.5f);

		// dmg
		world.getNearbyEntities(location, meteoriteExplosionRadius, meteoriteExplosionRadius, meteoriteExplosionRadius)
				.stream()
				.filter(g -> g instanceof Player)
				.forEach(player -> {
					GameEffectManager.applyEffect((Player)player, GameEffectType.CORROSION, corrosionTime);
					DamageFeature.damage((Player)player, executor, 20.0d, EnumDamageCause.METEORITE);
				});
	}

	public void createBlob(Location center, boolean last) {
		PlayerLib.spawnParticle(center, Particle.LAVA, 10, 1, 1, 1, 0);

		// ** Prev Clear
		this.clearTrash(center.clone());

		// ** Spawn
		center.subtract(1, 0, 1);

		final Set<Block> savedBlocks = new HashSet<>();

		//inner
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				final Block block = sendChange(center.clone().subtract(i, 0, j), Material.END_STONE_BRICKS);
				// only save the last iteration
				if (last) {
					savedBlocks.add(block);
				}
			}
		}

		//outer
		center.add(0, 1, 0);
		fillOuter(center, last ? savedBlocks : null);

		//outer 2
		center.subtract(0, 2, 0);
		fillOuter(center, last ? savedBlocks : null);

		if (last) {
			for (Block savedBlock : savedBlocks) {
				savedBlock.getState().update(false, false);
			}
			savedBlocks.clear();
		}

	}

	private Block sendChange(Location location, Material material) {
		final BlockData data = material.createBlockData();
		Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(location, data));
		return location.getBlock();
	}

	private void fillOuter(Location center, Set<Block> blocks) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if ((i == 0 || i == 2) && j != 1) {
					continue;
				}
				final Block block = sendChange(center.clone().subtract(i, 0, j), Material.END_STONE);
				if (blocks != null) {
					blocks.add(block);
				}
			}
		}
	}

	private void clearTrash(Location center) {

		// 1
		center.add(0, 2, 0);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if ((i == 0 || i == 2) && j != 1) {
					continue;
				}
				center.clone().subtract(i, 0, j).getBlock().getState().update(false, false);
			}
		}

		//2
		center.subtract(0, 1, 0);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (((i == 1 || i == 2) && j == 2) || (i == 2 && j == 1)) {
					continue;
				}
				center.clone().subtract(i, 0, j).getBlock().getState().update(false, false);
			}
		}

		//3
		center.subtract(0, 1, 0);
		center.clone().subtract(1, 0, 0).getBlock().getState().update(false, false);
		center.clone().subtract(0, 0, 1).getBlock().getState().update(false, false);
	}

}
