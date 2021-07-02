package ru.hapyl.classesfight.classes.iclass.extra;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.iclass.DarkMageClass;

public interface Spell {

	void execute(Player player);

	boolean test(SpellMode.SpellButton first, SpellMode.SpellButton second);

	DarkMageClass.EnumAbility getAbility();

}
