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

package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.GeometryLib;
import ru.hapyl.classesfight.utils.SoundLib;

import java.util.HashSet;
import java.util.Set;

public class ShockArrow extends Ability implements Listener {

	private final double SHOCK_EXPLOSION_RADIUS = 3.7d;
	private final double SHOCK_EXPLOSION_MAX_DAMAGE = 9.0d;

	private final int SHOCK_DART_WINDUP = 18;

	private final Set<Arrow> arrows;

	public ShockArrow() {
		super("Shock Dart", "Shoots an arrow infused with shock clusters. Upon hitting a block, swiftly charges and explodes, dealing damage based on distance.");
		this.setItem(Material.LIGHT_BLUE_DYE);
		this.arrows = new HashSet<>();
		this.setCooldown(120);
	}

	@Override
	public void onStop() {
		this.arrows.clear();
	}

	@EventHandler()
	public void handleArrows(ProjectileHitEvent ev) {
		if (!(ev.getEntity() instanceof final Arrow arrow)) {
			return;
		}

		if (!(ev.getEntity().getShooter() instanceof final Player shooter)) {
			return;
		}

		if (this.arrows.contains(arrow)) {
			executeShockExplosion(shooter, arrow.getLocation());
			this.arrows.remove(arrow);
		}
	}

	private void executeShockExplosion(Player who, Location loc) {

		GeometryLib.drawSphere(loc, 10, 4, new GeometryLib.TinyReddust(Color.fromRGB(89, 255, 233), 1, 0, 0, 0, 0));
		SoundLib.playAndCut(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2f, SHOCK_DART_WINDUP);

		new GameTask() {
			@Override
			public void run() {

				GeometryLib.drawSphere(loc, 10, SHOCK_EXPLOSION_RADIUS, new GeometryLib.TinyReddust(Color.RED, 1, 0, 0, 0, 0));
				SoundLib.play(loc, Sound.ENCHANT_THORNS_HIT, 1.2f);
				GameUtils.getPlayerInRange(loc, SHOCK_EXPLOSION_RADIUS).forEach(player -> DamageFeature.damage(player, who, (SHOCK_EXPLOSION_MAX_DAMAGE - player.getLocation().distance(loc)), EnumDamageCause.SHOCK_DART));

			}
		}.runTaskLater(SHOCK_DART_WINDUP);

	}

	@Override
	public Response useAbility(Player player) {
		final Arrow arrow = player.launchProjectile(Arrow.class);
		this.arrows.add(arrow);
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f);
		return Response.OK;
	}
}
