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

package ru.hapyl.classesfight.quest.relic;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.collection.GapList;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.GUI;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.PackagedReward;
import ru.hapyl.classesfight.database.entry.Booleans;
import ru.hapyl.classesfight.database.entry.Numbers;
import ru.hapyl.classesfight.utils.OldPlayerLib;
import ru.hapyl.classesfight.utils.RomanNumber;

import java.util.Locale;

public class RelicRewardMenu extends PlayerGUI {
	public RelicRewardMenu(Player player, GUI from) {
		super(player, "Relic Hunt Rewards", 6);
		this.setArrowBack(from);
		this.updateMenu();
		this.openInventory();
	}

	private final PackagedReward[] rewards =
			{
					PackagedReward.lazy(500, 0),
					PackagedReward.lazy(1000, 1),
					PackagedReward.lazy(2500, 2),
			};

	private static final GapList<PackagedReward> exchangeRewards = new GapList<>();

	static {
		exchangeRewards.add(PackagedReward.lazy(1000, 0));
		exchangeRewards.add(PackagedReward.lazy(2000, 0));
		exchangeRewards.add(PackagedReward.lazy(3500, 0));
		exchangeRewards.add(PackagedReward.lazy(5000, 0));
		exchangeRewards.add(PackagedReward.lazy(6000, 0));
		exchangeRewards.add(PackagedReward.lazy(7500, 2));
	}

	private void updateMenu() {

		int slot = 38;

		for (int i = 0; i < RelicType.values().length; ++i, ++slot) {
			final RelicType type = RelicType.values()[i];
			final String ufName = Chat.capitalize(type.name());

			final int totalOf = RelicHunt.current().getRelicAmount(type);
			final int playerFound = RelicHunt.current().getFoundRelicAmount(type, getPlayer());

			this.setItem(slot, ItemBuilder.playerHead(type.getTexture())
					.setName(ChatColor.GREEN + ufName)
					.setSmartLore("Relics are ancient artifacts left by unknowns... but someone is interested in them.")
					.addLore()
					.addLore("&7You've found &b%s &7of &b%s &7total %s relics.", playerFound, totalOf, ufName)
					.build());


			// minecart rewards
			for (int j = 0; j < 3; j++) {

				/**
				 * collectibles.relics
				 */

				final String path = "collectibles.relics." + type.name().toLowerCase(Locale.ROOT) + "_" + j;
				final boolean hasClaimed = Booleans.isTrue(getPlayer(), path);

				boolean canClaim = false;

				final ItemBuilder builder =
						new ItemBuilder(hasClaimed ? Material.MINECART : Material.CHEST_MINECART)
								.setName(((hasClaimed ? ChatColor.GREEN : ChatColor.RED) + (ufName + " Collector ")) + RomanNumber.toRoman(j + 1))
								.addLore("&8One Time Exchange")
								.addLore()
								.addLore("&7Claim Requirement:")
								.addSmartLore(getInfo(j, type), " ")
								.addLore()
								.addLore("&7Rewards:")
								.addLore(rewards[j].getRewardInfoLore())
								.addLore()
								.setAmount(j + 1);

				// claim test
				if (!hasClaimed) {
					switch (j) {
						case 0: {
							if (playerFound > 0) {
								canClaim = true;
							}
							break;
						}
						case 1: {
							if (playerFound > 0 && playerFound >= (totalOf / 2)) {
								canClaim = true;
							}
							break;
						}
						case 2: {
							if (playerFound > 0 && playerFound >= totalOf) {
								canClaim = true;
							}
							break;
						}
					}
				}

				if (hasClaimed) {
					builder.addLore("&aAlready claimed!");
				}
				else {
					if (canClaim) {
						builder.addLore("&eClick to claim!");
						builder.glow();
					}
					else {
						builder.addLore("&cNot eligible to claim!");
					}
				}

				final int targetSlot = slot - 9 * (j + 1);
				this.setItem(targetSlot, builder.build());
				if (canClaim) {
					int finalJ = j;
					this.setClick(targetSlot, player -> {
						Booleans.setValue(player, path, true);
						grantRewards(player, finalJ);
						this.updateMenu();
					});
				}
				else {
					this.setClick(targetSlot, player -> OldPlayerLib.playSound(player, Sound.BLOCK_LAVA_POP, 0.0f));
				}

			}
		}

		// load rewards
		//
		// [x] rewards per relic:
		//  find at least one
		//  find half of em
		//  find all of em
		//
		// special rewards:
		//  (only appears when fund all to not spoil) 48 -- find all all all of the relics
		//  [x] 50 -- permanent 5 exchange

		final ItemBuilder builder = new ItemBuilder(Material.END_PORTAL_FRAME).setName("&aAncient Transmitter")
				.addLore("&8Permanent Exchange").addLore().addSmartLore("Exchange every &bfive &7found relics for a small reward.");

		final int foundTotal = RelicHunt.current().getFoundRelics(getPlayer());
		final int exchangeLevel = Numbers.RELIC_EXCHANGE_LEVEL.getInt(getPlayer(), 0);
		final PackagedReward currentLevelRewards = exchangeRewards.get(exchangeLevel);
		final int validForExchange = Math.max(0, foundTotal - ((exchangeLevel * 5) - 5));

		builder.addLore();
		builder.addLore("&7Your exchange lvl &6[&l%s&6]", RomanNumber.toRoman(exchangeLevel + 1));
		builder.addLore("&7Next rewards:");
		builder.addLore(currentLevelRewards.getRewardInfoLore());
		builder.addLore();
		builder.addLore("&7Exchangeable Relics: &b" + validForExchange);
		builder.addLore("");

		if (validForExchange >= 5) {
			builder.addLore("&eClick to exchange!");
		}
		else {
			builder.addLore("&cCannot exchange! (%s/5)", validForExchange);
		}

		// found - exch lvl * 5 ==

		this.setItem(50, builder.build());
		if (validForExchange >= 5) {
			this.setClick(50, player -> {
				Numbers.RELIC_EXCHANGE_LEVEL.addValue(player, 1);
				currentLevelRewards.grantReward(player);
				OldPlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
				OldPlayerLib.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0f);
				this.updateMenu();
			});
		}

	}

	private String getInfo(int index, RelicType type) {
		final String ufName = Chat.capitalize(type);
		final int relicAmount = RelicHunt.current().getRelicAmount(type);
		switch (index) {

			case 0: {
				return String.format("Find at least one of %s relics.", ufName);
			}

			case 1: {
				return String.format("Find at least &b%s &7of %s relics.", Math.max(1, relicAmount / 2), ufName);
			}

			case 2: {
				return String.format("Find all of &b%s &7total %s relics.", relicAmount, ufName);
			}

		}

		return "invalid call";

	}

	private void grantRewards(Player player, int i) {
		rewards[i].grantReward(player);
	}

}
