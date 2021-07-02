package ru.hapyl.classesfight.quest.quest;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.quest.Quest;
import ru.hapyl.classesfight.quest.QuestProgress;

public class UseAbility extends Quest {

    public UseAbility() {
        super("Abilities", "Use your abilities 10 times.");
    }

    @Override
    public void startQuest(Player player) {
        new QuestProgress(player, this, 10) {
        };
    }

}
