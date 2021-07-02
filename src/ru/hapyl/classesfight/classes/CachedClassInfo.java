package ru.hapyl.classesfight.classes;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.classes.iclass.IClass;

public class CachedClassInfo {

	private final IClass clazz;
	private final ItemStack itemLocked;
	private final ItemStack itemUnlocked;

	public CachedClassInfo(IClass clazz) {
		this.clazz = clazz;
		final ClassInfo info = clazz.getInfo();

		// Locked
		this.itemLocked = new ItemBuilder(Material.COAL_BLOCK).setName(ChatColor.RED + clazz.getClassName())
				.addLore("&8Locked")
				.addSmartLore("Unlock this class by leveling up your Global Level!")
				.addLore()
				.addLore("&cUnable to select!")
				.toItemStack();

		// Unlocked Item
		final ItemBuilder builder = new ItemBuilder(clazz.getItem());

		builder.setName("&a" + clazz.getClassName() + (clazz.getStatus() == ClassStatus.NONE ? "" : " " + clazz.getStatus().toString()));
		builder.addLore("&8/class " + clazz.getDevName());
		builder.addLore();

		final String health = getStatAmount('❤', clazz.getHealth(), ChatColor.GREEN);
		final String attack = getStatAmount('⚔', clazz.getAttack(), ChatColor.RED);
		final String defense = getStatAmount('❈', clazz.getDefense(), ChatColor.DARK_GREEN);

		builder.addLore("&7Health: " + health);
		builder.addLore("&7Attack: " + attack);
		builder.addLore("&7Defense: " + defense);

		// magic if present
		if (clazz.getMagic() != EnumInfo.NONE) {
			builder.addLore("&7Magic: " + getStatAmount('✿', clazz.getMagic(), ChatColor.DARK_PURPLE));
		}

		// difficulty if present
		if (clazz.getDifficulty() != IClass.ClassDifficulty.NONE) {
			builder.addLore("&7Difficulty: " + getDifficultyFormatted());
		}

		// rating if present
		if (clazz.getRating() != null) {
			builder.addLore("&7Rating: " + clazz.getRating().getTier());
		}

		builder.addLore("");

		if (info.getAboutKit().isEmpty()) {
			builder.addSmartLore("" + info.getLore(), 35);
		}
		else {
			builder.addSmartLore(info.getLore(), "&7&o", 35);
			builder.addLore("__");
			builder.addSmartLore(info.getAboutKit(), 35);
		}

		builder.addLore("__&e&lULTIMATE: &6%s &8(%s)__", info.getUltimate(), clazz.getUltimate().getPoints());
		builder.addSmartLore(info.getUltimateLore(), 35);

		builder.addLore();
		builder.addLore("&eClick to select!");

		this.itemUnlocked = builder.build();
	}

	public final char[] difficultyGradient = {'a', '2', 'e', '6', 'c'};

	public String getDifficultyFormatted() {
		final IClass.ClassDifficulty[] values = IClass.ClassDifficulty.values();
		final StringBuilder builder = new StringBuilder(values.length - 2);
		for (int i = 1; i < values.length; i++) {
			if (clazz.getDifficulty().getIndex() > values[i].getIndex()) {
				builder.append(ChatColor.getByChar(difficultyGradient[i])).append(ChatColor.BOLD).append("☘");
			}
			else {
				builder.append(ChatColor.DARK_GRAY).append("☘");
			}
		}
		return builder.toString();
	}

	public ItemStack getItem(Player player) {
		return this.clazz.isUnlocked(player) ? this.itemUnlocked : itemLocked;
	}

	public ItemStack getItemLocked() {
		return itemLocked;
	}

	public ItemStack getItemUnlocked() {
		return itemUnlocked;
	}

	private String getStatAmount(char c, EnumInfo i, ChatColor applyColor) {

		final String emptyColor = ChatColor.DARK_GRAY.toString();
		final String color = applyColor.toString();

		String current = "";
		for (int j = 0; j < 4; j++) {
			if (j == 3) {
				if (i.index() == j)
					current = current.concat(color + c);
			}
			else
				current = current.concat((i.index() >= j ? color : emptyColor) + c);
		}
		return current;

	}

}
