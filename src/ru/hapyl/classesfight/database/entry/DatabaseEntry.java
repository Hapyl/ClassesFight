package ru.hapyl.classesfight.database.entry;

import ru.hapyl.classesfight.database.Database;

import java.util.ArrayList;
import java.util.Arrays;
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
