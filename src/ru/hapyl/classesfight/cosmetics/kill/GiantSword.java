package ru.hapyl.classesfight.cosmetics.kill;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;

public class GiantSword extends AbstractEffect {

	public GiantSword() {
		super("Giant's Sword", "A big RIP for this warrior.", 2000, Material.IRON_SWORD, ShopItemRarity.LEGENDARY);
	}

	@Override
	public void display(Player player) {
		player.getLocation().getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.RECORDS, 50, 0.5f);
		final Location location = player.getLocation().clone().subtract(1.6d, 0.6d, 4.3d);
		location.setYaw(0.0f);
		location.setPitch(0.0f);
		final Giant giant = player.getWorld().spawn(location, Giant.class, me -> {
			me.setInvisible(true);
			me.setInvulnerable(true);
			me.setCustomNameVisible(false);
			me.setCustomName("Dinnerbone"); // flip em
			me.setGravity(false);
			me.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
		});
		GarbageCollector.add(giant);
		new GameTask() {
			@Override
			public void run() {
				giant.remove();
			}
		}.runTaskLater(60);
	}
}
