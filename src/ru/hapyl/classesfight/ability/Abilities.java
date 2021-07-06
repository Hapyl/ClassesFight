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

package ru.hapyl.classesfight.ability;

import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.ability.storage.*;
import ru.hapyl.classesfight.classes.ClassEquipment;

public enum Abilities {

	// Arch-vile
	FIRE_BALL(new Fireball()),
	HOMING_WATER_BALL(new HomingBalloon()),

	// Moonwalker
	MOONWALKER_WEAPON(new MoonWalkerWeapon()),
	MOON_PILLAR(new MoonPillar()),

	// Pyratia
	FLOWER_ESCAPE(new FlowerEscape()),
	FLOWER_BREEZE(new FlowerBreeze()),

	// The Witcher
	AARD(new Aard()),
	IGNY(new Igny()),
	KVEN(new Kven()),
	AKCIY(new Akciy()),
	IRDEN(new Irden()),

	// Dr. Ed
	GRAVITY_GUN(new GravityGun()),
	UPGRADED_GRAVITY_GUN(new PhysGun()),
	POTION_BLINDNESS(new BlindnessPotion()),

	// Hunter
	SHOCK_ARROW(new ShockArrow()),
	TRIPLE_ARROW(new TripleArrow()),

	// Shark
	SUBMERGE(new Submerge()),

	// Techno
	TRAP_CAGE(new TrapCage()),
	TRAP_TRIPWIRE(new TrapTripwire()),
	TRAP_REFILL(new TrapRefill()),

	// Angels and Demons
	SPAWN_DEMON(new DemonFriend()),
	SPAWN_ANGEL(new AngelFriend()),

	// Vampire
	BATS_PACK(new BatsPack()),

	// Ninja
	SMOKE_BOMB(new SmokeBomb()),

	// Taker
	BONE_TRAP(new BoneTrap()),
	MIRROR_OR_DEATH(new DeathMirror()),
	DEATH_SWAP(new DeathSwap()),

	// Starter Class
	STARTED_WEAPON(new StartedWeapon()),
	MELEE_STANCE(new MeleeStance()),

	// Librarian
	BLACK_HOLE(new BlackHole()),
	ENTITY_DARKNESS(new Darkness()),
	LIBRARIAN_SHIELD(new LibrarianShield()),
	WEAPON_DARKNESS(new WeaponDarkness()),

	// HERCULES
	HERCULES_SHIFT(new HerculesShift()),

	// Princess
	PRINCESS_FLASH(new PrincessFlash()),
	PATH_WRITTER(new PrincessPath()),

	// Hocus
	DECOY(new Decoy()),
	DECOY_WALL(new DecoyWall()),

	// Wind Warrior
	WIND_STRIDER(new WindStrider()),

	// Juju
	ARROW_SHIELD(new ArrowShield()),
	CLIMB(new Climb()),

	// War Machine
	LASER_EYE(new LaserEye()),
	SHELL_GRENADE(new Grenade()),

	;

	private final Ability ability;

	Abilities(Ability ability) {
		this.ability = ability;
		if (ability instanceof Listener) {
			Bukkit.getPluginManager().registerEvents((Listener)ability, ClassesFight.getPlugin());
		}
	}

	public static void resetAll() {
		for (Abilities value : values()) {
			if (value.ability != null) {
				value.ability.onStop();
			}
		}
	}

	public void useAbility(Player player) {
		final Ability ability = this.getAbility();
		final Response response = ability.useAbility(player);

		if (response.isOk()) {
			ability.sendCastMessage(player);
		}
	}

	private boolean hasCooldown(Player player) {
		if (!this.getAbility().hasItem()) {
			return false;
		}
		return player.hasCooldown(this.getAbility().getItem().getType());
	}

	public int getCooldown() {
		return this.getAbility().getCooldown();
	}

	public String getName() {
		return this.getAbility().getName();
	}

	public String getAbout() {
		final Ability ability = this.getAbility();
		if (ability == null) {
			return "__&cInvalid Ability (null)";
		}

		final String cooldown = String.format(" &b(&b%s&bs &bcd&b)", ability.getCooldown() % 20 == 0 ?
				"" + (ability.getCooldown() / 20) :
				BukkitUtils.decimalFormat((ability.getCooldown() / 20f)));
		return "__&e○ " + ability.getName() + " &7" + ability.getAbout() + ((ability.getCooldown() == 0
				|| !ability.isShowCooldown()) ? "" : cooldown) + "__";
	}

	public Ability getAbility() {
		return ability;
	}

	public void addItemIfExists(Player player) {
		final Ability ability = this.getAbility();
		if (!ability.hasItem()) {
			return;
		}
		final AbilityItem item = ability.getItem();
		final ItemStack cloned = item.getItem().clone();
		cloned.setAmount(item.getStartAmount());
		player.getInventory().addItem(cloned);
	}

	public void addItemIfExists(ClassEquipment equipment) {
		if (equipment == null) {
			throw new NullPointerException("class equipment cannot be null");
		}
		final Ability ability = this.getAbility();
		if (!ability.hasItem()) {
			return;
		}
		final AbilityItem item = ability.getItem();
		final ItemStack cloned = item.getItem().clone();
		cloned.setAmount(item.getStartAmount());
		equipment.addItem(cloned);
	}

	// Shortcuts
	public void onDeath(Player player) {
		this.getAbility().onDeath(player);
	}

}
