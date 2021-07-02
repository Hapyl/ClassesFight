package ru.hapyl.classesfight.ability.extra;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.storage.MeleeStance;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.PlayerData;

public class MeleeData extends PlayerData {

    private final long enteredAt;
    private final ItemStack rangeItem;
    private final GameTask task;

    public MeleeData(Player player, ItemStack rangeItem) {
        super(player);
        this.enteredAt = System.currentTimeMillis();
        this.rangeItem = rangeItem;
        long maxMeleeStanceTime = 600;
        this.task = new GameTask() {
            @Override
            public void run() {
                ((MeleeStance)Abilities.MELEE_STANCE.getAbility()).switchToRange(player);
            }
        }.runTaskLater(maxMeleeStanceTime);
    }

    public void cancelTask() {
        this.task.cancel();
    }

    public long getEnteredAt() {
        return enteredAt;
    }


    public ItemStack getRangeItem() {
        return rangeItem;
    }
}
