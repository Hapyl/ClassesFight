package ru.hapyl.classesfight.utils.candle;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public enum Candles {

	WHITE(10, "Candle (White)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg3ZDgzNWI1NDNlZDFiMDI0MTU3MDFjYTdiM2Y4YzhhMGExMTJhZjEzMThmOWNlYzVhNWU5MWU0ODE0YTI0OSJ9fX0="),
	BLACK(11, "Candle (Black)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFmYzRiMDQ1OTgzZTY4OTVjNWY0MDAyOGU5OTNkNTE2NzMyZTkzNmRlMTU4M2VjYmEzZjMyZmUxNWZjZjAzZiJ9fX0="),
	BLUE(12, "Candle (Blue)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ5NjQ3N2Y0MDUyYWZjMzZiZDg4MDg2NjQzOTYxZGU3OTg3OWYyNDgwZDIzZGYxODI2MjBhNmQwYzdiZjNjMCJ9fX0="),
	BROWN(13, "Candle (Brown)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE0MDlmNmQxZGY1NWZhNTlkNjI3ZWM2NWNlYTBhZWRiNDNiOTgyNDE2ZDVkOTliNzEyZTBmNDYwZmY3M2QzOCJ9fX0="),
	CYAN(14, "Candle (Cyan)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzlkYzI2ZWJhYTI1NmNmYTAyNGM4ZmZiZDQxZGNjOGVkZDliZjE3YTEyODM3OTA2OTY3YjdhZjE5NGM3N2QzMiJ9fX0="),
	GREEN(15, "Candle (Green)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM1MmZkNTM1ZDZmYWE5MTk5ZGNhZDFhYzgxZmUxZTkxYWRiZjM5M2MyYTAzMWUyYzkwOGZmOGRhYTg2ZWMxZCJ9fX0="),
	GRAY(16, "Candle (Gray)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE2NjRhZGJmMzc1YjU4ZTk4NTQ5NDY5ZTljYjA0NWQxZDM2M2ZlZWVhNTYwODZjODQ0ZWQzY2E1OWZjMzYzNiJ9fX0="),
	LIGHT_BLUE(19, "Candle (Light Blue)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzM4ZjdiODlkZDViNDBkZjAyYzM1ZGVlZDQxZTg1Y2Q2NmVmN2YyNDM3ZTczOTZjYTcxZmVlMDg1N2RjOThkMCJ9fX0="),
	LIME(20, "Candle (Lime)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTI5N2U4NjU1MGZjMmRjOTFjNDYxN2EzMzIyMGZiNmUxNDAyN2QzMWRlN2Y3YjI5YmRkNzJmODE1NDczODU2OCJ9fX0="),
	LIGHT_GRAY(21, "Candle (Light Gray)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU3N2FkNTI1ZmJhODE0MzQ3MzNhZDU4NmQ5NTZhN2YzYjZjNjE5ZGM1MWIxNzQzNDE1MTAzNTNmYmE1MDExNSJ9fX0="),
	MAGENTA(22, "Candle (Magenta)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTIzZjhhNjU2MzlmNmUyNjQ2NmMzNTE4YTFlODE4NTA0YjgzNWFmY2JmNmZkNzA5NmVlM2JkOTc3MTMzZWMwMSJ9fX0="),
	ORANGE(23, "Candle (Orange)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZjMGIzMTdiZGVmMDBmYWJiOTA0YTk5MGM3NWM0NzcyYjNiZWIxNjQ2NWM0ZmNlYzY5YTVkNmIzM2FiOTZiMCJ9fX0="),
	PINK(24, "Candle (Pink)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY5MGFkNjA4YWI1Zjc5YmRiNTE3YWU2MzRiNGRjNDQzZGY2ZmI2ZDBiMTc2YzExMjQyYmY4ODQ3YjRlYjhjIn19fQ=="),
	PURPLE(25, "Candle (Purple)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTNiNDdiYTcwYzljMzZhYTA0ZDQ1OTA0ZWJmNDFiMWZhOTRjYzc0MDFhNjc2YTg2YjI1ZjE0MzRlZGMzNjhkOCJ9fX0="),
	RED(29, "Candle (Red)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTEzODgyNDhhYzFhOTA1YWNmZWM4MDE3MjkyOTNjZTVlYmE3OGViODlhNzAzYTJiOWQ4N2MxZTVhYzExMDQ3NCJ9fX0="),
	YELLOW(33, "Candle (Yellow)", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU2ZWY4OTFmM2QyNDlmZDIwN2Y3MWVmNWUyOWJhYjcyYjA0YzMwOTY3YzhhZWI1YzI5NzM3ZjAzMDgyNmMxMCJ9fX0=");

	private final int slot;
	private final String name;
	private final String texture;
	private final ItemStack itemStack;

	Candles(int slot, String name, String texture) {
		this.slot = slot;
		this.name = name;
		this.texture = texture;
		this.itemStack = createItemStack();
	}

	public int getSlot() {
		return slot;
	}

	public String getTexture() {
		return texture;
	}

	public String getName() {
		return name;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	private ItemStack createItemStack() {
		return ItemBuilder.playerHead(this.texture).setName(ChatColor.GREEN + this.name).toItemStack();
	}

}
