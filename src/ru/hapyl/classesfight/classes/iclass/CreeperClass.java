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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.feature.AbilitiesCooldown;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Sound.ENTITY_CREEPER_HURT;
import static org.bukkit.Sound.ENTITY_CREEPER_PRIMED;

public class CreeperClass extends IClass implements Listener {

	private final double tntRawDamage = 1.5d;
	private final int littleHelperFuse = 10 * 20;
	private final Map<Player, Integer> tntPlaced = new HashMap<>();

	public CreeperClass() {
		super("Creeper", Material.CREEPER_HEAD);
		this.setAttack(EnumInfo.LOW);
		this.setDefense(EnumInfo.LOW);
		this.setRole(ClassRole.MELEE);
		this.setRating(ClassRating.NOT_YET);

		this.setInfo("Highly blowing property of &bAwMannLabs. Green, loves TNT.",
				"Little Helper",
				"Instantly places marker at your current location and summons a &a&lLittle &a&lHelper &7creeper and takes control over it for &b" + (littleHelperFuse / 20) + "s&7; After, sends you back to marker and explodes, dealing AoE damage.",
				6);
		this.setUltimateSound(ENTITY_CREEPER_PRIMED, 0.5f);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet(Material.CREEPER_HEAD);
		eq.setChestplate(0, 128, 43);
		eq.setLeggings(0, 179, 60);
		eq.setBoots(0, 204, 68);

		eq.addItem(new CFItemBuilder(Material.TNT).setName("&cTNT &e&lPLACE TO USE").setAmount(16).build());

	}

	@Override
	public void onStart(Player player) {
		tntPlaced.put(player, 0);
	}

	@EventHandler
	public void handlePlacingTnt(BlockPlaceEvent ev) {
		final Player player = ev.getPlayer();
		final GameManager manager = GameManager.current();

		if (GameUtils.isValidPlayer(player, ClassManager.CREEPER)) {
			if (player.getInventory().getItemInMainHand().getType() == Material.TNT) {
				if (manager.arePlayersRevealed()) {
					ev.setCancelled(true);
					this.blowTnt(player, ev.getBlockPlaced().getLocation());
				}
			}
		}

	}

	@Override
	public void useUltimate(Player player) {

		this.setUsingUltimate(player, true, littleHelperFuse);
		GameUtils.hidePlayer(player);

		final Location marker = player.getLocation();
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, littleHelperFuse, 1, true, true));

		final Creeper creeper = GarbageCollector.spawnEntity(player.getLocation(), Creeper.class, me -> {
			me.setAI(false);
			me.setSilent(true);
			me.setPowered(true);
			me.setCustomName(Chat.format("&a&lLittle Helper"));
			me.setCustomNameVisible(true);
			me.setMaxFuseTicks(500);
		});

		broadcastLittleHelper("Why hello there! Here to help.");

		new GameTask() {

			private int tick = littleHelperFuse;

			@Override
			public void run() {

				if (Spectator.isSpectator(player)) {
					creeper.remove();
					broadcastLittleHelper("U-umm? Where did you go? Alright I'll leave too...");
					this.cancel();
					return;
				}

				// explode
				if (tick-- <= 0) {
					player.teleport(marker);
					GameUtils.showPlayer(player);

					Chat.sendTitle(player, "", "&c&bBOOM!", 0, 20, 10);
					broadcastLittleHelper("Ka-boom! Haha, that was fun...");

					GameUtils.createFakeExplosion(creeper.getLocation(), 6.0, 18.0d, player, EnumDamageCause.CREEPER_EXPLOSION);
					creeper.remove();
					this.cancel();
					return;
				}

				// indicate
				Chat.sendTitle(player, "&aLittle Helper", GameUtils.roundTick(tick), 0, 5, 0);

				creeper.teleport(player);

				// dialog fx
				if (littleHelperFuse / 1.5 == tick) {
					broadcastLittleHelper("Ooh, I'm getting excited!");
				}

				if (littleHelperFuse / 3 == tick) {
					broadcastLittleHelper(String.format("I'm coming for you%s!", getNearestPlayerName(creeper.getLocation(), player)));
				}

				if (tick == 30) {
					broadcastLittleHelper("Last chance to hide!");
					creeper.setExplosionRadius(0);
					creeper.setPowered(true);
				}

				// fx marker
				if ((tick % 10) == 0) {
					PlayerLib.spawnParticle(marker, Particle.NOTE, 1, 0, 0, 0, 0);
				}

				// fx creeper
				if ((tick % 30) == 0) {
					PlayerLib.playSound(creeper.getLocation(), ENTITY_CREEPER_PRIMED, 2.0f);
				}

			}
		}.runTaskTimer(0, 1);

	}

	private String getNearestPlayerName(Location location, Player exclude) {
		final Entity nearest = GameUtils.findNearestEntity(location, 50, exclude, EntityType.PLAYER);
		return nearest == null ? "" : " " + nearest.getName();
	}

	@EventHandler()
	public void handleTntExplode(EntityExplodeEvent ev) {
		// Auto-Generated
		if (ev.getEntity() instanceof TNTPrimed entity) {
			ev.setCancelled(true);
			if (entity.getSource() instanceof Player) {
				GameUtils.createFakeExplosion(entity.getLocation(), 5, 7.5, (Player)entity.getSource(), EnumDamageCause.CREEPER_EXPLOSION);
				ev.setYield(0.0f);
			}
		}
	}

	private void broadcastLittleHelper(String msg) {
		Chat.broadcast("&a&lLittle Helper: &f" + msg);
		PlayerLib.playSound(ENTITY_CREEPER_HURT, 1.0f);
	}

	@Override
	public void onStopOnce() {
		tntPlaced.clear();
	}

	public void blowTnt(Player player, Location loc) {
		if (player.hasCooldown(Material.TNT)) {
			return;
		}

		player.setCooldown(Material.TNT, 10);
		GameUtils.removeHeldItem(player, 1);
		tntPlaced.put(player, tntPlaced.getOrDefault(player, 0) + 1);

		GameUtils.createFakeExplosion(loc, 4, tntRawDamage, player, EnumDamageCause.CREEPER_EXPLOSION);

		//loc.getWorld().createExplosion(loc.add(0, 1, 0), 3, false, false);
		checkTnt(player);
	}

	public void checkTnt(Player player) {
		if (tntPlaced.getOrDefault(player, 0) >= 16) {
			tntPlaced.put(player, 0);
			AbilitiesCooldown.add(player, "Restocking TNT...", "sss.tnt", 100, g -> {
				g.sendMessage(ChatColor.GREEN + "TNT has been restocked!");
				g.getInventory().addItem(this.getClassEquipment().getItem(0));
			});
		}
	}
}
