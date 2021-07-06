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

package ru.hapyl.classesfight.utils.pn;

import com.google.common.collect.Maps;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassManager;

import java.util.HashMap;
import java.util.Map;

public enum PatchNotes {

	BETA_2_6(2.6);

	static {

		BETA_2_6.appendSystemUpdate()
				.addChange("Actionbar is now smart! (Kinda). It will not display messages and cooldowns only when they're needed! (Not all)")
				.addBugFix("Fixed some items not having &6&lCLICK &7suffixes.");

		BETA_2_6.appendClassChange(ClassManager.SWOOPER)
				.addBuff("Ultimate charge time increased to &b10s&7. Your cooldowns will also be reduced by two while in ultimate.")
				.addBugFix("Fixed satchel blowing and sticking to the ground at the same time causing having double the charge.")
				.addBugFix("Fixed satchel not properly displaying explosion location.");

		BETA_2_6.appendClassChange(ClassManager.MOONWALKER)
				.addNerf("Reduced Corrosion time %ss -> %ss.", colorInt(150 / 20), colorInt(130 / 20))
				.addNerf("Added a short delay before summoning Moonteorite.")
				.addChange(nameOf(Abilities.MOON_PILLAR) + " healing and pulses reduced to 13, but it pulses faster (1s -> 0.75s) and knock back enemies further.");

		BETA_2_6.appendClassChange(ClassManager.LIBRARIAN)
				.addNerf("Reduced " + nameOf(Abilities.BLACK_HOLE) + " damage over time.");

		final String shockName = nameOf(Abilities.SHOCK_ARROW);
		final String tripleName = nameOf(Abilities.TRIPLE_ARROW);

		BETA_2_6.appendClassChange(ClassManager.HUNTER)
				.addBuff("Hawkeye Arrow chance increased %s%% -> %s%%.", colorInt(10), colorInt(25))
				.addNerf("Increased %s cooldown to %s.", shockName, cdOf(Abilities.SHOCK_ARROW))
				.addNerf("Increased %s cooldown to %s.", tripleName, cdOf(Abilities.TRIPLE_ARROW));

		BETA_2_6.appendClassChange(ClassManager.DARK_MAGE)
				.addChange("Upon using ultimate, the wither will now spawn with health that player had and will be damageable. Killing the wither will kick Dark Mage from the ultimate.");

		BETA_2_6.appendClassChange(ClassManager.ZEALOT)
				.addNerf("Reduced sword damage from %s -> %s.", colorInt(7.0), colorInt(4.5));

		BETA_2_6.appendClassChange(ClassManager.STAR)
				.addChange("Your placed stars are now highlighted.")
				.addBugFix("Fixed star visual item not showing when the game starts.")
				.addChange("The final slash of the ultimate is now instant.");

		BETA_2_6.appendClassChange(ClassManager.ENDER)
				.addBuff("Ender will now have Strength buff.");

		BETA_2_6.appendClassChange(ClassManager.BLAST_KNIGHT)
				.addNerf("Increased ultimate points %s -> %s, and reduced damage multiplier while riding horse %s -> %s.", colorInt(4), colorInt(6), colorInt(2.0), colorInt(1.5));

		BETA_2_6.appendClassChange(ClassManager.PRINCESS)
				.addBuff("Increased radius and damage for Starfall. Also added radius indicator on explode.");

	}

	private static String nameOf(Abilities ability) {
		return "&e" + ability.getName() + "&7";
	}

	private static String colorInt(Number n) {
		return "&b" + n + "&7";
	}

	private static String cdOf(int tick) {
		return "&b" + BukkitUtils.roundTick(tick) + "&bs&7";
	}

	private static String cdOf(Abilities abilities) {
		return cdOf(abilities.getCooldown());
	}

	public static final Map<Double, PatchNotes> byVersion = new HashMap<>();

	static {
		for (final PatchNotes value : values()) {
			byVersion.put(value.getVersion(), value);
		}
	}

	private final double version;
	private final SystemUpdatePatch systemPatches;
	private final Map<ClassManager, ClassUpdatePatch> classPatches;

	PatchNotes(double version) {
		this.version = version;
		this.systemPatches = new SystemUpdatePatch();
		this.classPatches = Maps.newLinkedHashMap();
	}

	public double getVersion() {
		return version;
	}

	public static PatchNotes getByVersion(double ver) {
		return byVersion.getOrDefault(ver, null);
	}

	public ClassUpdatePatch appendClassChange(ClassManager clazz) {
		return getOrNew(clazz);
	}

	public SystemUpdatePatch appendSystemUpdate() {
		return this.systemPatches;
	}

	private ClassUpdatePatch getOrNew(ClassManager clazz) {
		ClassUpdatePatch classUpdatePatch = classPatches.get(clazz);
		if (classUpdatePatch == null) {
			classUpdatePatch = new ClassUpdatePatch(clazz);
			classPatches.put(clazz, classUpdatePatch);
		}
		return classUpdatePatch;
	}

	public Map<ClassManager, ClassUpdatePatch> getClassPatches() {
		return classPatches;
	}

	public SystemUpdatePatch getSystemPatches() {
		return systemPatches;
	}

	public String formatChanges(Type type) {
		return "not yet implemented";
	}

}
