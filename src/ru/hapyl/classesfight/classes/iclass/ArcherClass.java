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

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.Icons;

import java.util.HashSet;
import java.util.Set;

public class ArcherClass extends IClass implements Listener {

	private final Set<Entity> boomArrow = new HashSet<>();
	private final ItemStack BOOM_BOW = new CFItemBuilder(Material.BOW).setName("&6&lBOOM BOW")
			.addEnchant(Enchantment.ARROW_INFINITE, 1)
			.setUnbreakable()
			.build();

	public final double BOOM_BOW_DAMAGE = 10.0d;
	public final double NORMAL_ARROW_DAMAGE = 3.0d;

	public ArcherClass() {
		super("Hunter", Material.BOW);
		this.setRole(ClassRole.RANGE);
		this.setInfo("&7You are the hunter! With ability to infuse arrows, and well, obviously a cool bow!__", Icons.ABILITY_DOT + "Hawkeye &e&lPASSIVE &7Fully charged shots while sneaking have &b25% &7chance to launch a Hawkeye arrow. Hawkeye arrows will aim to a closest player when they're near enough.__" + Abilities.SHOCK_ARROW
				.getAbout() + Abilities.TRIPLE_ARROW.getAbout(), "BOOM BOW", "Equips a &6&lBOOM BOW &7for &b6s &7that shoots &c&lExlosive &7arrows that explodes upon hitting a block dealing massive damage.", 7);
		this.setUltimateSound(Sound.ITEM_CROSSBOW_SHOOT, 0.25f);
		this.setRating(ClassRating.NOT_YET);

		final ClassEquipment eq = this.getClassEquipment(true);
		eq.setHelmet(Material.CHAINMAIL_HELMET);
		eq.setChestplate(Material.CHAINMAIL_CHESTPLATE);
		eq.setLeggings(Material.LEATHER_LEGGINGS);
		eq.setBoots(Material.LEATHER_BOOTS);

		eq.addItem(new CFItemBuilder(Material.BOW).setName("&aHunter's Bow")
				.addEnchant(Enchantment.ARROW_INFINITE, 1)
				.applyDefaultSettings(false)
				.build());

		Abilities.SHOCK_ARROW.addItemIfExists(eq);
		Abilities.TRIPLE_ARROW.addItemIfExists(eq);

	}

	@Override
	public void useUltimate(Player player) {
		final PlayerInventory inventory = player.getInventory();
		inventory.setItem(4, BOOM_BOW);
		inventory.setHeldItemSlot(4);

		new GameTask() {
			@Override
			public void run() {
				PlayerLib.playSound(player, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 2.0f);
				Chat.sendMessage(player, "&6Your blowing power has gone!");
				inventory.setItem(4, new ItemStack(Material.AIR));
				inventory.setHeldItemSlot(0);
			}
		}.runTaskLater(120);

	}

	@Override
	public void onStopOnce() {
		boomArrow.clear();
	}

	@Override
	public void onStart(Player player) {
		player.getInventory().setItem(9, new ItemStack(Material.ARROW));
	}

	@EventHandler
	public void handleArrowHit(ProjectileHitEvent ev) {
		if (ev.getEntity() instanceof Arrow arrow) {

			Player player = null;
			ev.getEntity().remove();

			if (arrow.getShooter() instanceof Player) {
				player = (Player)arrow.getShooter();
			}

			if (player == null) {
				return;
			}

			if (boomArrow.contains(arrow)) {
				executeUltimateExplosion(arrow.getLocation());
				boomArrow.remove(arrow);
			}

		}
	}

	@EventHandler
	public void handleBowShooting(EntityShootBowEvent ev) {
		if (ev.getEntity() instanceof final Player player) {

			final Arrow arrow = (Arrow)ev.getProjectile();

			if (ClassManager.getClass(player) == ClassManager.HUNTER) {
				if (ev.getBow() == null) {
					return;
				}

				if (ev.getBow().isSimilar(BOOM_BOW)) {
					boomArrow.add(arrow);
					return;
				}

				if ((arrow.isCritical() && player.isSneaking()) && ThreadRandom.nextFloatAndCheckBetween(0.75f, 1.0f)) {
					PlayerLib.playSound(player, Sound.ENCHANT_THORNS_HIT, 1.25f);
					new GameTask() {
						@Override
						public void run() {
							if (arrow.isDead()) {
								this.cancel();
								return;
							}

							PlayerLib.spawnParticle(arrow.getLocation(), Particle.CRIT_MAGIC, 1, 0, 0, 0, 0);
							final Entity nearestPlayer = GameUtils.getNearestPlayer(arrow.getLocation(), 3, player);

							if (nearestPlayer == null) {
								return;
							}

							final Vector vector = nearestPlayer.getLocation()
									.clone()
									.add(0d, 0.5d, 0.0d)
									.toVector()
									.subtract(arrow.getLocation().toVector())
									.normalize()
									.multiply(0.7);
							arrow.setVelocity(vector);


						}
					}.runTaskTimer(0, 2);
				}

				arrow.setDamage(NORMAL_ARROW_DAMAGE);
			}
		}
	}

	public void executeUltimateExplosion(Location at) {
		GameUtils.createFakeExplosion(at, 6, BOOM_BOW_DAMAGE, null, EnumDamageCause.BOOM_BOW_ULTIMATE);
	}


}
