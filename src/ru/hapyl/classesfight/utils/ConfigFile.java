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

package ru.hapyl.classesfight.utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.hapyl.classesfight.Main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigFile {

	private static final Map<String /*name*/, ConfigFile /*this*/> savedValues = new HashMap<>();

	private final String name;
	private File file;
	private YamlConfiguration config;

	public ConfigFile(String nameWithoutYml) {
		nameWithoutYml = nameWithoutYml.replace(".yml", "");
		if (savedValues.containsKey(nameWithoutYml)) {
			throw new IllegalArgumentException(String.format("Cannot create %s since it's already exist.", nameWithoutYml));
		}
		this.name = nameWithoutYml;
	}

	public YamlConfiguration getConfig() {
		return config;
	}

	public void loadFile() {
		this.loadFile0();
	}

	public void writeToFile() {
		this.saveToFile();
	}

	public void set(String path, Object value) {
		this.getConfig().set(path, value);
	}

	public long getLong(String path, long def) {
		return this.getConfig().getLong(path, def);
	}

	private void loadFile0() {
		try {
			this.file = new File(Main.getMain().getDataFolder(), this.name + ".yml");
			this.config = YamlConfiguration.loadConfiguration(this.file);
			this.config.options().copyDefaults(true);
			this.saveToFile();
		} catch (Exception error) {
			sendInfo("An error occurred whilst trying to load file.");
			error.printStackTrace();
			return;
		} finally {
			savedValues.put(this.name, this);
		}
		sendInfo("Successfully read %s from a file.", this.name);
	}

	private void saveToFile() {
		try {
			this.config.save(this.file);
		} catch (Exception error) {
			sendInfo("An error occurred whilst trying to save file.");
			error.printStackTrace();
			return;
		}
		sendInfo("Successfully wrote %s to a file.", this.name);
	}

	private void sendInfo(String abc, Object... obj) {
		Bukkit.getConsoleSender().sendMessage(String.format(ChatColor.DARK_GRAY + "[%s] " + ChatColor.RESET + String.format(abc, obj),
				WordUtils.capitalize(this.name)));
	}

}
