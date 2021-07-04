package ru.hapyl.classesfight.consumable;

import ru.hapyl.classesfight.consumable.consume.Burger;
import ru.hapyl.classesfight.consumable.consume.Soda;

public enum ConsumableEnum {

	BURGER(new Burger()),
	SODA(new Soda());

	private final Consumable consumable;

	ConsumableEnum(Consumable burger) {
		this.consumable = burger;
	}

	public Consumable getConsumable() {
		return consumable;
	}
}
