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

package ru.hapyl.classesfight.consumable;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.Singleton;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.CoinsEntry;
import ru.hapyl.classesfight.exceptions.SingletonInstantiationException;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.HashMap;
import java.util.Map;

public class ConsumableManager implements Listener, Singleton {

	private static ConsumableManager current;

	private final Map<Player, Integer> consumablesBought;
	private final Map<ArmorStand, Consumable> aliveConsumables;
	private final int CONSUMABLE_LIMIT = 3;
	private final String PREFIX = "&6&lCONSUMABLE! ";

	public ConsumableManager(JavaPlugin b) {
		if (current != null) {
			throw new SingletonInstantiationException(this);
		}
		current = this;
		this.aliveConsumables = new HashMap<>();
		this.consumablesBought = new HashMap<>();
		b.getServer().getPluginManager().registerEvents(this, b);
	}

	public static ConsumableManager current() {
		return current;
	}

	public void removeAll() {
		consumablesBought.clear();
		aliveConsumables.forEach((a, b) -> a.remove());
		aliveConsumables.clear();
	}

	public void spawnAll() {
		this.removeAll();
		for (ConsumableEnum value : ConsumableEnum.values()) {
			final Consumable consumable = value.getConsumable();
			for (ConsumableLocation location : consumable.getSpawnLocations()) {
				if (!location.compareMap()) {
					continue;
				}
				final Location spawnLocation = location.toLocation();
				createArmorStand(consumable, spawnLocation);
			}
		}
	}

	private void createArmorStand(Consumable consumable, Location where) {
		if (where.getWorld() == null) {
			throw new NullPointerException("Could not create consumable armor stand since location was null");
		}
		// Can't be marker because it must be clickable, just don't use plugins that can interact
		GarbageCollector.spawnEntity(where, ArmorStand.class, i -> {
			GameUtils.lockArmorStandSlots(i);
			i.setVisible(false);
			i.setInvulnerable(true);
			i.getEquipment().setHelmet(consumable.getItemStack());
			i.setCustomName(Chat.format("&e&lRIGHT CLICK"));
			i.setCustomNameVisible(true);
			i.setRotation(45f, 0f);
			i.setGravity(false);
			aliveConsumables.put(i, consumable);
		});
		GarbageCollector.spawnEntity(where.clone().add(0.0d, 2.3d, 0.0d), ArmorStand.class, i -> {
			GameUtils.lockArmorStandSlots(i);
			i.setVisible(false);
			i.setInvulnerable(true);
			i.setMarker(true);
			i.setCustomName(Chat.format("&6&l%s &7(%s)", consumable.getName().toUpperCase(), consumable.getPrice()));
			i.setCustomNameVisible(true);
			i.setRotation(45f, 0f);
			i.setGravity(false);
			aliveConsumables.put(i, consumable);
		});
	}

	@EventHandler()
	public void handleConsumableClick(PlayerInteractAtEntityEvent ev) {
		// Auto-Generated
		final Entity entity = ev.getRightClicked();
		final Player player = ev.getPlayer();

		if (ev.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}

		if (entity instanceof ArmorStand
				&& !Spectator.isSpectator(player)) {

			final Consumable consumable = this.checkAndGet((ArmorStand)entity);

			if (consumable == null) {
				return;
			}

			if (!GameManager.current().arePlayersRevealed()) {
				Chat.sendMessage(player, this.PREFIX + "&cYou cannot buy consumable while players are not revealed!");
				GameUtils.villagerNo(player);
				return;
			}

			if (consumablesBought.getOrDefault(player, 0) >= this.CONSUMABLE_LIMIT) {
				Chat.sendMessage(player, this.PREFIX + "&cYou can only buy %s consumables per game!", CONSUMABLE_LIMIT);
				GameUtils.villagerNo(player);
				return;
			}

			final CoinsEntry coins = Database.getDatabase(player).getCoinsEntry();
			if (!coins.hasCoins(consumable.getPrice())) {
				Chat.sendMessage(player, this.PREFIX + "&cYou don't have enough coins.");
				GameUtils.villagerNo(player);
				return;
			}

			player.getInventory().addItem(consumable.getItemStack());
			consumablesBought.put(player, consumablesBought.getOrDefault(player, 0) + 1);
			Chat.sendMessage(player, PREFIX + "&eYou just bought &6%s&e! &7-%s Coins", consumable.getName(), consumable.getPrice());
			GameUtils.villagerYes(player);
			coins.removeCoins(consumable.getPrice());

		}
	}


	private Consumable checkAndGet(ArmorStand stand) {
		if (this.aliveConsumables.isEmpty()) {
			return null;
		}
		return this.aliveConsumables.getOrDefault(stand, null);
	}

	public void checkNotUsedConsumables(Player player) {

		final PlayerInventory inventory = player.getInventory();
		final Map<ConsumableEnum, Integer> hash = new HashMap<>();

		inventory.forEach(item -> {
			for (ConsumableEnum value : ConsumableEnum.values()) {
				if (value.getConsumable().getItemStack().isSimilar(item)) {
					hash.put(value, item.getAmount());
				}
			}
		});

		if (hash.isEmpty())
			return;
		int total = 0, coins = 0;
		for (ConsumableEnum consumable : hash.keySet()) {
			coins += consumable.getConsumable().getPrice() * hash.get(consumable);
			total += hash.get(consumable);
		}

		Chat.sendMessage(player, PREFIX + "&eYou have been refunded for &6%s &eunused consumables. &7+%s Coins", total, coins);
		hash.clear();

	}

}
