package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.annotate.NOTNULL;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.storage.Kven;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.classes.ClassRating;
import ru.hapyl.classesfight.classes.iclass.extra.Combo;
import ru.hapyl.classesfight.event.PlayerDamageByPlayerEvent;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.Icons;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import java.util.HashMap;
import java.util.Map;

public class TheWitcher extends IClass implements Listener {

	private final Map<Player, Combo> combos;
	private final int ultimateLength = 200;
	private final double COMBO_DAMAGE = 2.5d;

	public TheWitcher() {
		super("The Witcher", Material.CRIMSON_ROOTS);
		this.combos = new HashMap<>();

		//Some say that his the most trainer Witcher ever. Well versed in magic...
		this.setInfo("&7Some say, that he's the most trained Witcher ever; Well versed in any kind of magic...__", Icons.abilityDot("Combo &e&lPASSIVE", "Dealing continuous charged hits will increase your combo, greater combo hits deals increased damage.") + Abilities.AARD
						.getAbout() + Abilities.IGNY.getAbout() + Abilities.KVEN.getAbout() + Abilities.AKCIY.getAbout() + Abilities.IRDEN.getAbout(),
				"All the Trainings",
				String.format("Remember all your trainings and unleash them at once. Creating infinite %1$s shield and %2$s aura that follows you for &b10s&7. Both %1$s and %2$s starts their cooldowns.", Abilities.KVEN
						.getName(), Abilities.IRDEN.getName()),
				8);

		this.setRating(ClassRating.S);
		this.setLvlRequired(4);

		final ClassEquipment equipment = this.getClassEquipment(true);

		equipment.addItem(new ItemBuilder(Material.IRON_SWORD)
				.setName("&aAerondight")
				.addAttribute(Attribute.GENERIC_ATTACK_SPEED, -2.0d, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
				.applyDefaultSettings(false).setPureDamage(COMBO_DAMAGE)
				.build());

		equipment.setHelmet("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTEwOTA1YmU0ZjY3ZTJmY2FkMjkxY2RmOGFlYjJlOWZmNTVmZTkzZjI3YjhjMWYwOTU5MDI0YTNjYjRhNzA1MiJ9fX0=");
		equipment.setChestplate(44, 48, 101);
		equipment.setLeggings(60, 66, 69);
		equipment.setBoots(29, 29, 33);

		Abilities.AARD.addItemIfExists(equipment);
		Abilities.IGNY.addItemIfExists(equipment);
		Abilities.KVEN.addItemIfExists(equipment);
		Abilities.AKCIY.addItemIfExists(equipment);
		Abilities.IRDEN.addItemIfExists(equipment);

		equipment.addItem(new CFItemBuilder(Material.POTION, "wither_healing").setName("&aSwallow").addClickEvent(this::healingPotion).build());

	}

	@Override
	public void processDamageEventAsVictim(Player player, PlayerDamageByPlayerEvent event) {
		final Kven kven = (Kven)Abilities.KVEN.getAbility();

		// FIXME: 024. 05/24/2021 - Double shield damage thing
		if (event.getDamage() <= 1.0d) {
			return;
		}

		if (kven.getShieldCharges(player) > 0) {
			kven.decreaseShieldCharges(player);
			PlayerLib.playSound(player, Sound.ITEM_SHIELD_BREAK, 0.75f);
			Chat.sendMessage(player, "&aOne of your " + kven.getName() + " shield broke!");
			event.setCancelled(true);
		}
	}

	@Override
	public void processDamageEventAsDamager(Player player, PlayerDamageByPlayerEvent event) {
		event.setDamage(this.testAndCalculateDamage(player, event.getDamage()));
	}

	private void healingPotion(Player player) {
		DamageFeature.addHealth(player, 10.0d);
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10 * 20, 2));
		PlayerLib.playSound(player, Sound.ENTITY_GENERIC_DRINK, 0.75f);
		GameUtils.removeHeldItem(player, 1);
	}

	public double testAndCalculateDamage(Player player, double initDamage) {

		double damage = initDamage;
		final TheWitcher witcher = (TheWitcher) ClassManager.WITCHER.getTheClass();
		final int currentCombo = witcher.getComboHit(player);

		/**
		 * Didn't really found out hot to test for damage,
		 * so I'm just testing for damage. +1 is for the strength class bonus
		 */
		if (damage >= (COMBO_DAMAGE + 1.0d)) {
			witcher.getCombo(player).incrementCombo(1);
		}
		else {
			witcher.getCombo(player).reset();
		}

		if (currentCombo > 2) {
			damage += (currentCombo * 0.3);
			OldPlayerLib.playSound(player, Sound.ITEM_SHIELD_BREAK, 1.75f);
		}

		return damage;

	}

	@Override
	public void onStart(Player player) {

		// apply cooldown
		Abilities.IRDEN.getAbility().applyStartCooldown(player);
		Abilities.AKCIY.getAbility().applyStartCooldown(player);

		this.combos.put(player, new Combo(player));
	}

	public int getComboHit(Player player) {
		return getCombo(player).getComboIfPresent();
	}

	@Override
	public void onStartOnce() {
		new GameTask() {
			@Override
			public void run() {
				ClassManager.getPlayersInClass(ClassManager.WITCHER).forEach(player -> {

					final int combo = getComboHit(player);
					if (combo > 2) {
						Chat.sendTitle(player, "        &6Combo", "          &4&lx" + (combo - 2), 0, 10, 10);
					}

				});
			}
		}.runTaskTimer(0, 1);
	}

	@Override
	public void onStopOnce() {
		this.combos.clear();
	}

	@Override
	public void useUltimate(Player player) {

		this.setUsingUltimate(player, true);
		GameEffectManager.applyEffect(player, GameEffectType.RESISTANCE, ultimateLength, true);
		Abilities.KVEN.getAbility().applyItemCooldown(player);
		Abilities.IRDEN.getAbility().applyItemCooldown(player);

		new GameTask() {

			private int tick = 0;

			@Override
			public void run() {

				if (tick++ >= ultimateLength) {
					setUsingUltimate(player, false);
					this.cancel();
					return;
				}

				/**
				 * Yes copy/pasting code so what
				 */

				// first and every 20 ticks
				if (tick % 10 == 0 || tick == 1) {
					Geometry.drawCircle(player.getLocation(), 3.5d, Quality.LOW, new WorldParticle(Particle.SPELL_WITCH));
				}

				GameUtils.getPlayerInRange(player.getLocation(), 3.5d).forEach(target -> {

					if (target == player) {
						return;
					}

					target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 3));
					GameEffectManager.applyEffect(target, GameEffectType.VULNERABLE, 5, true);
					GameEffectManager.applyEffect(target, GameEffectType.IMMOVABLE, 5, true);
					GameEffectManager.applyEffect(target, GameEffectType.WEAKNESS, 5, true);

				});


			}
		}.runTaskTimer(0, 1);

	}

	@NOTNULL
	public Combo getCombo(final Player player) {
		return this.combos.getOrDefault(player, new Combo(player));
	}

}
