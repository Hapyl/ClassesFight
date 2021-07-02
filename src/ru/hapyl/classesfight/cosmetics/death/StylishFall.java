package ru.hapyl.classesfight.cosmetics.death;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;

public class StylishFall extends AbstractEffect {
	public StylishFall() {
		super("Stylish Fall", "Fall like a hero!", -1, Material.PLAYER_HEAD, ShopItemRarity.LEGENDARY);
	}

	@Override
	public void display(Player player) {
		display0(player);
	}

	private void display0(Player player) {
		final Location location = player.getLocation().add(0.0d, 0.12d, 0.0d);
		location.setPitch(0.0f);
		//location.setYaw(0);

		final HumanNPC npc = new HumanNPC(location, "", player.getName());
		npc.showAll();
		npc.setCollision(false);
		npc.setDataWatcherByteValue(0, (byte) 0x80);

		GameTask.runLater(() -> {
			PlayerLib.spawnParticle(location, Particle.CLOUD, 16, 0.5d, 0.1d, 0.5d, 0.04f);
			PlayerLib.playSound(location, Sound.ENTITY_LLAMA_SPIT, 0.65f);
			npc.remove();
		}, 60).addCancelEvent(npc::remove);

	}

}
