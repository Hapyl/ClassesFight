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
