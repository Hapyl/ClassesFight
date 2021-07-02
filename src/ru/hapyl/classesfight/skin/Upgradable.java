package ru.hapyl.classesfight.skin;

import java.util.Map;

public interface Upgradable {

    Map<Integer, SkinUpgrade> getUpgrades();

    void setUpgrade(int lvl, SkinUpgrade upgrade);

}
