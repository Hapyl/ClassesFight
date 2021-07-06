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

import ru.hapyl.classesfight.database.Database;

import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseEntry {

    public Database database;

    public DatabaseEntry(Database database) {
        this.database = database;
    }

    public static String enumToJson(Enum<?> var) {
        return var.name();
    }

    public static String jsonToEnum(String json) {
        return json;
    }

    public final <T extends Enum<T>> List<String> enumListToString(List<T> enumList) {
        final List<String> strings = new ArrayList<>();
        for (T theEnum : enumList) {
            strings.add(theEnum.name());
        }
        return strings;
    }

    public final <T extends Enum<T>> List<T> stringListToEnum(List<String> strings, Class<T> type) {
        List<T> container = new ArrayList<>();

        for (String string : strings) {
            try {
                final T enumValue = Enum.valueOf(type, string);
                container.add(enumValue);
            } catch (IllegalArgumentException ignored0) {
            }
        }

        return container;
    }

}
