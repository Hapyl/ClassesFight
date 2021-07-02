package ru.hapyl.classesfight.utils;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.Setting;
import ru.hapyl.classesfight.database.entry.StatType;
import ru.hapyl.classesfight.feature.perk.Perk;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;

import java.util.Random;
import java.util.Set;

public class CFItemBuilderListener implements Listener {

	public static boolean isDisallowedClass(ClassManager manager) {

		switch (manager) {
			//case GRAVITY:
			case SWOOPER, RPS, FROZEN_GUY, TECHNO, ZEALOT, TAKER, SCOUT, MAGE, ENDER, CREEPER, BLAST_KNIGHT, HUNTER -> {
				return true;
			}
		}

		return false;

	}

	@EventHandler
	private void handleSlotSwitch(PlayerItemHeldEvent ev) {
		final Player player = ev.getPlayer();
		final int newSlot = ev.getNewSlot();

		if (!GameManager.current().isGameInProgress()
				|| !Setting.FAST_ABILITY_USE.isEnabled(player)
				|| (newSlot == 0 || newSlot == 8)) {
			return;
		}

		if (isDisallowedClass(ClassManager.getClass(player))) {
			return;
		}

		// Switch back to Weapon
		player.getInventory().setHeldItemSlot(0);
		final ItemStack heldItem = ev.getPlayer().getInventory().getItem(newSlot);
		if (heldItem == null) {
			Chat.sendMessage(player, "&cCouldn't use ability at slot %s!", newSlot + 1);
			return;
		}

		final CFItemBuilder builder = CFItemBuilder.itemHolder.getOrDefault(CFItemBuilder.getItemID(heldItem), null);

		if (builder == null) {
			return;
		}

		proceedFunction(player, builder, Action.RIGHT_CLICK_AIR);

	}

	@EventHandler()
	private void handleClick(PlayerInteractEvent ev) {

		if (ev.getHand() == EquipmentSlot.OFF_HAND
				|| ev.getAction() == Action.PHYSICAL) {
			return;
		}

		final Player player = ev.getPlayer();
		final Action action = ev.getAction();
		final ItemStack item = player.getInventory().getItemInMainHand();
		final String itemId = CFItemBuilder.getItemID(item);
		final CFItemBuilder builder = CFItemBuilder.itemHolder.getOrDefault(itemId, null);

		if (builder == null) {
			return;
		}

		ev.setCancelled(proceedFunction(player, builder, action));

	}

	private boolean proceedFunction(Player player, CFItemBuilder builder, Action action) {

		final GameManager manager = GameManager.current();
		if (manager.isGameInProgress() && !manager.arePlayersRevealed()) {
			if (!manager.isDebugMode()) {
				Chat.sendActionbar(player, "&4&lCannot use while players aren't revealed");
				return true;
			}
		}

		final boolean returnValue = builder.cancel;

		// Ability usage
		if (builder.ability != null) {

			if (!GameManager.current().isGameInProgress()) {
				Chat.sendMessage(player, "&cThis ability can only be used in game!");
				return true;
			}

			final Ability ability = builder.ability;

			// Left Click Test
			if ((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) && !ability.isAllowLeftClick()) {
				return returnValue;
			}

			// Right Click Test
			if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && !ability.isAllowRightClick()) {
				return returnValue;
			}

			if (GameEffectManager.playerHasEffect(player, GameEffectType.STUN)) {
				Chat.sendMessage(player, "&cCannot use ability while stunned!");
				return returnValue;
			}

			// Check for cooldown
			final Material type = ability.getItem().getType();

			// Cooldown check
			if (player.hasCooldown(type)) {
				Chat.sendMessage(player, String.format("&cAbility on cooldown for %ss.", BukkitUtils.roundTick(player.getCooldown(type))));
				OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
				return returnValue;
			}

			// Use ability
			final Response response = ability.useAbility(player);

			if (response.isOk()) {
				ability.sendCastMessage(player);
				if (ability.isRemoveItemOnUse()) {
					final ItemStack item = player.getInventory().getItemInMainHand();
					if (item.getItemMeta() == null) {
						return returnValue;
					}
					if (item.isSimilar(builder.item)) {
						item.setAmount(item.getAmount() - 1);
						player.updateInventory();
					}
				}
				if (!manager.isDebugMode()) {
					if (ability.getCooldown() > 0) {
						player.setCooldown(type, ability.getCooldown());
					}
					if (Perk.LUCKY_SEVEN.hasPerkAndEnabled(player)) {
						final int random = new Random().nextInt(101);
						if (random >= 93) {
							player.setCooldown(builder.item.getType(), 1);
							OldPlayerLib.playSound(player, Sound.ENTITY_BLAZE_DEATH, 1.75f);
							Chat.sendMessage(player, "&d&lLUCKY SEVEN! &7Your ability cooldown has been refreshed!");
						}
					}
					if (GameManager.current().isGameInProgress()) {
						Database.getDatabase(player).getStatistics().addStat(StatType.ABILITY_USED, 1);
					}
				}
			}
			else if (response.isError()) {
				response.sendMessageIfError(player);
				return returnValue;
			}

			return returnValue;
		}

		if (GameEffectManager.playerHasEffect(player, GameEffectType.STUN)) {
			Chat.sendMessage(player, "&cCannot use ability while stunned!");
			return returnValue;
		}

		// FIXME: 028. 06/28/2021 - rework this to use Ability instead / or maybe backwards
		final Set<CFItemBuilder.ItemAction> functions = builder.functions;
		for (CFItemBuilder.ItemAction func : functions) {
			if (func.hasAction(action)) {

				// Cooldown check
				if (player.hasCooldown(builder.item.getType())) {
					Chat.sendMessage(player, String.format("&cAbility on cooldown for %ss.", BukkitUtils.roundTick(player.getCooldown(builder.item.getType()))));
					OldPlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
					return returnValue;
				}

				if (builder.cd > 0) {
					if (builder.predicate != null && builder.predicate.test(player)) {
						if (!builder.error.isEmpty())
							player.sendMessage(ChatColor.RED + builder.error);
						continue;
					}

					if (player.hasCooldown(builder.item.getType())) {
						continue;
					}

					// If in debug mode then ignore cooldowns
					if (!manager.isDebugMode()) {
						player.setCooldown(builder.item.getType(), builder.cd);
						if (Perk.LUCKY_SEVEN.hasPerkAndEnabled(player)) {
							final int random = new Random().nextInt(101);
							if (random >= 93) {
								player.setCooldown(builder.item.getType(), 1);
								OldPlayerLib.playSound(player, Sound.ENTITY_BLAZE_DEATH, 1.75f);
								Chat.sendMessage(player, "&d&lLUCKY SEVEN! &7Your ability cooldown has been refreshed!");
							}
						}
					}

				}

				if (GameManager.current().isGameInProgress()) {
					Database.getDatabase(player).getStatistics().addStat(StatType.ABILITY_USED, 1);
				}
				func.execute(player);
			}
		}

		return returnValue;

	}


}
