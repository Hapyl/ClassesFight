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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.ability.extra.TrapBones;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BoneTrap extends Ability implements Listener {

	private final Map<Player, ConcurrentSet<TrapBones>> traps;
	private final int maxTrapsAmount = 4;

	public BoneTrap() {
		super("Bone Trap", "Place a trap of bones at your target block. Upon touching a trap, enemy will be paralyzed and damaged and you will receive 1 Spiritual Bone.");
		this.setItem(Material.BONE);
		this.getItem().setStartAmount(maxTrapsAmount);
		this.traps = new HashMap<>();
		this.setCooldownSec(3);
		this.setRemoveItemOnUse(true);
		this.setAllowLeftClick(false);
	}

	@Override
	public void onDeath(Player player) {
		final ConcurrentSet<TrapBones> traps = this.traps.get(player);
		for (final TrapBones trap : traps) {
			trap.remove();
		}
		traps.clear();
		this.traps.remove(player);
	}

	@Override
	public void onStop() {
		traps.clear();
	}

	@EventHandler()
	public void handlePlayerMove(PlayerMoveEvent ev) {
		if (!GameManager.current().canAbilitiesBeUsed()) {
			return;
		}
		this.testPlayer(ev.getPlayer());
	}

	@EventHandler()
	public void handleTrapPickup(PlayerInteractEvent ev) {
		if (!GameManager.current().canAbilitiesBeUsed()) {
			return;
		}

		final Player player = ev.getPlayer();

		if (ClassManager.getClass(player) != ClassManager.TAKER
				|| ev.getAction() != Action.LEFT_CLICK_BLOCK) {
			return;
		}

		final Block block = ev.getClickedBlock();

		if (block == null) {
			return;
		}

		for (ConcurrentSet<TrapBones> value : this.traps.values()) {
			for (TrapBones trap : value) {
				if (trap.getPlayer() != player) {
					continue;
				}
				if (trap.getLocation().distance(block.getLocation()) <= 1.5d) {
					trap.remove();
					value.remove(trap);
					player.getInventory().addItem(this.getItem().getItem());
					this.applyItemCooldown(player);
					Chat.sendMessage(player, "&aPicked up Bone Trap!");
				}
			}
		}
	}

	public void testPlayer(Player player) {
		if (this.traps.isEmpty()) {
			return;
		}
		for (Set<TrapBones> value : this.traps.values()) {
			for (TrapBones trapBones : value) {
				if (trapBones.getPlayer() == player || Spectator.isSpectator(player)) {
					continue;
				}
				if (trapBones.getLocation().distance(player.getLocation()) <= 1.5d) {
					value.remove(trapBones);
					trapBones.addBoneCharge();
					trapBones.remove();
					DamageFeature.damage(player, trapBones.getPlayer(), 5.0d, EnumDamageCause.ENTITY_ATTACK);
					PlayerLib.addEffect(player, PotionEffectType.SLOW, 20, 5);
					PlayerLib.playSound(player, Sound.ENTITY_SKELETON_DEATH, 1.25f);
					break;
				}
			}
		}
	}

	@Override
	public Response useAbility(Player player) {

		final Block block = player.getTargetBlockExact(5);

		if (block == null) {
			return Response.error("No valid block in sight!");
		}

		final ConcurrentSet<TrapBones> oldTraps = traps.getOrDefault(player, new ConcurrentSet<>());
		if (oldTraps.size() >= maxTrapsAmount) {
			return Response.error("You've reached the trap limit!");
		}

		oldTraps.add(new TrapBones(player, block.getLocation().add(0.5d, 0.27d, 0.5d)));
		PlayerLib.playSound(player, Sound.ENTITY_SKELETON_AMBIENT, 0.75f);
		traps.put(player, oldTraps);

		return Response.OK;
	}
}
