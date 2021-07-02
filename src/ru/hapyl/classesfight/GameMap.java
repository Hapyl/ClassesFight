package ru.hapyl.classesfight;

import kz.hapyl.spigotutils.module.annotate.NOTNULL;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import ru.hapyl.classesfight.menu.MapFeatureInfo;
import ru.hapyl.classesfight.menu.MapSize;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum GameMap {

	NONE(),
	SPAWN("The Lobby", "This is the plate you spawn in!", Material.NETHER_STAR, MapSize.SMALL, new MapLocation(0, 15, 0)),

	ARENA("Arena", "Great arena to fight in!", Material.COARSE_DIRT, MapSize.MEDIUM, new MapLocation(-94, 3, -112)),
	JAPAN("Japan", "This map based on real-life temple &e平等院 (Byōdō-in)&7!", new MapFeatureInfo("Healing Sakura", "Stand inside sakura's range to feel it petals!"), Material.PINK_GLAZED_TERRACOTTA, MapSize.LARGE, new MapLocation(106, 12, -115), null),
	GREENHOUSE("Greenhouse", "The action happening in the unknown Railway Station. Big area to fight, and to hide.", Material.OAK_SAPLING, MapSize.SMALL, new MapLocation(10, 7, 65)),
	RAILWAY("Railway Station", "The nether is hot place with a bunch of lava, so if you didn't bring your suntan cream with you, don't stay there for a long time!", Material.RAIL, MapSize.LARGE, new MapLocation(110, 16, 11)),
	NETHER("The Nether", "This greenhouse has a lot of flowers to hide, and bunch of secret passages.__&8&oWho's made them?", Material.CRIMSON_NYLIUM, MapSize.SMALL, new MapLocation(-165, 3, -66)),
	SKY("The Clouds", "Ruined city built on the clouds somewhere in the sky.", new MapFeatureInfo("Boosters", "Propel yourself to another island. Do not fall though, it would be hard to explain your death..."), Material.WHITE_STAINED_GLASS, MapSize.MASSIVE, new MapLocation(1052, 26, 1008), null),

	//SPLIT("Split", "VALORANT inspired map with no description provided because developers are lazy AS frick.", Material.NETHERITE_SCRAP, MapSize.LARGE, Arrays.asList(new MapLocation(400, 44, -237), new MapLocation(470, 43, -239))),
	//ASCENT("Ascent", "VALORANT inspired map created by &bGlentex&7, &bArchitektPinguin &7and &bKawayyy&7.", Material.BELL, MapSize.MASSIVE, Arrays.asList(new MapLocation(373, 58, 62), new MapLocation(400, 57, -84)))

	LIBRARY("Infinite Library", "translate.string.empty", Material.BOOKSHELF, MapSize.MEDIUM, Arrays.asList(new MapLocation(99, 10, 115, -180, 0), new MapLocation(108, 21, 114, -180, 0), new MapLocation(90, 21, 114, -180, 0))),
	ALCHEMY("alchemy", "", Material.CAULDRON, MapSize.MEDIUM, new MapLocation(327, 12, 78)),
	VIKING_SHIP("Dragon's Gorge", "translate.map.viking_ship.lore", new MapFeatureInfo("Sheer Cold", "Water in this gorge is so cold! While in water, your cold meter will increase. Better keep an eye on it, who knows might happen when it hits 100%"), Material.DARK_OAK_BOAT, MapSize.MEDIUM, null, Arrays
			.asList(new MapLocation(-184, 20, 154), new MapLocation(-199, 18, 140), new MapLocation(-223, 18, 173)));

	//CITY("Facade", new MapLocation(0, 90, 0)),

	private static GameMap CURRENT_MAP = SPAWN;

	private final MapSize size;
	private final MapLocation loc;
	private final List<MapLocation> locations; // if this is not empty than use loc
	private final String mapName;

	private final String lore;
	private final MapFeatureInfo info;
	private final Material icon;

	GameMap(String name, String lore, @Nullable MapFeatureInfo info, Material icon, MapSize size, MapLocation loc, List<MapLocation> locations) {
		this.mapName = name;
		this.icon = icon;
		this.loc = loc;
		this.size = size;
		this.lore = lore;
		this.info = info;
		this.locations = locations;
	}

	GameMap() {
		this("Not Selected", null, null, null, null, null, null);
	}

	GameMap(String name, String lore, Material material, MapSize size, List<MapLocation> locations) {
		this(name, lore, null, material, size, null, locations);
	}

	GameMap(String name, String lore, Material icon, MapSize size, MapLocation loc) {
		this(name, lore, null, icon, size, loc, new ArrayList<>());
	}

	GameMap(String name, String lore, Material material, MapFeatureInfo info, MapSize size, List<MapLocation> locations) {
		this(name, lore, info, material, size, null, locations);
	}

	GameMap(String name, String lore, Material icon, MapFeatureInfo info, MapSize size, MapLocation loc) {
		this(name, lore, info, icon, size, loc, new ArrayList<>());
	}

	public MapSize getSize() {
		return size;
	}

	public boolean isValidMap() {
		return this != NONE && this != SPAWN;
	}

	public String getLore() {
		return lore;
	}

	public MapFeatureInfo getInfo() {
		return info;
	}

	public static GameMap getCurrentMap() {
		return CURRENT_MAP;
	}

	public static void setCurrentMap(GameMap newMap) {
		CURRENT_MAP = newMap;
		saveCurrentMap();
	}

	public static void saveCurrentMap() {
		ClassesFight.getPlugin().getConfig().set("last-map", CURRENT_MAP.name());
	}

	public static void loadCurrentMap() {
		final String strName = ClassesFight.getPlugin().getConfig().getString("last-map", "NONE");
		CURRENT_MAP = Validate.getEnumValue(GameMap.class, strName, NONE);
	}

	@NOTNULL
	public Location getLocation() {
		// return the location
		if (loc != null) {
			return this.loc.getBukkitLocation();
		}
		// return random location
		else if (!locations.isEmpty()) {
			return locations.get(new Random().nextInt(locations.size())).getBukkitLocation();
		}
		else {
			GameManager.current().errorMessage("Could not find location for this map!");
			return new Location(Bukkit.getWorlds().get(0), 0, 15, 0);
		}
	}

	public Material getIcon() {
		return icon;
	}

	public String getMapName() {
		return mapName;
	}

	public static class MapLocation {
		private final Location loc;

		MapLocation(int x, int y, int z, float a, float b) {
			this.loc = new Location(Bukkit.getWorlds().get(0), x + 0.5d, y + 0.5d, z + 0.5d, a, b);
		}

		MapLocation(int x, int y, int z) {
			this(x, y, z, 0, 0);
		}

		public Location getBukkitLocation() {
			return loc;
		}

	}


}
