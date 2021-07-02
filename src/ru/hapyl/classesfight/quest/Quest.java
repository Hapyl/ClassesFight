package ru.hapyl.classesfight.quest;

import org.bukkit.entity.Player;

/**
 * This will be used to create and store Quests
 */
public abstract class Quest {

    private int id;
    private final String name;
    private final String questInfo;

    public Quest(String name, String questInfo) {
        this.id = -1;
        this.name = name;
        this.questInfo = questInfo;
    }

    public abstract void startQuest(Player player);

    public final void setId(int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

    public final boolean isRegistered() {
        return this.id != -1;
    }

    public final String getName() {
        return name;
    }

    public final String getQuestInfo() {
        return questInfo;
    }
}
