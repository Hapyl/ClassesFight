package ru.hapyl.classesfight.database.entry;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.annotations.Shortcut;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.utils.CFItemBuilderListener;

import javax.annotation.Nullable;

public enum Setting {

    ULTIMATE_POINT_SOUND(Material.EMERALD, 28, "Plays a sound whenever you get an ult points."),
    ULTIMATE_READY_SOUND(Material.EMERALD_BLOCK, 29, "Plays a sound whenever your ultimate is ready."),
    HIDE_SCOREBOARD_IN_GAME(Material.RAIL, 30, "Whenever scoreboard in the right will be hidden form you. Only while in game.", false),
    HIDE_START_FX(Material.SPRUCE_SIGN, 31, "Controls whenever you will see and hear \"THE FIGHT BEGINS\" fx. The lightning effect will still strike."),
    SEE_CONTRAILS(Material.ROSE_BUSH, 32, "Whenever you will see others contrails while in game."),
    SPECTATE(Material.ENDER_EYE, 33, "Whenever you will spectate instead of playing.", false),
    USE_BOOSTERS(Material.POTION, 34, "Whenever you will drink a potion at the game start. (If you have them!)"),

    ALWAYS_RANDOM_CLASS(Material.RABBIT_HIDE, 2, "Always start with a random class.", false),
    FAST_ABILITY_USE(Material.FLOWER_POT, 4, "Changes your ability use to hotbar click (2-9). If there is an ability on slot you clicked, it will be used and your hotbar will switch to your weapon slot (1). &e&lNote: &eThis &efeature &eis &eexperimental &eand &ethus &enot &eavailable &efor &ecertain &eclasses.", false),
    NIGHT_MODE(Material.CLOCK, 6, "Whenever the map appears at night time for you.", false);

    private final Material menuIcon;
    // yes still hardcoding menu positioned
    private final int menuPos;
    private final String info;
    private final boolean defValue;

    Setting(Material menuIcon, int menuPos, String info) {
        this(menuIcon, menuPos, info, true);
    }

    Setting(Material menuIcon, int menuPos, String info, boolean defValue) {
        this.menuIcon = menuIcon;
        this.menuPos = menuPos;
        this.info = info;
        this.defValue = defValue;
    }

    public String getName() {
        return Chat.format(this.name().replace("_", " "));
    }

    public String getInfo() {
        return info;
    }

    public int getMenuPos() {
        return menuPos;
    }

    public Material getMenuIcon() {
        return menuIcon;
    }

    public boolean isEnabledByDefault() {
        return defValue;
    }

    @Shortcut
    public boolean isEnabled(Player player) {
        return Database.getDatabase(player).getSettingEntry().isEnabled(this);
    }

    @Shortcut
    public void setEnabled(Player player, boolean flag) {
        Database.getDatabase(player).getSettingEntry().setEnabled(this, flag);
    }


}
