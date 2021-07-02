package ru.hapyl.classesfight;

import kz.hapyl.spigotutils.module.block.BlockMagic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Consumer;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.HashSet;
import java.util.Set;

public final class GarbageCollector {

	public static final Set<Entity> entityCollector = new HashSet<>();
	public static final Set<Block> blockCollector = new HashSet<>();
	public static final Set<BukkitTask> taskCollector = new HashSet<>();

	public static void add(Entity entity) {
		entityCollector.add(entity);
	}

	public static void add(Entity... entities) {
		for (Entity entity : entities) {
			add(entity);
		}
	}

	public static void add(Block block) {
		blockCollector.add(block);
	}

	public static void add(BukkitTask runnable) {
		taskCollector.add(runnable);
	}

	public static void onStop() {
		entityCollector.iterator().forEachRemaining(Entity::remove);
		entityCollector.clear();

		blockCollector.iterator().forEachRemaining(b -> b.setType(Material.AIR, false));
		blockCollector.clear();

		taskCollector.iterator().forEachRemaining(BukkitTask::cancel);
		taskCollector.clear();

	}

	public static <T extends Entity> T spawnEntity(Location location, Class<T> entityClass, Consumer<T> consumer, int lifeTime) {
		final T entity = spawnEntity(location, entityClass, consumer);
		new GameTask() {
			@Override
			public void run() {
				entity.remove();
			}
		}.runTaskLater(lifeTime);
		return entity;
	}

	public static <T extends Entity> T spawnEntity(Location location, Class<T> entityClass, Consumer<T> consumer) {
		if (location.getWorld() == null) {
			throw new NullPointerException("world is null");
		}
		final T entity = location.getWorld().spawn(location, entityClass, consumer);
		GarbageCollector.add(entity);
		return entity;
	}


}
