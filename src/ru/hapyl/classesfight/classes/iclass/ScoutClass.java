package ru.hapyl.classesfight.classes.iclass;

import com.google.common.collect.Sets;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.SoundLib;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScoutClass extends IClass implements Listener {

	private final String STRING_ARROW = ChatColor.AQUA + "➥ ";

	private final ItemStack POTION = new CFItemBuilder(Material.POTION)
			.setLore(ChatColor.GRAY + "Drinking this potion provides those effects:____"
					+ STRING_ARROW + ChatColor.DARK_AQUA + "Speed & Strength I for 30s__"
					+ STRING_ARROW + ChatColor.DARK_AQUA + "Speed & Strength II for 20s__"
					+ STRING_ARROW + ChatColor.DARK_AQUA + "Speed & Strength III for 10s____"
					+ "&eEach effect applies after each other.__&eUsing potion while effects are active will reset them.")
			.setName(ChatColor.AQUA + "Scout Potion")
			.setPotionMeta(PotionEffectType.LUCK, 70, 60, Color.BLUE)
			.hideFlags()
			.applyDefaultSettings()
			.build();

	private final ItemStack WEAPON_BLADE = new CFItemBuilder(Material.SHEARS).setName("&aBlade").setPureDamage(3.0).build();

	public final Map<Player, Set<GameTask>> tasks = new HashMap<>();

	public ScoutClass() {
		super("Scout", Material.SHEARS);

		this.setRole(ClassRole.STRATEGIST);
		this.setRating(ClassRating.C);
		this.setInfo("Fast and brave warrior with a potion that makes him &bFaster &7and &bStronger &7overtime!____&eScout Potion &7Drinking the potion applies &bSpeed &7and &bStrength &7effects overtime.____&3➥ &b30s &7of level &a&lI &7effects. &3➥ &b20s &7of level &a&lII &7effects. &3➥ &b10s &7of level &a&lIII &7effects.__Drinking a potion while another is active will reset the buff.", "Extra Supply", "Grants another Scout Potion!", 4);
		this.setUltimateSound(Sound.ENTITY_GENERIC_DRINK, 0.0f);
		this.setLvlRequired(4);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.setChestplate(165, 232, 232);
		eq.setLeggings(165, 232, 232);

		eq.addItem(WEAPON_BLADE);
		eq.addItem(POTION);


	}

	@Override
	public void onStart(Player player) {
	}

	@Override
	public void onStopOnce() {
		this.tasks.clear();
	}

	@Override
	public void useUltimate(Player player) {
		player.getInventory().remove(Material.POTION);
		player.getInventory().addItem(POTION);
	}

	@EventHandler
	public void handleScoutPotion(EntityPotionEffectEvent ev) {
		if (ev.getEntity() instanceof Player) {
			Player player = (Player)ev.getEntity();
			if (ev.getAction() == EntityPotionEffectEvent.Action.ADDED || ev.getAction() == EntityPotionEffectEvent.Action.CHANGED) {
				if (ev.getNewEffect().getType().equals(PotionEffectType.LUCK)) {
					if (player.getActivePotionEffects().contains(ev.getNewEffect())) {
						ev.setCancelled(true);
						player.sendMessage(ChatColor.RED + "Please wait before using another Scout Potion!");
						return;
					}

					removeEffectIfHad(player);

					if (!tasks.isEmpty() && tasks.get(player) != null) {
						tasks.get(player).forEach(GameTask::cancel);
					}

					applyEffects(player);
					player.getInventory().remove(Material.GLASS_BOTTLE);

				}
			}
		}
	}

	private void applyEffects(Player player) {

		// cancel old effects
		if (tasks.containsKey(player)) {
			for (GameTask task : tasks.get(player)) {
				if (!task.isCancelled()) {
					task.cancel();
				}
			}
			tasks.remove(player);
		}

		final int delaySecondPotion = 30 * 20;
		final int delayThirdPotion = 50 * 20;

		PlayerLib.playSound(player, Sound.ENTITY_GENERIC_DRINK, 1.2f);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 31 * 20, 0));
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 31 * 20, 0));

		final GameTask firstTask = new GameTask() {
			@Override
			public void run() {
				if (Spectator.isSpectator(player)) {
					this.cancel();
					return;
				}
				PlayerLib.playSound(player, Sound.ENTITY_GENERIC_DRINK, 1.2f);
				removeEffectIfHad(player);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 21 * 20, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 21 * 20, 1));

			}
		}.runTaskLater(delaySecondPotion);

		final GameTask secondTask = new GameTask() {
			@Override
			public void run() {
				if (Spectator.isSpectator(player)) {
					this.cancel();
					return;
				}
				SoundLib.play(player, Sound.ENTITY_GENERIC_DRINK, 1.2f);
				removeEffectIfHad(player);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 11 * 20, 2));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 11 * 20, 2));
			}
		}.runTaskLater(delayThirdPotion);

		tasks.put(player, Sets.newHashSet(firstTask, secondTask));

	}


	private void removeEffectIfHad(Player player) {
		player.getActivePotionEffects().forEach(potion -> {
			if (potion.getType().equals(PotionEffectType.SPEED) || potion.getType().equals(PotionEffectType.INCREASE_DAMAGE))
				player.removePotionEffect(potion.getType());
		});
	}

}
