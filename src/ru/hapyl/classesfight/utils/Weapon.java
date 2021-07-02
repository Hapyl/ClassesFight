package ru.hapyl.classesfight.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameManager;

public class Weapon {

	private final Material material;
	private final String id;
	private String name;
	private String lore;
	private String smartLore;
	private double pureDamage;
	private boolean cancelEvent;

	public Weapon(Material material) {
		this(material, null);
	}

	public Weapon(Material material, String id) {
		this.material = material;
		this.name = "";
		this.id = id;
		this.lore = null;
		this.pureDamage = 1.0d;
		this.cancelEvent = true;
	}

	public void onLeftClick(Player player) {

	}

	public void onRightClick(Player player) {
	}

	public void dontCancelEvent() {
		this.cancelEvent = false;
	}

	public void applyCooldown(Player player, int cd) {
		if (GameManager.current().isDebugMode()) {
			return;
		}
		player.setCooldown(this.material, cd);
	}

	public void applyCooldownSec(Player player, int cdSec) {
		this.applyCooldown(player, cdSec * 20);
	}

	public Weapon withName(String name) {
		this.name = name;
		return this;
	}

	public Weapon withDamage(double damage) {
		this.pureDamage = damage;
		return this;
	}

	public Weapon withLore(String lore) {
		this.lore = lore;
		return this;
	}

	public Weapon withSmartLore(String smartLore) {
		this.smartLore = smartLore;
		return this;
	}

	public String getLore() {
		return lore;
	}

	public Material getMaterial() {
		return material;
	}

	public double getPureDamage() {
		return pureDamage;
	}

	public String getName() {
		return name;
	}

	public ItemStack build() {
		return this.create();
	}

	public ItemStack create() {
		final CFItemBuilder builder = this.id == null ? new CFItemBuilder(this.material) : new CFItemBuilder(this.material, this.id);
		if (this.lore != null) {
			builder.addLore(this.lore);
		}
		if (this.smartLore != null) {
			builder.addSmartLore(this.smartLore);
		}
		if (this.id != null) {
			builder.addClickEvent(this::onLeftClick, Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK);
			builder.addClickEvent(this::onRightClick, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
		}
		builder.setCancelEvent(this.cancelEvent);
		return builder.setName(ChatColor.GREEN + this.name).setPureDamage(this.pureDamage).applyDefaultSettings(false).build();
	}

}
