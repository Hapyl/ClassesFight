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

package ru.hapyl.classesfight.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticlesBuilder {

	private Particle type;
	private Location loc;
	private double offX;
	private double offY;
	private double offZ;
	private int count;
	private double speed;

	public ParticlesBuilder(){

	}

	public ParticlesBuilder(Particle type, Location loc, double offX, double offY, double offZ, int count, double speed) {
		this.type = type;
		this.loc = loc;
		this.offX = offX;
		this.offY = offY;
		this.offZ = offZ;
		this.count = count;
		this.speed = speed;
	}

	/**
	 * Shows particles to certain player.
	 *
	 * @param player to show.
	 */
	public ParticlesBuilder show(Player player) {
		player.spawnParticle(this.type, this.loc, this.count, this.offX, this.offY, this.offZ, this.speed);
		return this;
	}

	/**
	 * Shows particles in the world for every player.
	 */
	public ParticlesBuilder show() {
		this.loc.getWorld().spawnParticle(this.type, this.loc, this.count, this.offX, this.offY, this.offZ, this.speed);
		return this;
	}

	/**
	 * Classes down below can be accessed from ParticlesBuilder.<class>
	 */

	static public class RestoneDust extends ParticlesBuilder {

		private final Location loc;
		private final Color color;
		private final int count;
		private final double offX;
		private final double offY;
		private final double offZ;
		private final double speed;

		/**
		 * Creates redstone dust particles.
		 *
		 * @param loc      Locaiton.
		 * @param rgbColor Bukkit Color.
		 * @param offX     offset X.
		 * @param offY     offset Y.
		 * @param offZ     offset Z.
		 * @param count    Amount of particles.
		 * @param speed    Speed of particles.
		 */
		public RestoneDust(Location loc, Color rgbColor, double offX, double offY, double offZ, int count, double speed) {
			this.loc = loc;
			this.color = rgbColor;
			this.offX = offX;
			this.offY = offY;
			this.offZ = offZ;
			this.count = count;
			this.speed = speed;
		}

		public RestoneDust show(Player player) {
			player.spawnParticle(Particle.REDSTONE, this.loc, this.count, this.offX, this.offY, this.offZ, this.speed, fetchColor());
			return this;
		}

		public RestoneDust show() {
			this.loc.getWorld().spawnParticle(Particle.REDSTONE, this.loc, this.count, this.offX, this.offY, this.offZ, this.speed, fetchColor());
			return this;
		}

		private Particle.DustOptions fetchColor() {
			return new Particle.DustOptions(this.color, this.count);
		}

	}

	static public class BlockBreak {

		private final Location loc;

		private final Material block;
		private final int count;
		private final double offX;
		private final double offY;
		private final double offZ;
		private final double speed;

		/**
		 * Creates block break particle effect.
		 *
		 * @param block Material fo the block.
		 */
		public BlockBreak(Location loc, Material block, double offX, double offY, double offZ, int count, double speed) {
			this.loc = loc;
			this.block = block;
			this.offX = offX;
			this.offY = offY;
			this.offZ = offZ;
			this.count = count;
			this.speed = speed;
		}

		public BlockBreak show(Player player) {
			player.spawnParticle(Particle.BLOCK_CRACK, this.loc, this.count, this.offX, this.offY, this.offZ, this.speed, this.block.createBlockData());
			return this;
		}

		public BlockBreak show() {
			this.loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, this.loc, this.count, this.offX, this.offY, this.offZ, this.speed, this.block.createBlockData());
			return this;
		}

	}

	static public class BlockDust {

		private final Location loc;

		private final Material block;
		private final int count;
		private final double offX;
		private final double offY;
		private final double offZ;
		private final double speed;

		/**
		 * Creates falling sand dust particle effect.
		 *
		 * @param block Material fo the block.
		 */
		public BlockDust(Location loc, Material block, double offX, double offY, double offZ, int count, double speed) {
			this.loc = loc;
			this.block = block;
			this.offX = offX;
			this.offY = offY;
			this.offZ = offZ;
			this.count = count;
			this.speed = speed;
		}

		public BlockDust show(Player player) {
			player.spawnParticle(Particle.BLOCK_DUST, this.loc, this.count, this.offX, this.offY, this.offZ, this.speed, this.block.createBlockData());
			return this;
		}

		public BlockDust show() {
			this.loc.getWorld().spawnParticle(Particle.BLOCK_DUST, this.loc, this.count, this.offX, this.offY, this.offZ, this.speed, this.block.createBlockData());
			return this;
		}

	}

	static public class SpellMob {

		private final Location loc;
		private final Color color;
		private final double bright;

		/**
		 * Withc particles
		 *
		 * @param brightness Brightness of the particles. Set to 'Double.MIN_NORMAL' to remove fade.
		 * @param rgbColor   Bukkit Color.
		 */
		public SpellMob(Location loc, double brightness, Color rgbColor) {
			this.loc = loc;
			this.color = rgbColor;
			this.bright = brightness;
		}

		public SpellMob show(Player player) {
			player.spawnParticle(Particle.SPELL_MOB, this.loc.getX(), this.loc.getY(), this.loc.getZ(), 0, this.bright, this.color.getRed() / 255f, this.color.getGreen() / 255f, this.color.getBlue() / 255f);
			return this;
		}

		public SpellMob show() {
			this.loc.getWorld().spawnParticle(Particle.SPELL_MOB, this.loc.getX(), this.loc.getY(), this.loc.getZ(), 0, this.color.getRed() / 255f, this.color.getGreen() / 255f, this.color.getBlue() / 255f);
			return this;
		}

	}

	static public class SpellMobAmbient {

		private final Location loc;
		private final Color color;
		private final double bright;

		/**
		 * Same as SpellMob but ambient.
		 */
		public SpellMobAmbient(Location loc, double brightness, Color rgbColor) {
			this.loc = loc;
			this.color = rgbColor;
			this.bright = brightness;
		}

		public SpellMobAmbient show(Player player) {
			player.spawnParticle(Particle.SPELL_MOB_AMBIENT, this.loc.getX(), this.loc.getY(), this.loc.getZ(), 0, this.bright, this.color.getRed() / 255f, this.color.getGreen() / 255f, this.color.getBlue() / 255f);
			return this;
		}

		public SpellMobAmbient show() {
			this.loc.getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, this.loc.getX(), this.loc.getY(), this.loc.getZ(), 0, this.color.getRed() / 255f, this.color.getGreen() / 255f, this.color.getBlue() / 255f);
			return this;
		}
	}

}
