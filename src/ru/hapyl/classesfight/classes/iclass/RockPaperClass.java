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

package ru.hapyl.classesfight.classes.iclass;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.iclass.extra.RPSInfo;
import ru.hapyl.classesfight.classes.iclass.extra.RPSPlayerInfo;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.utils.CFItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class RockPaperClass extends IClass {

	public final Map<Player, RPSPlayerInfo> ultimateMode = new HashMap<>();

	private final double PURE_DAMAGE = 3.0d;

	private final ItemStack ITEM_ROCK = new CFItemBuilder(Material.COBBLESTONE).setName("&aRock").setPureDamage(PURE_DAMAGE).build();
	private final ItemStack ITEM_PAPER = new CFItemBuilder(Material.PAPER).setName("&aPaper").setPureDamage(PURE_DAMAGE).build();
	private final ItemStack ITEM_SCISSORS = new CFItemBuilder(Material.SHEARS).setName("&aScissors").setPureDamage(PURE_DAMAGE).build();

	public RockPaperClass() {
		super("Rock Paper Scissors", Material.COBBLESTONE);

		this.setRating(ClassRating.C);
		this.setInfo("Rock, Paper, Scissors...", "Rockin' Rollin'", "Every alive player receives one of the material (Rock, Paper, Scissors) for 3 hits. Hitting with stronger material will double damage them, with weaker will damage you.", 5);

		final ClassEquipment eq = this.getClassEquipment(true);
		// eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGQyZjc4MWYwM2MzNjViYmM1ZGQxZTcxODZhYjM4ZGM2OTQ2NWU4MzZjOWZlMDY2YTlhODQ0ZjM0YTRkYTkyIn19fQ==
		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmVmY2RiYjdkOTU1MDJhY2MxYWUzNWEzMmE0MGNlNGRlYzhmNGM5ZjBkYTI2YzlkOWZlN2MyYzNlYjc0OGY2In19fQ==");
		eq.setChestplate(238, 230, 255);
		eq.setLeggings(221, 204, 255);
		eq.setBoots(255, 230, 242);

		eq.addItem(ITEM_ROCK);
		eq.addItem(ITEM_PAPER);
		eq.addItem(ITEM_SCISSORS);

	}

	@Override
	public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
		final RPSHitResponse response = invokeHit(player, event.getPlayer());
		switch (response) {
			case STRONG -> event.setDamage(event.getDamage() * 2.0d);
			case WEAK -> {
				DamageFeature.damage(player, player, 2.5d, EnumDamageCause.ENTITY_ATTACK);
				event.setDamage(0.0d);
			}
		}
	}

	@Override
	public void onStopOnce() {
		this.ultimateMode.forEach((player, ult) -> ult.removePlayer(player));
		this.ultimateMode.clear();
	}

	public void removePlayer(Player player) {
		this.ultimateMode.forEach((owner, info) -> info.removePlayer(player));
	}

	@Override
	public void useUltimate(Player player) {

		// clean old data
		if (ultimateMode.containsKey(player)) {
			final RPSPlayerInfo rps = ultimateMode.get(player);
			rps.clearTrash();
		}

		final RPSPlayerInfo rps = new RPSPlayerInfo(player);
		GameManager.current().forEachBut(rps::addPlayer, player);
		ultimateMode.put(player, rps);
	}

	// 1 - opposite, 0 - normal, -1 - wrong, -2 invalid
	private byte compareMaterial(Player player, Player target, RPSInfo.RPSMaterial material) {
		if (!ultimateMode.containsKey(player)) {
			return 0;
		}
		final RPSPlayerInfo rps = ultimateMode.get(player);
		final RPSInfo info = rps.getData(target);

		if (info == null) {
			return 0;
		}

		final RPSInfo.RPSMaterial mat = info.getMaterial();
		return (byte)((mat.getOpposite() == material) ? 1 : (mat.getWeak() == material) ? -1 : 0);
	}

	public enum RPSHitResponse {
		STRONG,
		NORMAL,
		WEAK,
		INVALID
	}

	public RPSHitResponse invokeHit(Player damager, Player player) {

		RPSInfo.RPSMaterial material = getMaterial(damager);

		if (material == null) {
			return RPSHitResponse.INVALID;
		}

		final byte bit = compareMaterial(damager, player, material);
		final RPSPlayerInfo rps = ultimateMode.get(damager);

		if (rps == null) {
			return RPSHitResponse.INVALID;
		}

		if (!rps.hasData(player)) {
			return RPSHitResponse.NORMAL;
		}

		rps.decrementHit(player);

		return bit == 1 ? RPSHitResponse.STRONG : bit == 0 ? RPSHitResponse.NORMAL : bit == -1 ? RPSHitResponse.WEAK : RPSHitResponse.INVALID;
	}

	private RPSInfo.RPSMaterial getMaterial(Player player) {
		final ItemStack stack = player.getInventory().getItemInMainHand();
		if (stack.isSimilar(ITEM_ROCK)) {
			return RPSInfo.RPSMaterial.ROCK;
		}
		else if (stack.isSimilar(ITEM_PAPER)) {
			return RPSInfo.RPSMaterial.PAPER;
		}
		else if (stack.isSimilar(ITEM_SCISSORS)) {
			return RPSInfo.RPSMaterial.SCISSORS;
		}
		else {
			return null;
		}
	}

}
