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

package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;

public class FlowerBreeze extends Ability {

	private final int effectDuration = 80;
	private final double damageAmount = 5.0d;

	public FlowerBreeze() {
		super("Flower Breeze", "Feel the breeze of the flowers... damaging yourself but gaining attack boost and resistance for &b4s&7. This ability cannot kill.");
		this.setCooldown(effectDuration * 4);
		this.setItem(Material.RED_DYE);
	}

	private final Material[] flowers = {Material.POPPY, Material.DANDELION, Material.ALLIUM, Material.RED_TULIP, Material.ORANGE_TULIP, Material.PINK_TULIP, Material.WHITE_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.AZURE_BLUET};

	@Override
	public Response useAbility(Player player) {

		final Location location = player.getLocation();
		PlayerLib.playSound(location, Sound.ENTITY_HORSE_BREATHE, 0.0f);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 2));

		final World world = location.getWorld();
		final double health = DamageFeature.getHealth(player);

		// can't go lower than 1 heart
		DamageFeature.setHealth(player, Math.max(1.0d, health - damageAmount));

		if (world != null) {
			for (int i = 0; i < 20; i++) {
				final Item item = world.dropItemNaturally(location, new ItemStack(CollectionUtils.randomElement(flowers, flowers[0])));
				item.setPickupDelay(10000);
				item.setTicksLived(5900);
			}
		}

		//fx
		GameEffectManager.applyEffect(player, GameEffectType.RESISTANCE, effectDuration);
		GameEffectManager.applyEffect(player, GameEffectType.STRENGTH, effectDuration);

		return Response.OK;
	}
}
