package ru.hapyl.classesfight.skin;

import org.bukkit.entity.Player;

public abstract class SkinUpgrade {

    private final String name;
    private final Type type;

    public SkinUpgrade(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public abstract void affect(Player player);

}
