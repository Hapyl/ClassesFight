package ru.hapyl.classesfight.classes.iclass.extra;

import kz.hapyl.spigotutils.module.hologram.Hologram;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.entity.Player;

public class BodyData {

	private final Player player;
	private final HumanNPC npc;
	private final Hologram hologram;
	private int health;
	private long lastDamage;

	public BodyData(Player player, HumanNPC npc) {
		this.player = player;
		this.npc = npc;
		this.health = 14;
		this.hologram = new Hologram().addLine(player.getName()).addLine(String.format("&a❤ %s", this.health)).create(npc.getLocation()).showAll();
		this.lastDamage = 0L;
	}

	public int getHealth() {
		return health;
	}

	public void damageIfCan() {
		if (System.currentTimeMillis() - this.lastDamage >= 500 && health > 0) {
			--this.health;
			this.lastDamage = System.currentTimeMillis();
			this.hologram.setLine(1, "&a❤ " + this.health).showAll();
		}
	}

	public Player getPlayer() {
		return player;
	}

	public HumanNPC getNpc() {
		return npc;
	}

	public void remove() {
		this.npc.remove();
		this.hologram.destroy();
	}

}