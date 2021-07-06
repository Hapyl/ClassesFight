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
import kz.hapyl.spigotutils.module.particle.AbstractParticleBuilder;
import kz.hapyl.spigotutils.module.particle.ParticleBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.reflect.Reflect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GeometryLib;
import ru.hapyl.classesfight.utils.SoundLib;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public class TechnoClass extends IClass implements Listener {

	private final Set<Entity> lockdowns = new HashSet<>();
	private final int LOCKDOWN_RADIUS = 20; // 20
	private final int LOCKDOWN_WINDUP_TIME = 10000; // in millis
	private final int LOCKDOWN_AFFECT_TIME = 170; // in ticks

	public TechnoClass() {
		super("Techie", Material.IRON_TRAPDOOR);

		this.setRating(ClassRating.A);
		this.setAttack(EnumInfo.LOW);
		this.setRole(ClassRole.STRATEGIST);
		this.setInfo("Anonymous hacker, who hacked his way to the fight. Weak by himself, but specifies on traps that makes him stronger.",
				"&e○ Neural Theft &e&lPASSIVE &7Every &b10s &7reveals all enemies locations.__" + Abilities.TRAP_CAGE.getAbout()
						+ Abilities.TRAP_TRIPWIRE.getAbout()
						+ Abilities.TRAP_REFILL.getAbout(),
				"Lockdown",
				"Place a device that charges over &b" + (this.LOCKDOWN_WINDUP_TIME / 1000) + "&bs&7. When charged, blows and affects all enemies in " + this.LOCKDOWN_RADIUS + " &7blocks radius for &b" + (this.LOCKDOWN_AFFECT_TIME / 20) + "&b&7. Players affected by &bLockdown&7 can't move nor use abilities.", 5);
		this.setUltimateSound(Sound.BLOCK_BELL_USE, 0.0f);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU1Yjc4OTg3YzcwZDczZjJhZDkzYTQ1NGY4NWRjYWI0NzZjNWI1Njc5ZjUwZWFhZjU1M2QyNDA0ZWRjOWMifX19");
		eq.setChestplate(205, 205, 205);
		eq.setLeggings(217, 217, 217);
		eq.setBoots(255, 230, 204);

		eq.addItem(new CFItemBuilder(Material.IRON_SWORD).setPureDamage(3.0)
				.addEnchant(Enchantment.KNOCKBACK, 1)
				.setName("&bNano Sword")
				.applyDefaultSettings()
				.build());

		Abilities.TRAP_CAGE.addItemIfExists(eq);
		Abilities.TRAP_TRIPWIRE.addItemIfExists(eq);
		Abilities.TRAP_REFILL.addItemIfExists(eq);

	}

	@Override
	public void onDeath(Player player) {
		Abilities.TRAP_CAGE.onDeath(player);
		Abilities.TRAP_TRIPWIRE.onDeath(player);
		Abilities.TRAP_REFILL.onDeath(player);
	}

	@Override
	public void useUltimate(Player player) {
		placeLockdown(player, player.getLocation().getBlock());
	}

	@Override
	public void onStart(Player player) {
		setItemAmount(player, 1, (byte)3);
		setItemAmount(player, 2, (byte)2);
	}

	private void setItemAmount(Player player, int slot, byte amount) {
		final ItemStack item = player.getInventory().getItem(slot);
		if (item != null) {
			item.setAmount(amount);
		}
	}

	@Override
	public void onStartOnce() {
		new GameTask() {
			@Override
			public void run() {
				GameManager.current().getPlayers().iterator().forEachRemaining(player -> {
					if (ClassManager.getClass(player) == ClassManager.TECHNO)
						revealPlayers(player);
				});
			}
		}.runTaskTimer(200, 200);

	}

	@Override
	public void onStopOnce() {
		lockdowns.clear();
	}

	private void revealPlayers(Player player) {
		if (GameManager.current().getPlayers().size() <= 1) {
			return;
		}

		int revealed = 0;
		for (Player online : GameManager.current().getPlayers()) {
			if (online != player && (online.getLocation().distance(player.getLocation()) >= 10.0d || !player.hasLineOfSight(online))) {
				glowEntitySilhouette(online, player, 20);
				++revealed;
			}
		}

		if (revealed > 0) {
			Chat.sendActionbar(player, "&f%s Players Revealed", revealed);
			PlayerLib.playSound(player, Sound.ENTITY_BEE_DEATH, 2.0f);
		}
	}

	private final String empty = "                          ";

	private ArmorStand createArmorStand(Location loc, Consumer<ArmorStand> pre) {
		final ArmorStand stand = GarbageCollector.spawnEntity(loc, ArmorStand.class, me -> {
			me.setMarker(true);
			me.setInvulnerable(true);
			me.setVisible(false);
		});
		pre.accept(stand);
		return stand;
	}

	private void placeLockdown(Player player, Block block) {

		Location location = block.getRelative(BlockFace.UP).getLocation().add(0.5, -2.4, 0.5);

		ArmorStand stand = createArmorStand(location, me -> {
			me.getEquipment().setHelmet(new ItemStack(Material.DAYLIGHT_DETECTOR));
			me.setCustomName(Chat.format("&a&l"));
			me.setCustomNameVisible(true);
			me.addScoreboardTag(player.getUniqueId().toString());
		});

		GarbageCollector.add(stand);
		lockdowns.add(stand);
		player.sendMessage(Chat.format("&aCountdown Initiated!"));

		final long startedAt = System.currentTimeMillis();
		final int lockDownWindupInTicks = (LOCKDOWN_WINDUP_TIME / 1000) * 20;

		final AbstractParticleBuilder particleSelf = ParticleBuilder.redstoneDust(Color.fromRGB(57, 123, 189))
				.setAmount(2)
				.setOffX(0.2d)
				.setOffZ(0.2d)
				.setSpeed(10);
		final AbstractParticleBuilder particleOther = ParticleBuilder.redstoneDust(Color.fromRGB(255, 51, 51))
				.setAmount(2)
				.setOffX(0.2d)
				.setOffZ(0.2d)
				.setSpeed(10);
		final int delayBetweenDraw = 15;

		new GameTask() {

			private int tick = lockDownWindupInTicks;

			@Override
			public void run() {

				if (tick-- <= 0) {
					lockdownAffect(player, stand);
					this.cancel();
					return;
				}

				// fx
				if ((tick % delayBetweenDraw) == 0) {
					playersInRangeOf(location, LOCKDOWN_RADIUS).forEachRemaining(player -> {
						SoundLib.play(player, Sound.BLOCK_BEACON_AMBIENT, 2.0f);
						Chat.sendActionbar(player, "&4&lDANGER! &eYou're in Lockdown range!");
					});
					GeometryLib.drawSphere(location, LOCKDOWN_RADIUS * 1.5, LOCKDOWN_RADIUS, loc -> {
						for (Player online : Bukkit.getOnlinePlayers()) {
							if (online == player) {
								particleSelf.display(loc, player);
							}
							else {
								particleOther.display(loc, online);
							}
						}
					}, true);
				}

				// countdown
				long timeLeft = ((startedAt + LOCKDOWN_WINDUP_TIME) - System.currentTimeMillis()) / 10;
				GameManager.current().getPlayers().iterator().forEachRemaining(gamer -> {

					String timeLeftFormat = new DecimalFormat("##,##").format(timeLeft);

					if (gamer == player) {
						gamer.sendTitle(" ", Chat.format("&bYour Lockdown &l" + timeLeftFormat + empty), 0, 20, 0);
					}
					else {
						gamer.sendTitle(" ", Chat.format(empty + "&cEnemy Lockdown &l" + timeLeftFormat), 0, 20, 0);
					}

				});
			}
		}.runTaskTimer(0, 1);

	}

	private void lockdownAffect(Player owner, Entity entity) {

		final Location location = entity.getLocation();
		final Set<Player> affectedPlayers = new HashSet<>();

		entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_TOTEM_USE, SoundCategory.MASTER, 100, 2);

		playersInRangeOf(location, LOCKDOWN_RADIUS).forEachRemaining(player -> {
			if (player != owner) {
				affectedPlayers.add(player);
				GameEffectManager.applyEffect(player, GameEffectType.LOCK_DOWN, LOCKDOWN_AFFECT_TIME);
			}
		});

		GameManager.current().forEachInGamePlayer(player -> {
			final String empty = "                    ";
			if (player == owner) {
				player.sendTitle("", Chat.format("&bLockdown affected &l" + affectedPlayers.size() + " &bplayers." + empty), 10, 20, 10);
			}
			else {
				player.sendTitle("", Chat.format(empty + "&cLockdown affected &l" + affectedPlayers.size() + " &cplayers."), 10, 20, 10);
			}
		});

		// check if all enemies affected
		if ((affectedPlayers.size() - 1) == GameManager.current().getPlayers().size()) {
			owner.sendMessage(Chat.format("&aLockdown affected all enemies!"));
		}

		lockdowns.remove(entity);
		entity.remove();

	}

	private Iterator<Player> playersInRangeOf(Location of, double range) {
		Set<Player> set = new HashSet<>();
		of.getWorld().getNearbyEntities(of, range, range, range).iterator().forEachRemaining(e -> {
			if (e instanceof Player && !Spectator.isSpectator((Player)e)) {
				set.add((Player)e);
			}
		});
		return set.iterator();
	}

	// Temporary solution since packet glowing broke :{
	public void glowEntitySilhouette(Entity who, Player viewer, int delay) {
		final Skeleton entity = GarbageCollector.spawnEntity(who.getLocation(), Skeleton.class, me -> {
			me.setInvisible(true);
			me.setFireTicks(-1000);
			setCollision(me, false);
			me.setCustomName("reveal_entity");
			me.setAI(false);
			me.setSilent(true);
			me.setGravity(false);
			me.setInvulnerable(true);
			me.getEquipment().clear();
			me.setGlowing(true);
		});
		Reflect.hideEntity(entity, GameManager.current().getPlayersExcept(viewer));
		if (delay <= 0) {
			delay = 1;
		}
		new GameTask() {
			@Override
			public void run() {
				entity.remove();
			}
		}.runTaskAtCancel().runTaskLater(delay);
	}

	public void setCollision(Entity entity, boolean flag) {
		for (Player viewer : Bukkit.getOnlinePlayers()) {
			Team team = getTeamOrCreate(viewer.getScoreboard());
			team.setOption(Team.Option.COLLISION_RULE, flag ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER);
			team.addEntry(entity.getCustomName() == null ? "reveal_entity" : entity.getCustomName());
		}
	}

	private static Team getTeamOrCreate(Scoreboard scoreboard) {
		if (scoreboard.getTeam("npcNameTag") == null) {
			Team team = scoreboard.registerNewTeam("npcNameTag");
			team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
			return team;
		}
		else {
			return scoreboard.getTeam("npcNameTag");
		}
	}

}
