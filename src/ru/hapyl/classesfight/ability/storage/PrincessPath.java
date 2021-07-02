package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.extra.PrincessCrown;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrincessPath extends Ability implements Listener, PrincessCrown {

	private final Map<Player, Long> lastUsed;
	private final Map<Player, Set<Block>> affectedBlocks;

	public PrincessPath() {
		super("Golden Path", "Toss your crown into the air. After a brief delay, a trail will follow you for &b3s &7which leaves gold on the ground. After, the trail will constantly explode for &b4s&7, damaging everyone who steps on it.");
		this.setItem(Material.RAW_GOLD);
		this.setCooldownSec(30);

		this.lastUsed = new HashMap<>();
		this.affectedBlocks = new HashMap<>();

	}

	@Override
	public void onStop() {
		for (final Player player : this.affectedBlocks.keySet()) {
			reset(player);
		}
		this.affectedBlocks.clear();
		this.lastUsed.clear();
	}

	private void reset(Player player) {
		final Set<Block> affected = getAffected(player);
		for (final Block block : affected) {
			block.getState().update(false, false);
		}
		affected.clear();
	}

	private void createPath(Player player) {
		final Block relative = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		final Set<Block> affected = getAffected(player);
		if (!relative.getType().isAir() && !affected.contains(relative)) {
			affected.add(relative);
			affectedBlocks.put(player, affected);
			// Update all Blocks
			for (final Player online : Bukkit.getOnlinePlayers()) {
				online.sendBlockChange(relative.getLocation(), Material.GOLD_BLOCK.createBlockData());
			}
		}
	}

	private Set<Block> getAffected(Player player) {
		return affectedBlocks.getOrDefault(player, new HashSet<>());
	}

	@EventHandler()
	public void handleMoveEvent(PlayerMoveEvent ev) {
		final Player player = ev.getPlayer();
		if (GameUtils.isValidPlayer(player, ClassManager.PRINCESS)) {
			if (System.currentTimeMillis() - lastUsed.getOrDefault(player, 0L) < 3000) {
				createPath(player);
				return;
			}
			lastUsed.remove(player);
		}
	}

	private void useAbility0(Player player) {
		PlayerLib.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 2.0f);
		final int period = 5;
		this.lastUsed.put(player, System.currentTimeMillis());
		GameTask.runTaskTimerTimes((task, tick) -> {
			boolean played = false;
			for (final Block block : getAffected(player)) {
				final Location location = block.getLocation().add(0.5d, 1, 0.5d);
				if (!played) {
					PlayerLib.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.75f);
					played = true;
				}
				PlayerLib.spawnParticle(location, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0);
				GameUtils.getPlayerInRange(location, 0.5d)
						.forEach(target -> {
							DamageFeature.setLastDamager(target, player);
							DamageFeature.damageTick(target, null, 0.5d, EnumDamageCause.ENTITY_ATTACK, period);
						});
			}

			if (tick == 0) {
				reset(player);
			}

		}, (3 * 20) + 10, period, 4 * 20 / period);
	}

	@Override
	public Response useAbility(Player player) {
		playCrownAnimation(player, 15, (me) -> useAbility0(player));
		return Response.OK;
	}
}
