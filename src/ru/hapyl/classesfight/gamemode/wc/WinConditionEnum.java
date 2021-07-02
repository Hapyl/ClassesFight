package ru.hapyl.classesfight.gamemode.wc;

import ru.hapyl.classesfight.GameManager;

public enum WinConditionEnum {

	LAST_MAN_STANDING(manager -> manager.getPlayers().size() <= 1),
	TIME_LIMIT(manager -> false);

	private final WinCondition condition;

	WinConditionEnum(WinCondition condition) {
		this.condition = condition;
	}

	public WinCondition getCondition() {
		return condition;
	}

	public boolean validate(final GameManager manager) {
		return this.condition.checkCondition(manager);
	}

}
