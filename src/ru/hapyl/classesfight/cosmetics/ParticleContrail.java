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

package ru.hapyl.classesfight.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.Setting;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public abstract class ParticleContrail extends AbstractEffect {

	private static final String suffixLore = "____&6This &6is &6a &6Particle &6Contrail! &6It &6will &6display &6behind &6you &6whenever &6you &6move.";

	private final Particle particle;
	private final int count;

	public ParticleContrail(String name, String lore, long cost, Material icon, ShopItemRarity rarity, Particle particle, int count) {
		super(name, lore + suffixLore, cost, icon, rarity);
		this.particle = particle;
		this.count = count;
	}

	@Override
	public final void display(Player player) {
		final Location behind = getLocationBehind(player);
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online == player) {
				showAt(behind, online);
			}
			else {
				final boolean enabled = Database.getDatabase(online).getSettingEntry().isEnabled(Setting.SEE_CONTRAILS);
				if (enabled || Spectator.isSpectator(online)) {
					showAt(behind, online);
				}
			}
		}
	}

	private void showAt(Location where, Player viewer) {
		viewer.spawnParticle(this.particle, where, this.count, 0, 0, 0, 0);
	}

	public Location getLocationBehind(Player player) {
		return player.getLocation().clone().add(player.getLocation().getDirection().normalize().multiply(-0.5)).add(0.0d, 1.0d, 0.0d);
	}

}
