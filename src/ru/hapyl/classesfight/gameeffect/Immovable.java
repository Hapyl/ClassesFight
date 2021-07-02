package ru.hapyl.classesfight.gameeffect;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Immovable implements GameEffect {

    private final Map<Player, Double> oldValue = new HashMap<>();

    @Override
    public void onEffectStart(Player player, Object... extra) {
        final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (attribute == null) {
            return;
        }
        oldValue.put(player, attribute.getBaseValue());
        attribute.setBaseValue(1.0d);
    }

    @Override
    public void onEffectStop(Player player) {
        final AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (attribute == null) {
            return;
        }
        attribute.setBaseValue(oldValue.getOrDefault(player, 0.0d));
        oldValue.remove(player);
    }

    @Override
    public String getEffectName() {
        return "Immovable";
    }
}
