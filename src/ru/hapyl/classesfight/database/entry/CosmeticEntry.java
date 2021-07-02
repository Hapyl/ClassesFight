package ru.hapyl.classesfight.database.entry;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.utils.Validator;

import java.util.ArrayList;
import java.util.List;

public class CosmeticEntry extends DatabaseEntry {

    public CosmeticEntry(Database database) {
        super(database);
    }

    public boolean hasCosmetic(EnumEffect effect) {
        return getOwnedCosmetics().contains(effect);
    }

    public List<EnumEffect> getOwnedCosmetics() {
        final List<String> hash = this.database.getYaml().getStringList("cosmetics");
        final List<EnumEffect> enums = new ArrayList<>();
        for (String str : hash) {
            final EnumEffect enumValue = Validator.getEnumValue(EnumEffect.class, jsonToEnum(str));
            if (enumValue != null) {
                enums.add(enumValue);
            }
        }
        return enums;
    }

    public List<EnumEffect> getOwnedCosmetics(EnumEffect.Type type) {
        final List<EnumEffect> hash = new ArrayList<>();
        for (EnumEffect ownedCosmetic : this.getOwnedCosmetics()) {
            if (ownedCosmetic.getType() == type) {
                hash.add(ownedCosmetic);
            }
        }
        return hash;
    }

    private void setCosmetic(List<EnumEffect> effects) {
        List<String> stringList = new ArrayList<>();
        for (EnumEffect effect : effects) {
            stringList.add(enumToJson(effect));
        }
        this.database.getYaml().set("cosmetics", stringList);
    }

    public void revokeCosmetic(EnumEffect effect) {
        final List<EnumEffect> hash = getOwnedCosmetics();
        hash.remove(effect);
        setCosmetic(hash);
    }

    public void grantCosmetic(EnumEffect effect) {
        final List<EnumEffect> hash = getOwnedCosmetics();
        if (hash.contains(effect)) {
            return;
        }
        hash.add(effect);
        setCosmetic(hash);
    }

    @Nullable
    public EnumEffect getCurrentEffect(EnumEffect.Type type) {
        final String string = this.database.getYaml().getString("cosmetic." + type.name() + ".selected");
        if (string != null) {
            return Validator.getEnumValue(EnumEffect.class, jsonToEnum(string));
        }
        return null;
    }

    public void resetCurrentEffect(EnumEffect.Type type) {
        this.database.getYaml().set("cosmetic." + type.name() + ".selected", null);
    }

    public void setCurrentEffect(EnumEffect effect) {
        this.database.getYaml().set("cosmetic." + effect.getType().name() + ".selected", enumToJson(effect));
    }

    public boolean isEnabled(EnumEffect.Type type) {
        return this.database.getYaml().getBoolean("cosmetic." + type.name() + ".enabled", true);
    }

    public void setEnabled(EnumEffect.Type type, boolean value) {
        this.database.getYaml().set("cosmetic." + type.name() + ".enabled", value);
    }

}
