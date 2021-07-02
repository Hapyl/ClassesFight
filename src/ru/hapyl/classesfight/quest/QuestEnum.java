package ru.hapyl.classesfight.quest;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.quest.quest.UseAbility;

public enum QuestEnum {

    INVALID_QUEST(new Quest("Invalid Quest", "You should have never able to see this!") {
        @Override
        public void startQuest(Player player) {
            player.sendMessage("Invalid Quest Invokation Error");
        }
    }),
    USE_ABILITY(new UseAbility());

    private final Quest quest;

    QuestEnum(Quest quest) {
        this.quest = quest;
    }

    public void startQuest(Player player) {
        this.quest.startQuest(player);
    }

    public QuestEnum byQuest(Quest quest) {
        for (QuestEnum value : values()) {
            if (value.getQuest() == quest) {
                return value;
            }
        }
        return INVALID_QUEST;
    }

    public boolean isValid() {
        return this != INVALID_QUEST;
    }

    public Quest getQuest() {
        return quest;
    }
}
