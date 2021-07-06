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

package ru.hapyl.classesfight.experience;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.Main;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.ExperienceEntry;
import ru.hapyl.classesfight.experience.reward.LazyReward;
import ru.hapyl.classesfight.experience.reward.RareReward;
import ru.hapyl.classesfight.experience.reward.Reward;
import ru.hapyl.classesfight.experience.reward.RewardType;
import ru.hapyl.classesfight.feature.perk.Perk;
import ru.hapyl.classesfight.utils.CenterChat;
import ru.hapyl.classesfight.utils.ChainSound;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Experience {

	/**
	 * Experience should make game more fun and allow for progression.
	 * Though don't wanna make it so lvl 100 kills lvl 1 in 1 hit.
	 */

	private static final Map<Integer, Reward> levelRewards = new HashMap<>();
	private static final Map<Integer, Long> expPerLevel = new HashMap<>();

	private static final Experience classInstance = new Experience();

	private static final long firstExpGoal = 50;

	private static final ChainSound LEVEL_UP_SOUND
			= new ChainSound(Main.getMain())
			.append(Sound.ENTITY_PLAYER_LEVELUP, 1.25f, 0)
			.append(Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 5)
			.append(Sound.ENTITY_PLAYER_LEVELUP, 0.75f, 4)
			.append(Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 6);

	static {

		// *==* Assign Exp *==*
		// --------This uses some weird formula that I randomly tuned until liked it.
		// Thanks DiDenPro for the better formula!

		long currentExp = firstExpGoal;

		// 50 levels for now
		for (int i = 1; i < 51; i++) {
			if (i > 1) {
				currentExp += currentExp * 1.2;
				if (currentExp % 10 < 5) {
					currentExp += 5 - currentExp % 10;
				}
				else {
					currentExp += 10 - currentExp % 10;
				}
			}
			putExp(currentExp, i);
		}

		// Assign Rewards
		putReward(1, new LazyReward(100, 1));
		putReward(2, new LazyReward(200, 1));
		putReward(3, new LazyReward(300, 1));
		putReward(4, new LazyReward(400, 1));
		putReward(5, new LazyReward(500, 3)
				.setRareReward("Slithering", "You have no idea how fast I am!",
						Perk.SLITHERING::grantPerk, Perk.SLITHERING::revokePerk, RewardType.PERK));

		putReward(6, new LazyReward(1000, 1));
		putReward(7, new LazyReward(1500, 1));
		putReward(8, new LazyReward(2000, 1));
		putReward(9, new LazyReward(2500, 1));
		putReward(10, new LazyReward(3000, 5)
				.setRareReward("Couture", "Stylish is the least thing you can say...", (player) -> {
					EnumEffect.COUTURE_KILL.grantItem(player);
					EnumEffect.COUTURE_DEATH.grantItem(player);
				}, RewardType.COSMETIC)
				.setRevokeAction(player -> {
					EnumEffect.COUTURE_KILL.revokeItem(player);
					EnumEffect.COUTURE_DEATH.revokeItem(player);
				})
				.setPredicate(player -> EnumEffect.COUTURE_KILL.hasItem(player) || EnumEffect.COUTURE_DEATH.hasItem(player))

		);

		putReward(11, new LazyReward(5000, 2));
		putReward(12, new LazyReward(7500, 2));
		putReward(13, new LazyReward(10000, 2));
		putReward(14, new LazyReward(12500, 2));
		putReward(15, new LazyReward(15000, 10)
				.setRareReward("Lucky Seven", "It's not luck, just skill.",
						Perk.LUCKY_SEVEN::grantPerk, Perk.LUCKY_SEVEN::revokePerk, RewardType.PERK));

		putReward(16, new LazyReward(20000, 2));
		putReward(17, new LazyReward(20001, 2));
		putReward(18, new LazyReward(20002, 2));
		putReward(19, new LazyReward(20003, 2));
		putReward(20, new LazyReward(20004, 15)
				.setRareReward("Bed Rocking", "Stronger than you think!",
						EnumEffect.BED_ROCKING::grantItem,
						EnumEffect.BED_ROCKING::revokeItem,
						RewardType.COSMETIC)
				.setPredicate(EnumEffect.BED_ROCKING::hasItem));

		putReward(21, new LazyReward(25000, 3));
		putReward(22, new LazyReward(30000, 4));
		putReward(23, new LazyReward(35000, 5));
		putReward(24, new LazyReward(40000, 6));
		putReward(25, new LazyReward(45000, 25)
				.setRareReward("Ultimate Trainings", "If you work hard enough everything is possible...",
						Perk.ULTIMATE_MADNESS::grantPerk, Perk.ULTIMATE_MADNESS::revokePerk, RewardType.PERK));
	}

	public void rewardPlayer(Player player, boolean checkBooster, RewardReason reason) {
		final long amount = reason.getExpAmount();
		final long finalAmount = checkBooster ? (GameManager.current().hasDrunkBooster(player) ? amount * 2 : amount) : amount;
		grantExp(player, finalAmount);
		Chat.sendMessage(player, "&2&lEXPERIENCE! &a+%s Exp %s&7(%s)",
				finalAmount, (GameManager.current().hasDrunkBooster(player) ? "&e&lBOOSTED " : ""), reason.getReason());
		OldPlayerLib.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f);
	}

	public void grantExp(Player player, long exp) {

		Database.getDatabase(player).getExperienceEntry().addExp(exp);

		// Level up if we can
		if (canLevelUp(player)) {
			levelUp(player);
		}

	}

	/**
	 * If admin level up or later added reward
	 */
	public void fixRewards(Player player) {
		for (int i = 5; i < 50; i += 5) {
			// check only special rewards
			final Reward reward = getReward(i);
			if (reward != null) {
				if (getLvl(player) >= i) {
					if (!reward.getRareReward().predicate(player)) {
						reward.getRareReward().grantReward(player);
					}
				}
				else {
					reward.getRareReward().revokeReward(player);
				}
			}
		}
	}

	public boolean canLevelUp(Player player) {
		final ExperienceEntry entry = Database.getDatabase(player).getExperienceEntry();
		final int currentLvl = entry.getLvl();
		final long currentExp = entry.getExp();
		return currentExp >= getExpNeeded(currentLvl + 1);
	}

	public void levelUp(Player player) {

		LEVEL_UP_SOUND.clearListeners();
		LEVEL_UP_SOUND.addListener(player);

		final ExperienceEntry entry = Database.getDatabase(player).getExperienceEntry();

		final int currentLvl = entry.getLvl();
		final int nextLvl = currentLvl + 1;
		entry.setLvl(nextLvl);

		final String strikeThrough = "&2&m[                                                                            ]";
		Chat.sendMessage(player, strikeThrough);
		Chat.sendMessage(player, "");

		CenterChat.sendCenteredMessage(player, "&6&lGLOBAL LEVEL UP!");
		CenterChat.sendCenteredMessage(player, String.format("&8[&l%s&8] &f➠ &a[&l%s&a]", currentLvl, nextLvl));

		final Reward reward = getReward(nextLvl);
		if (reward != null) {
			CenterChat.sendCenteredMessage(player, "&f&lRewards");

			final StringBuilder builder = new StringBuilder();

			builder.append(String.format("&7%s Coins", reward.coinsReward()));

			Database.getDatabase(player).getCoinsEntry().addCoins(reward.coinsReward());

			final long rubyAmount = reward.rubiesReward();
			if (rubyAmount > 0) {
				builder.append(String.format(", %s %s", rubyAmount, rubyAmount == 1 ? "Ruby" : "Rubies"));
				Database.getDatabase(player).getRubyEntry().addRubies(rubyAmount);
			}

			// class rewards
			final List<ClassManager> classesAtLvl = ClassManager.getClassesAtLvl(nextLvl);
			if (!classesAtLvl.isEmpty()) {
				CenterChat.sendCenteredMessage(player, getUnlockedClassesPronoun(classesAtLvl));
			}

			//Combine coins and rubies
			CenterChat.sendCenteredMessage(player, builder.toString());

			final RareReward rareReward = reward.getRareReward();
			if (rareReward != null) {
				CenterChat.sendCenteredMessage(player, String.format("&6&lRARE REWARD! &e%s", rareReward.getName()));
				rareReward.grantReward(player);
			}
		}

		Chat.sendMessage(player, "");
		Chat.sendMessage(player, strikeThrough);
		updatePlayerProgress(player);
		LEVEL_UP_SOUND.play();
	}

	private String getUnlockedClassesPronoun(List<ClassManager> list) {
		final StringBuilder builder = new StringBuilder();
		int index = 0;
		for (ClassManager clazz : list) {
			// add and if it's the last class
			if (index > 0 && index == list.size() - 1) {
				builder.append(ChatColor.GRAY).append(" and ");
			}
			// add comma if there is mo classes
			else if (index > 0 && index < list.size() - 1) {
				builder.append(ChatColor.GRAY).append(", ");
			}
			builder.append(ChatColor.GRAY).append(clazz.getDisplayName(ChatColor.GRAY));
			index++;
		}
		return builder.append(list.size() == 1 ? " Class" : " Classes").append(" Access").toString();
	}

	public void updatePlayerProgress(Player player) {
		final Experience experience = Experience.getInstance();
		final int playerLvl = getLvl(player);
		final int nextLvl = playerLvl + 1;
		final long totalExpForNextLvlScaled = experience.getExpNeededFor(nextLvl) - experience.getExpNeededFor(nextLvl - 1);
		final long currentExpScaled = experience.getExp(player) - experience.getExpNeededFor(nextLvl - 1);

		final float percentDone = Math.max(Math.min((float)(currentExpScaled * 100 / totalExpForNextLvlScaled * 0.010), 1.0f), 0.0f);

		player.setLevel(playerLvl);
		player.setExp(percentDone);
	}

	private long getExpNeeded(int lvl) {
		// long.MAX_VALUE so we can't level up after the limit
		return expPerLevel.getOrDefault(lvl, Long.MAX_VALUE);
	}

	private boolean isBetween(int i, int a, int b) {
		return i >= a && i < b;
	}

	private static void putExp(long exp, int lvl) {
		expPerLevel.put(lvl, exp);
	}

	private static void putReward(int lvl, Reward reward) {
		levelRewards.put(lvl, reward);
	}

	public long getExpNeededFor(int lvl) {
		return expPerLevel.getOrDefault(lvl, 0L);
	}

	public Reward getReward(int lvl) {
		return levelRewards.getOrDefault(lvl, null);
	}

	public long getExp(Player player) {
		return Database.getDatabase(player).getExperienceEntry().getExp();
	}

	public int getLvl(Player player) {
		return Database.getDatabase(player).getExperienceEntry().getLvl();
	}

	public static Experience getInstance() {
		return classInstance;
	}

	public long getHowMuchNeedeForLevelUp(Player player) {
		return getExpNeeded(getLvl(player) + 1) - getExp(player);
	}

	public void setLvl(Player target, int amount) {
		Database.getDatabase(target).getExperienceEntry().setLvl(amount);
	}

	public void setExp(Player target, long expNeededFor) {
		Database.getDatabase(target).getExperienceEntry().setExp(expNeededFor);
	}

}
