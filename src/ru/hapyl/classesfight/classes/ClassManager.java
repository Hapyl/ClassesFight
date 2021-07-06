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

package ru.hapyl.classesfight.classes;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.iclass.*;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.stats.StatsContainer;
import ru.hapyl.classesfight.utils.ChainSound;

import javax.annotation.Nullable;
import java.util.*;

public enum ClassManager {

	/**
	 * This class is basically a test for a better class system.
	 */

	/*
	 * Ultimate Info => 1 point - 10 seconds
	 */

	STARTER(new StartedClass()),
	ALCHEMIST(new AlchemistClass()),
	HUNTER(new ArcherClass(), ClassStatus.UPDATED),
	BLAST_KNIGHT(new BlastKnightClass(), ClassStatus.UPDATED),
	CREEPER(new CreeperClass(), ClassStatus.UPDATED_ULTIMATE),
	DARK_MAGE(new DarkMageClass(), ClassStatus.REWORKED),
	NIGHTMARE(new BooClass()),
	ENDER(new EnderClass()),
	// ON REWORK -> HEALER(new HealerClass()),
	MAGE(new MageClass()),
	NINJA(new NinjaClass(), ClassStatus.UPDATED),
	SCOUT(new ScoutClass()),
	SHARK(new SharkClass(), ClassStatus.UPDATED),
	TAKER(new TakerClass(), ClassStatus.REWORKED),
	TROLL(new TrollClass()),
	TAMER(new TamerClass(), ClassStatus.UPDATED),
	ZEALOT(new ZealotClass()),
	TECHNO(new TechnoClass(), ClassStatus.UPDATED),
	// ON REWORK -> VAMPIRE(new VampireClass(), ClassStatus.UPDATED),

	FIRE_GUY(new FireGuy()),
	FROZEN_GUY(new FrozenGuy()),
	HERCULES(new Hercules(), ClassStatus.REWORKED),
	SHADOW_WARRIOR(new ShadowWarrior()),
	RPS(new RockPaperClass()),

	SWOOPER(new SwooperClass(), ClassStatus.UPDATED_ULTIMATE),
	STAR(new StarClass()),
	PYRO(new Pyrotand()),
	MOONWALKER(new Moonwalker()),

	PYRATIA(new Pytaria()),
	WITCHER(new TheWitcher()),

	GRAVITY(new GravityClass()),
	LIBRARIAN(new Librarian()),
	PRINCESS(new Princess(), ClassStatus.NEW),
	HOCUS(new HocusClass(), ClassStatus.NEW),
	WIND_WARRIOR(new WindWarrior(), ClassStatus.NEW),
	JUJU(new JujuArcher(), ClassStatus.NEW),
	WAR_MACHINE(new KillingMachine(), ClassStatus.NEW),

	//NEBULA(new Nebula(), ClassStatus.NEW)
	//AAD(new AngelAndDemon()),

	;

	public static final String theFormat = ChatColor.GREEN + ChatColor.BOLD.toString() + "♦ " + ChatColor.DARK_GREEN;
	private static final Map<UUID, ClassManager> classSelected = new HashMap<>();

	private final IClass theClass;
	private final CachedClassInfo cachedInfo;

	ClassManager(IClass theClass) {
		this(theClass, theClass.getStatus());
	}

	ClassManager(IClass theClass, ClassStatus overrideStatus) {
		this.theClass = theClass;
		this.theClass.setStatus(overrideStatus);
		this.theClass.setDevName(this.name().toLowerCase(Locale.ROOT));
		this.cachedInfo = new CachedClassInfo(theClass);
		if (theClass instanceof Listener) {
			ClassesFight.getPlugin().getServer().getPluginManager().registerEvents((Listener)theClass, ClassesFight.getPlugin());
		}
	}

	public CachedClassInfo getCachedInfo() {
		return cachedInfo;
	}

	public static List<ClassManager> getByRole(IClass.ClassRole role) {
		final List<ClassManager> hash = new ArrayList<>();
		for (ClassManager value : values()) {
			if (value.getTheClass().getRole() == role)
				hash.add(value);
		}
		return hash;
	}

	public static List<ClassManager> getClassesAtLvlAndBelow(int lvl) {
		final List<ClassManager> validClasses = new ArrayList<>();

		for (ClassManager theClass : getValidClasses()) {
			if (theClass.getTheClass().getLvlRequired() <= lvl) {
				validClasses.add(theClass);
			}
		}

		return validClasses;
	}

	public static List<ClassManager> getClassesAtLvl(int lvl) {
		final List<ClassManager> validClasses = new ArrayList<>();

		for (ClassManager theClass : getValidClasses()) {
			if (theClass.getTheClass().getLvlRequired() == lvl) {
				validClasses.add(theClass);
			}
		}

		return validClasses;
	}


	public static ClassManager getRandomValidClass(Player player) {
		final List<ClassManager> valid
				= getClassesAtLvlAndBelow(Database.getDatabase(player).getExperienceEntry().getLvl());
		return valid.get(new Random().nextInt(valid.size()));
	}

	public ClassUltimate getUltimate() {
		return this.theClass.getUltimate();
	}

	public boolean predicateUltimate(Player player) {
		return theClass.predicateUltimate(player);
	}

	private int indexOf() {
		return this.ordinal();
	}

	public void useUltimate(Player a) {
		this.theClass.useUltimate(a);
	}

	public IClass getTheClass() {
		return this.theClass;
	}

	public void setStat(StatsContainer.ClassStat stat, long value) {
		StatsContainer.current().setClassStat(this, stat, value);
	}

	public void addStat(StatsContainer.ClassStat stat, long value) {
		StatsContainer.current().addClassStat(this, stat, value);
	}

	public long getStat(StatsContainer.ClassStat stat, long def) {
		return StatsContainer.current().getClassStat(this, stat, def);
	}

	public static List<Player> getPlayersInClass(ClassManager clazz) {
		final List<Player> temp = new ArrayList<>();
		Bukkit.getOnlinePlayers().iterator().forEachRemaining(player -> {
			if (getClass(player) == clazz)
				temp.add(player);
		});
		return temp;
	}

	public String getDisplayName() {
		return ChatColor.GOLD + this.theClass.getClassName();
	}

	public String getDisplayName(ChatColor... comps) {
		String str = "";
		for (ChatColor color : comps) {
			str = str.concat(color.toString());
		}
		return str.concat(this.theClass.getClassName());
	}

	/////

	public static List<ClassManager> getValidClasses() {
		return new ArrayList<>(Arrays.asList(values()));
	}

	public static void setClass(Player player, ClassManager clazz, boolean sendMessage) {
		classSelected.put(player.getUniqueId(), clazz);
		// Also store the class since I changed the system
		Database.getDatabase(player).getClassEntry().setClass(clazz);
		if (sendMessage) {
			Chat.sendMessage(player, theFormat + "Selected &6%s &2class!", clazz.getDisplayName());
			new ChainSound(ClassesFight.getPlugin()).append(Sound.BLOCK_NOTE_BLOCK_PLING, 1.2f, 0)
					.append(Sound.BLOCK_NOTE_BLOCK_PLING, 1.35f, 2)
					.addListener(player)
					.play();
		}
	}

	public static boolean resetClass(Player player) {
		boolean b = classSelected.containsKey(player.getUniqueId());
		classSelected.remove(player.getUniqueId());
		return b;
	}

	public static ClassManager getClass(@Nullable Player player) {
		if (player == null) {
			return STARTER;
		}
		return classSelected.getOrDefault(player.getUniqueId(), STARTER);
	}

	public static void executeRevealedScriptForEach() {
		ClassUltimate.onPlayersRevealed();
		GameManager.current().forEachInGamePlayer(player -> {
			final ClassManager cz = getClass(player);
			cz.theClass.onPlayerRevealed(player);
		});
	}

	public static void executeStartScriptForEach() {

		Bukkit.getOnlinePlayers().iterator().forEachRemaining(player -> {
			final ClassManager cz = getClass(player);
			final ClassEquipment equipment = cz.theClass.getClassEquipment();
			if (equipment.isAutoEquipOnStart()) {
				equipment.equip(player);
			}
			cz.theClass.onStart(player);
		});

		for (ClassManager value : values()) {
			if (value.theClass != null)
				value.theClass.onStartOnce();
		}

	}

	public static void executeStopScriptForEach() {

		Bukkit.getOnlinePlayers().iterator().forEachRemaining(player -> {
			final ClassManager cz = getClass(player);
			cz.theClass.onStop(player);
		});

		for (ClassManager value : values()) {
			// could be null for testing classes
			if (value.theClass != null) {
				value.theClass.onStopOnce();
				value.theClass.clearUltimate();
			}
		}

	}

	public boolean isUnlocked(Player player) {
		return Experience.getInstance().getLvl(player) >= this.getTheClass().getLvlRequired();
	}
}
