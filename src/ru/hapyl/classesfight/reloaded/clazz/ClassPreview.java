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

package ru.hapyl.classesfight.reloaded.clazz;

import kz.hapyl.spigotutils.module.hologram.Hologram;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import kz.hapyl.spigotutils.module.reflect.npc.ItemSlot;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.IClass;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.HashMap;
import java.util.Map;

public class ClassPreview {

	public static final Map<Player, ClassPreview> preview = new HashMap<>();

	private final static Location viewLocation = new Location(Bukkit.getWorlds().get(0), -82.5, 16.0, -9.5, 0, 0);
	private final static Location npcLocation = new Location(Bukkit.getWorlds().get(0), -82.5, 16.0, -5.5, 180, 0);
	private final static Location holoRightLocation = BukkitUtils.defLocation(-84.5, 16.0, -6.0);
	private final static Location holoLeftLocation = BukkitUtils.defLocation(-81.5, 16.0, -6.0);

	private final Player player;
	private final ClassManager manager;
	private final HumanNPC npc;
	private final Hologram holoRight;
	private final Hologram holoLeft;

	private GameTask task;

	public ClassPreview(final Player player, final ClassManager manager) {
		this.player = player;
		this.manager = manager;
		this.npc = new HumanNPC(npcLocation, manager.getDisplayName(), player.getName());
		this.npc.show(player);

		// Equipment
		final ClassEquipment eq = manager.getTheClass().getClassEquipment();
		this.npc.setItem(ItemSlot.HEAD, eq.getHelmet());
		this.npc.setItem(ItemSlot.CHEST, eq.getChestplate());
		this.npc.setItem(ItemSlot.LEGS, eq.getLeggings());
		this.npc.setItem(ItemSlot.FEET, eq.getBoots());
		this.npc.setItem(ItemSlot.MAINHAND, eq.getItem(0));
		this.npc.setItem(ItemSlot.OFFHAND, eq.getItem(1));

		this.holoLeft = createLeftHologram();
		this.holoRight = new Hologram();
		final ClassPreview old = preview.get(player);
		if (old != null) {
			old.cleanUp();
		}
		preview.put(player, this);
		this.startTask();
	}

	private Hologram createLeftHologram() {

		final Hologram holo = new Hologram();

		final IClass c = this.manager.getTheClass();
		holo.addLine("&b&lAbout");
		holo.addLine("&c&l⚔ &7" + c.getAttack());
		holo.addLine("&c&lhealth &7" + c.getHealth());
		holo.addLine("&c&ldefense &7" + c.getDefense());
		return holo.create(holoLeftLocation).show(player);

	}

	@Nullable
	public static ClassPreview getPreview(Player player) {
		return preview.get(player);
	}

	public void cleanUp() {
		this.stopTask();
		this.npc.remove();
		this.holoRight.destroy();
		this.holoLeft.destroy();
		GameUtils.showPlayer(player);
		player.teleport(GameMap.SPAWN.getLocation());
		preview.remove(player);
	}

	private void startTask() {
		GameUtils.hidePlayer(player);
		this.stopTask();
		this.task = new GameTask() {

			private int tick = 0;

			@Override
			public void run() {
				player.teleport(viewLocation);
				final Location location = npc.getLocation();
				location.setYaw(location.getYaw() + 4.5f);
				npc.setLocation(location);

				if (tick++ % 60 == 0) {
					npc.swingMainHand();
				}

			}
		}.runTaskTimer(0, 1);
	}

	private void stopTask() {
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
	}


}
