package ru.hapyl.classesfight.quest;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

/**
 * This class is used to store players progress to certain Quest
 */
public abstract class QuestProgress {

    private final Player player;
    private final Quest quest;

    private final int targetInt;
    private int currentInt;

    private final QuestManager manager = QuestManager.current();

    public QuestProgress(Player player, Quest quest, int targetInt) {
        this.player = player;
        this.quest = quest;
        this.targetInt = targetInt;
        this.currentInt = 0;

        if (manager.hasQuest(player, quest)) {
            this.sendMessage("Cannot start %s since you already have this quest active!", quest.getName());
            return;
        }

        manager.startQuest(player, this);

    }

    // finals
    public final void finishQuest() {

    }

    public final void increment() {
        ++this.currentInt;
        if (this.currentInt >= this.targetInt) {
            this.finishQuest();
        }
    }

    public final void sendMessage(String message, Object... replacements) {
        this.sendMessage(this.getQuest().getName(), message, replacements);
    }

    public final void sendMessage(String prefix, String message, Object... replacements) {
        Chat.sendMessage(this.getPlayer(), Chat.GREEN_BOLD + prefix + " &7" + message, replacements);
    }

    public Player getPlayer() {
        return player;
    }

    public Quest getQuest() {
        return quest;
    }


    public int getCurrentInt() {
        return currentInt;
    }

    public int getTargetInt() {
        return targetInt;
    }

    /**
     * Returns progress of quest completion as float between 0.0f - 1.0f
     *
     * @return progress of quest completion as float between 0.0f - 1.0f
     */
    public float getProgress() {
        return Math.max(0.0f, Math.min(1.0f, (float)this.currentInt / this.targetInt));
    }

    public boolean isFinished() {
        return this.getProgress() == 1.0f;
    }

}
