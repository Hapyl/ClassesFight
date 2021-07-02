package ru.hapyl.classesfight.database.entry;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.feature.perk.Perk;
import ru.hapyl.classesfight.utils.Validator;

import java.util.ArrayList;
import java.util.List;

public class PerkEntry extends DatabaseEntry {

    private final YamlConfiguration yaml;

    public PerkEntry(Database database) {
        super(database);
        this.yaml = this.database.getYaml();
    }

    public void grantPerk(Perk perk) {
        setOwned(perk, true);
    }

    public void revokePerk(Perk perk) {
        clearPerk(perk);
    }

    public List<Perk> getPerks() {
        final List<Perk> ownedPerks = new ArrayList<>();
        for (Perk value : Perk.values()) {
            if (hasPerk(value)) {
                ownedPerks.add(value);
            }
        }

        return ownedPerks;
    }

    /**
     * perks.PERK {
     * owned: boolean
     * enabled: boolean
     * }
     */
    public boolean hasPerk(Perk perk) {
        return yaml.getBoolean(String.format("perks.%s.owned", perk.name()), false);
    }

    public void clearPerk(Perk perk) {
        yaml.set(String.format("perks.%s", perk.name()), null);
    }

    public boolean isEnabled(Perk perk) {
        return yaml.getBoolean(String.format("perks.%s.enabled", perk.name()), false);
    }

    public void setOwned(Perk perk, boolean owned) {
        this.yaml.set(String.format("perks.%s.owned", perk.name()), owned);

    }

    public void setEnabled(Perk perk, boolean enabled) {
        this.yaml.set(String.format("perks.%s.enabled", perk.name()), enabled);
    }


}
