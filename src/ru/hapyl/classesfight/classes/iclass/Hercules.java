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
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.storage.HerculesShift;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.Icons;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Hercules extends IClass implements Listener {

	private final int CLASS_ITEM_COOLDOWN = 300;
	private final int ultimateTime = 12 * 20;

	private final Map<Player, Trident> tridentFragile = new ConcurrentHashMap<>();
	private final ItemStack CLASS_ITEM = new CFItemBuilder(Material.TRIDENT).setPureDamage(2.0)
			.setName("&aGorynych")
			.applyDefaultSettings(false)
			.addEnchant(Enchantment.LOYALTY, 3)
			.build();

	public Hercules() {
		super("Hercules", Material.PISTON);

		this.setHealth(EnumInfo.HIGH);
		this.setDefense(EnumInfo.VERY_HIGH);
		this.setRole(ClassRole.STRATEGIST);
		this.setRating(ClassRating.NOT_YET);

		this.setInfo("The greatest warrior of all time - \"The Great Hercules\" descended from heaven to punish the infidels! Super-Duper strong punches give you a chance to win.__",
				Icons.abilityDot("Plunge &e&lPASSIVE", "While airborne, &e&lSNEAK &7to perform plunging attack, dealing damage to nearby enemies.") +
						Icons.abilityDot("Gorynych", "A loyal trident which will return to you no matter what!") +
						Icons.abilityDot("Dash", "A chargeable ability that instantly pulls your into direction you moving. &b(Max &b3 &bcharges)"),
				"Crush the Ground",
				"Gain a Hercules power for &b" + (BukkitUtils.roundTick(ultimateTime)) + "s&7. While active, your jump height and plunge damage will be boosted!", 6);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxMGM5NjFiOWQ3ODczMjdjMGQxNjQ2ZTY1YWU0MGM2ZDgzNDUxNDg3NzgyNDMzNWQ0YjliNjJiMjM2NWEyNCJ9fX0=");
		eq.setChestplate(Color.WHITE);
		eq.setBoots(Material.LEATHER_BOOTS);
		eq.addItem(CLASS_ITEM);
		Abilities.HERCULES_SHIFT.addItemIfExists(eq);

	}

	@Override
	public void onPlayerRevealed(Player player) {
		((HerculesShift)Abilities.HERCULES_SHIFT.getAbility()).grantBack(player, 5 * 20);
		((HerculesShift)Abilities.HERCULES_SHIFT.getAbility()).grantBack(player, 10 * 20);
	}

	@Override
	public void onStartOnce() {
		new GameTask() {
			@Override
			public void run() {
				tridentFragile.forEach((player, trident) -> {
					if (trident.isDead()) {
						giveTridentBack(player, false);
					}
				});
			}
		}.runTaskTimer(20, 20);
	}

	private int getPlungeDistance(Player player) {
		final Location location = player.getLocation().clone();
		if (player.isOnGround()) {
			return -1;
		}
		for (int i = 1; i < location.getBlockY(); i++) {
			location.subtract(0.0d, i, 0.0d);
			if (!location.getBlock().getType().isAir()) {
				return i;
			}
			location.add(0.0d, i, 0.0d);
		}
		return -1;
	}

	private boolean canPlunge(Player player) {
		return getPlungeDistance(player) > 3;
	}

	@Override
	public void useUltimate(Player player) {
		this.setUsingUltimate(player, true, ultimateTime);
	}

	@EventHandler()
	public void handlePlayerJump(PlayerStatisticIncrementEvent ev) {
		final Player player = ev.getPlayer();
		if (ev.getStatistic() == Statistic.JUMP) {
			if (isUsingUltimate(player)) {
				final Vector velocity = player.getVelocity();
				player.setVelocity(velocity.setY(1.25f));
			}
		}
	}

	@EventHandler
	public void handleFragileTrident(ProjectileLaunchEvent ev) {
		// Auto-Generated
		if (ev.getEntity() instanceof final Trident trident) {
			if (trident.getShooter() instanceof final Player player) {
				if (player.hasCooldown(Material.TRIDENT)) {
					ev.setCancelled(true);
					return;
				}
				if (tridentFragile.containsKey(player)) {
					tridentFragile.get(player).remove();
				}
				tridentFragile.put(player, trident);
			}
		}
	}

	private void giveTridentBack(Player player, boolean lessCooldown) {
		final Trident trident = tridentFragile.get(player);
		trident.remove();
		player.setCooldown(Material.TRIDENT, lessCooldown ? CLASS_ITEM_COOLDOWN / 3 : CLASS_ITEM_COOLDOWN);
		player.getInventory().setItem(0, CLASS_ITEM);
		player.updateInventory();
		tridentFragile.remove(player);
	}

	@EventHandler
	public void handleFragileBack(ProjectileHitEvent ev) {
		if (GameManager.current().isGameInProgress()) {
			final Projectile entity = ev.getEntity();
			if (entity instanceof Trident) {
				final ProjectileSource shooter = ev.getEntity().getShooter();
				if (shooter instanceof Player) {
					giveTridentBack((Player)shooter, ev.getHitEntity() != null);
				}
			}
		}
	}

	@EventHandler
	public void handleUltimate(PlayerToggleSneakEvent ev) {
		final Player player = ev.getPlayer();
		if (GameManager.current().isGameInProgress()
				&& !Spectator.isSpectator(player)
				&& ClassManager.getClass(player) == ClassManager.HERCULES
				&& player.isSneaking() && canPlunge(player) && !isPlunging(player)) {
			performPlunge(player, getPlungeDistance(player));
		}
	}

	@Override
	public void onStop(Player player) {
		player.removeScoreboardTag("plunging");
	}

	private boolean isPlunging(Player player) {
		return player.getScoreboardTags().contains("plunging");
	}

	private void performPlunge(Player player, int dist) {
		final double plungeDamage = 3.5d + (0.25 * dist);
		PlayerLib.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_2, 1.75f);
		GameEffectManager.applyEffect(player, GameEffectType.FALL_DAMAGE_RESISTANCE, 80, true);
		player.setVelocity(new Vector(0.0d, -1.0d, 0.0d));
		player.addScoreboardTag("plunging");
		new GameTask() {
			private int tickTime = 80;

			@Override
			public void run() {
				if (tickTime-- <= 0 || player.isOnGround()) {
					this.cancel();
					player.removeScoreboardTag("plunging");
					EnumEffect.GROUND_PUNCH.display(player);
					GameUtils.getPlayerInRange(player.getLocation(), 4).forEach(target -> {
						if (target == player) {
							return;
						}
						DamageFeature.damage(target, player, isUsingUltimate(player) ?
								plungeDamage * 2 :
								plungeDamage, EnumDamageCause.PLUNGE);
					});
				}

			}
		}.runTaskTimer(0, 1);
	}

}
