package ru.hapyl.classesfight.translate;

import ru.hapyl.classesfight.utils.ConfigFile;

public class Locale extends ConfigFile {

    private final String displayName;
    private final String localeName;

    public Locale(String localeName, String display) {
        super(localeName.toLowerCase() + "");
        this.displayName = display;
        this.localeName = localeName.toLowerCase();
    }

    public final String getByKeyOr(String key, String or) {
        return this.getConfig().getString(key, or);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLocaleName() {
        return localeName;
    }
}
