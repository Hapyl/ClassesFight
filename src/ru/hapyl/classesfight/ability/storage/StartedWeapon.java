package ru.hapyl.classesfight.ability.storage;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;

public class StartedWeapon extends Ability {
	public StartedWeapon() {
		super("Bow", "Well, it's a bow! &e&lLEFT &e&lCLICK &7to shoot a quick arrow. &e&lHOLD &e&lRIGHT &e&lCLICK &7to charge and shoot a charged arrow!");
		this.setAllowRightClick(false);
		this.setItem(Material.BOW, false);
		this.getItem().getItem().addEnchantment(Enchantment.ARROW_INFINITE, 1);
		this.setCooldown(10);
	}

	@Override
	public Response useAbility(Player player) {
		final Arrow arrow = player.launchProjectile(Arrow.class);
		arrow.setDamage(0.5d);
		return Response.OK;
	}
}
