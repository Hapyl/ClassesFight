package ru.hapyl.classesfight.quest.relic;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RelicRunnerListener implements Runnable, Listener {

	@Override
	public void run() {
		final Map<Integer, Relic> relics = RelicHunt.current().getRelics();

		if (relics.isEmpty()) {
			return;
		}

		relics.forEach((id, relic) -> {
			final Location location = relic.getCoordinates().toLocation();
			if (location.getWorld() == null) {
				return;
			}

			final Set<Entity> nearby = new HashSet<>(location.getWorld().getNearbyEntities(location, 10, 10, 10));
			for (Entity entity : nearby) {
				if (!(entity instanceof Player player)) {
					return; // how?
				}
				Location locationShift = location.clone().add(0.0d, 0.5d, 0.0d);
				if (relic.hasFound(player)) {
					player.spawnParticle(Particle.CRIT, locationShift, 3, 0.25, 0.15, 0.25, 0.03f);
				}
				else {
					player.spawnParticle(Particle.CRIT_MAGIC, locationShift, 2, 0.25, 0.15, 0.25, 0.05f);
					player.spawnParticle(Particle.SPELL_WITCH, locationShift, 3, 0.25, 0.15, 0.25, 0.03f);
				}
			}
		});
	}

	@EventHandler()
	public void handleRelicClick(PlayerInteractEvent ev) {

		if (ev.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}

		if (ev.getClickedBlock() == null) {
			return;
		}
		final Player player = ev.getPlayer();
		final Block block = ev.getClickedBlock();
		if (block.getType() != Material.PLAYER_HEAD) {
			return;
		}

		final Relic relic = RelicHunt.current().getByBlock(block);
		if (relic == null) {
			return;
		}

		if (!relic.hasFound(player)) {
			relic.grant(player, true);
		}
	}

}
