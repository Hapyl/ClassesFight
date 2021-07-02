package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.ability.extra.Element;
import ru.hapyl.classesfight.ability.extra.ElementType;

import java.util.HashMap;
import java.util.Map;

public class GravityGun extends Ability {

    private final Map<Player, Element> elements;

    public GravityGun() {
        //Right Click a block to harvest energy from it and equip. Right Click again with equipped block to launch it forward, damaging up to 1 enemy on it's way. The damage is scaled of element of the block. (Eg. Stone deals more damage than Wood)
        super("Dr. Ed's Gravity Energy Capacitor Mk. 3", "A tool that is capable of absorbing blocks elements.____&e&lRIGHT CLICK &7a block to harvest element from it.____&e&lRIGHT CLICK &7again with element equipped to launch it forward, damaging up to &bone &7enemy on it's way. The damage and cooldown is scaled based on the element. Switching from ability item will remove the element.");
        this.setItem(Material.IRON_HORSE_ARMOR);
        this.elements = new HashMap<>();
    }

    @Override
    public void onStop() {
        this.elements.values().forEach(Element::remove);
        this.elements.clear();
    }

    @NULLABLE
    private Element getElement(Player player) {
        return elements.getOrDefault(player, null);
    }

    private boolean hasElement(Player player) {
        return this.getElement(player) != null;
    }

    public void setElement(Player player, @NULLABLE Element element) {
        if (element == null) {
            this.elements.remove(player);
            return;
        }
        this.elements.put(player, element);
    }

    @Override
    public Response useAbility(Player player) {

        final Block targetBlock = player.getTargetBlockExact(7);

        // throw
        if (hasElement(player)) {
            final Element element = getElement(player);
            element.stopTask();
            element.throwEntity();
            this.setElement(player, null);
            return Response.OK;
        }

        // collect

        if (targetBlock == null) {
            return Response.error("No valid block in sight!");
        }

        if (ElementType.getElementOf(targetBlock.getType()) == ElementType.NULL) {
            return Response.error("Target block does not have any valid elements...");
        }

        if (!targetBlock.getType().isBlock()) {
            return Response.error("Target block is not a block?");
        }

        final Element element = new Element(player, targetBlock);
        // fix instant throw
        player.setCooldown(this.getItem().getType(), 2);
        element.startTask();
        setElement(player, element);
        Chat.sendMessage(player, "&aPicked up element of %s!", Chat.capitalize(targetBlock.getType()));

        return Response.OK;
    }

}
