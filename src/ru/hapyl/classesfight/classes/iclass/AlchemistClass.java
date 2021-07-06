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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.feature.GameTeam;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.Icons;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Sound.ENTITY_WITCH_AMBIENT;
import static org.bukkit.potion.PotionEffectType.*;

public class AlchemistClass extends IClass implements Listener {

	public final int POTION_COOLDOWN = 50;

	private final Map<Player, Integer> toxinLevel;

	// 40 -> 60
	private final ItemStack POTION = new CFItemBuilder(Material.POTION, "alchemist_potion").setName("&3Random Potion &e&lRIGHT CLICK")
			.addClickEvent(this::applyRandomEffect)
			.build();
	private final ItemStack POTION_COOLDOWN_ITEM = new CFItemBuilder(Material.BREWING_STAND).setName("&aBrewing...").build();
	private final ItemStack STICK = new CFItemBuilder(Material.STICK).setName("&6Stick")
			.setPureDamage(2.0)
			.addEnchant(Enchantment.KNOCKBACK, 1)
			.applyDefaultSettings()
			.build();

	public AlchemistClass() {
		super("Alchemist", Material.BREWING_STAND);

		this.setAttack(EnumInfo.LOW);
		this.setDefense(EnumInfo.LOW);
		this.setRole(ClassRole.STRATEGIST);

		this.setInfo("Alchemist is a week warrior by himself, but has a Magical Potion that gives him random effect for a little time." + Icons.ABILITY_DOT + "Intoxication &e&lPASSIVE &7Drinking potions will increase intoxication level that will decrease constantly. Keeping an eye on intoxication level is a good idea, who knows what can happen when it's high...",
				"Alchemical Madness", "&7Gives caster random &a&lPositive &7effect for &b30s&7, and random &c&lNegative &7effect &7for everyone else for &b15s&7.", 5);

		this.setUltimateSound(ENTITY_WITCH_AMBIENT, 0.5f);

		this.setDifficulty(ClassDifficulty.HARD);
		this.setLvlRequired(3);
		this.setRating(ClassRating.SS);

		this.toxinLevel = new HashMap<>();

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setHelmet(Material.IRON_HELMET);
		eq.addItem(STICK);

	}

	public int getToxinLevel(Player player) {
		return toxinLevel.getOrDefault(player, 0);
	}

	public void setToxinLevel(Player player, int value) {
		toxinLevel.put(player, Math.max(0, Math.min(value, 100)));
	}

	public boolean canUsePotion(Player player) {
		return getToxinLevel(player) < 90;
	}

	@Override
	public void onStopOnce() {
		this.toxinLevel.clear();
	}

	@Override
	public void onStartOnce() {
		new GameTask() {
			@Override
			public void run() {
				ClassManager.getPlayersInClass(ClassManager.ALCHEMIST).forEach(player -> {
					if (Spectator.isSpectator(player)) {
						return;
					}

					final int toxin = getToxinLevel(player);
					final int scaledToxin = toxin * 15 / 100;

					ChatColor color = ChatColor.GREEN;

					if (isBetween(toxin, 30, 50)) {
						color = ChatColor.YELLOW;
					}

					if (isBetween(toxin, 50, 75)) {
						color = ChatColor.GOLD;
						player.addPotionEffect(new PotionEffect(POISON, 10 * 20, 2));
					}

					if (isBetween(toxin, 75, 90)) {
						color = ChatColor.RED;
						player.addPotionEffect(new PotionEffect(WITHER, 5 * 20, 2));
					}

					if (isBetween(toxin, 90, 101)) {
						color = ChatColor.DARK_RED;
					}

					if (toxin >= 100) {
						DamageFeature.damage(player, player, DamageFeature.getHealth(player) + 1.0d, EnumDamageCause.TOXIN);
						return;
					}

					final StringBuilder builder = new StringBuilder();
					for (int i = 0; i < 15; i++) {
						builder.append(i < scaledToxin ? color : ChatColor.GRAY).append("⬛");
					}

					Chat.sendActionbar(player, "&eIntoxication &l" + builder);
					setToxinLevel(player, getToxinLevel(player) - 1);

				});
			}
		}.runTaskTimer(0, 10);
	}

	private boolean isBetween(int value, int min, int max) {
		return value >= min && value < max;
	}

	@Override
	public void onStart(Player player) {
		this.toxinLevel.put(player, 0);
	}

	@Override
	public void onPlayerRevealed(Player player) {
		player.getInventory().addItem(POTION);
	}

	@Override
	public void useUltimate(Player player) {
		applyUltimate(player);
	}

	private void applyRandomEffect(Player player) {

		if (!canUsePotion(player)) {
			Chat.sendMessage(player, "&cCannot use potion! Too much Intoxication!");
			return;
		}

		setToxinLevel(player, getToxinLevel(player) + 20);
		int random = (int)(Math.random() * 6);

		player.getInventory().setItem(1, POTION_COOLDOWN_ITEM);
		GameTask.runLater(() -> player.getInventory().setItem(1, POTION), POTION_COOLDOWN);
		switch (random) {
			case 0 -> {
				applyEffect(player, SPEED, 3, 1);
				Chat.sendTitle(player, " ", "&aGained Speed", 5, 15, 5);
			}
			case 1 -> {
				applyEffect(player, PotionEffectType.JUMP, 5, 1);
				Chat.sendTitle(player, " ", "&aGained Jump Boost", 5, 15, 5);
			}
			case 2 -> {
				// 4 -> 3
				applyEffect(player, PotionEffectType.INCREASE_DAMAGE, 3, 2);
				Chat.sendTitle(player, " ", "&aGained Strength", 5, 15, 5);
			}
			case 3 -> {
				GameEffectManager.applyEffect(player, GameEffectType.RESISTANCE, 4 * 20);
				Chat.sendTitle(player, " ", "&aGained Resistance", 5, 15, 5);
			}
			case 4 -> {
				GameEffectManager.applyEffect(player, GameEffectType.INVISIBILITY, 4 * 20);
				applyEffect(player, PotionEffectType.INVISIBILITY, 4, 1);
				Chat.sendTitle(player, " ", "&aGained Invisibility", 5, 15, 5);
			}
			case 5 -> {
				Chat.sendTitle(player, " ", "&aGained Heal", 5, 15, 5);
				//                applyEffect(player, PotionEffectType.HEAL, 1, 1);
				DamageFeature.addHealth(player, 4);
			}
		}

	}

	private void applyEffect(Player player, PotionEffectType type, int sec, int lvl) {
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SWIM, SoundCategory.MASTER, 2, 1.8f);
		player.addPotionEffect(new PotionEffect(type, sec * 20, lvl, true));
	}

	private void applyUltimate(Player player) {

		int randomSelf = (int)(Math.random() * 6);
		int randomOther = (int)(Math.random() * 6);

		switch (randomSelf) {
			case 0 -> {
				applyEffect(player, SPEED, 30, 2);
				player.sendMessage(ChatColor.GREEN + "Alchemical Madness gave you SPEED effect!");
			}
			case 1 -> {
				applyEffect(player, PotionEffectType.JUMP, 30, 1);
				player.sendMessage(ChatColor.GREEN + "Alchemical Madness gave you JUMP BOOST effect!");
			}
			case 2 -> {
				applyEffect(player, PotionEffectType.INCREASE_DAMAGE, 30, 2);
				player.sendMessage(ChatColor.GREEN + "Alchemical Madness gave you STRENGTH effect!");
			}
			case 3 -> {
				GameEffectManager.applyEffect(player, GameEffectType.RESISTANCE, 30 * 20);
				//                applyEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 30, 2);
				player.sendMessage(ChatColor.GREEN + "Alchemical Madness gave you RESISTANCE!");
			}
			case 4 -> {
				GameEffectManager.applyEffect(player, GameEffectType.INVISIBILITY, 30 * 20);
				applyEffect(player, PotionEffectType.INVISIBILITY, 10, 1);
				player.sendMessage(ChatColor.GREEN + "Alchemical Madness gave you INVISIBILITY effect!");
			}
			case 5 -> {
				DamageFeature.setHealth(player, this.getHealth().getValue() / 2);
				//                applyEffect(player, PotionEffectType.HEAL, 1, 10);
				player.sendMessage(ChatColor.GREEN + "Alchemical Madness healed you!");
			}
		}

		switch (randomOther) {
			case 0 -> applyUltimateCurse(player, PotionEffectType.POISON, 15, 0, "POISON");
			case 1 -> applyUltimateCurse(player, PotionEffectType.BLINDNESS, 15, 1, "BLINDNESS");
			case 2 -> applyUltimateCurse(player, PotionEffectType.WITHER, 7, 1, "WITHER");
			case 3 -> applyUltimateCurse(player, PotionEffectType.SLOW, 15, 2, "SLOWNESS");
			case 4 -> applyUltimateCurse(player, PotionEffectType.WEAKNESS, 15, 2, "WEAKNESS");
			case 5 -> applyUltimateCurse(player, PotionEffectType.CONFUSION, 15, 1, "NAUSEA");
		}

	}

	private void applyUltimateCurse(Player player, PotionEffectType type, int sec, int lvl, String s) {
		final String effectName = ChatColor.YELLOW + ChatColor.BOLD.toString() + s + ChatColor.RED;
		player.sendMessage(ChatColor.YELLOW + String.format("Your Alchemical Madness gave everyone %s!", effectName));
		GameManager.current().getPlayers().forEach(thePlayer -> {
			if (GameTeam.isTeammate(player, thePlayer))
				return;
			if (thePlayer != player && !Spectator.isSpectator(thePlayer)) {
				thePlayer.addPotionEffect(new PotionEffect(type, sec * 20, lvl, true));
				thePlayer.sendMessage(ChatColor.RED + player.getName() + "'s Alchemical Madness cursed you with " + effectName + "!");
				thePlayer.playSound(thePlayer.getLocation(), Sound.ENTITY_ENDERMAN_HURT, SoundCategory.MASTER, 2, .2f);
			}
		});
	}

}
