/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight.menu.collectibles.gui;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.cosmetics.Prefix;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.CoinsEntry;
import ru.hapyl.classesfight.database.entry.CosmeticEntry;
import ru.hapyl.classesfight.database.entry.ExperienceEntry;
import ru.hapyl.classesfight.menu.ConfirmMenu;
import ru.hapyl.classesfight.menu.collectibles.ItemShop;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.utils.GameUtils;

public class ShopGUI extends PlayerGUI {

    private final CosmeticEntry cosmetics;
    private final ItemShop shop;

    public ShopGUI(Player player) {
        super(player, "Item Shop - " + GameUtils.getTodayDate() + (ChatColor.DARK_GRAY + (ItemShop.getShop(player).getReRollCount() > 0 ? " (" + ItemShop.getShop(player).getReRollCount() + ")" : "")), 4);
        this.cosmetics = Database.getDatabase(player).getCosmetics();
        this.shop = ItemShop.getShop(player);
        this.updateMenu();
    }

    private final ItemStack itemGoBackToCollectionIcon = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setName("&a<- To Collection").build();
    private final ItemStack itemOwnedIcon = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName("&aYou own this item!").toItemStack();
    private final ItemStack itemNotOwnedIcon = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("&cYou don't own this item!").toItemStack();
    private final ItemStack itemBlackBarIcon = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&0").build();

    private void updateMenu() {

        this.clearEverything();

        int slot = 11;
        for (EnumEffect item : this.shop.getItems()) {
            final boolean isOwned = item.isOwned(getPlayer());

            this.setItem(slot, buildShopItem(item));
            this.setItem(slot + 9, buildRarityItem(item));

            // owned indicators
            this.setItem(slot - 9, isOwned ? itemOwnedIcon : itemNotOwnedIcon);
            this.setItem(slot + 18, isOwned ? itemOwnedIcon : itemNotOwnedIcon);

            if (isOwned) {
                this.setClick(slot, this::soundPop);
            }
            else {
                this.setClick(slot, player -> new ConfirmMenu(player, "Confirm purchase " + item.getClazz().getName(), (t) -> performPurchase(item), target -> this.updateMenu()));
            }

            ++slot;

        }

        for (int i = 0; i < 4; i++) {
            this.setItem(i * 9, itemGoBackToCollectionIcon, CollectionGUI::new);
            this.setItem((8 * (i + 1)) + i, itemBlackBarIcon);
        }

        // reroll
        buildRerollItem();
        this.openInventory();

    }


    private void performPurchase(EnumEffect effect) {
        try {
            final CoinsEntry coins = Database.getCoins(getPlayer());
            final AbstractEffect effectClazz = effect.getClazz();
            final long itemCost = effectClazz.getCost();

            if (coins.hasCoins(itemCost)) {

                // yes
                coins.removeCoins(itemCost);
                effect.grantItem(getPlayer());

                // fx
                sendShopMessage("&a&lPurchase Complete! &aYou purchased %s for %s Coins.", effectClazz.getName(), effectClazz.getCost());
                PlayerLib.playSound(getPlayer(), Sound.ENTITY_VILLAGER_YES, 1.25f);
            }
            else {
                getPlayer().closeInventory();
                sendShopMessage("&cCould not perform your purchase because you don't have enough coins!");
            }

            this.updateMenu();

        } catch (Exception error) {
            sendShopMessage("&cCould not perform your purchase, please try again later!");
            getPlayer().closeInventory();
        }
    }

    private void buildRerollItem() {
        final int rerollPrice = this.shop.getReRollCount() + 1;

        final ItemBuilder builder = new ItemBuilder(Material.FEATHER)
                .setName("&aRe-roll Shop")
                .addSmartLore("If you don't like today's shop, you may re-roll it to get new offers!")
                .addLore()
                .addLore("&7Cost")
                .addLore(" &6%s Ruby", rerollPrice)
                .addLore();


        if (Database.getRubies(getPlayer()).getRubies() >= rerollPrice) {
            builder.addLore("&eClick to re-roll");
            this.setItem(26, builder.toItemStack(), (player) -> new ConfirmMenu(player, "Item Shop Re-Roll", t -> rerollShop(), t -> updateMenu()));
        }
        else {
            builder.addLore("&cCannot afford re-roll");
            this.setItem(26, builder.toItemStack(), this::soundPop);
        }

    }

    private void rerollShop() {
        this.shop.incrementReRollCount();
        this.shop.generateItems();

        // fx
        PlayerLib.playSound(getPlayer(), Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1.75f);
        PlayerLib.playSound(getPlayer(), Sound.BLOCK_PISTON_EXTEND, 1.0f);
        Database.getRubies(getPlayer()).removeRubies(this.shop.getReRollCount());
        sendShopMessage("&aRe-Roll! &7Your shop has been re-rolled, enjoy!");

        // create new shop to update the shop number
        new ShopGUI(getPlayer());
    }

    private void sendShopMessage(String msg, Object... a) {
        Chat.sendMessage(getPlayer(), "&d&lSHOP! &7" + msg, a);
    }

    private void soundPop(Player player) {
        PlayerLib.playSound(player, Sound.BLOCK_LAVA_POP, 0.0f);
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

    private ItemStack buildShopItem(EnumEffect enumEffect) {
        final AbstractEffect effect = enumEffect.getClazz();
        final boolean isOwned = enumEffect.isOwned(getPlayer());

        final ItemBuilder builder = new ItemBuilder(effect.getIcon())
                .setName("&a%s", effect.getName())
                .addLore("&8%s Effect", enumEffect.getTypeString())
                .addLore()
                .addSmartLore(effect.getLore())
                .addLore();

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


        builder.addSmartLore("Cosmetics effects grants no combat advantage.", "&8");
        builder.addLore().addLore("&7Price").addLore(" &6%s", effect.getCost()).addLore();
        builder.predicate(isOwned, ItemBuilder::glow);
        builder.addLoreIf("&aYou already own this item!", isOwned);
        builder.addLoreIf("&eClick to purchase!", !isOwned);

        return builder.hideFlags().toItemStack();
    }

}
