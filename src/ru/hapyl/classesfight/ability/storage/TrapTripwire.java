/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight.ability.storage;

import io.netty.util.internal.ConcurrentSet;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.ability.extra.Tripwire;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.*;

public class TrapTripwire extends Ability implements Listener {

	private final int pickupCooldown = 8 * 20;
	private final ConcurrentSet<Tripwire> traps;
	private final Map<Player, Integer> placedTraps;

	public TrapTripwire() {
		super("Tripwire",
				"Place a tripwire between two blocks. Enemies who &etouches &7a tripwire will be slowed, affected by Vulnerability and their " +
						"location will be revealed. &e&lPUNCH &7a tripwire block to pick it up.");
		this.setItem(Material.STRING, "PLACE");
		this.setAllowLeftClick(false);
		this.setRemoveItemOnUse(true);
		this.setCooldownSec(1);
		this.setShowCooldown(false);
		this.traps = new ConcurrentSet<>();
		this.placedTraps = new HashMap<>();
	}

	@Override
	public void onDeath(Player player) {
		for (final Tripwire trap : this.traps) {
			if (trap.getPlayer() == player) {
				trap.clearBlocks();
				this.traps.remove(trap);
			}
		}
	}

	@Override
	public void onStart() {
		new GameTask() {
			@Override
			public void run() {
				if (traps.isEmpty()) {
					return;
				}
				for (Tripwire trap : traps) {
					trap.drawLine();
				}
			}
		}.runTaskTimer(0, 10);
	}

	@Override
	public void onStop() {
		this.traps.forEach(Tripwire::clearBlocks);
		this.traps.clear();
		this.placedTraps.clear();
	}

	@Override
	public Response useAbility(Player player) {

		final Set<Block> targetBlock = getTargetBlock(player);

		if (placedTraps.getOrDefault(player, 0) >= 2) {
			return Response.error("You cannot place more than 2 traps!");
		}

		if (targetBlock == null) {
			return Response.ERROR;
		}

		final Tripwire tripwire = new Tripwire(player, targetBlock);
		tripwire.setBlocks();
		traps.add(tripwire);
		placedTraps.put(player, placedTraps.getOrDefault(player, 0) + 1);

		return Response.OK;
	}

	@EventHandler()
	public void handleMoveEvent(PlayerMoveEvent ev) {
		if (!GameManager.current().isGameInProgress() || !GameManager.current().arePlayersRevealed()) {
			return;
		}

		// check for a block not distance

		if (this.traps.isEmpty()) {
			return;
		}

		final Player player = ev.getPlayer();
		final Location location = player.getLocation();

		if (Spectator.isSpectator(player)) {
			return;
		}

		byte bit = 0;
		for (Block block = location.getBlock(); bit <= 1; block = block.getRelative(BlockFace.UP), ++bit) {
			if (block.getType() == Material.TRIPWIRE) {
				final Tripwire trap = byBlock(block);
				if (trap != null && trap.getPlayer() != player) {
					this.removeTrap(trap);
					trap.affectPlayer(player);
					return;
				}
			}
		}

	}

	private Tripwire byBlock(Block block) {
		for (final Tripwire trap : this.traps) {
			if (trap.isBlockATrap(block)) {
				return trap;
			}
		}
		return null;
	}

	private boolean isCloseEnough(Set<Block> blocks, Player player) {
		for (Block block : blocks) {
			if (BukkitUtils.centerLocation(block.getLocation()).distance(player.getLocation()) <= 1.5d) {
				return true;
			}
		}
		return false;
	}

	@EventHandler()
	public void handleBlockBreak(BlockBreakEvent ev) {
		if (!GameManager.current().isGameInProgress() || !GameManager.current().arePlayersRevealed()) {
			return;
		}

		final Block block = ev.getBlock();
		if (block.getType() != Material.TRIPWIRE || traps.isEmpty()) {
			return;
		}

		ev.setCancelled(true);
		final Player player = ev.getPlayer();

		for (Tripwire trap : traps) {
			if (trap.isBlockATrap(block) && trap.getPlayer() == player) {
				removeTrap(trap);
				Chat.sendMessage(player, "&aPicked up Tripwire Trap.");
				player.setCooldown(this.getItem().getType(), pickupCooldown);
				player.getInventory().addItem(this.getItem().getItem());
				return;
			}
		}

	}

	private void removeTrap(Tripwire trap) {
		trap.clearBlocks();
		this.traps.remove(trap);
		this.placedTraps.put(trap.getPlayer(), this.placedTraps.getOrDefault(trap.getPlayer(), 1) - 1);
	}

	private Set<Block> getTargetBlock(Player player) {
		final List<Block> targetBlocks = player.getLastTwoTargetBlocks(null, 5);

		if (targetBlocks.size() != 2 || targetBlocks.get(1).getType().isAir()) {
			return null;
		}

		final Block targetBlock = targetBlocks.get(1);
		final Block adjacentBlock = targetBlocks.get(0);
		final BlockFace face = targetBlock.getFace(adjacentBlock);

		if (face == null || face == BlockFace.UP || face == BlockFace.DOWN) {
			return null;
		}

		// check for allowed block
		switch (targetBlock.getType()) {

			case BARRIER:
			case TRIPWIRE: {
				Chat.sendMessage(player, "Cannot place tripwire to this block!");
				PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
				return null;
			}

		}

		final Set<Block> blocks = new HashSet<>();
		Block next = adjacentBlock;

		for (int i = 0; i < 7; i++) {
			// Hit another block, break the loop
			if (!next.getType().isAir()) {
				break;
			}
			blocks.add(next);
			next = next.getRelative(face);
		}

		if (next.getType().isAir()) {
			Chat.sendMessage(player, "&cCouldn't find a block to connect!");
			PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
			return null;
		}

		return blocks;
	}


}
