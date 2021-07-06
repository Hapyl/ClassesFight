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
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ClassesFight;

public class CandleUI extends PlayerGUI {

	private final CandleController candles;

	public CandleUI(Player player) {
		super(player, "Select Candle Block", 5);
		this.candles = ClassesFight.getSingleton().getCandles();
		this.updateMenu();
	}

	private void updateMenu() {

		for (final Candles value : Candles.values()) {
			this.setItem(value.getSlot(), value.getItemStack());
			this.setClick(value.getSlot(), this.candles.getCurrentCandle(getPlayer()) == value ?
					PlayerLib::lavaPop :
					(player) -> this.switchCandle(value));
		}

		final boolean randomOffset = this.candles.isRandomOffset(getPlayer());
		this.setItem(31, new ItemBuilder(randomOffset ? Material.LIME_DYE : Material.GRAY_DYE).setName((randomOffset ? "&a" : "&c") + "Random Offset")
				.setSmartLore("If enabled, armor stand head will have a random horizontal offset.")
				.toItemStack());
		this.setClick(31, (player) -> {
			this.candles.setRandomOffset(player, !randomOffset);
			PlayerLib.plingNote(player, 2.0f);
			this.updateMenu();
		});

		this.openInventory();
	}

	private void switchCandle(Candles candle) {
		final Player player = getPlayer();

		PlayerLib.plingNote(player, 2.0f);
		Chat.sendMessage(player, "&aSelected %s.", candle.getName());

		this.candles.setCurrentCandle(player, candle);
		player.getInventory().addItem(this.candles.getItemCandle());
		player.closeInventory();
	}

}
