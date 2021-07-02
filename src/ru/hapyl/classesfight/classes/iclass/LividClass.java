package ru.hapyl.classesfight.classes.iclass;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.ClassInfo;
import ru.hapyl.classesfight.classes.ClassUltimate;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.utils.CFItemBuilder;

public class LividClass extends IClass {

    /**
     * --[ 6 Faces ]--
     *
     * 1. Sneaky (Invisibility, Low Damage)
     * 2. Enraged (Strong, High Damage)
     * 3. Healthy (A lot of health, Low Damage)
     * 4.
     * 5.
     * 6.
     *
     */

    public LividClass() {
        super("Masked", Material.ROTTEN_FLESH, EnumInfo.MEDIUM, EnumInfo.MEDIUM, EnumInfo.MEDIUM, EnumInfo.VERY_HIGH, ClassRole.STRATEGIST,
                new ClassInfo("", "", ""),
                new ClassUltimate("", Integer.MAX_VALUE));
    }
    
    private static final ItemStack WEAPON = new CFItemBuilder(Material.IRON_SWORD)
            .setName("&aWeapon")
            .build();


    @Override
    public void useUltimate(Player player) {

    }
}
