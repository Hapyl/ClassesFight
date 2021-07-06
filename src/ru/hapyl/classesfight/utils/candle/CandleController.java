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

package ru.hapyl.classesfight.utils.candle;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CandleController implements Listener {

	protected final Map<UUID, CandleData> data = new HashMap<>();
	protected final ItemStack itemCandle;

	public CandleController() {
		this.itemCandle = new ItemBuilder(Material.TORCH, "cf_candle").addClickEvent(CandleUI::new, Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK)
				.setName("&aCandle")
				.build();
	}

	@EventHandler()
	public void handleBlockPlaceEvent(BlockPlaceEvent ev) {
		final Player player = ev.getPlayer();
		final Block block = ev.getBlock();
		if (GameManager.current().isGameInProgress()) {
			return;
		}

		if (block.getType() != Material.TORCH || !compareId(player)) {
			return;
		}

		final Location location = block.getLocation();
		final CandleData data = getDataOrCreate(player);

		if (location.getWorld() == null) {
			player.sendMessage("world is null");
			return;
		}

		if (data.isRandomOffset()) {
			location.setYaw(ThreadLocalRandom.current().nextFloat() * 160);
		}

		location.getWorld().spawn(location.add(0.5d, -1.5d, 0.5d), ArmorStand.class, me -> {
			me.setInvulnerable(true);
			me.setSilent(true);
			me.setInvisible(true);
			me.setMarker(true);
			me.setGravity(false);
			me.setCustomNameVisible(false);
			me.getScoreboardTags().add("CANDLE");
			final EntityEquipment equipment = me.getEquipment();
			if (equipment != null) {
				equipment.setHelmet(data.getCandle().getItemStack());
			}
		});

	}

	@EventHandler()
	public void handleBlockBreakEvent(BlockBreakEvent ev) {
		final Player player = ev.getPlayer();
		final Block block = ev.getBlock();

		if (GameManager.current().isGameInProgress() || block.getType() != Material.TORCH) {
			return;
		}

		final Location location = block.getLocation();
		if (location.getWorld() == null) {
			return;
		}

		final Set<Entity> candles = location.getWorld()
				.getNearbyEntities(location, 1.0d, 2.0d, 1.0d)
				.stream()
				.filter(entity -> entity instanceof ArmorStand && entity.getScoreboardTags().contains("CANDLE")).collect(Collectors.toSet());

		if (!candles.isEmpty()) {
			candles.forEach(Entity::remove);
			Chat.sendMessage(player, "&aRemoved Candle.");
			PlayerLib.playSound(player, Sound.ITEM_SHIELD_BREAK, 2.0f);
			candles.clear();
		}

	}

	public CandleData getDataOrCreate(Player player) {
		final CandleData candle = data.get(player.getUniqueId());
		if (candle == null) {
			final CandleData candleData = new CandleData(player, Candles.WHITE, true);
			data.put(player.getUniqueId(), candleData);
			return candleData;
		}
		return candle;
	}

	public Candles getCurrentCandle(Player player) {
		return getDataOrCreate(player).getCandle();
	}

	public boolean isRandomOffset(Player player) {
		return getDataOrCreate(player).isRandomOffset();
	}

	public void setCurrentCandle(Player player, Candles candles) {
		this.getDataOrCreate(player).setCandle(candles);
	}

	public void setRandomOffset(Player player, boolean flag) {
		this.getDataOrCreate(player).setRandomOffset(flag);
	}

	private boolean compareId(Player player) {
		final String id = ItemBuilder.getItemID(player.getInventory().getItemInMainHand());
		return (id != null) && (id.equalsIgnoreCase("cf_candle"));
	}

	public ItemStack getItemCandle() {
		return itemCandle;
	}
}
