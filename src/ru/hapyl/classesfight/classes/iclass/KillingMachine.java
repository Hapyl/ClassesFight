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

package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.reflect.glow.Glowing;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.Weapon;

public class KillingMachine extends IClass {

	private final int ultimateTime = 12 * 20;

	public KillingMachine() {
		super("War Machine", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWMyZjNkNWQ2MmZkOWJlNmQ2NTRkMzE0YzEyMzM5MGFiZmEzNjk4ZDNkODdjMTUxNmE0NTNhN2VlNGZjYmYifX19");
		this.setInfo("A machine of war that was left for scrap, until now...",
				Abilities.LASER_EYE.getAbout() + Abilities.SHELL_GRENADE.getAbout(),
				"Overload", "Overload yourself for &b" + BukkitUtils.roundTick(ultimateTime) + "s&7. While overloaded, your fire rate is increased by &b100%&7 and all enemies are highlighted.", 5);
		this.setRole(ClassRole.RANGE);

		final ClassEquipment eq = this.getClassEquipment();

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWMyZjNkNWQ2MmZkOWJlNmQ2NTRkMzE0YzEyMzM5MGFiZmEzNjk4ZDNkODdjMTUxNmE0NTNhN2VlNGZjYmYifX19");
		eq.setChestplate(Material.CHAINMAIL_CHESTPLATE);
		eq.setLeggings(Material.CHAINMAIL_LEGGINGS);
		eq.setBoots(Material.CHAINMAIL_BOOTS);

		eq.addItem(new Weapon(Material.IRON_HORSE_ARMOR, "war_machine_gun") {
			@Override
			public void onRightClick(Player player) {
				this.applyCooldown(player, isUsingUltimate(player) ? 5 : 10);
				PlayerLib.playSound(player.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.4f);
				GameUtils.rayTraceLine(player.getEyeLocation(), 50, 0.5d, 0, move -> {
					move.stopIfNotPassable();
					PlayerLib.spawnParticle(move.getLocation(), Particle.END_ROD, 1, 0, 0, 0, 0);
				}, hit -> {
					if (hit == player) {
						return;
					}
					DamageFeature.damage(hit, player, 2.0d, EnumDamageCause.ENTITY_ATTACK);
				});
			}
		}.withName("&aRifle").build());
		Abilities.LASER_EYE.addItemIfExists(eq);
		Abilities.SHELL_GRENADE.addItemIfExists(eq);

	}

	@Override
	public void useUltimate(Player player) {
		this.setUsingUltimate(player, true, ultimateTime);
		GameManager.current().getPlayers().forEach(pl -> {
			if (pl == player) {
				return;
			}
			new Glowing(pl, ChatColor.RED, ultimateTime).addViewer(player);
		});
	}

}
