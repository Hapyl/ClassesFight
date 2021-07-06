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

package ru.hapyl.classesfight.cosmetics;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.annotations.Reward;
import ru.hapyl.classesfight.cosmetics.contrail.BedRockingContrail;
import ru.hapyl.classesfight.cosmetics.contrail.MusicContrail;
import ru.hapyl.classesfight.cosmetics.contrail.ObsidianContrail;
import ru.hapyl.classesfight.cosmetics.contrail.RainbowContrail;
import ru.hapyl.classesfight.cosmetics.death.*;
import ru.hapyl.classesfight.cosmetics.kill.*;
import ru.hapyl.classesfight.cosmetics.prefix.Fighter;
import ru.hapyl.classesfight.cosmetics.prefix.Octave;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.CosmeticEntry;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

import java.util.HashSet;
import java.util.Set;

public enum EnumEffect {

	// kills
	SQUID_LAUNCH(Type.KILL, new SquidLaunch()),
	BLOOD(Type.KILL, new Blood()),
	LIGHTNING(Type.KILL, new Lightning()),
	COUTURE_KILL(Type.KILL, new CoutureKill()),
	COOKIE_MADNESS(Type.KILL, new CookiesMadness()),
	GIANT_SWORD(Type.KILL, new GiantSword()),
	GROUND_PUNCH(Type.KILL, new GroundPunch()),

	// death
	LAST_MESSAGE(Type.DEATH, new LastMessage()),
	EXPLOSION(Type.DEATH, new Explosion()),
	COUTURE_DEATH(Type.DEATH, new CoutureDeath()),
	SCARY_POOP(Type.DEATH, new ScaryPoop()),
	STYLISH_DEATH(Type.DEATH, new StylishFall()),
	CAGE(Type.DEATH, new CageBox()),
	ELECTROCUTE(Type.DEATH, new Electrecute()),
	LASER_SHOW(Type.DEATH, new LaserShow()),

	// contrail
	MUSIC(Type.CONTRAIL, new MusicContrail()),
	RAINBOW(Type.CONTRAIL, new RainbowContrail()),
	OBSIDIAN(Type.CONTRAIL, new ObsidianContrail()),

	@Reward
	BED_ROCKING(Type.CONTRAIL, new BedRockingContrail()),

	// prefix
	FIGHTER(Type.PREFIX, new Fighter()),
	OCTAVE(Type.PREFIX, new Octave()),
	STAR(Type.PREFIX, new Prefix("Star", "I'm on a roll!", "&e★&6&lStar&e☆", 1000, Material.GOLD_NUGGET, ShopItemRarity.EPIC)),
	BIOHAZARD(Type.PREFIX, new Prefix("Biohazard", "Put your mask on!", "&a☢&2&lBiohazard&a☣", 1000, Material.SLIME_BALL, ShopItemRarity.EPIC)),
	LOVE(Type.PREFIX, new Prefix("Love", "Love is...", "&c♥&d&lLove&c❤", 777, Material.APPLE, ShopItemRarity.RARE)),
	HAPPY(Type.PREFIX, new Prefix("Happy", "Just be happy!", "&a☺&lHappy&a☻", 200, Material.EMERALD, ShopItemRarity.COMMON)),
	// gender series
	MALE(Type.PREFIX, new Prefix("Gender: Male", "Express your gender!", "&b♂&3&lMale&b♂", 300, Material.SOUL_LANTERN, ShopItemRarity.RARE)),
	FEMALE(Type.PREFIX, new Prefix("Gender: Female", "Express your gender!", "&d♀&5&lFemale&d♀", 300, Material.LANTERN, ShopItemRarity.RARE)),
	ANNIHILATOR(Type.PREFIX, new Prefix("Annihilator", "Show me what you got!", "&c☠&4&lAnnihilator&c&l☠", 1000, Material.WITHER_SKELETON_SKULL, ShopItemRarity.LEGENDARY)),
	SUNNY(Type.PREFIX, new Prefix("Sunny", "It's a nice weather outside :)", "&e☀&6&lSunny&e☀", 500, Material.GOLD_BLOCK, ShopItemRarity.EPIC)),
	RAINY(Type.PREFIX, new Prefix("Rainy", "I've got my umbrella!", "&b🌧&3&lRainy&b☂", 500, Material.WATER_BUCKET, ShopItemRarity.EPIC)),
	GLITCH(Type.PREFIX, new Prefix("Glitch", "Is this thing on?", "&a✚&lGl&k&l1&atch&a&k&l✚&R", 666, Material.REDSTONE_TORCH, ShopItemRarity.RARE)),

	;


	public enum Type {

		KILL(Material.IRON_SWORD, "Play effect whenever you kill someone!"),
		DEATH(Material.SKELETON_SKULL, "Express your death!"),
		CONTRAIL(Material.DIAMOND, "These effects will follow you in the battlefield!"),
		PREFIX(Material.SPRUCE_SIGN, "Select a prefix, show everyone your status! Prefixes are not available in tab during game.");
		// DO NOT ADD ANYMORE TYPES IT WILL BREAK THE MENU

		private final Material material;
		private final String about;

		Type(Material material, String about) {
			this.material = material;
			this.about = about;
		}

		public Material getMaterial() {
			return material;
		}

		public String getAbout() {
			return about;
		}
	}

	// This used to keep track of cosmetic mobs so they can be removed in case of crash.
	public static final String COSMETIC_ID = "cosmeticMob";

	private final Type type;
	private final AbstractEffect clazz;

	EnumEffect(Type type, AbstractEffect clazz) {
		this.type = type;
		this.clazz = clazz;
	}

	public static Set<EnumEffect> getByType(Type t) {
		final Set<EnumEffect> hash = new HashSet<>();
		for (EnumEffect value : values()) {
			if (value.type == t) hash.add(value);
		}
		return hash;
	}

	public boolean availableInShop() {
		return this.clazz.getCost() > 0;
	}

	public void grantItem(Player player) {
		final CosmeticEntry cosmetics = Database.getDatabase(player).getCosmetics();
		if (!cosmetics.hasCosmetic(this)) {
			cosmetics.grantCosmetic(this);
		}
	}

	public void revokeItem(Player player) {
		final CosmeticEntry cosmetics = Database.getDatabase(player).getCosmetics();
		if (cosmetics.hasCosmetic(this)) {
			cosmetics.revokeCosmetic(this);
		}
	}

	public boolean isEnabled(Player player) {
		return Database.getDatabase(player).getCosmetics().isEnabled(this.getType());
	}

	public boolean isSelected(Player player) {
		final EnumEffect effect = Database.getDatabase(player).getCosmetics().getCurrentEffect(this.getType());
		return effect == this;
	}

	public boolean isOwned(Player player) {
		return Database.getDatabase(player).getCosmetics().hasCosmetic(this);
	}

	public boolean hasItem(Player player) {
		return Database.getDatabase(player).getCosmetics().hasCosmetic(this);
	}

	public AbstractEffect getClazz() {
		return clazz;
	}

	public void display(Player player) {
		clazz.display(player);
	}

	public Type getType() {
		return type;
	}

	public String getTypeString() {
		return Chat.capitalize(getType().name());
	}
}
