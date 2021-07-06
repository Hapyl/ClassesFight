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

package ru.hapyl.classesfight.menu.collectibles.gui;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.BoosterEntry;
import ru.hapyl.classesfight.database.entry.CosmeticEntry;
import ru.hapyl.classesfight.database.entry.Setting;
import ru.hapyl.classesfight.database.entry.SettingEntry;

public class CollectionGUI extends PlayerGUI {

	private final CosmeticEntry cosmetics;
	private final BoosterEntry boosters;
	private final SettingEntry settings;

	public CollectionGUI(Player player) {
		super(player, player.getName() + "'s Collection", 4);

		final Database database = Database.getDatabase(player);
		this.cosmetics = database.getCosmetics();
		this.boosters = database.getBoosterEntry();
		this.settings = database.getSettingEntry();

		this.updateMenu();
	}

	private void updateMenu() {

		this.clearEverything();

		int slot = 10;
		for (EnumEffect.Type value : EnumEffect.Type.values()) {
			final String typeName = Chat.capitalize(value.name());

			// Icon
			this.setItem(slot, buildCategoryItem(value));
			this.setClick(slot, player -> {
				new CollectionSelectGUI(player, value);
			});

			// Button
			boolean isEnabled = cosmetics.isEnabled(value);

			this.setItem(slot + 9,
					new ItemBuilder(isEnabled ? Material.LIME_DYE : Material.GRAY_DYE)
							.setName(isEnabled ? "&aEnabled" : "&cDisabled")
							.addLore("&7%s effect is currently %s", typeName, (isEnabled ? "enabled" : "disabled"))
							.addLore()
							.addLore("&eClick to %s", (isEnabled ? "disable" : "enable"))
							.toItemStack());

			this.setClick(slot + 9, player -> {
				cosmetics.setEnabled(value, !isEnabled);
				PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
				this.sendCollectionMessage("%s effect is now %s.", typeName, !isEnabled ? "enabled" : "disabled");
				this.updateMenu();
			});

			++slot;
		}

		// Skins
		this.setItem(14,
				ItemBuilder.playerHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQzNjc5Njg4ZDYyNjZhOTY3OWIzOGU1YjVlM2M2MGFhYzY0NGY2MWVlZjI3NTBiMDYwNTQxODM0MjY4MmM5In19fQ==")
						.setName("&aClass Skins")
						.setSmartLore("Refresh your class look by using a skin!")
						.addLore()
						.addLore("&eComing Soon!")
						.toItemStack());

		this.setItem(14 + 9, new ItemBuilder(Material.LIME_DYE).setName("&aEnabled").addLore("&cUnable to disable").toItemStack());

		// Boosters
		this.setItem(15, new ItemBuilder(Material.DRAGON_BREATH)
				.setName("&aBoosters")
				.setSmartLore("Boost your Coins and Experience by drinking those boosters!")
				.addLore("")
				.addLore("&7Your Boosters:")
				.addLore(" &aCoins and Exp - &l" + (boosters.getBoosters() == 0 ? "None!" : boosters.getBoosters()))
				.addLore()
				.addLore("&7You have no other boosters.")
				.toItemStack());

		final boolean boosterUseEnabled = settings.isEnabled(Setting.USE_BOOSTERS);

		this.setItem(15 + 9, new ItemBuilder(boosterUseEnabled ? Material.LIME_DYE : Material.GRAY_DYE)
				.setName(boosterUseEnabled ? "&aEnabled" : "&cDisabled")
				.addLore("&7Booster use is currently %s", (boosterUseEnabled ? "enabled" : "disabled"))
				.addLore()
				.addLore("&eClick to %s", (boosterUseEnabled ? "disable" : "enable"))
				.toItemStack(), player -> {
			settings.setEnabled(Setting.USE_BOOSTERS, !boosterUseEnabled);
			PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
			this.sendCollectionMessage("Booster use is now %s.", !boosterUseEnabled ? "enabled" : "disabled");
			this.updateMenu();
		});

		final ItemStack iconOpenShop = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName("&a-> To Item Shop").build();

		for (int i = 0; i < 4; i++) {
			this.setItem((8 * (i + 1)) + i, iconOpenShop, ShopGUI::new);
		}

		this.openInventory();

	}


	private void sendCollectionMessage(String msg, Object... a) {
		Chat.sendMessage(getPlayer(), "&d&lCOLLECTION! &7" + msg, a);
	}

	private ItemStack buildCategoryItem(EnumEffect.Type effect) {
		final EnumEffect selectedEffect = cosmetics.getCurrentEffect(effect);
		return new ItemBuilder(effect.getMaterial())
				.setName("&a%s Effect", Chat.capitalize(effect.name()))
				.addLore()
				.addLore("&7Selected Effect")
				.addLore(" &a" + (selectedEffect == null ? "None!" : Chat.capitalize(selectedEffect.name())))
				.addLore()
				.addSmartLore(effect.getAbout())
				.addLore()
				.addLore(String.format("&eClick to select %s effect", effect.name().toLowerCase()))
				.hideFlags()
				.toItemStack();
	}

}
