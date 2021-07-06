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

import com.google.common.collect.Maps;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.iclass.extra.TakerData;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.Icons;

import java.util.Map;

public class TakerClass extends IClass {

	private final Map<Player, TakerData> boneCharges = Maps.newConcurrentMap();
	private final ItemStack[] ultimateArmor = {ItemBuilder.leatherTunic(Color.fromRGB(207, 48, 48)).toItemStack(), ItemBuilder.leatherPants(Color.fromRGB(165, 39, 39)).toItemStack(), ItemBuilder
			.leatherBoots(Color.fromRGB(103, 24, 24)).toItemStack()};

	public TakerClass() {
		super("Taker", Material.IRON_HOE);
		this.setRating(ClassRating.NOT_YET);
		// One of the first warriors who joined the fight, not alone, but with his deadly friend - &bScythe&7. Oh yes, almost forgot about the &cDealy Potion&7!
		this.setInfo("One of the first warriors who joined the fight! Prefers to rip out opponents bones before killing them...__" + Icons.ABILITY_DOT + "Spiritual Bones &e&lPASSIVE &7You may have up to &b4 &7bones flying around you. These bones can be used in exchange for Blessings of Death. You will start with one Spiritual Bone.__" + Abilities.BONE_TRAP
						.getAbout() + Abilities.MIRROR_OR_DEATH.getAbout(),
				"Embodiment of Death",
				"Instantly consume all remaining bones to summon the Death and refill " + Abilities.BONE_TRAP.getName() + " charges. While death is present you will be healed, your damage and speed is drastically increased. Healing and duration of this effect is based on consumed bones.", 5);
		this.setUltimateSound(Sound.ENTITY_ELDER_GUARDIAN_DEATH, 2.0f);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTRkMTlmYzM3ZjI2ZjAwZWIzNmVlNTNjYWMxYjliMDhjOTY4YjYxMWIxNGM4YWZlOGUwYzUwNmNlMzhhNmUyMyJ9fX0=");
		eq.setChestplate(48, 54, 50);
		eq.setLeggings(24, 27, 25);
		eq.setBoots(0, 0, 0);

		// -25%
		eq.addItem(new CFItemBuilder(Material.IRON_HOE).setName("&aScythe")
				.setPureDamage(5.625d)
				.addAttribute(Attribute.GENERIC_ATTACK_SPEED, -0.75, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND)
				.applyDefaultSettings(false)
				.build());

		Abilities.BONE_TRAP.addItemIfExists(eq);
		Abilities.DEATH_SWAP.addItemIfExists(eq);
		Abilities.MIRROR_OR_DEATH.addItemIfExists(eq);

	}

	@Override
	public void onDeath(Player player) {
		Abilities.BONE_TRAP.onDeath(player);
	}

	private void equipArmor(Player player, boolean ultimate) {
		if (ultimate) {
			final EntityEquipment equipment = player.getEquipment();
			equipment.setChestplate(ultimateArmor[0]);
			equipment.setLeggings(ultimateArmor[1]);
			equipment.setBoots(ultimateArmor[2]);
		}
		else {
			this.getClassEquipment().equipArmor(player);
		}
	}

	@Override
	public void onStopOnce() {
		this.boneCharges.clear();
	}

	@Override
	public void onStart(Player player) {
		final TakerData data = new TakerData(player);
		this.boneCharges.put(player, data);
		data.addBone();
	}

	public void addBoneCharge(Player player) {
		final TakerData data = this.getData(player);
		if (data.getBoneCharges() >= data.getMaxBones()) {
			return;
		}
		Chat.sendMessage(player, "&2&l☠ &aYou received a bone!");
		PlayerLib.playSound(player, Sound.ENTITY_SKELETON_AMBIENT, 1.0f);
		data.addBone();
	}

	public TakerData getData(Player player) {
		return this.boneCharges.getOrDefault(player, new TakerData(player));
	}

	@Override
	public boolean predicateUltimate(Player player) {
		return getData(player).getBoneCharges() > 0;
	}

	@Override
	public String predicateMessage() {
		return "Not enough Spiritual Bones!";
	}

	@Override
	public void useUltimate(Player player) {

		// Reset Bones
		this.getClassEquipment().equip(player);

		final TakerData data = getData(player);
		final int charges = data.getBoneCharges();
		final int ultimateDuration = (4 + (charges * 2)) * 20;
		this.setUsingUltimate(player, true, ultimateDuration);
		data.resetBones();

		this.equipArmor(player, true);
		GameTask.runLater(() -> this.equipArmor(player, false), ultimateDuration);

		Chat.sendMessage(player, "&4&lYou have summon the Death for %ss!", BukkitUtils.roundTick(ultimateDuration));

		DamageFeature.addHealth(player, 4.0d * charges);
		PlayerLib.addEffect(player, PotionEffectType.SLOW_DIGGING, ultimateDuration, 2);
		PlayerLib.addEffect(player, PotionEffectType.INCREASE_DAMAGE, ultimateDuration, 2);
		PlayerLib.addEffect(player, PotionEffectType.SPEED, ultimateDuration, 3);

	}

}
