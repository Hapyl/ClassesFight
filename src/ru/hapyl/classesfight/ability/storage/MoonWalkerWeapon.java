package ru.hapyl.classesfight.ability.storage;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;

public class MoonWalkerWeapon extends Ability {
    public MoonWalkerWeapon() {
        super("Stinger", "Shoots an arrow in front of player.");
        this.setCooldown(20);
        this.setItem(Material.STONE_HOE);
    }

    @Override
    public Response useAbility(Player player) {
        final Arrow arrow = player.launchProjectile(Arrow.class);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 20, 1.25f);
        arrow.setDamage(1.25d);
        return Response.OK;
    }
}
