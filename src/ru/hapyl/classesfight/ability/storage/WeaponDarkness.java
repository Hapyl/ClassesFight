package ru.hapyl.classesfight.ability.storage;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.ability.extra.GrimoireCooldownApplier;
import ru.hapyl.classesfight.classes.iclass.Librarian;
import ru.hapyl.classesfight.classes.iclass.extra.GrimmoreBook;
import ru.hapyl.classesfight.classes.iclass.inst.Instance;
import ru.hapyl.classesfight.runnable.GameTask;

public class WeaponDarkness extends Ability implements GrimoireCooldownApplier {
	public WeaponDarkness() {
		super("Infusion of Darkness", "Infuses your weapon for &b10&7/&b13&7/&b16&7/&b19s &7with higher damage.");
		this.setItem(Material.INK_SAC);
	}

	private final int[] tickPerLevel = {200, 260, 320, 380};

	@Override
	public Response useAbility(Player player) {
		final Librarian librarian = Instance.LIBRARIAN;
		librarian.infuseWeapon(player, true);
		new GameTask() {
			@Override
			public void run() {
				librarian.infuseWeapon(player, false);
			}
		}.runTaskLater(tickPerLevel[librarian.getBookLevel(player) - 1]);
		librarian.removeSpellItems(player, Abilities.WEAPON_DARKNESS);
		GrimmoreBook.applyCooldown(player, getGrimmoreCooldown());
		return Response.OK;
	}

	@Override
	public int getGrimmoreCooldown() {
		return 30 * 20;
	}
}
