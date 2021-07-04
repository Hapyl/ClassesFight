package ru.hapyl.classesfight.feature;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import ru.hapyl.classesfight.menu.GameMenuGUI;
import ru.hapyl.classesfight.menu.GameSettingMenu;
import ru.hapyl.classesfight.menu.collectibles.gui.CollectionGUI;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class GameItem {

	private final Map<Integer, ItemStack> items;

	public GameItem(JavaPlugin plugin) {
		this.items = new HashMap<>();
		// Init Items
		this.items.put(2, createItem(Material.HEART_OF_THE_SEA, "Main Menu", GameMenuGUI::new));
		this.items.put(4, createItem(Material.CHEST, "Collection", CollectionGUI::new));
		this.items.put(6, createItem(Material.DIAMOND, "Start Game", player -> player.performCommand("start")));
		this.items.put(7, createItem(Material.COMPARATOR, "Game Settings", GameSettingMenu::new));
	}

	private ItemStack createItem(Material material, String name, Consumer<Player> clickAction) {
		return new ItemBuilder(material, "item_" + name.toLowerCase(Locale.ROOT).replace(" ", "_")).setName(String.format("&a%s &6&lCLICK", name))
				.addClickEvent(clickAction)
				.build();
	}

	public void giveAll(Player player) {
		this.items.forEach((slot, item) -> player.getInventory().setItem(slot, item));
	}


}
