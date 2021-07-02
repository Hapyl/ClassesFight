package ru.hapyl.classesfight.database.entry;

import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.quest.relic.Relic;

import java.util.List;

public class CollectibleEntry extends DatabaseEntry {
    public CollectibleEntry(Database database) {
        super(database);
    }

    private final String RELIC_PATH = "collectibles.relic";

    public List<Integer /*id*/> getFoundRelics() {
        return this.database.getYaml().getIntegerList(RELIC_PATH);
    }

    public void addFoundRelic(Relic relic) {
        final List<Integer> foundRelics = getFoundRelics();
        foundRelics.add(relic.getId());
        setFoundRelic(foundRelics);
    }

    public boolean hasFoundRelic(Relic relic) {
        return getFoundRelics().contains(relic.getId());
    }

    public void removeFoundRelic(Relic relic) {
        final List<Integer> foundRelics = getFoundRelics();
        foundRelics.remove((Integer)relic.getId());
        setFoundRelic(foundRelics);
    }

    private void setFoundRelic(List<Integer> ids) {
        this.database.getYaml().set(RELIC_PATH, ids);
    }

}
