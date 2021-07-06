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

package ru.hapyl.classesfight.cosmetics.death;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;

public class Box extends AbstractEffect {

	private final ItemStack item;
	private final Sound sound;
	private final float pitch;
	private final int tickLifeTime;

	public Box(Material material, Sound sound, float pitch, int tickLifeTime, String name, String lore, long cost, Material icon, ShopItemRarity rarity) {
		super(name, lore, cost, icon, rarity);
		this.item = new ItemStack(material);
		this.sound = sound;
		this.pitch = pitch;
		this.tickLifeTime = tickLifeTime;
	}

	public void displayExtra(Location location) {
	}

	@Override
	public final void display(Player player) {
		final Location location = player.getLocation();
		final HumanNPC npc = new HumanNPC(player.getLocation(), "", player.getName());
		PlayerLib.playSound(location, this.sound, this.pitch);
		npc.showAll();
		for (Location loc : getLocations(location.clone().subtract(0.0d, 1.305d, 0.0d))) {
			for (int i = 0; i < 3; i++) {
				this.spawnStand(loc, tickLifeTime);
				loc.add(0.0d, 0.61d, 0.0d);
			}
		}
		this.displayExtra(location);
		new GameTask() {
			@Override
			public void run() {
				npc.remove();
			}
		}.runTaskAtCancel().runTaskLater(tickLifeTime);
	}

	protected void spawnStand(Location location, int tick) {
		GarbageCollector.spawnEntity(location, ArmorStand.class, me -> {
			me.setMarker(true);
			me.setSilent(true);
			me.setInvisible(true);
			me.getEquipment().setHelmet(this.item);
		}, tick);
	}

	public float getPitch() {
		return pitch;
	}

	public ItemStack getItem() {
		return item;
	}

	public Sound getSound() {
		return sound;
	}

	protected Location[] getLocations(Location center) {
		final double offset = 0.305d;
		final World world = center.getWorld();
		final double x = center.getX();
		final double y = center.getY();
		final double z = center.getZ();

		return new Location[]{
				new Location(world, x + offset, y, z + offset),
				new Location(world, x - offset, y, z + offset),
				new Location(world, x + offset, y, z - offset),
				new Location(world, x - offset, y, z - offset)
		};

	}

}
