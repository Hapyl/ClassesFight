package ru.hapyl.classesfight.menu.collectibles.gui;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.cosmetics.Prefix;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.CosmeticEntry;
import ru.hapyl.classesfight.database.entry.ExperienceEntry;
import ru.hapyl.classesfight.menu.collectibles.CollectionSort;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

import java.util.List;

public class CollectionSelectGUI extends PlayerGUI {

    private final List<EnumEffect> ownedCosmetics;
    private final CosmeticEntry cosmetics;

    private CollectionSort sort;

    public CollectionSelectGUI(Player player, EnumEffect.Type value) {
        super(player, String.format("Select %s Effect", Chat.capitalize(value)), 4);
        this.cosmetics = Database.getDatabase(player).getCosmetics();
        this.ownedCosmetics = this.cosmetics.getOwnedCosmetics(value);
        this.sort = CollectionSort.A_TO_Z;
        this.sortItems();
        this.updateMenu(0);
    }

    private void sortItems() {
        this.sort.sort(this.ownedCosmetics);
    }

    private void updateMenu(int start) {

        this.clearEverything();

        if (this.ownedCosmetics.isEmpty()) {
            this.setItem(13, new ItemBuilder(Material.CAULDRON).setName("&cNo Cosmetics :(").setSmartLore("You can purchase cosmetics in the Item Shop for coins!").toItemStack(), this::soundPop);
        }
        else {

            // Arrows
            //  Next Page
            if (this.ownedCosmetics.size() > (start + 7)) {
                this.setItem(17, new ItemBuilder(Material.ARROW).setName("&aNext Page").toItemStack());
                this.setClick(17, player -> this.updateMenu(start + 7));
            }

            //  Previous Page
            if (start >= 7) {
                this.setItem(9, new ItemBuilder(Material.ARROW).setName("&aPrevious Page").toItemStack());
                this.setClick(9, player -> this.updateMenu(start - 7));
            }

            for (int slot = 10, index = start; slot < 17; ++slot, ++index) {
                if ((this.ownedCosmetics.size() - 1) >= index) {
                    final EnumEffect cosmetic = this.ownedCosmetics.get(index);
                    final boolean isSelected = cosmetic.isSelected(getPlayer());

                    this.setItem(slot, buildOwnedItem(cosmetic));
                    this.setItem(slot + 9, buildRarityItem(cosmetic));

                    if (isSelected) {
                        this.setClick(slot, this::soundPop);
                    }
                    else {
                        this.setClick(slot, player -> {
                            cosmetics.setCurrentEffect(cosmetic);
                            PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);
                            this.sendCollectionMessage("Selected %s as %s effect!", cosmetic.getClazz().getName(), cosmetic.getTypeString());
                            this.updateMenu(start);
                        });
                    }
                }
            }

            // Sort Button
            this.setItem(34, buildSortItem());

            // left
            this.setClick(34, player -> {
                this.sort = this.sort.next();
                this.sortItems();
                PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 2.0f);
                this.updateMenu(start);
            }, ClickType.LEFT, ClickType.SHIFT_LEFT);

            // right
            this.setClick(34, player -> {
                this.sort = this.sort.prev();
                this.sortItems();
                PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 2.0f);
                this.updateMenu(start);
            }, ClickType.RIGHT, ClickType.RIGHT);

        }

        // Arrow go back
        this.setItem(31, new ItemBuilder(Material.ARROW).setName("&aGo Back").setLore("&7To Collection").toItemStack(), CollectionGUI::new);
        this.openInventory();
    }

    private ItemStack buildSortItem() {
        final ItemBuilder builder = new ItemBuilder(Material.NAME_TAG).setName("&aSort").addSmartLore("Sort items by your wishes.").addLore();
        for (CollectionSort value : CollectionSort.values()) {
            final boolean isCurrentValue = value == this.sort;
            builder.addLore((isCurrentValue ? " &b&l" : "&8") + Chat.capitalize(value));
            if (isCurrentValue) {
                builder.addSmartLore(value.getAbout(), "  ");
            }
        }
        builder.addLore();
        builder.addLore("&eLeft-Click to cycle forwards");
        builder.addLore("&eRight-Click to cycle backwards");
        return builder.toItemStack();
    }

    private void sendCollectionMessage(String msg, Object... a) {
        Chat.sendMessage(getPlayer(), "&d&lCOLLECTION! &7" + msg, a);
    }

    private ItemStack buildRarityItem(EnumEffect enumEffect) {
        final ShopItemRarity rarity = enumEffect.getClazz().getRarity();
        return new ItemBuilder(rarity.getIcon())
                .setName("&aRarity")
                .addLore(String.format("&7This item is %s%s&7!", rarity.getColor(true), rarity.getName()))
                .addLore("")
                .addSmartLore("Rarer items less likely to appear in the shop and usually look better.")
                .toItemStack();
    }

    private ItemStack buildOwnedItem(EnumEffect enumEffect) {
        final AbstractEffect effect = enumEffect.getClazz();
        final boolean isSelected = enumEffect.isSelected(getPlayer());
        final ItemBuilder builder = new ItemBuilder(effect.getIcon()).setName("&a%s%s", isSelected ? ChatColor.BOLD : "", effect.getName()).setSmartLore(effect.getLore()).addLore();

        if (effect instanceof Prefix) {
            final Prefix prefix = (Prefix)effect;
            final ClassManager playerClass = ClassManager.getClass(getPlayer());
            final ExperienceEntry exp = Database.getDatabase(getPlayer()).getExperienceEntry();
            builder.addLore("&7Prefix Preview:")
                    .addLore(" &8Chat:")
                    .addLore(String.format("  &e(Lv%s) %s %s &e%s: &fHello everyone!", exp.getLvl(), playerClass.getDisplayName(), prefix.getPrefix(), getPlayer().getName()))
                    .addLore(" &8Tab:")
                    .addLore(String.format("  &6[%s&6] %s &e%s", playerClass.getDisplayName(ChatColor.WHITE, ChatColor.BOLD), prefix.getPrefix(), getPlayer().getName()))
                    .addLore("");
        }

        builder.addLore(isSelected ? "&eCurrently selected" : "&eClick to select");
        builder.predicate(isSelected, ItemBuilder::glow);

        return builder.hideFlags().toItemStack();
    }

    private void soundPop(Player player) {
        PlayerLib.playSound(getPlayer(), Sound.BLOCK_LAVA_POP, 0.0f);
    }

}
