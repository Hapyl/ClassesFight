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

package ru.hapyl.classesfight.ability.extra;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.particle.AbstractParticleBuilder;
import kz.hapyl.spigotutils.module.particle.ParticleBuilder;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.Action;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.storage.GravityGun;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

public class Element {

	private final Player player;
	private final Entity entity;
	private final Material material;
	private final ElementType type;

	private final ItemStack stack;
	private GameTask task;

	public Element(Player player, Block block) {
		final Location location = player.getLocation();
		this.player = player;
		this.material = block.getType();
		this.type = ElementType.getElementOf(block.getType());

		if (this.material == Material.PLAYER_HEAD
				|| this.material == Material.PLAYER_WALL_HEAD) {
			final String texture = this.getHeadTexture(block);
			if (texture == null) {
				this.stack = new ItemStack(this.material);
			}
			else {
				this.stack = ItemBuilder.playerHead(texture).toItemStack();
			}
		}
		else {
			this.stack = new ItemStack(this.material);
		}

		this.entity = spawnBlockEntity(location.add(location.getDirection().multiply(2)));

	}

	public void remove() {
		this.entity.remove();
	}

	private String getHeadTexture(Block block) {
		// Get head texture if PLAYER_HEAD
		final Skull skull = (Skull)block.getState();
		try {

			final Field field = skull.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			final GameProfile profile = (GameProfile)field.get(skull);
			final Collection<Property> textures = profile.getProperties().get("textures");
			for (Property texture : textures) {
				return texture.getValue();
			}
			field.setAccessible(false);

		} catch (Exception exception) {
			return null;
		}
		return null;
	}

	public void throwEntity() {
		if (this.entity instanceof ArmorStand) {

			final AbstractParticleBuilder particles = ParticleBuilder.blockDust(this.material).setAmount(3).setOffX(0.1d).setOffY(0.05d).setOffZ(0.1d).setSpeed(0.015f);
			final String name = this.material.name();
			final ArmorStand stand = (ArmorStand)Element.this.entity;
			final AnimationType animationType = name.contains("SLAB") ? AnimationType.SLAB : name.contains("STAIRS") ? AnimationType.STAIRS : AnimationType.FULL_BLOCK;

			player.setCooldown(Abilities.GRAVITY_GUN.getAbility().getItem().getType(), this.type.getCd());

			new GameTask() {
				private int distance = 0;

				@Override
				public void run() {
					if (distance++ >= 60) {
						entityPoof();
						this.cancel();
						return;
					}

					final Location location = entity.getLocation();
					final Location fixedLocation = entity.getLocation().clone().add(0.0d, 1.5d, 0.0d);
					final Vector vector = location.getDirection();

					entity.teleport(location.add(vector.multiply(1)));

					// fx
					particles.display(fixedLocation);
					stand.setHeadPose(stand.getHeadPose().add(animationType.getX(), animationType.getY(), animationType.getZ()));

					// block hit detection
					if (!fixedLocation.getBlock().getType().isAir()) {
						entityPoof();
						this.cancel();
						return;
					}

					final Set<Player> range = GameUtils.getPlayerInRange(Element.this.entity.getLocation(), 1.0d);
					if (!range.isEmpty()) {
						entityPoof();
						range.forEach(target -> {
							DamageFeature.damage(target, player, type.getDamage(), EnumDamageCause.GRAVITY_GUN);
							final Action<Player> effect = type.getEffect();
							if (effect != null) {
								effect.use(target);
							}
						});
						this.cancel();
					}

				}
			}.addCancelEvent(new BukkitRunnable() {
				@Override
				public void run() {
					entity.remove();
				}
			}).runTaskTimer(0, 1);
		}
	}

	private void entityPoof() {
		final Location fixedLocation = entity.getLocation().add(0.0d, 1.5d, 0.0d);
		PlayerLib.playSound(fixedLocation, this.material.createBlockData().getSoundGroup().getBreakSound(), 0.75f);
		PlayerLib.spawnParticle(fixedLocation, Particle.EXPLOSION_NORMAL, 3, 0.1d, 0.05d, 0.1d, 0.02f);
		entity.remove();
	}

	public void startTask() {
		stopTask();
		this.task = new GameTask() {
			@Override
			public void run() {

				if (!CFItemBuilder.compareItemsIDs(player.getInventory().getItemInMainHand(), Abilities.GRAVITY_GUN.getAbility().getItem().getItem())) {
					entityPoof();
					((GravityGun)Abilities.GRAVITY_GUN.getAbility()).setElement(player, null);
					PlayerLib.playSound(Sound.ITEM_SHIELD_BREAK, 0.75f);
					Chat.sendMessage(player, "&aYour current equipped element has shattered!");
					this.cancel();
					return;
				}

				Chat.sendActionbar(player, "&f&lEquipped Element: &a" + Chat.capitalize(type));
				entity.teleport(player.getLocation().add(player.getLocation().getDirection().multiply(2)));
			}
		}.runTaskTimer(0, 1);
	}

	public void stopTask() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
	}

	private Entity spawnBlockEntity(Location location) {

		final World world = location.getWorld();
		if (world == null) {
			throw new IllegalArgumentException("world is null");
		}

		return world.spawn(location, ArmorStand.class, me -> {
			me.setMarker(true);
			me.setInvisible(true);
			me.getEquipment().setHelmet(this.stack);
		});

	}

	public Entity getEntity() {
		return entity;
	}

	private enum AnimationType {

		FULL_BLOCK(0.2d, 0.0d, 0.1d),
		SLAB(0.0d, 0.35d, 0.0d),
		STAIRS(0.25d, 0.0d, 0.0d);

		private final double x, y, z;

		AnimationType(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getZ() {
			return z;
		}
	}
}
