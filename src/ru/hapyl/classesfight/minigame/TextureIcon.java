package ru.hapyl.classesfight.minigame;

import kz.hapyl.spigotutils.module.inventory.Icon;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TextureIcon extends Icon {

    private final String texture;
    private final boolean smart;

    public TextureIcon(String texture, String name, String lore, boolean smart) {
        super(Material.PLAYER_HEAD, name, lore);
        this.texture = texture;
        this.smart = smart;
    }

    public TextureIcon(String texture, String name, String lore) {
        this(texture, name, lore, true);
    }

    public String getTexture() {
        return this.texture;
    }

    @Override
    public ItemStack buildIcon() {
        return this.getBuilder().toItemStack();
    }

    @Override
    public ItemBuilder getBuilder() {
        final ItemBuilder builder = super.getBuilder().setHeadTexture(this.getTexture()).removeLore().addLore();
        if (this.smart) {
            return builder.addSmartLore(this.getLore());
        }
        else {
            return builder.addLore(this.getLore());
        }
    }
}
