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

package ru.hapyl.classesfight.database.entry;

import com.google.common.collect.Lists;
import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.skin.SkinContainer;
import ru.hapyl.classesfight.skin.SkinUpgrade;
import ru.hapyl.classesfight.skin.Upgradable;
import ru.hapyl.classesfight.skin.WrappedSkin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkinEntry extends DatabaseEntry {

	private static final String ENTRY_SKIN_LEVEL = "skins.upgrade.%s";
	private static final String ENTRY_SKIN_SELECTED = "skins.selected.%s";
	private static final String ENTRY_SKIN_OWNED = "skins.owned";

	public SkinEntry(Database database) {
		super(database);
	}

	public boolean hasSkin(SkinContainer skin) {
		return getOwnedSkins().contains(skin);
	}

	public void grantSkin(SkinContainer skin) {
		final List<SkinContainer> allSkins = getOwnedSkins();
		allSkins.add(skin);
		setSkins(allSkins);
	}

	public void revokeSkin(SkinContainer skin) {
		final List<SkinContainer> oldList = getOwnedSkins();
		oldList.remove(skin);
		setSkins(oldList);
	}

	private void setSkins(List<SkinContainer> newList) {
		this.database.getYaml().set(ENTRY_SKIN_OWNED, super.enumListToString(newList));
	}

	@Nullable
	public void getSelectedSkin(ClassManager forClass) {
		this.database.getYaml().getString(String.format(ENTRY_SKIN_SELECTED, forClass.name()), null);
	}

	public void setSelectedSkin(ClassManager forClass, SkinContainer skin) {
		if (hasSkin(skin)) {
			this.database.getYaml().set(String.format(ENTRY_SKIN_SELECTED, forClass.name()), skin.name());
		}
		else {
			Chat.sendMessage(this.database.getPlayer(), "&cYou don't own this skin!");
		}
	}

	public void setUpgradeSkinLevel(SkinContainer skin, int lvl) {
		if (!(skin.getWrappedSkin() instanceof Upgradable)) {
			this.database.getPlayer().sendMessage(ChatColor.RED + "Cannot upgrade this skin!");
			return;
		}
		this.database.getYaml().set(String.format(ENTRY_SKIN_LEVEL, skin.name()), lvl);
	}

	public int getCurrentUpgrade(SkinContainer skin) {
		final WrappedSkin wrapped = skin.getWrappedSkin();
		if (!(wrapped instanceof Upgradable)) {
			return -1;
		}
		return this.database.getYaml().getInt(String.format(ENTRY_SKIN_LEVEL, skin.name()), 0);
	}

	public List<SkinUpgrade> getOwnedUpgrades(SkinContainer skin) {
		final int currentUpgrade = getCurrentUpgrade(skin);
		if (currentUpgrade == -1) {
			return Lists.newArrayList();
		}
		// At this point skins have to be upgradable
		final List<SkinUpgrade> unlocked = new ArrayList<>();
		final Map<Integer, SkinUpgrade> upgrades = ((Upgradable)skin.getWrappedSkin()).getUpgrades();
		upgrades.forEach((lvl, upgrade) -> {
			if (currentUpgrade >= lvl) {
				unlocked.add(upgrade);
			}
		});
		return unlocked;
	}

	public List<SkinContainer> getOwnedSkins() {
		return super.stringListToEnum(this.database.getYaml().getStringList(ENTRY_SKIN_OWNED), SkinContainer.class);
	}

}
