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

package ru.hapyl.classesfight.runnable;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.hapyl.classesfight.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This tasks will be cancelled after game reset
 */
public abstract class GameTask implements Runnable {

	private static final Map<Long, GameTask> byId = new HashMap<>();

	private BukkitTask task;
	private BukkitRunnable atCancel;

	public GameTask() {
		byId.put((long)byId.size(), this);
	}

	public static GameTask runTaskTimerTimes(Consumer<GameTask> runnable, int delay, int period, int maxTimes) {
		return runTaskTimerTimes((a, b) -> runnable.accept(a), delay, period, maxTimes);
	}

	public static GameTask runTaskTimerTimes(Consumer<GameTask> runnable, int period, int maxTimes) {
		return runTaskTimerTimes((a, b) -> runnable.accept(a), 0, period, maxTimes);
	}

	public static GameTask runTaskTimerTimes(BiConsumer<GameTask, Integer> runnable, int delay, int period, int maxTimes) {
		return new GameTask() {
			private int tick = Math.max(1, maxTimes);

			@Override
			public void run() {

				if (tick-- <= 0) {
					this.cancel();
					return;
				}

				runnable.accept(this, tick);

			}
		}.runTaskTimer(delay, period);
	}

	public static GameTask runTaskTimerTimes(BiConsumer<GameTask, Integer> runnable, int delayBetween, int maxTimes) {
		return runTaskTimerTimes(runnable, 0, delayBetween, maxTimes);
	}

	/**
	 * Cancels all game tasks
	 */
	public static void clearTasks() {
		if (byId.isEmpty()) {
			return;
		}

		byId.forEach((id, gameTask) -> gameTask.deepCancel());
		byId.clear();
	}

	public static GameTask runLater(Runnable runnable, int later) {
		return runLater(runnable, later, false);
	}

	public static GameTask runLater(Runnable runnable, int later, boolean runAtCancel) {
		final GameTask task = new GameTask() {
			@Override
			public void run() {
				runnable.run();
			}
		}.runTaskLater(later);
		if (runAtCancel) {
			task.runTaskAtCancel();
		}
		return task;
	}

	public GameTask addCancelEvent(BukkitRunnable task) {
		this.atCancel = task;
		return this;
	}

	public GameTask addCancelEvent(Runnable runnable) {
		return this.addCancelEvent(new BukkitRunnable() {
			@Override
			public void run() {
				runnable.run();
			}
		});
	}

	public synchronized GameTask runTaskAtCancel() {
		this.addCancelEvent(new BukkitRunnable() {
			@Override
			public void run() {
				GameTask.this.run();
			}
		});
		return this;
	}

	public synchronized GameTask runTaskLater(long later) {
		this.validateDoesNotExists();
		return this.setupTask(Bukkit.getScheduler().runTaskLater(Main.getMain(), this, later));
	}

	public synchronized GameTask runTaskTimer(long delay, long period) {
		this.validateDoesNotExists();
		return this.setupTask(Bukkit.getScheduler().runTaskTimer(Main.getMain(), this, delay, period));
	}

	public synchronized GameTask runTask() {
		this.validateDoesNotExists();
		return this.setupTask(Bukkit.getScheduler().runTask(Main.getMain(), this));
	}

	private synchronized void deepCancel() {
		if (atCancel != null) {
			atCancel.runTask(Main.getMain());
		}
		this.cancel();
	}

	public synchronized int getId() {
		this.validateExists();
		return this.task.getTaskId();
	}

	public synchronized void cancel() {
		this.validateExists();
		Bukkit.getScheduler().cancelTask(this.task.getTaskId());
	}

	public synchronized boolean isCancelled() {
		this.validateExists();
		return this.task.isCancelled();
	}

	private void validateExists() {
		if (this.task == null) {
			throw new IllegalStateException("Could not validate task being active");
		}
	}

	private void validateDoesNotExists() {
		if (this.task != null) {
			throw new IllegalStateException(String.format("Cannot run task since it's already running as %s!", this.getId()));
		}
	}

	public synchronized GameTask setupTask(BukkitTask task) {
		this.task = task;
		return this;
	}

}
