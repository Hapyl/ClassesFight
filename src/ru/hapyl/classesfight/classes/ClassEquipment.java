package ru.hapyl.classesfight.classes;

import kz.hapyl.spigotutils.module.annotate.NOTNULL;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.hapyl.classesfight.utils.CFItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ClassEquipment {

	private final static ItemStack NULL_ITEM = new ItemStack(Material.AIR);

	private final ItemStack[] armor;
	private final List<ItemStack> items;

	private boolean autoEquipOnStart;

	public ClassEquipment() {
		this.armor = new ItemStack[4];
		this.items = new ArrayList<>();
		this.autoEquipOnStart = true;
	}

	public ClassEquipment equipAtStart() {
		this.autoEquipOnStart = true;
		return this;
	}

	public boolean isAutoEquipOnStart() {
		return autoEquipOnStart;
	}

	public void setArmor(ItemStack[] stack) {
		if (stack.length == 4) {
			System.arraycopy(stack, 0, this.armor, 0, this.armor.length);
		}
	}

	public ClassEquipment setHelmet(ItemStack stack) {
		this.armor[0] = stack;
		return this;
	}

	public ClassEquipment setHelmet(Material material) {
		this.armor[0] = new ItemBuilder(material).cleanToItemSack();
		return this;
	}

	public ClassEquipment setChestplate(Material material) {
		this.armor[1] = new ItemBuilder(material).cleanToItemSack();
		return this;
	}

	public ClassEquipment setLeggings(Material material) {
		this.armor[2] = new ItemBuilder(material).cleanToItemSack();
		return this;
	}

	public ClassEquipment setBoots(Material material) {
		this.armor[3] = new ItemBuilder(material).cleanToItemSack();
		return this;
	}

	public ClassEquipment setArmorPiece(Material material) {
		final String lowerName = material.name().toLowerCase(Locale.ROOT);
		switch (lowerName) {
			case "helmet" -> {
				return this.setHelmet(material);
			}
			case "chestplate" -> {
				return this.setChestplate(material);
			}
			case "leggings" -> {
				return this.setLeggings(material);
			}
			case "boots" -> {
				return this.setBoots(material);
			}
			default -> throw new IllegalArgumentException(String.format("couldn't figure out where to put %s!", material.name()));
		}

	}

	public ClassEquipment setHelmet(String headTexture) {
		return this.setHelmet(ItemBuilder.playerHead(headTexture).cleanToItemSack());
	}

	public ClassEquipment setHelmet(int red, int green, int blue) {
		return this.setHelmet(ItemBuilder.leatherHat(Color.fromRGB(red, green, blue)).cleanToItemSack());
	}

	public ClassEquipment setChestplate(ItemStack stack) {
		this.armor[1] = stack;
		return this;
	}

	public ClassEquipment setChestplate(int red, int green, int blue) {
		return this.setChestplate(ItemBuilder.leatherTunic(Color.fromRGB(red, green, blue)).cleanToItemSack());
	}

	public ClassEquipment setLeggings(ItemStack stack) {
		this.armor[2] = stack;
		return this;
	}

	public ClassEquipment setLeggings(int red, int green, int blue) {
		return this.setLeggings(ItemBuilder.leatherPants(Color.fromRGB(red, green, blue)).cleanToItemSack());
	}

	public ClassEquipment setBoots(ItemStack stack) {
		this.armor[3] = stack;
		return this;
	}

	public ClassEquipment setBoots(int red, int green, int blue) {
		return this.setBoots(ItemBuilder.leatherBoots(Color.fromRGB(red, green, blue)).cleanToItemSack());
	}

	public ClassEquipment addItem(ItemStack stack) {
		this.items.add(stack);
		return this;
	}

	public void createAndAddItem(Material material, String id, Consumer<Player> action, int actionCd, String name, String... lore) {
		final CFItemBuilder builder = new CFItemBuilder(material, id)
				.setName("&a" + name)
				.addClickEvent(action);

		if (actionCd > 0) {
			builder.withCooldown(actionCd);
		}

		if (lore.length > 0) {
			if (lore.length == 1) {
				builder.addSmartLore(lore[0]);
			}
			else {
				for (String s : lore) {
					builder.addLore(s);
				}
			}
		}
		this.addItem(builder.applyDefaultSettings(false).build());
	}

	public void createAndAddItem(Material material, String id, Consumer<Player> action, String name, String... lore) {
		this.createAndAddItem(material, id, action, 0, name, lore);
	}

	@NOTNULL
	public ItemStack getHelmet() {
		return this.itemOrNull(this.armor[0]);
	}

	@NOTNULL
	public ItemStack getChestplate() {
		return this.itemOrNull(this.armor[1]);
	}

	@NOTNULL
	public ItemStack getLeggings() {
		return this.itemOrNull(this.armor[2]);
	}

	@NOTNULL
	public ItemStack getBoots() {
		return this.itemOrNull(this.armor[3]);
	}

	public void equip(Player player) {
		this.equipArmor(player);
		if (!this.items.isEmpty()) {
			int slot = 0;
			final PlayerInventory inventory = player.getInventory();
			for (ItemStack weapon : this.items) {
				inventory.setItem(slot++, weapon);
			}
		}
		player.updateInventory();
	}

	public void equipArmor(Player player) {
		final EntityEquipment equipment = player.getEquipment();
		if (equipment != null) {
			equipment.setHelmet(this.armorOrNull(0));
			equipment.setChestplate(this.armorOrNull(1));
			equipment.setLeggings(this.armorOrNull(2));
			equipment.setBoots(this.armorOrNull(3));
		}
	}

	public void unequip(Player player) {
		player.getInventory().clear();
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	public ItemStack getItem(int index) {
		return (index >= this.items.size()) ? NULL_ITEM : this.items.get(index);
	}

	public List<ItemStack> getItems() {
		return items;
	}

	private ItemStack armorOrNull(int index) {
		return this.itemOrNull(this.armor[index]);
	}

	private ItemStack itemOrNull(ItemStack s) {
		return s == null ? NULL_ITEM : s;
	}

	public void setHelmet(Color color) {
		this.setHelmet(color.getRed(), color.getGreen(), color.getBlue());
	}

	public void setChestplate(Color color) {
		this.setChestplate(color.getRed(), color.getGreen(), color.getBlue());
	}

	public void setLeggings(Color color) {
		this.setLeggings(color.getRed(), color.getGreen(), color.getBlue());
	}

	public void setBoots(Color color) {
		this.setBoots(color.getRed(), color.getGreen(), color.getBlue());
	}
}
