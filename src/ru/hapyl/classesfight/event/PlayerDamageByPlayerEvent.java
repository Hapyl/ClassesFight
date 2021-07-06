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

package ru.hapyl.classesfight.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import ru.hapyl.classesfight.feature.EnumDamageCause;

import javax.annotation.Nullable;

public class PlayerDamageByPlayerEvent extends PlayerEvent implements Cancellable {

	public static HandlerList handlerList = new HandlerList();

	private final Player player;
	private final Player damager;
	private final EnumDamageCause cause;
	private double damage;

	private boolean cancelled;

	public PlayerDamageByPlayerEvent(Player player, @Nullable Player damager, double damage, EnumDamageCause cause) {
		super(player);
		this.player = player;
		// if there is no damager let it be self
		this.damager = damager == null ? player : damager;
		this.damage = damage;
		this.cause = cause;
	}

	public PlayerDamageByPlayerEvent(Player player, double damage, EnumDamageCause cause) {
		this(player, null, damage, cause);
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	public Player getWhoGotDamaged() {
		return player;
	}

	@Nullable
	public Player getDamager() {
		return damager;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public EnumDamageCause getCause() {
		return cause;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancelled = b;
	}
}
