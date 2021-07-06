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

package ru.hapyl.classesfight.database;

import kz.hapyl.spigotutils.module.annotate.NOTNULL;
import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.Main;
import ru.hapyl.classesfight.database.entry.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Database {

	public static final Map<UUID, Database> hashedDatabases = new HashMap<>();

	private Player player;
	private UUID uuid;
	private File file;
	private YamlConfiguration config;
	private boolean loaded = false;

	private ClassEntry classEntry;
	private StatisticsEntry statisticsEntry;
	private CoinsEntry coinsEntry;
	private RubyEntry rubyEntry;
	private PerkEntry perkEntry;
	private CosmeticEntry cosmeticEntry;
	private ExperienceEntry experienceEntry;
	private BoosterEntry boosterEntry;
	private SettingEntry settingEntry;
	private BooleanEntry booleanEntry;
	private SkinEntry skinEntry;
	private CollectibleEntry collectibleEntry;

	public Database(Player player) {
		sendInfo("Attempting to create instance for %s.", player.getName());
		if (hashedDatabases.containsKey(player.getUniqueId())) {
			sendInfo("Redundant call, %s already has instance, use 'getDatabase()'!", player.getName());
			return;
		}
		this.player = player;
		this.uuid = player.getUniqueId();
		loadFile();
		loadEntries();
	}

	private void loadEntries() {
		sendInfo("Attempting to load entries for %s.", this.player.getName());
		try {

			this.classEntry = new ClassEntry(this);
			this.statisticsEntry = new StatisticsEntry(this);
			this.coinsEntry = new CoinsEntry(this);
			this.rubyEntry = new RubyEntry(this);
			this.perkEntry = new PerkEntry(this);
			this.cosmeticEntry = new CosmeticEntry(this);
			this.experienceEntry = new ExperienceEntry(this);
			this.boosterEntry = new BoosterEntry(this);
			this.settingEntry = new SettingEntry(this);
			this.booleanEntry = new BooleanEntry(this);
			this.skinEntry = new SkinEntry(this);
			this.collectibleEntry = new CollectibleEntry(this);

			this.loaded = true;

		}
		catch (Exception error) {
			if (this.player != null) {
				this.player.sendMessage(ChatColor.RED + "Could not load entries from database, report this issue!");
			}
			error.printStackTrace();
		}
	}

	public ClassEntry getClassEntry() {
		return this.classEntry;
	}

	public StatisticsEntry getStatistics() {
		return this.statisticsEntry;
	}

	public CoinsEntry getCoinsEntry() {
		return this.coinsEntry;
	}

	public RubyEntry getRubyEntry() {
		return this.rubyEntry;
	}

	public PerkEntry getPerkEntry() {
		return this.perkEntry;
	}

	public CosmeticEntry getCosmetics() {
		return this.cosmeticEntry;
	}

	public ExperienceEntry getExperienceEntry() {
		return this.experienceEntry;
	}

	public BoosterEntry getBoosterEntry() {
		return this.boosterEntry;
	}

	public SettingEntry getSettingEntry() {
		return this.settingEntry;
	}

	public BooleanEntry getBooleanEntry() {
		return this.booleanEntry;
	}

	public SkinEntry getSkinEntry() {
		return this.skinEntry;
	}

	public CollectibleEntry getCollectibleEntry() {
		return collectibleEntry;
	}

	public YamlConfiguration getConfig() {
		return config;
	}

	/////////////////////// Static Members
	public static Database createInstance(Player player) {
		if (hasDatabase(player)) {
			return getDatabase(player);
		}
		else {
			return new Database(player);
		}
	}

	public static CoinsEntry getCoins(Player player) {
		return getDatabase(player).getCoinsEntry();
	}

	public static ExperienceEntry getExp(Player player) {
		return getDatabase(player).getExperienceEntry();
	}

	public static RubyEntry getRubies(Player player) {
		return getDatabase(player).getRubyEntry();
	}

	public static boolean hasDatabase(Player player) {
		return hashedDatabases.containsKey(player.getUniqueId());
	}

	@NOTNULL
	public static Database getDatabase(Player player) {
		// get or default does not work for some reason, use this instead
		final Database database = hashedDatabases.get(player.getUniqueId());
		if (database == null) {
			return createInstance(player);
		}
		else {
			return database;
		}
	}

	private void loadFile() {
		try {
			this.file = new File(Main.getMain().getDataFolder() + "/players", this.uuid.toString() + ".yml");
			this.config = YamlConfiguration.loadConfiguration(this.file);
			this.config.options().copyDefaults(true);
			this.saveToFile(false);
		}
		catch (Exception error) {
			sendInfo("An error occurred whilst trying to load file.");
			this.player.sendMessage(ChatColor.RED + "Could not load your data, try reconnecting to the server.");
			error.printStackTrace();
		}
		finally {
			hashedDatabases.put(this.player.getUniqueId(), this);
		}
	}

	public static void saveData(Player player) {
		hashedDatabases.get(player.getUniqueId()).saveToFile();
	}

	public void saveToFile(boolean flag) {
		if (flag) {
			sendInfo("Attempting to save database for %s.", this.player.getName());
		}
		try {
			this.config.save(this.file);
		}
		catch (Exception error) {
			if (flag) {
				sendInfo("An error occurred whilst trying to save file.");
				if (this.player != null) {
					this.player.sendMessage(ChatColor.RED + "Could not save your data, report this issue!");
				}
			}
			error.printStackTrace();
			return;
		}
		if (flag) {
			sendInfo("Successfully saved database for %s.", this.player.getName());
		}
	}

	public void saveToFile() {
		saveToFile(true);
	}

	public static boolean isDataLoaded(Player player) {
		final Database database = hashedDatabases.get(player.getUniqueId());
		return hashedDatabases.containsKey(player.getUniqueId()) && database != null && database.loaded;
	}

	public static void sendDiscontinuedCommandInfo(CommandSender player) {
		player.sendMessage(Chat.format("&cKeep in mind that this command will be discontinued soon! Use &e/database &centies instead!"));
	}

	public void write(String path, Object value) {
		this.config.set(path, value);
	}

	public YamlConfiguration getYaml() {
		return this.config;
	}

	public Player getPlayer() {
		return this.player;
	}

	private void sendInfo(String str, Object... args) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "[ClassesFight, Database] " + ChatColor.RESET + String.format(str, args));
	}

}
