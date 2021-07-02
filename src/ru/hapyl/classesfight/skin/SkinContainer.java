package ru.hapyl.classesfight.skin;

import ru.hapyl.classesfight.classes.ClassManager;

public enum SkinContainer {

    DRAGON("Ender Dragon", ClassManager.ENDER, WrappedSkin.emptySkin());

    private final String skinName;
    private final ClassManager forClass;
    private final WrappedSkin wrappedSkin;

    SkinContainer() {
        this("Invalid or Incomplete Skin", ClassManager.ENDER, WrappedSkin.emptySkin());
    }

    SkinContainer(String name, ClassManager forClass, WrappedSkin wrappedSkin) {
        this.skinName = name;
        this.forClass = forClass;
        this.wrappedSkin = wrappedSkin;
    }

    public WrappedSkin getWrappedSkin() {
        return wrappedSkin;
    }
}
