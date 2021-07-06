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

package ru.hapyl.classesfight.ability.extra;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.TechnoClass;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;

import java.util.Set;

public class Tripwire {

	private final Player player;
	private final Set<Block> blocks;

	public Tripwire(Player player, Set<Block> blocks) {
		this.player = player;
		this.blocks = blocks;
	}

	public Player getPlayer() {
		return player;
	}

	public Set<Block> getBlocks() {
		return blocks;
	}

	public void drawLine() {
		for (Block block : blocks) {
			PlayerLib.spawnParticle(this.getPlayer(), BukkitUtils.centerLocation(block.getLocation())
					.subtract(0.0d, 0.4d, 0.0d), Particle.CRIT, 1, 0, 0, 0, 0);
		}
	}

	public void setBlocks() {
		this.blocks.forEach(block -> block.setType(Material.TRIPWIRE, false));
	}

	public void clearBlocks() {
		this.blocks.forEach(block -> block.setType(Material.AIR, false));
	}

	public void affectPlayer(Player player) {
		Chat.sendTitle(this.getPlayer(), "&aTripwire Triggered!", "&7You caught " + player.getName(), 10, 20, 10);
		GameEffectManager.applyEffect(player, GameEffectType.VULNERABLE, 80);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 4, true, true));
		PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_SCREAM, 1.25f);
		((TechnoClass)ClassManager.TECHNO.getTheClass()).glowEntitySilhouette(player, this.getPlayer(), 20);
	}

	public boolean isBlockATrap(Block block) {
		return this.getBlocks().contains(block);
	}
}
