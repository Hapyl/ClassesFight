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

package ru.hapyl.classesfight.database.entry;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.feature.perk.Perk;

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
