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

package ru.hapyl.classesfight.cosmetics.kill;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.utils.ParticlesBuilder;

public class Blood extends AbstractEffect {

    public Blood() {
        super("Blood", "Spawn blood around the player.", 200, Material.REDSTONE, ShopItemRarity.RARE);
    }

    @Override
    public void display(Player player) {
        final Location at = player.getLocation();
        new ParticlesBuilder.BlockBreak(at.clone().add(0, .5, 0), Material.REDSTONE_BLOCK, .4, .4, .4, 20, .5).show();
        PlayerLib.playSound(at, Sound.BLOCK_STONE_BREAK, 1.0f);
    }
}
