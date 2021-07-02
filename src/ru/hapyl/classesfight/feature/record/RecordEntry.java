package ru.hapyl.classesfight.feature.record;

import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import kz.hapyl.spigotutils.module.reflect.npc.ItemSlot;
import kz.hapyl.spigotutils.module.reflect.npc.NPCPose;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class RecordEntry {

	private final Location location;

	// equipment
	private final ItemStack helmet;
	private final ItemStack chestplate;
	private final ItemStack leggings;
	private final ItemStack boots;
	private final ItemStack mainHand;
	private final ItemStack offHand;

	private final boolean isCrouching;
	private final boolean isSleeping;
	private final boolean isSwimming;
	private final boolean isOnFire;
	private final boolean tookDamage;
	private final boolean attackMainHand;
	private final boolean attackOffHand;

	public RecordEntry(Player player) {
		this.location = player.getLocation();

		// equipment
		final EntityEquipment equipment = player.getEquipment();
		this.helmet = notNullItem(equipment.getHelmet());
		this.chestplate = notNullItem(equipment.getChestplate());
		this.leggings = notNullItem(equipment.getLeggings());
		this.boots = notNullItem(equipment.getBoots());
		this.mainHand = notNullItem(equipment.getItemInMainHand());
		this.offHand = notNullItem(equipment.getItemInOffHand());

		this.isCrouching = player.isSneaking();
		this.isSleeping = player.isSleeping();
		this.isSwimming = player.isSwimming();
		this.isOnFire = player.getFireTicks() > 0;
		this.tookDamage = false;
		this.attackOffHand = false;
		this.attackMainHand = false;
	}

	private ItemStack notNullItem(ItemStack stack) {
		return stack == null ? new ItemStack(Material.AIR) : stack;
	}

	public void applyStates(HumanNPC entity) {
		if (entity == null) {
			return;
		}

		entity.setLocation(this.getLocation());
		entity.setItem(ItemSlot.MAINHAND, this.mainHand);
		entity.setItem(ItemSlot.OFFHAND, this.offHand);
		entity.setItem(ItemSlot.HEAD, this.helmet);
		entity.setItem(ItemSlot.CHEST, this.chestplate);
		entity.setItem(ItemSlot.LEGS, this.leggings);
		entity.setItem(ItemSlot.FEET, this.boots);

		if (this.isCrouching()) {
			entity.setPose(NPCPose.CROUCHING);
		}
		else if (this.isSleeping()) {
			entity.setPose(NPCPose.SLEEPING);
		}
		else if (this.isSwimming()) {
			entity.setPose(NPCPose.SWIMMING);
		}
		else {
			entity.setPose(NPCPose.STANDING);
		}

	}

	public boolean isCrouching() {
		return isCrouching;
	}

	public boolean isSleeping() {
		return isSleeping;
	}

	public boolean isSwimming() {
		return isSwimming;
	}

	public boolean isOnFire() {
		return isOnFire;
	}

	public boolean isTookDamage() {
		return tookDamage;
	}

	public boolean isAttackMainHand() {
		return attackMainHand;
	}

	public boolean isAttackOffHand() {
		return attackOffHand;
	}

	public Location getLocation() {
		return location;
	}

}
