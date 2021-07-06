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

package ru.hapyl.classesfight.ability;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.iclass.extra.PrincessCrown;

public class PrincessFlash extends Ability implements PrincessCrown {
	public PrincessFlash() {
		super("Shining Gold", "Toss your crown into the air. After a brief delay, shines it's brightest - blinds and damages everyone who was looking at it.");
		this.setItem(Material.GOLDEN_HELMET);
		this.setCooldownSec(16);
	}

	@Override
	public Response useAbility(Player player) {
		PlayerLib.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1.0f);
		playCrownAnimation(player, 20, stand -> {
			PlayerLib.spawnParticle(stand.getLocation(), Particle.FLASH, 5, 0, 0, 0, 0);
			PlayerLib.playSound(stand.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 0.5f);
			GameManager.current().getPlayers().forEach(target -> {
				if (target == player) {
					return;
				}
				if (target.hasLineOfSight(stand)) {
					PlayerLib.addEffect(target, PotionEffectType.BLINDNESS, 60, 1);
				}
			});
		});
		return Response.OK;
	}
}
