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

package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.command.SimpleCommand;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.hapyl.classesfight.Main;
import ru.hapyl.classesfight.utils.GeometryLib;

import java.util.List;

public class PostProcessorTestCommand extends SimpleCommand {

	public PostProcessorTestCommand(String str) {
		super(str);
		this.setAllowOnlyPlayer(true);
		this.setAllowOnlyOp(true);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		Player player = (Player)sender;

		double rings = 100;
		double radius = 10;
		Location center = player.getLocation();

		if (true) {
			new BukkitRunnable() {
				@Override
				public void run() {
					GeometryLib.drawHalfSphere(center, rings, radius, new GeometryLib.TinyParticle(Particle.VILLAGER_HAPPY));
				}
			}.runTaskAsynchronously(Main.getMain());

		}
	}


	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}