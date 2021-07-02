package ru.hapyl.classesfight.menu.clazz;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.ClassInfo;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.EnumInfo;
import ru.hapyl.classesfight.classes.iclass.IClass;
import ru.hapyl.classesfight.menu.GameMenuGUI;

import java.util.Locale;

public class ClassRoleSelectGUI extends PlayerGUI {

    public ClassRoleSelectGUI(Player player) {
        super(player, "Class Select ▶ Role", 3);
        this.buildMenu();
    }

    private void buildMenu() {

        this.setItem(10, new ItemBuilder(Material.IRON_SWORD).setName("&aMelee").applyDefaultSettings(false).setSmartLore(IClass.ClassRole.MELEE.getInfo()).build(), player -> new ClassSelectGUI(player, IClass.ClassRole.MELEE));
        this.setItem(12, new ItemBuilder(Material.BOW).setName("&aRange").applyDefaultSettings(false).setSmartLore(IClass.ClassRole.RANGE.getInfo()).build(), player -> new ClassSelectGUI(player, IClass.ClassRole.RANGE));
        this.setItem(14, new ItemBuilder(Material.FEATHER).setName("&aAssassin").applyDefaultSettings(false).setSmartLore(IClass.ClassRole.ASSASSIN.getInfo()).build(), player -> new ClassSelectGUI(player, IClass.ClassRole.ASSASSIN));
        this.setItem(16, new ItemBuilder(Material.BOOK).setName("&aStrategist").applyDefaultSettings(false).setSmartLore(IClass.ClassRole.STRATEGIST.getInfo()).build(), player -> new ClassSelectGUI(player, IClass.ClassRole.STRATEGIST));

        this.setItem(this.getSize() - 4, new ItemBuilder(Material.CHEST).setName("&aLegacy Class Selection").setSmartLore("Opens a legacy selection where all classes are present.").build(), player -> new ClassLegacySelectGUI(player, this));

        // arrow back
        this.setArrowBack("Main Menu", GameMenuGUI::new);
        this.openInventory();

    }

}
