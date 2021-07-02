package ru.hapyl.classesfight.menu;

import kz.hapyl.spigotutils.module.annotate.NOTNULL;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.utils.CFItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class MapSelectMenu extends PlayerGUI {

	public MapSelectMenu(Player player) {
		super(player, "Map Selector", 4);
		final SmartComponent smart = this.newSmartComponent();
		for (GameMap value : GameMap.values()) {
			if (value.isValidMap()) {
				final MenuItem menuItem = new MenuItem(value.getIcon(), value.getMapName())
						.classReference(value)
						.setDescription(value.getLore())
						.setMapSize(value.getSize());

				if (value.getInfo() != null) {
					menuItem.addMapFeature(value.getInfo());
				}

				smart.add(menuItem.toItemStack(), (t) -> setMap(value));
			}
		}
		smart.fillItems(this);
		// middle of the last row
		this.setItem(this.getSize() - 5, new ItemBuilder(Material.ARROW).setName("&aGo Back")
				.setLore("To Main Menu")
				.toItemStack(), GameMenuGUI::new);
		this.setItem(this.getSize() - 6, new ItemBuilder(Material.SNOWBALL).setName("&aRandom Map")
				.setSmartLore("Couldn't decide what map you want to play on? Click here to let RNGesus to decide it for you!")
				.toItemStack(), this::randomMap);
		this.openInventory();
	}

	private void setMap(GameMap map) {
		if (GameMap.getCurrentMap() == map) {
			Chat.sendMessage(this.getPlayer(), "&cCurrent map is already %s!", map.getMapName());
			return;
		}

		if (!GameMap.getCurrentMap().isValidMap()) {
			Chat.sendMessage(this.getPlayer(), "&cMap you trying to select is marked as invalid!");
			return;
		}

		GameMap.setCurrentMap(map);
		this.sendMapMessage("&6%s &echanged current map to &6%s&e.", this.getPlayer().getName(), map.getMapName());

	}

	public void randomMap(Player player) {
		GameMap gameMap = CollectionUtils.randomElement(GameMap.values(), GameMap.ARENA);
		while (gameMap == null || !gameMap.isValidMap()) {
			gameMap = CollectionUtils.randomElement(GameMap.values());
		}
		GameMap.setCurrentMap(gameMap);
		this.sendMapMessage("A new random map is %s!", gameMap.getMapName());
	}

	private void sendMapMessage(String message, Object... replacements) {
		Chat.broadcast("&6&l➔ &a" + message, replacements);
	}

	private static class MenuItem {

		private final Material material;
		private final String name;
		private String description;
		private final List<MapFeatureInfo> mapFeatureInfoList = new ArrayList<>();
		private MapSize size;
		@NOTNULL private GameMap map = GameMap.SPAWN;

		public MenuItem(Material material, String name) {
			this.material = material;
			this.name = name;
		}

		public MenuItem setMapSize(MapSize size) {
			this.size = size;
			return this;
		}

		public MenuItem setDescription(String string) {
			this.description = string;
			return this;
		}

		public void addMapFeature(MapFeatureInfo info) {
			mapFeatureInfoList.add(info);
		}

		public void addMapFeature(String name, String about) {
			this.addMapFeature(new MapFeatureInfo(name, about));
		}

		public GameMap getMap() {
			return this.map;
		}

		public MenuItem classReference(GameMap map) {
			this.map = map;
			return this;
		}

		public ItemStack toItemStack() {

			final CFItemBuilder builder = new CFItemBuilder(this.material);

			// npe map should have size
			if (this.size != null) {
				builder.addLore(this.size.toString());
				builder.addLore();
			}

			builder.setName(ChatColor.GREEN + this.name).addSmartLore(ChatColor.GRAY + this.description);

			if (!this.mapFeatureInfoList.isEmpty()) {
				builder.addLore();
				builder.addLore(String.format("&e&lMap Feature%s:", this.mapFeatureInfoList.size() == 1 ? "" : "s"));
				for (MapFeatureInfo feature : this.mapFeatureInfoList) {
					builder.addLore(ChatColor.AQUA + feature.getName());
					builder.addSmartLore(ChatColor.GRAY + feature.getInfo());
					builder.addLore();
				}
			}
			else {
				builder.addLore();
			}

			builder.addLore(ChatColor.YELLOW + "Click to select!");

			return builder.toItemStack();
		}

	}


}
