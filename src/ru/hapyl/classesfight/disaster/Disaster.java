package ru.hapyl.classesfight.disaster;

import org.bukkit.Material;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.utils.GameElement;

public abstract class Disaster implements GameElement {

	private final Material material;
	private final String name;
	private final String about;
	private boolean enabled;

	protected final GameManager manager = GameManager.current();
	protected int currentTick;

	public Disaster(Material material, String name, String about) {
		this.material = material;
		this.name = name;
		this.about = about;
		this.enabled = false;
		this.currentTick = 0;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Material getMaterial() {
		return material;
	}

	public String getName() {
		return name;
	}

	public String getAbout() {
		return about;
	}

	public int currentTick() {
		return currentTick;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public abstract void onTick();

}
