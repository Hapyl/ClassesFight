package ru.hapyl.classesfight.utils.pn.gui;

import com.google.common.collect.Lists;
import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import kz.hapyl.spigotutils.module.math.IntInt;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.utils.pn.ClassUpdatePatch;
import ru.hapyl.classesfight.utils.pn.Note;
import ru.hapyl.classesfight.utils.pn.PatchNotes;

import java.util.List;
import java.util.Map;

public class ClassChangesGUI extends PatchGUI {

	public ClassChangesGUI(Player player, PatchNotes patch) {
		super(player, patch, ARROW_FORWARD + " Class Changes", 5);
	}

	@Override
	protected void updateMenu() {
		final Map<ClassManager, ClassUpdatePatch> classPatches = patch.getClassPatches();

		final IntInt intInt = new IntInt(10);
		classPatches.forEach((clazz, notes) -> {
			final int i = intInt.get();
			setItem(i, buildClassChangeItem(notes));
			intInt.addAndGet(i % 9 == 7 ? 3 : 1);
		});

		this.openInventory();
	}

	private ItemStack buildClassChangeItem(ClassUpdatePatch cup) {
		final ItemBuilder builder = new ItemBuilder(cup.getClazz().getTheClass().getItem()).setName(cup.getClazz().getDisplayName(ChatColor.GREEN));
		final Map<Note.Type, List<Note>> about = cup.getAbout();

		final List<Note> changes = about.getOrDefault(Note.Type.CHANGE, Lists.newArrayList());
		final List<Note> buffs = about.getOrDefault(Note.Type.BUFF, Lists.newArrayList());
		final List<Note> nerfs = about.getOrDefault(Note.Type.NERF, Lists.newArrayList());
		final List<Note> bugFixes = about.getOrDefault(Note.Type.BUG_FIX, Lists.newArrayList());

		final Icon icons = PatchGUI.ICONS;
		if (!changes.isEmpty()) {
			for (final Note change : changes) {
				builder.addSmartLore(icons.getChangeIcon() + change.getNote(), 50);
			}
		}

		if (!buffs.isEmpty()) {
			for (final Note change : buffs) {
				builder.addSmartLore(icons.getBuffIcon() + change.getNote(), 50);
			}
		}

		if (!nerfs.isEmpty()) {
			for (final Note change : nerfs) {
				builder.addSmartLore(icons.getNerfIcon() + change.getNote(), 50);
			}
		}

		if (!bugFixes.isEmpty()) {
			for (final Note bugFix : bugFixes) {
				builder.addSmartLore(icons.getBugFixIcon() + bugFix.getNote(), 50);
			}
		}

		return builder.toItemStack();

	}

}
