package ru.hapyl.classesfight.classes.iclass;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;

public class AngelAndDemon extends IClass {

    public AngelAndDemon() {
        super("Angels and Demons", Material.LEATHER);

        this.setInfo("", "", "", 2);

        final ClassEquipment eq = this.getClassEquipment(true);
        Abilities.SPAWN_DEMON.addItemIfExists(eq);
        Abilities.SPAWN_ANGEL.addItemIfExists(eq);
    }

    @Override
    public void useUltimate(Player player) {

    }
}
