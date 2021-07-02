package ru.hapyl.classesfight.gameeffect;

import io.netty.util.internal.ConcurrentSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import ru.hapyl.classesfight.Main;

import java.util.concurrent.ConcurrentHashMap;

public class GameEffectManager {

	private static final ConcurrentHashMap<Player, ConcurrentSet<ActiveGameEffect>> activeEffects = new ConcurrentHashMap<>();

	// This used to store tasks to cancel them upon reload or stop
	private static BukkitTask activeTask;
	private static final BukkitScheduler scheduler = Bukkit.getScheduler();

	public static void applyEffect(Player player, GameEffectType effectType, long lengthTicks) {
		applyEffect(player, effectType, lengthTicks, false);
	}

	public static void applyShield(Player player, long length, int shieldCharges) {
		applyEffect(player, GameEffectType.SHIELDED, length, true, shieldCharges);
	}

	public static void applyShieldDamage(Player player, long length, double shieldHealth) {
		applyEffect(player, GameEffectType.BREAKABLE_SHIELD, length, true, shieldHealth);
	}

	// normal shield
	public static int getShieldCharges(Player player) {
		return ((Shield)GameEffectType.SHIELDED.getEffect()).getValue(player);
	}

	public static void removeShieldCharge(Player player) {
		((Shield)GameEffectType.SHIELDED.getEffect()).setValue(player, getShieldCharges(player) - 1);
		if (getShieldCharges(player) == 0) {
			removeEffect(player, GameEffectType.SHIELDED);
		}
	}

	public static boolean hasShieldHits(Player player) {
		return playerHasEffect(player, GameEffectType.SHIELDED);
	}

	// don't care im doint this my way
	public static double getShieldHealthDamage(Player player) {
		return ((HealthShield)GameEffectType.BREAKABLE_SHIELD.getEffect()).getValue(player);
	}

	public static void removeShieldHealthDamage(Player player, double d) {
		((HealthShield)GameEffectType.BREAKABLE_SHIELD.getEffect()).setValue(player, getShieldHealthDamage(player) - d);
	}

	public static boolean hasShieldDamage(Player player) {
		return playerHasEffect(player, GameEffectType.BREAKABLE_SHIELD);
	}

	/**
	 * Applies GameEffect to the player.
	 *
	 * @param player      - Player
	 * @param effectType  - Effect
	 * @param lengthTicks - Effect duration in Minecraft ticks
	 * @param override    - Override if already applied
	 */
	public static void applyEffect(Player player, GameEffectType effectType, long lengthTicks, boolean override, Object... extra) {

		if (lengthTicks < 1) {
			throw new IllegalArgumentException("Length must be at least 1 tick.");
		}

		// Add 1 ticks because of delay
		lengthTicks += 1;

		final ConcurrentSet<ActiveGameEffect> alreadyActive = activeEffects.getOrDefault(player, new ConcurrentSet<>());

		if (alreadyActive.isEmpty() || !playerHasEffect(player, effectType)) {
			alreadyActive.add(new ActiveGameEffect(effectType, lengthTicks));
			final GameEffect effect = effectType.getEffect();
			effect.onEffectStart(player, extra);
			activeEffects.put(player, alreadyActive);
		}
		else {
			for (ActiveGameEffect actEffect : alreadyActive) {
				// Player has effect
				if (actEffect.getType() == effectType) {
					// If override replace with new ticks
					if (override) {
						actEffect.setTicksRemaining(lengthTicks);
					}
					// Else add ticks
					else {
						actEffect.addTicksRemaining(lengthTicks);
					}
				}
			}
		}

	}

	/**
	 * Removes active GameEffect from a player.
	 *
	 * @param player - Player
	 * @param effect - GameEffect
	 */
	public static void removeEffect(Player player, GameEffectType effect) {
		final ConcurrentSet<ActiveGameEffect> actEffects = activeEffects.getOrDefault(player, new ConcurrentSet<>());
		if (!actEffects.isEmpty()) {
			for (ActiveGameEffect actEffect : actEffects) {
				if (actEffect.getType() == effect) {
					actEffect.setTicksRemaining(0);
					actEffect.getEffect().onEffectStop(player);
					actEffects.remove(actEffect);
					activeEffects.put(player, actEffects);
				}
			}
		}
	}

	public static void resetEffects(Player player) {
		final ConcurrentSet<ActiveGameEffect> effects = activeEffects.getOrDefault(player, new ConcurrentSet<>());
		if (effects.isEmpty()) {
			return;
		}
		effects.forEach((effect) -> {
			effect.setTicksRemaining(0);
			effect.getEffect().onEffectStop(player);
		});
		activeEffects.put(player, new ConcurrentSet<>());
	}

	public static boolean playerHasEffect(Player player, GameEffectType effect) {
		final ConcurrentSet<ActiveGameEffect> alreadyActive = activeEffects.getOrDefault(player, new ConcurrentSet<>());
		if (alreadyActive.isEmpty()) {
			return false;
		}
		for (ActiveGameEffect act : alreadyActive) {
			if (act.getType() == effect) {
				return true;
			}
		}
		return false;
	}

	public static void onStop() {
		// Cancel tasks
		if (activeTask != null) {
			if (!activeTask.isCancelled()) {
				activeTask.cancel();
			}
			activeTask = null;
		}

		// Remove all active effects
		if (!activeEffects.isEmpty()) {
			activeEffects.forEach((player, gameEffects) -> {
				for (ActiveGameEffect gameEffect : gameEffects) {
					removeEffect(player, gameEffect.getType());
				}
			});
			activeEffects.clear();
		}
	}

	public static void onStart() {
		activeTask = scheduler.runTaskTimer(Main.getMain(), new Runnable() {
			@Override
			public void run() {
				if (!activeEffects.isEmpty()) {
					for (Player player : activeEffects.keySet()) {
						final ConcurrentSet<ActiveGameEffect> effectHashSet = activeEffects.getOrDefault(player, new ConcurrentSet<>());
						if (!effectHashSet.isEmpty()) {
							for (ActiveGameEffect effect : effectHashSet) {
								final GameEffect gameEffect = effect.getEffect();

								// Stop effect is zero or less
								if (effect.getTicksRemaining() <= 0) {
									gameEffect.onEffectStop(player);
									removeEffect(player, effect.getType());
									return;
								}

								// Affect every tick when module is 0
								if (gameEffect.getTaskDelay() > 0) {
									if (effect.getTicksRemaining() % gameEffect.getTaskDelay() == 0) {
										gameEffect.affectTask(player);
									}
								}

								effect.tickDown();
							}
						}
					}
				}
			}
		}, 0, 1);
	}

	public static ConcurrentSet<ActiveGameEffect> getActiveEffects(Player player) {
		return activeEffects.getOrDefault(player, new ConcurrentSet<>());
	}
}
