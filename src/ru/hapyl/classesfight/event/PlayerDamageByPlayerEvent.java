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
