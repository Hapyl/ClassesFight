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

import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.ability.extra.MeleeData;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.HashMap;
import java.util.Map;

public class MeleeStance extends Ability {

	private final Map<Player, MeleeData> enteredAt;
	private final ItemStack meleeItem = new ItemBuilder(Material.IRON_SWORD).setPureDamage(2.0d).setName("&aSturdy Sword").applyDefaultSettings(false).build();

	public MeleeStance() {
		super("Melee Stance", "Enter a Melee Stance for maximum of &b30s &7to replace your Bow with a Sword! Use again in Melee Stance to get your Bow back. Cooldown is based on how long you are in Melee Stance.");
		this.setCooldownSec(1);
		this.enteredAt = new HashMap<>();
		this.setItem(Material.IRON_INGOT);
	}

	@Override
	public void onStop() {
		enteredAt.clear();
	}

	@Override
	public Response useAbility(Player player) {

		final MeleeData data = getData(player);

		if (data != null) {
			this.switchToRange(player);
			return Response.OK;
		}

		switchToMelee(player, this.meleeItem);
		return Response.OK;
	}

	public void switchToMelee(Player player, ItemStack meleeItem) {
		final MeleeData data = getData(player);
		if (data != null) {
			data.cancelTask();
		}

		final PlayerInventory inventory = player.getInventory();
		this.enteredAt.put(player, new MeleeData(player, inventory.getItem(0)));

		inventory.setItem(0, meleeItem);
		inventory.setHeldItemSlot(0);

		// Fx
		PlayerLib.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.25f);
		Chat.sendTitle(player, "&2⚔", "", 5, 15, 5);
//        Chat.sendMessage(player, "&2⚔ &aSwitched to Melee Stance!");

	}

	public void switchToRange(Player player) {
		final MeleeData data = getData(player);
		if (data == null) {
			Chat.sendMessage(player, "&cCannot switch to range stance!");
			return;
		}

		data.cancelTask();
		final long timePassed = System.currentTimeMillis() - data.getEnteredAt();
		final int abilityCooldown = (int)(20 * (10 + (2 * (timePassed / 1000))));

		// 10s + 2s for each second in melee stance?
		GameTask.runLater(() -> player.setCooldown(this.getItem().getType(), abilityCooldown), 1);
		Chat.sendMessage(player, "&aMelee Stance is on cooldown for %ss!", BukkitUtils.roundTick(abilityCooldown));

		final ItemStack item = data.getRangeItem();
		final PlayerInventory inventory = player.getInventory();
		inventory.setItem(0, item);
		inventory.setHeldItemSlot(0);
		this.enteredAt.remove(player);

		// Fx
		PlayerLib.playSound(player, Sound.ENTITY_ARROW_SHOOT, 0.75f);
		Chat.sendTitle(player, "&2🏹", "", 5, 15, 5);
//        Chat.sendMessage(player, "&2🏹 &aSwitched to Range Stance!");

	}

	public boolean isInMeleeStance(Player player) {
		return getData(player) != null;
	}

	@NULLABLE
	private MeleeData getData(Player player) {
		return this.enteredAt.getOrDefault(player, null);
	}

}
