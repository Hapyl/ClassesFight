package ru.hapyl.classesfight.ability;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Locale;

public abstract class Ability {

    private final String name;
    private final String about;

    private AbilityItem item;

    // extra
    private boolean allowLeftClick;
    private boolean allowRightClick;
    private boolean removeItemOnUse;
    private boolean showCooldown;

    private int cooldown;
    private String[] castMessage;

    public Ability(String name, String about) {
        this(name, about, null);
    }

    protected Ability(String name, String about, String[] castMessage) {
        this.name = name;
        this.about = about;
        this.castMessage = castMessage;
        this.allowLeftClick = true;
        this.allowRightClick = true;
        this.removeItemOnUse = false;
        this.showCooldown = true;
    }

    public void setShowCooldown(boolean showCooldown) {
        this.showCooldown = showCooldown;
    }

    public boolean isShowCooldown() {
        return showCooldown;
    }

    public void setRemoveItemOnUse(boolean removeItemOnUse) {
        this.removeItemOnUse = removeItemOnUse;
    }

    public boolean isRemoveItemOnUse() {
        return removeItemOnUse;
    }

    public boolean isAllowLeftClick() {
        return allowLeftClick;
    }

    public void setAllowLeftClick(boolean allowLeftClick) {
        this.allowLeftClick = allowLeftClick;
    }

    protected void setItem(AbilityItem item) {
        this.item = item;
    }

    protected void setItem(Material material) {
        this.setItem(new AbilityItem(material, this));
    }

    protected void setItem(Material material, boolean flag) {
        this.setItem(new AbilityItem(material, this, flag));
    }

    protected void setItem(String texture, String suffix) {
        this.setItem(new AbilityItem(texture, this, suffix));
    }

    protected void setItem(Material material, String suffix) {
        this.setItem(new AbilityItem(material, this, suffix));
    }

    public AbilityItem getItem() {
        return this.item;
    }

    public boolean hasItem() {
        return this.getItem() != null;
    }

    protected void setCooldown(int cd) {
        this.cooldown = cd;
    }

    protected void setCooldownSec(int seconds) {
        this.setCooldown((seconds * 20));
    }

    public int getCooldown() {
        return cooldown;
    }

    public void applyStartCooldown(Player player) {
        if (this.hasItem()) {
            player.setCooldown(this.getItem().getType(), this.getCooldown() / 2);
        }
    }

    public void setCastMessage(String... cast) {
        this.castMessage = cast;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return this.name.toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    public String getAbout() {
        return about;
    }

    public void sendCastMessage(Player player) {
        if (this.castMessage == null) {
            return;
        }
        Chat.sendMessage(player, " &7&o" + CollectionUtils.randomElement(this.castMessage));
    }

    private boolean hasCooldown(Player player) {
        if (!this.hasItem()) {
            return false;
        }
        return player.hasCooldown(this.getItem().getType());
    }

    public abstract Response useAbility(Player player /*executor*/);

    public void onStop() {

    }

    public void onStart() {

    }

    public void onDeath(final Player player) {

    }

    @Override
    public String toString() {
        for (Abilities value : Abilities.values()) {
            if (value.getAbility() == this) {
                return value.getAbout();
            }
        }
        return "\"getAbout()\"";
    }

    public void applyItemCooldown(Player player) {
        if (!this.hasItem()) {
            return;
        }
        player.setCooldown(this.getItem().getType(), this.getCooldown());
    }

    protected void setAllowRightClick(boolean flag) {
        this.allowRightClick = flag;
    }

    public boolean isAllowRightClick() {
        return this.allowRightClick;
    }

}
